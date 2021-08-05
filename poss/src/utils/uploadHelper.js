'use strict'
const { isBrowser } = require('ipfs-utils/src/env')

if (isBrowser) {
    module.exports = require('./uploadHelpler.browser')
} else {
    module.exports = require('./uploadHelpler.node')
}
