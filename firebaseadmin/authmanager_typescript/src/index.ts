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

const db = fs.getFirestore();

const starttime = fs.Timestamp.fromDate(new Date()); //tijd waar script is opgestart
const users = db.collection('users');
const query = users.where('timestamp', '>=', starttime);
const auth = fbauth.getAuth();


const observer = query.onSnapshot(snap => {
   try {
      console.log("snapshot")
      snap.docChanges().forEach(async change => {
         console.log("add event")
         if (change.type === 'added' || change.type === 'modified') {
            try{
               const data = (await users.doc(change.doc.id).get()).data()
               if(data.enablestatus === "enabled") return
               
               if (!data.kaartnummer) { return; }
               const existsquery = db.collection('whitelist').where("kaartnummer", "==", data.kaartnummer).count();
               const count = await (await existsquery.get()).data().count;
               if (count >= 1) {
                  console.log(`nieuwe user ${change.doc.id} is in whitelist, token aanpassen...`);
                  const user = await auth.getUser(change.doc.id);
                  let currentclaims = user.customClaims;
                  if(currentclaims === undefined){
                     currentclaims = {};
                  }
                  currentclaims['enabled'] = true; //account is nu enabled, kan interageren met user database van app
                  await auth.setCustomUserClaims(change.doc.id, currentclaims);

                  //zet status message
                  change.doc.ref.update({enablestatus: "enabled"})
               } else {
                  console.log(`nieuwe user ${change.doc.id} is niet in de whitelist`);
                  change.doc.ref.update({enablestatus: "not_in_whitelist_error"})
               }
            }catch(err){
               change.doc.ref.update({enablestatus: "unknown_error"})
               console.error("FOUT: bij nieuwe user enabling")
               console.error(err);
            }
         }
      })
   } catch (err) {
      console.error("FOUT: bij zetten van docchanges iterator");
      console.error(err)
   }

}, err => {
   console.log(`error: ${err}`)
})

console.log("authenticatie manager gestart")
