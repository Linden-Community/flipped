'use strict'

const CID = require('cids')
const multiaddr = require('multiaddr')
const multibase = require('multibase')
const multicodec = require('multicodec')
const multihash = require('multihashes')
const globSource = require('./utils/glob-source')
const urlSource = require('ipfs-utils/src/files/url-source')

function create(options = {}) {
    options.possVersion = options.possVersion || 'v1'
    options.url = options.url || (options.clientID ? 'https://csg.lindensys.cn/poss/' + options.possVersion + '/' + options.clientID : null)
    const client = {
        add: require('./add')(options),
        get: require('./get')(options),
        dag: require('./dag')(options),
        pubsub: require('./pubsub')(options),
        keytools: require('./utils/keyTools'),
        aes: require('./utils/crypto'),
    }

    return client
}

module.exports = {
    create,
    CID,
    multiaddr,
    multibase,
    multicodec,
    multihash,
    globSource,
    urlSource
}