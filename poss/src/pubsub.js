'use strict'

const createClient = require('ipfs-http-client')
const { globSource } = createClient

module.exports = (options) => {
    const client = createClient(options)
    return client.pubsub
}
