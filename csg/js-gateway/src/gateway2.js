const express = require('express');
const app = express();
app.all("*", function (req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "*");
    res.header("Access-Control-Allow-Methods", "DELETE,PUT,POST,GET,OPTIONS");
    if (req.method.toLowerCase() == 'options')
        res.send(200);
    else
        next();
})
const jwt = require('express-jwt');
const jwks = require('jwks-rsa');
const jwtCheck = jwt({
    secret: jwks.expressJwtSecret({
        cache: true,
        rateLimit: true,
        jwksRequestsPerMinute: 5,
        jwksUri: 'https://linden.authing.cn/oidc/.well-known/jwks.json',
    }),
    // audience: '617a439caf58aafc0f93cb8e',
    issuer: 'https://linden.authing.cn/oidc',
    algorithms: ['RS256'],
    getToken: function fromHeaderOrQuerystring(req) {
        if (req.headers['access-token']) {
            return req.headers['access-token']
        } else if (req.query && req.query['access-token']) {
            return req.query['access-token']
        }
        return null
    }
});
app.use(jwtCheck);
app.use(function (err, req, res, next) {
    if (err.name === 'UnauthorizedError') {
        res.status(401).send({ code: 401, message: err.message });
    }
})

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

function checkScope(req, expect = "") {
    if(req.user.scope.includes("store:myspace:")){
        return true
    }
    expect += " *"
    let scopes = req.user.scope.split(' ')
    let clientId = req.url.split("/")[3]
    return expect.split(' ').some((v) => {
        if (scopes.includes(`store:${clientId}:${v}`)) {
            return true;
        }
    })
}

app.all("/poss/v2/*/ipfs/*", function (req, res) {
    console.log("req1:", req.url, new Date())
    if (!checkScope(req, "read")) {
        return res.status(401).json({ code: 401, message: 'Unauthorized! scope->' + req.user.scope });
    }
    res.url = req.url
    req.url = req.url.replace(/\/poss\/v2\/[^\/]+\/ipfs\//, "/ipfs/")
    apiProxy.web(req, res, {
        target: "http://127.0.0.1:8080"
    }, function (e) {
        res.status(504).send({ code: 504, message: e.message });
    });
});


app.all("/poss/v2/*", function (req, res) {
    console.log("req2:", req.url, new Date())
    if (!checkScope(req, "write")) {
        return res.status(401).json({ code: 401, message: 'Unauthorized! scope->' + req.user.scope });
    }
    res.url = req.url
    req.url = req.url.replace(/\/poss\/v2\/[^\/]+\//, "/api/v0/")
    apiProxy.web(req, res, {
        target: proxyTarget
    }, function (e) {
        res.status(504).send({ code: 504, message: e.message });
    });
});

app.listen(6002, () => {
    console.log("gateway start on port 6002");
});