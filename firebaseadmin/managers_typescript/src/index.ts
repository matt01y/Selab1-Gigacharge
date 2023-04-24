import * as firebase from 'firebase-admin'
import * as firestore from 'firebase-admin/firestore'
import * as fbauth from 'firebase-admin/auth'
import * as fcm from 'firebase-admin/messaging'
import * as serviceAccount from '/home/robin/docs/code/project2023-groep-3/firebaseadmin/managers_typescript/env/key.json'

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
            }
        });
    }catch (err) {
        console.error("FOUT: bij zetten van docchanges iterator");
        console.error(err)
    }
}, err => {
    console.log(`error: ${err}`)
})