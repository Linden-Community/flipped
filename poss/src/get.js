'use strict'

const createClient = require('ipfs-http-client')
const { globSource } = createClient

module.exports = (options) => {
    options.url = options.url || (options.clientID ? 'http://poss.cipfs.cn/v1/get/' + options.clientID + '/api/v0' : null)

    const client = createClient(options)

    return {
        data: client.cat,
        file: client.get
    }
}