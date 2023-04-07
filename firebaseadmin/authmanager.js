//require('dotenv').config({ path: __dirname + '/.env' })

const { initializeApp, applicationDefault, cert } = require('firebase-admin/app');
const { getFirestore, Timestamp, FieldValue } = require('firebase-admin/firestore');
const { getAuth } = require('firebase-admin/auth');

var serviceAccount = require("./env/gigacharge-961d6-firebase-adminsdk-8jhn6-3a4b808040.json");


initializeApp({
   credential: cert(serviceAccount)
});

const db = getFirestore();
const auth = getAuth();

const starttime = Timestamp.fromDate(new Date()); //tijd waar script is opgestart
const users = db.collection('users');
const query = users.where('timestamp', '>=', starttime);


const observer = query.onSnapshot(snap => {
   try {
      console.log("snapshot")
      snap.docChanges().forEach(async change => {
         console.log("add event")
         try {
            if (change.type === 'added') {
               const data = (await users.doc(change.doc.id).get()).data()
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
               } else {
                  console.log(`nieuwe user ${change.doc.id} is niet in de whitelist`);
               }
            }
         } catch (err) {
            console.error("FOUT: bij nieuwe user enabling")
            console.error(err);
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
