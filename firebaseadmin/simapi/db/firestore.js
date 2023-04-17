require('dotenv').config({ path: __dirname + '/../.env' })

const { initializeApp, applicationDefault, cert } = require('firebase-admin/app');
const { getFirestore, Timestamp, FieldValue } = require('firebase-admin/firestore');
console.log(__dirname)

var serviceAccount = require("../" + process.env.SERVICE_ACCOUNT);


const app = initializeApp({
    credential: cert(serviceAccount)
});

const firestore = getFirestore(app)

module.exports = [firestore, FieldValue]
