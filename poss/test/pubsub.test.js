const assert = require('assert').strict;
const createClient = require('../src/index')

describe('.pubsub', function () {
    let client

    before(() => {
        client = createClient.create({clientID: "testnet"})
    })

    it('test ls', async () => {
        const ls = await client.pubsub.ls()
        console.log("ls:", ls)
    })

    const topic = 'aabbcc'
    it('test peers', async () => {
        const peerIds = await client.pubsub.peers(topic)
        console.log("peerIds:", peerIds)
    })

    it('test pub', async () => {
        await client.pubsub.publish(topic, " apple")
    })
})