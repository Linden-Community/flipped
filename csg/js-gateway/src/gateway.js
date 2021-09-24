const express = require('express');
const app = express();
const httpProxy = require('http-proxy');
const apiProxy = httpProxy.createProxyServer();

const args = process.argv.splice(2)
const MongoClient = require('mongodb').MongoClient;
const mongoUrl = args[1] || "mongodb://linden:123456@192.168.0.91:27017/flipped";
const proxyTarget = args[0] || "http://127.0.0.1:5001"
const client = new MongoClient(mongoUrl, { useNewUrlParser: true, useUnifiedTopology: true });

console.log("args", args[0], args[1], args[2])

const producer = require('../mq/producer');

let cids
client.connect(function (err, db) {
    if (err) throw err;
    cids = db.db().collection("cids")
    console.log("mongodb connect seccess.")
});

producer.init(args[2] || "192.168.0.72:9876")

apiProxy.on('proxyRes', function (proxyRes, req, res) {
    proxyRes.on('data', function (dataBuffer) {
        if (!res.url.match(/\/add|\/dag\/put/)) {
            console.log("res: not '/add' or '/dag/put' function.")
            return
        }

        let str = dataBuffer.toString('utf8')
        try {
            let json = JSON.parse(str)
            if (res.url.match(/\/dag\/put/)) {
                json.Hash = json.Cid['/']
                delete json.Cid
            }
            json.api = res.url.replace(/\?.*/, "")
            json.query = res.req.query
            json.createAt = new Date()
            json.target = proxyTarget
            json.Name = json.Name || res.req.query["proof-name"]
            json.from = req.headers['x-forwarded-for'] || req.socket.remoteAddress
            let jsonStr = JSON.stringify(json)

            if (json.Size)
                json.Size = parseInt(json.Size)

            console.log("res: " + jsonStr)

            cids.insertOne(json)
            producer.send(json.Hash, jsonStr)
        } catch (e) {
            console.log("res: " + e)
        }
    });
});

app.all("/poss/v1/*/ipfs/*", function (req, res) {
    console.log("req1:", req.url, new Date())
    res.url = req.url
    req.url = req.url.replace(/\/poss\/v1\/[^\/]+\/ipfs\//, "/ipfs/")
    apiProxy.web(req, res, {
        target: "http://127.0.0.1:8080"
    });
});

app.all("/poss/v1/*", function (req, res) {
    console.log("req2:", req.url, new Date())
    res.url = req.url
    req.url = req.url.replace(/\/poss\/v1\/[^\/]+\//, "/api/v0/")
    apiProxy.web(req, res, {
        target: proxyTarget
    });
});

app.listen(6001, () => {
    console.log("gateway start on port 6001");
});