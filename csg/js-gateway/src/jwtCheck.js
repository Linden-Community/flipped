const express = require('express');
const app = express();
app.all("*",function(req,res,next){
    res.header("Access-Control-Allow-Origin","*");
    res.header("Access-Control-Allow-Headers","content-type, access-token");
    res.header("Access-Control-Allow-Methods","DELETE,PUT,POST,GET,OPTIONS");
    if (req.method.toLowerCase() == 'options')
        res.send(200); 
    else
        next();
})
const jwt = require('express-jwt');
const jwks = require('jwks-rsa');
const port = process.env.PORT || 8081;
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
// 检验 AccessToken 合法性
app.use(jwtCheck);

app.use(function (err, req, res, next) {
    if (err.name === 'UnauthorizedError') {
        res.status(401).send({ code: 401, message: err.message });
    }
})

app.post('/poss/v2/*', function (req, res) {
    // 检验 AccessToken 是否具备所需要的权限项目
    if (!checkScope(req, "read")) {
        return res.status(401).json({ code: 401, message: 'Unauthorized! scope->' + req.user.scope });
    }
    res.send('Secured Resource');
});

function checkScope(req, expect="") {
    expect += " *"
    let scopes = req.user.scope.split(' ')
    let clientId = req.url.split("/")[3]
    return expect.split(' ').some((v) => {
        if  (scopes.includes(`store:${clientId}:${v}`)){
            return true;
        }
    })
}

app.listen(port);