'use strict'

const CID = require('cids')
const multiaddr = require('multiaddr')
const multibase = require('multibase')
const multicodec = require('multicodec')
const multihash = require('multihashes')
const globSource = require('ipfs-utils/src/files/glob-source')
const urlSource = require('ipfs-utils/src/files/url-source')

function create(options = {}) {
    options.url = options.url || (options.clientID ? 'http://poss.cipfs.cn/v1/' + options.clientID + '/api/v0' : null)
    const client = {
        add: require('./add')(options),
        get: require('./get')(options),
        // aes: require('./ase')(options),
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