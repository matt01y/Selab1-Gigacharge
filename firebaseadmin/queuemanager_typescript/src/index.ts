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

const USERID_FIELD = "user-id"

//user types voor chargers
const USER_TYPE = "USER"
const NONUSER_TYPE = "NONUSER"

const db = fs.getFirestore();
const auth = fbauth.getAuth();

const starttime = fs.Timestamp.fromDate(new Date()); //tijd waar script is opgestart
const users = db.collection('users');
//const updatedUsers = users.where('timestamp', '>=', starttime);
const updatedUsers = users;
const chargersquery = db.collectionGroup(CHARGERS_COLLECTION);
const queuequery = db.collectionGroup(QUEUE_COLLECTION);
const messaging = fcm.getMessaging();

const deleteField = fs.FieldValue.delete();

const EXPIRY_MS = 5 * 60 * 1000;



const chargerObserver = chargersquery.onSnapshot(snap => {
    try{
        snap.docChanges().forEach(async change => {
            if (change.type === 'added' || change.type == 'modified') {
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

        //als de locatie momenteel een queue heeft
        if(location!.status === QUEUE_PROGRAM){
            //als de charger vrij is moeten we de volgende in de rij 
            if(charger.status === STATUS_FREE){
                console.log(`charger ${charger.description} is nu vrij`)
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

                if(charger.status === STATUS_CHARGING && charger.assignedUser !== undefined && charger.usertype === USER_TYPE && charger.user === charger.assignedUser){
                    //de ge-assignde gebruiker is nu aan het laden, episch!
                    console.log(`ge-assignede user ${charger.user} is nu op de correcte plek aan het opladen`)
                    queueCollection.doc(charger.assignedJoin).update({status: STATUS_COMPLETE}) //YIPPIE!
                }else{

                    let swapcharger : fs.DocumentReference | undefined = undefined;

                    if(location!.status === QUEUE_PROGRAM && charger.usertype === USER_TYPE){
                        
                        //de user van de paal heeft misschien gaan laden bij de foute paal
                        
                        //zoek de ge-assignde of waiting joinDocs van de user en complete ze
                        const joinDocs = (await queueCollection.where(USERID_FIELD, '==', charger.user).where(STATUS_FIELD, 'in', [STATUS_ASSIGNED, STATUS_WAITING]).get()).docs;
                        joinDocs.forEach(e => {
                            //confirmed dat er bij de foute paal wordt geladen
                            //de paal waar aan ge-assigned was kan nu gebruikt worden om de (eventuele) persoon waarvan gestolen is te assignen
                            swapcharger = chargersCollection.doc(e.data().assigned)
                            console.log("bozo laadt op foute plek op")
                            e.ref.update({status: STATUS_COMPLETE});
                        })

                    }

                    if(charger.assignedUser !== undefined){
                        //probeer ongelukkige een nieuwe paal toe te wijzen
                        console.log(`${charger.assignedUser} is zijn plek kwijtgeraakt, nieuwe toewijzen...`)
                        const freeCharger = swapcharger == undefined ? await getFreeChargerIfExists(chargersCollection) : await swapcharger.get();
                        if(freeCharger == undefined){
                            console.log("dat is niet gelukt frick")
                            //geen vrije laders meer om toe te wijzen aan de ongelukkige persoon, persoon moet terug in de wacht
                            queueCollection.doc(charger.assignedJoin).update({status: STATUS_WAITING, deleteField})
                        }else{
                            console.log("en dat lukt, thank god")
                            //we kunnen de mens nog redden met een nieuwe charger
                            assignUserToCharger(await queueCollection.doc(charger.assignedJoin).get(), freeCharger);
                        }
                    }

                    
                }

                chargerref.update({assignedJoin: deleteField, assignedUser: deleteField});

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
    try{
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
    }catch(err){

    }
    
}

async function setProgram(program : any, locationRef : fs.DocumentReference){
    locationRef.update({status: program})
}

async function assignUserToCharger(joinDoc : fs.DocumentSnapshot, chargerDoc : fs.DocumentSnapshot){
    console.log("assigning...")
    const locationref = chargerDoc.ref.parent.parent;
    const queueCollection = locationref.collection(QUEUE_COLLECTION);
    const chargerdata = chargerDoc.data()
    const joindata = joinDoc.data()
    //TODO: zend de FCM melding naar de user
    
    const now = new Date();
    let expiretime = new Date(new Date().getTime() + EXPIRY_MS); //15 minuten om naar de laadpaal te gaan
    const expiretimestamp = fs.Timestamp.fromDate(expiretime);
    //TODO: start een timer die de wachtrij join expiret na de ingestelde tijd, en de beurt aan iemand anders geeft
    console.log(`user ${joindata['user-id']} wordt ge-assigned`)
    queueCollection.doc(joinDoc!.id).update({status: STATUS_ASSIGNED, assigned: chargerDoc.id, expires: expiretimestamp})
    chargerDoc.ref.update({status: STATUS_ASSIGNED, assignedJoin: joinDoc.id, assignedUser: joindata['user-id']})

    sendNotification(joindata['user-id'], {
        title: "Er is een laadpaal vrij",
        body: `U kan u wagen opladen bij ${chargerdata.description}`
    });

    setTimeout(() => {
        console.log("expiring...")
        expireAssignment(chargerDoc.ref, joinDoc.ref, expiretimestamp)
    }, Math.abs(expiretime.getTime() - now.getTime()));
}

async function expireAssignment(chargerDoc : fs.DocumentReference, joinDoc : fs.DocumentReference, expiretimestamp : fs.Timestamp){
    const charger = (await chargerDoc.get()).data();
    const join = (await joinDoc.get()).data();
    const topOfLine = await getTopOfLine(joinDoc.parent)
    const queueEmpty = topOfLine == undefined

    console.log(join);
    console.log(chargerDoc.id);

    if(join.status === STATUS_ASSIGNED && join.assigned === chargerDoc.id){ //is er nog niks van updates gebeurt in de tijd voor het expiren?
        console.log("check 1 ok");
        const join = (await joinDoc.get()).data()
        await joinDoc.update({status: STATUS_EXPIRED, assigned: deleteField, expires: deleteField});
        
        if(queueEmpty){
            if(charger.status === STATUS_ASSIGNED && charger.assignedJoin === joinDoc.id){
                chargerDoc.update({status: STATUS_FREE, assignedUser: deleteField, assignedJoin: deleteField})
            }
            //stuur een melding dat die uit de queue is, maar de plaats nu wel 
            sendNotification(join['user-id'], {
                title: "Laadbeurt is verlopen",
                body: `Er is voorlopig niemand in de wachtrij dus u kunt nog altijd opladen bij ${charger.description}.`
            })
        }else{
            
            sendNotification(join['user-id'], {
                title: "Laadbeurt is verlopen",
                body: "U beurt wordt doorgegeven aan de volgende in de wachtrij."
            })

            if(charger.status === STATUS_ASSIGNED && charger.assignedJoin === joinDoc.id){
                chargerDoc.update({assignedUser: deleteField, assignedJoin: deleteField})
                assignUserToCharger(topOfLine, await chargerDoc.get())
            }
            
        }

    }
}


async function getTopOfLine(queueCollection : fs.CollectionReference){
    try{
        const res = await (await queueCollection.where(STATUS_FIELD, "==", STATUS_WAITING).orderBy(JOINEDAT_FIELD).limit(1).get()).docs[0]
        return res;
    }catch(err){
        return undefined;
    }
    
}


const updatedUsersQuery = updatedUsers.onSnapshot(snap => {
    try{
        snap.docChanges().forEach(async change => {
            try{
                if (change.type === 'removed') {
                    const user = change.doc.data();
                    console.log("VERWIJDERING")
                    console.log(user)
                    //dit account is verwijderd
    
                    //complete al zijn joinDocs
                    const userJoins = await queuequery.where("user-id", '==', change.doc.id).get()
                    userJoins.docs.forEach(join => {
                        console.log("join complete")
                        join.ref.update({status: STATUS_COMPLETE});
                    })
    
    
                    //zet alle chargers waaraan hij assigned is op free
                    const userChargers = await chargersquery.where(STATUS_FIELD, '==', STATUS_ASSIGNED).where("assignedUser", '==', change.doc.id).get();
                    userChargers.docs.forEach(charger =>{
                        console.log("charger losgelaten")
                        charger.ref.update({status: STATUS_FREE, assignedUser: deleteField, assignedJoin: deleteField})
                    });
    
    
                }
            }catch(err){
                console.error(err);
            }
        });
    }catch (err) {
        console.error("FOUT: bij zetten van docchanges iterator");
        console.error(err)
    }
}, err => {
    console.log(`error: ${err}`)
})