const assert = require('assert').strict;
const createClient = require('../src/index')

describe('.keytools', function () {

    let client

    before(() => {
        client = createClient.create({clientID: "testnet"})
    })

    let encryptData
    it('create private key', async () => {
        encryptData = await client.aes.encrypt("lindensys.com", "passwd")
        console.log('encryptData:', encryptData)
    })

    let decryptData
    it('create private key', async () => {
        decryptData = await client.aes.decrypt(encryptData, "passwd")
        assert(decryptData == "lindensys.com", "encrypt decrypt error.")
        console.log('decryptData:', decryptData.toString())
    })
})