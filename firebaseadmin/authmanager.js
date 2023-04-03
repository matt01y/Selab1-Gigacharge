require('dotenv').config({path: __dirname + '/.env'})

const { initializeApp, applicationDefault, cert } = require('firebase-admin/app');
const { getFirestore, Timestamp, FieldValue } = require('firebase-admin/firestore');

var serviceAccount = require("./env/gigacharge-961d6-firebase-adminsdk-8jhn6-6970d77010.json");


initializeApp({
  credential: cert(serviceAccount)
});

const db = getFirestore();

const starttime = Timestamp.fromDate(new Date()); //tijd waar script is opgestart
const query = db.collection('users').where('timestamp', '>=', starttime);


const observer = query.onSnapshot(snap => {
    snap.docChanges().forEach(async change => {
        if (change.type === 'added') {
            const data = change.doc.data();
            if(!data.kaartnummer){ return; }
            const existsquery = db.collection('whitelist').where("kaartnummer", "==", data.kaartnummer).count();
            const count = await (await existsquery.get()).data().count;
            //const count = 1;
            console.log(count)
            if(count >= 1){
                console.log("found");
            }else{
                console.log("not found")
            }
            console.log(change.doc.data());
        }
    })
}, err => {
    console.log(`error: ${err}`)
})


/*
getAuth()
  .getUserByEmail('user@admin.example.com')
  .then((user) => {
    // Add incremental custom claim without overwriting existing claims.
    const currentCustomClaims = user.customClaims;
    if (currentCustomClaims['admin']) {
      // Add level.
      currentCustomClaims['accessLevel'] = 10;
      // Add custom claims for additional privileges.
      return getAuth().setCustomUserClaims(user.uid, currentCustomClaims);
    }
  })
  .catch((error) => {
    console.log(error);
  });



// On sign up.
exports.processSignUp = functions.auth.user().onCreate(async (user) => {
  // Check if user meets role criteria.
  if (
    user.email &&
    user.email.endsWith('@admin.example.com') &&
    user.emailVerified
  ) {
    const customClaims = {
      admin: true,
      accessLevel: 9
    };

    try {
      // Set custom user claims on this newly created user.
      await getAuth().setCustomUserClaims(user.uid, customClaims);

      // Update real-time database to notify client to force refresh.
      const metadataRef = getDatabase().ref('metadata/' + user.uid);

      // Set the refresh time to the current UTC timestamp.
      // This will be captured on the client to force a token refresh.
      await  metadataRef.set({refreshTime: new Date().getTime()});
    } catch (error) {
      console.log(error);
    }
  }
});*/