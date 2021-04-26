const assert = require('assert').strict;
const createClient = require('../src/index')

describe('.aes', function () {

    let client

    before(() => {
        client = createClient.create()
    })

    let privateKey
    it('create private key', async () => {
        privateKey = await client.aes.createPrivateKey()
        console.log('privateKey:', privateKey)
    })

    let publicKey
    it('private to public', () => {
        publicKey = client.aes.privateToPublic(privateKey)
        console.log('publicKey:', publicKey)
    })

    let encryptData
    it('encrypt data', () => {
        encryptData = client.aes.encrypt(privateKey, publicKey, "abc 111")
        console.log('encrypt:', encryptData)
    })

    let decryptData
    it('decrypt data', () => {
        decryptData = client.aes.decrypt(privateKey, publicKey, encryptData)
        console.log('decryptData:', decryptData.toString())
    })
})