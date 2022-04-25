const args = process.argv.splice(2)
const Web3 = require('web3');
const RINKEBY_WSS = Web3.givenProvider || args[0] || "wss://speedy-nodes-nyc.moralis.io/4134993de3de6b5bca90813a/bsc/testnet/ws";
const contract = args[1] || "0x0cd43FFF2a992E094E829B4b826fC67aBAe2D6E3"
const producer = require('../mq/producer');
producer.init(args[2] || "127.0.0.1:30876")
const chainId = args[3] || "97"

let options = {
    clientConfig: {
        keepalive: true,
        keepaliveInterval: 60000,
    },
    reconnect: {
        auto: true,
        delay: 10000, // ms
        maxAttempts: 60,
        onTimeout: false
    }
}
var provider = new Web3.providers.WebsocketProvider(RINKEBY_WSS, options);
var web3 = new Web3(provider);
subscribe();

function subscribe() {
    let subscription = web3.eth.subscribe('logs', {
        address: contract,
        // topics: ['0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef']
    }, function (error, result) {
        if (error) {
            console.log("error:", error)
        }
        if (result) {
            result.chainId = chainId
            result.createAt = Date.now()
            producer.sendWithTag(chainId, result.transactionHash, JSON.stringify(result))
            console.log("sendMQ:", chainId, JSON.stringify(result))
        }
    });
    console.log("*** subscription:", subscription);
}

// provider.on('error', e => console.log('WS Error', e));
// provider.on('end', e => {
//     console.log('WS closed');
//     console.log('Attempting to reconnect...');
//     provider = new Web3.providers.WebsocketProvider(RINKEBY_WSS);

//     provider.on('connect', function () {
//         console.log('WSS Reconnected');
//     });
    
//     web3.setProvider(provider);
// });