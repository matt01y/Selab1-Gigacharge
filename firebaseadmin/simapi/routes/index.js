var express = require('express');
var router = express.Router();
var [db, fv] = require('../db/firestore')

function collectionmap(doc){
  let data = doc.data();
  data.docid = doc.id;
  return data;
}

/* GET home page. */
router.get('/:type',async function(req, res, next) {
    const data = (await db.collection(req.params.type).get()).docs.map(collectionmap)
    res.json(data);
});

router.get('/:type/:id',async function(req, res, next) {
  const data = await db.collection(req.params.type).doc(req.params.id).get();
  res.json(data);
});

router.get('/:type/:id/:collection',async function(req, res, next) {
  const data = (await db.collection(req.params.type).doc(req.params.id).collection(req.params.collection).get()).docs.map(collectionmap);
  res.json({collection: data});
});

router.get('/:type/:id/:collection/:docid',async function(req, res, next) {
  const doc = (await db.collection(req.params.type).doc(req.params.id).collection(req.params.collection).doc(req.params.docid).get())
  const data = doc.data();
  data.docid = doc.id;
  res.json(data);
});

router.post('/:type/:id/:collection/:docid',async function(req, res) {
  console.log(req.params);
  console.log(req.body)
  const data = req.body
  for(const [key, val] of Object.entries(data)){
    if(val === "NONE"){
      data[key] = fv.delete();
    }
  }
  await db.collection(req.params.type).doc(req.params.id).collection(req.params.collection).doc(req.params.docid).update(data)
  res.json({ok: true});
});


module.exports = router;
