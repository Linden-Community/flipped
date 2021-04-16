'use strict'

const CID = require('cids')
const multiaddr = require('multiaddr')
const multibase = require('multibase')
const multicodec = require('multicodec')
const multihash = require('multihashes')
const globSource = require('ipfs-utils/src/files/glob-source')
const urlSource = require('ipfs-utils/src/files/url-source')

function create(options = {}) {
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