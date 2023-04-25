import * as firebase from 'firebase-admin'
import * as firestore from 'firebase-admin/firestore'
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

const db = firestore.getFirestore();
const auth = fbauth.getAuth();

const starttime = firestore.Timestamp.fromDate(new Date()); //tijd waar script is opgestart
const users = db.collection('users');
const queuequery = db.collectionGroup(CHARGERS_COLLECTION);
const messaging = fcm.getMessaging();



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

async function handleChange(change : firestore.QueryDocumentSnapshot<firestore.DocumentData>){
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
                    console.log("niemand in rij, dus naar open programma")
                    locationref!.update({status: OPEN_PROGRAM});
                    chargerref.update({assignedJoin: firestore.FieldValue.delete(), assignedUser: firestore.FieldValue.delete()})
                }else{
                    
                    const firstInLineData = topOfLine!.data();
                    //TODO: zend de FCM melding naar de user
                    
                    let expiretime = new Date(new Date().getTime() + 15 * 60_000); //15 minuten om naar de laadpaal te gaan
                    //TODO: start een timer die de wachtrij join expiret na de ingestelde tijd, en de beurt aan iemand anders geeft
                    console.log(`user ${firstInLineData['user-id']} wordt ge-assigned`)
                    queueCollection.doc(topOfLine!.id).update({status: STATUS_ASSIGNED, assigned: chargerref.id, expires:firestore.Timestamp.fromDate(expiretime)})
                    chargerref.update({status: STATUS_ASSIGNED, assignedJoin: topOfLine.id, assignedUser: firstInLineData['user-id']})

                    
                    const user = (await users.doc(firstInLineData['user-id']).get()).data();
                    if(user!.fcmtoken){
                        const message = {
                            notification: {
                                title: "Er is een laadpaal vrij",
                                body: `U kan u wagen opladen bij ${charger.description}`
                            },
                            token: user!.fcmtoken
                        }
                        messaging.send(message)
                    }else{
                        console.log("GEEN FCMTOKEN BIJ USER")
                    }
                    
                }
            }else if(charger.status === STATUS_CHARGING || charger.status === STATUS_OUT){
                //als we in queue modus zijn,
                //moet hier moet gecheckt worden of de persoon die aan het laden is,
                //wel de persoon was die eraan toegewezen was.
                //zo niet, probeer een andere toe te wijzen.
                //en als dat niet lukt, zet hem opnieuw in de wachtrij
                
                if(charger.assignedUser !== undefined){
                    if(charger.usertype === USER_TYPE && charger.user === charger.assignedUser){
                        queueCollection.doc(charger.assignedJoin).update({status: STATUS_COMPLETE, assignedUser: firestore.FieldValue.delete(), assignedJoin: firestore.FieldValue.delete()}) //YIPPIE!
                    }else{
                        //frick, het is een andere user
                        const freechargersQuery = chargersCollection.where(STATUS_FIELD, "==", STATUS_FREE)
                        const freechargersCount = await (await freechargersQuery.count().get()).data().count
                        if(freechargersCount <= 0){
                            //geen vrije laders meer om toe te wijzen aan de ongelukkige persoon
                            queueCollection.doc(charger.assignedJoin).update({status: STATUS_WAITING, assigned: firestore.FieldValue.delete()})
                            chargerref.update({assignedJoin: firestore.FieldValue.delete()})
                        }else{
                            if(charger.assignedJoin == undefined){

                            }else{
                                //er was iemand assigned aan deze chargrer maar hij is gestolen, geef die mens nen nieuwen
                                const freecharger = (await freechargersQuery.limit(1).get()).docs[0];
                                let expiretime = new Date(new Date().getTime() + 15 * 60_000); //15 minuten om naar de laadpaal te gaan
                                freecharger.ref.update({status: STATUS_ASSIGNED, assignedJoin: charger.assignedJoin, assignedUser: charger.assignedUser})
                                queueCollection.doc(charger.assignedJoin).update({status: STATUS_ASSIGNED, assigned: freecharger.id, expires: firestore.Timestamp.fromDate(expiretime)})

                                
                                const user = (await users.doc(charger.assignedUser).get()).data();
                                if(user.fcmtoken){
                                    const message = {
                                        notification: {
                                            title: "Er is een laadpaal vrij",
                                            body: `U kan u wagen opladen bij ${charger.description}`
                                        },
                                        token: user.fcmtoken
                                    }
                                    messaging.send(message)
                                }else{
                                    console.log("GEEN FCMTOKEN BIJ USER")
                                }

                                chargerref.update({assignedJoin: firestore.FieldValue.delete(), assignedUser: firestore.FieldValue.delete()}); //verwijder toegewezen join.
                            }
                        }
                    }
                }else{
                    //geen probleem, we zijn in open modus, er is een wachtrij of maar de paal was niet assigned for some reason
                }
            }
        }else{
            //check of een queue moet gestartn worden
            //dat is zo wanneer alle laders bezet zijn
            const freechargersCount = await (await chargersCollection.where(STATUS_FIELD, "==", STATUS_FREE).count().get()).data().count
            console.log(`free chargers: ${freechargersCount}`)
            if(freechargersCount <= 0){
                //start queue op
                locationref.update({status: QUEUE_PROGRAM})
                location.status = QUEUE_PROGRAM
            }
        }
    }catch (err) {
        console.error(err);
    }
}

async function getTopOfLine(queueCollection : firestore.CollectionReference){
    try{
        const res = await (await queueCollection.where(STATUS_FIELD, "==", STATUS_WAITING).orderBy(JOINEDAT_FIELD).limit(1).get()).docs[0]
        return res;
    }catch(err){
        return null;
    }
    
}