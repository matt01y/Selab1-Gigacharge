import * as firebase from 'firebase-admin'
import * as fs from 'firebase-admin/firestore'
import * as fbauth from 'firebase-admin/auth'
import * as fcm from 'firebase-admin/messaging'
import * as serviceAccount from './env/key.json'

const params = {
  type: serviceAccount.type,
  projectId: serviceAccount.project_id,
  privateKeyId: serviceAccount.private_key_id,
  privateKey: serviceAccount.private_key,
  clientEmail: serviceAccount.client_email,
  clientId: serviceAccount.client_id,
  authUri: serviceAccount.auth_uri,
  tokenUri: serviceAccount.token_uri,
  authProviderX509CertUrl: serviceAccount.auth_provider_x509_cert_url,
  clientC509CertUrl: serviceAccount.client_x509_cert_url
}
   
firebase.initializeApp({
  credential: firebase.credential.cert(params),
})


const QUEUE_COLLECTION = "queue"
const CHARGERS_COLLECTION = "chargers"
const JOINEDAT_FIELD = "joined-at"
const STATUS_FIELD = "status"

//voor charger status
const STATUS_FREE = "FREE"          //klaar voor gebruik
const STATUS_CHARGING = "CHARGING"  //aan het laden, dus niet bruikbaar om queue op te vullen
const STATUS_OUT = "OUT"            //out of service

//voor locatie status
const QUEUE_PROGRAM = "QUEUE"       //alle laders zijn bezet, dus wachtrij modus is aan
const OPEN_PROGRAM = "OPEN"         //er zijn laders vrij zonder wachtrij, dus je kan niet inschrijven in de wachtrij

//voor queue collectie
const STATUS_WAITING = "waiting"    //aan het wachten in de rij
const STATUS_LEFT = "left"          //heeft de rij verlaten
const STATUS_ASSIGNED = "assigned"  //een melding is gestuurd en een laadpaal is toegewezen
const STATUS_EXPIRED = "expired"    //persoon heeft niet op tijd gereageerd en is zijn plaats in de rij kwijt
const STATUS_COMPLETE = "complete"  //persoon is aan het laden / heeft geladen

//user types voor chargers
const USER_TYPE = "USER"
const NONUSER_TYPE = "NONUSER"

const db = fs.getFirestore();
const auth = fbauth.getAuth();

const starttime = fs.Timestamp.fromDate(new Date()); //tijd waar script is opgestart
const users = db.collection('users');
const queuequery = db.collectionGroup(CHARGERS_COLLECTION);
const messaging = fcm.getMessaging();

const deleteField = fs.FieldValue.delete();



const observer = queuequery.onSnapshot(snap => {
    try{
        console.log("snapshot");
        snap.docChanges().forEach(async change => {
            if (change.type === 'added' || change.type == 'modified') {
                console.log(`${change.type} event`)
                await handleChange(change.doc)
            }
        });
    }catch (err) {
        console.error("FOUT: bij zetten van docchanges iterator");
        console.error(err)
    }
}, err => {
    console.log(`error: ${err}`)
})

async function handleChange(change : fs.QueryDocumentSnapshot<fs.DocumentData>){
    try {
        const chargerref = change.ref
        const charger = change.data()
        const locationref = change.ref.parent.parent
        const location = await (await locationref!.get()).data()
        const chargersCollection = change.ref.parent;
        const queueCollection = locationref!.collection(QUEUE_COLLECTION)
        console.log(charger);

        //als de locatie momenteel een queue heeft
        if(location!.status === QUEUE_PROGRAM){
            //als de charger vrij is moeten we de volgende in de rij 
            if(charger.status === STATUS_FREE){
                console.log(`charger ${charger.id} reserveren...`)
                //reserveCharger(locationref, chargerref) //nog niet geimplementeerd want doodoo js en ts wil nie
                
                const topOfLine = await getTopOfLine(queueCollection);
                //Als er nu niemand in de queue zit, moet status van de locatie terug naar open
                if(topOfLine == undefined){
                    console.log("niemand in rij, dus naar open programma");
                    setProgram(OPEN_PROGRAM, locationref);
                    chargerref.update({assignedJoin: deleteField, assignedUser: deleteField})
                }else{
                    assignUserToCharger(topOfLine, await chargerref.get())
                }
            }else if(charger.status === STATUS_CHARGING || charger.status === STATUS_OUT){
                //als we in queue modus zijn,
                //moet hier moet gecheckt worden of de persoon die aan het laden is,
                //wel de persoon was die eraan toegewezen was.
                //zo niet, probeer een andere toe te wijzen.
                //en als dat niet lukt, zet hem opnieuw in de wachtrij
                
                if(charger.assignedUser !== undefined){ //is er iemand assigned aan deze charger?
                    if(charger.usertype === USER_TYPE && charger.user === charger.assignedUser){
                        //de gebruiker is diegene die assigned is
                        queueCollection.doc(charger.assignedJoin).update({status: STATUS_COMPLETE}) //YIPPIE!
                        unAssignCharger(chargerref);
                    }else{
                        //frick, het is een andere user, is er nog een vrije lader die we kunnen gebruiken om toe te wijzen?
                        const freeCharger = await getFreeChargerIfExists(chargersCollection);
                        if(freeCharger == undefined){
                            //geen vrije laders meer om toe te wijzen aan de ongelukkige persoon, persoon moet terug in de wacht
                            queueCollection.doc(charger.assignedJoin).update({status: STATUS_WAITING, deleteField})
                        }else{
                            //we kunnen de mens nog redden met een nieuwe charger
                            assignUserToCharger(await queueCollection.doc(charger.assignedJoin).get(), freeCharger);
                        }
                        unAssignCharger(chargerref);
                    }
                }else{
                    //geen probleem, we zijn in open modus, er is een wachtrij of maar de paal was niet assigned for some reason
                }
            }
        }else if(location!.status === OPEN_PROGRAM){
            //check of een queue moet gestartn worden
            //dat is zo wanneer alle laders bezet zijn
            if(! (await hasFreeChargers(chargersCollection))){
                console.log("er zijn geen chargers over, naar queue programma");
                //start queue op
                setProgram(QUEUE_PROGRAM, locationref);
            }
        }
    }catch (err) {
        console.error(err);
    }
}

async function unAssignCharger(chargerref : fs.DocumentReference){
    chargerref.update({assignedJoin: deleteField, assignedUser: deleteField});
}

async function hasFreeChargers(chargersCollection : fs.CollectionReference){
    const amount : number = (await chargersCollection.where(STATUS_FIELD, "==", STATUS_FREE).limit(1).count().get()).data().count;
    return amount === 1
}

async function getFreeChargerIfExists(chargersCollection : fs.CollectionReference){
    const freecharger = (await chargersCollection.where(STATUS_FIELD, "==", STATUS_FREE).limit(1).get()).docs[0];
    return freecharger;
}

async function sendNotification(userId : string, content: fcm.Notification){
    const fcmtoken = (await users.doc(userId).get()).data().fcmtoken

    if(fcmtoken){
        const message : fcm.TokenMessage = {
            notification : content,
            token : fcmtoken
        }
        messaging.send(message)
    }else{
        console.error("GEEN FCMTOKEN BIJ USER")
    }
}

async function setProgram(program : any, locationRef : fs.DocumentReference){
    locationRef.update({status: program})
}

async function assignUserToCharger(joinDoc : fs.DocumentSnapshot, chargerDoc : fs.DocumentSnapshot){
    const locationref = chargerDoc.ref.parent.parent;
    const queueCollection = locationref.collection(QUEUE_COLLECTION);
    const chargerdata = chargerDoc.data()
    const joindata = joinDoc.data()
    //TODO: zend de FCM melding naar de user
    
    let expiretime = new Date(new Date().getTime() + 15 * 60_000); //15 minuten om naar de laadpaal te gaan
    //TODO: start een timer die de wachtrij join expiret na de ingestelde tijd, en de beurt aan iemand anders geeft
    console.log(`user ${joindata['user-id']} wordt ge-assigned`)
    queueCollection.doc(joinDoc!.id).update({status: STATUS_ASSIGNED, assigned: chargerDoc.id, expires:fs.Timestamp.fromDate(expiretime)})
    chargerDoc.ref.update({status: STATUS_ASSIGNED, assignedJoin: joinDoc.id, assignedUser: joindata['user-id']})

    sendNotification(joindata['user-id'], {
        title: "Er is een laadpaal vrij",
        body: `U kan u wagen opladen bij ${chargerdata.description}`
    });
}

async function getTopOfLine(queueCollection : fs.CollectionReference){
    try{
        const res = await (await queueCollection.where(STATUS_FIELD, "==", STATUS_WAITING).orderBy(JOINEDAT_FIELD).limit(1).get()).docs[0]
        return res;
    }catch(err){
        return null;
    }
    
}