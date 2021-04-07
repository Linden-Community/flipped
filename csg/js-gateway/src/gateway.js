const express = require('express');
const app = express();
const httpProxy = require('http-proxy');
const apiProxy = httpProxy.createProxyServer();

const args = process.argv.splice(2)
const MongoClient = require('mongodb').MongoClient;
const passwd = args[1] || "123456"
const mongoUrl = "mongodb://linden:" + passwd + "@localhost:27017/flipped";
const client = new MongoClient(mongoUrl, { useNewUrlParser: true, useUnifiedTopology: true });

let cids;
client.connect(function(err, db) {
    if (err) throw err;
    cids = db.db("flipped").collection("cids")
});

apiProxy.on('proxyRes', function (proxyRes, req, res) {
    proxyRes.on('data', function (dataBuffer) {
        let str = dataBuffer.toString('utf8')
        let json = JSON.parse(str)
        json.api = req.path
        json.createAt = new Date()
        cids.insertOne(json)
        console.log("res: " + JSON.stringify(json))
    });
});

app.all("/*", function (req, res) {
    console.log("req:", req.path, new Date())
    apiProxy.web(req, res, {
        target: args[0] || "http://localhost:5001"
    });
});

app.listen(6001);