const args = process.argv.splice(2)
const Web3 = require('web3');
const web3 = new Web3(Web3.givenProvider || args[0] || "wss://speedy-nodes-nyc.moralis.io/4134993de3de6b5bca90813a/bsc/testnet/ws");
const address = args[1] || "0x0cd43FFF2a992E094E829B4b826fC67aBAe2D6E3"

const producer = require('../mq/producer');
producer.init(args[2] || "127.0.0.1:30876")
const tag = args[3] || "NFT"

var subscription = web3.eth.subscribe('logs', {
    address: address,
    // topics: ['0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef']
}, function (error, result) {
    if (error) {
        console.log("error:", error)
    }
    if (result) {
        producer.sendWithTag(tag, result.transactionHash, JSON.stringify(result))
        console.log("sendMQ:", tag, JSON.stringify(result))
    }
});

console.log("subscription:", subscription);