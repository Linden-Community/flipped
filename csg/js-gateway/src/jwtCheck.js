var express = require('express');
var app = express();
var jwt = require('express-jwt');
var jwks = require('jwks-rsa');
var port = process.env.PORT || 8080;
var jwtCheck = jwt({
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
    console.log(req.url.split("/")[3])
    // 检验 AccessToken 是否具备所需要的权限项目
    if (!req.user.scope.split(' ').includes('store:' + req.url.split("/")[3]+":*")) {
        return res.status(401).json({ code: 401, message: 'Unauthorized! scope->' +  req.user.scope});
    }
    res.send('Secured Resource');
});

app.listen(port);