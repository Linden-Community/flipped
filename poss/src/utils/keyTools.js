'use strict'

const ecc = require('eosjs-ecc');
var ByteBuffer = require('bytebuffer');
var Long = ByteBuffer.Long;

const CryptoHelper = {
    createPrivateKey: async function () {
        // return await ecc.randomKey();
        return await ecc.unsafeRandomKey();
    },
    getPrivateKey: function (id, salt = "linden", keyName = "") {
        const seed = id + salt + keyName
        return ecc.seedPrivate(seed);
    },
    privateToPublic: function (privateKey) {
        return ecc.privateToPublic(privateKey);
    },
    encrypt: function (myPrivate, someonesPublicKey, message) {
        return ecc.Aes.encrypt(myPrivate, someonesPublicKey, message)
    },
    decrypt: function (someonesPrivateKey, myPublic, encryptedMessage) {
        let nonce = encryptedMessage.nonce
        nonce = new Long(nonce.low, nonce.high, nonce.unsigned)
        return ecc.Aes.decrypt(someonesPrivateKey, myPublic, nonce, encryptedMessage.message, encryptedMessage.checksum)
    },
    sign: function (str, sk) {
        return ecc.sign(str, sk)
    },
    verify: function (sign, str, pk) {
        return ecc.verify(sign, str, pk)
    }
}

module.exports = CryptoHelper