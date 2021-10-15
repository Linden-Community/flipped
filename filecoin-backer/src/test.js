const Filecoin = require('filecoin-api-client')

const fc = Filecoin({
    apiAddr: '/ip4/127.0.0.1/tcp/3453/http' // (optional, default)
});

(async function () {
    const version = await fc.version()
    console.log(version)
})();