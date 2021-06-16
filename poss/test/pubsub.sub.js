const assert = require('assert').strict;
const createClient = require('../src/index')

const client = createClient.create({clientID: "testnet"});
const uint8Array = require('uint8arrays')

const topic = '/orbitdb/zdpuAkpFFaTtzSRqisaaMSuVNyJ2XQPb31iZSjEjd934tywHN/kv4'
const receiveMsg = (msg) => {
    console.log(uint8Array.toString(msg.data))
}

(async () => {
    await client.pubsub.subscribe(topic, receiveMsg)
    console.log(`subscribed to ${topic}`)
})()
