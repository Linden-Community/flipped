const { PushConsumer } = require("apache-rocketmq");

let consumer
const receiver = {
    init: (nameServer, pinit) => {
        consumer = new PushConsumer("pinner", {
            nameServer: nameServer,
            threadCount: 3
        });

        consumer.on("message", pinit);

        consumer.subscribe("ipfs");

        consumer.start().then(() => {
            console.log("consumer is ready!")
        }).catch(err => {
            console.log("consumer starting err:", err)
        });
    },
}

module.exports = receiver
