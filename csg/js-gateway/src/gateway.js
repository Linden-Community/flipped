const express = require('express');
const app = express();
const httpProxy = require('http-proxy');
const apiProxy = httpProxy.createProxyServer();

const args = process.argv.splice(2)
const MongoClient = require('mongodb').MongoClient;
const mongoUrl = args[1] || "mongodb://linden:123456@192.168.0.72:27017/flipped";
const proxyTarget = args[0] || "http://192.168.0.72:5001"
const client = new MongoClient(mongoUrl, { useNewUrlParser: true, useUnifiedTopology: true });

const producer = require('../mq/producer');

let cids
client.connect(function (err, db) {
    if (err) throw err;
    cids = db.db().collection("cids")
    console.log("mongodb connect seccess.")
});

producer.init(args[2] || "111.19.254.170:9876")

apiProxy.on('proxyRes', function (proxyRes, req, res) {
    proxyRes.on('data', function (dataBuffer) {
        let str = dataBuffer.toString('utf8')
        let json = JSON.parse(str)
        json.api = res.url
        json.createAt = new Date()
        json.target = proxyTarget
        json.from = req.headers['x-forwarded-for'] || req.socket.remoteAddress

        cids.insertOne(json)

        let jsonStr = JSON.stringify(json)
        console.log("res: " + jsonStr)
        producer.send(json.Hash, jsonStr)
    });
});

app.all("/poss/v1/*", function (req, res) {
    console.log("req:", req.url, new Date())
    res.url = req.url
    req.url = '/api/v0/add'
    apiProxy.web(req, res, {
        target: proxyTarget
    });
});

app.listen(6001);