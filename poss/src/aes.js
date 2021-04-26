'use strict'

const ecc = require('eosjs-ecc');

const CryptoHelper = {
    createPrivateKey: async function () {
        // return await ecc.randomKey();
        return await ecc.unsafeRandomKey();
    },
    privateToPublic: function (privateKey) {
        return ecc.privateToPublic(privateKey);
    },
    encrypt: function (myPrivate, someonesPublicKey, message) {
        return ecc.Aes.encrypt(myPrivate, someonesPublicKey, message)
    },
    decrypt: function (someonesPrivateKey, myPublic, encryptedMessage) {
        return ecc.Aes.decrypt(someonesPrivateKey, myPublic, encryptedMessage.nonce, encryptedMessage.message, encryptedMessage.checksum)
    }
}

module.exports = CryptoHelper