const assert = require('assert').strict;
const createClient = require('../src/index')

describe('.keytools', function () {

    let client

    before(() => {
        client = createClient.create({clientID: "testnet"})
    })

    let privateKey
    it('create private key', async () => {
        privateKey = await client.keytools.createPrivateKey()
        console.log('privateKey:', privateKey)
    })

    it('get private key', () => {
        privateKey = client.keytools.getPrivateKey("0x5B38Da6a701c568545dCfcB03FcB875f56beddC4", "webdisk", "k1")
        assert(privateKey == "5KCkNmy5QeKXhTJ2jpZCQQYNu14mPeV5phAPLvq6s65SLCw9xMU", "get private key error.")
        console.log('privateKey:', privateKey)
    })

    let publicKey
    it('private to public', () => {
        publicKey = client.keytools.privateToPublic(privateKey)
        assert(publicKey == "EOS6EgigBYpgHzn8AgJ7e4EBs4YGAwEK82oBTdkByeKrY86e7A4HX", "private to public error.")
        console.log('publicKey:', publicKey)
    })

    let encryptData
    it('encrypt data', () => {
        encryptData = client.keytools.encrypt(privateKey, publicKey, "abc 111")
        console.log('encrypt:', encryptData)
    })

    let decryptData
    it('decrypt data', () => {
        decryptData = client.keytools.decrypt(privateKey, publicKey, encryptData)
        assert(decryptData == "abc 111", "encrypt decrypt error.")
        console.log('decryptData:', decryptData.toString())
    })

    let sign
    it('sign', () => {
        sign = client.keytools.sign("lindensys.com", privateKey)
        assert(sign == "SIG_K1_JzoFaP3tESyzKbAb9DSWjiGEqBLBGr8opbiYfeYm693rGztjsmCbD1AQMgMju8GMxeL797chjXf1gVcHTuU4ivS2eYe7HT", "sign error.")
        console.log('sign:', sign)
    })

    it('verify', () => {
        let flag = client.keytools.verify(sign, "lindensys.com", publicKey)
        assert(flag == true, "verify error.")
        console.log('verify:', flag)
    })
})