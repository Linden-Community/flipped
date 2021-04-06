const express = require('express');
const app = express();
const httpProxy = require('http-proxy');

args = process.argv.splice(2)

const apiProxy = httpProxy.createProxyServer();
apiProxy.on('proxyRes', function(proxyRes, req, res){
    proxyRes.on('data' , function(dataBuffer){
        console.log("onData:", req.path, new Date())
        let data = dataBuffer.toString('utf8');
        console.log("This is the data from target server : "+ data);
    }); 
});

app.all("/*", function(req, res) {
    console.log("request:", req.path, args[0])
    apiProxy.web(req, res, {
        target: args[0]
    });
});

app.listen(6001);