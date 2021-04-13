const { Producer } = require("apache-rocketmq");

let producer;
const sender = {
    init: (nameServer) => {
        producer = new Producer("csg-cluster", {
            nameServer: nameServer,
        });

        producer.start().then(() => {
            console.log("producer is ready!")
        }).catch(err => {
            console.log("producer starting err:", err)
        });
    },
    send: (cid, msg) => {
        producer.send("ipfs", msg, {
            keys: cid,
            tags: "add"
        }, function (err, result) {
            if (err) {
                console.log(err)
            } else {
                console.log("mq return:", result);
            }
        });
    }
}

module.exports = sender