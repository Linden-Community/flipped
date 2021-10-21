const assert = require('assert').strict;
const createClient = require('../src/index')

describe('.add', function () {
    this.timeout(60 * 1000)

    let client

    before(() => {
        client = createClient.create({ clientID: "testnet" })
    })

    it('add data', async () => {
        const res = await client.add.data('Hello world!1234565', { "user": "p1" })
        console.log('Added file contents:', res)

        assert(res.cid == "QmebsgayqHpYSzSGZDZLu8dhkfyu1j1EBKseqit1gyvaqc", "add data error.")
    })

    it('add encryptedData', async () => {
        const res = await client.add.encryptedData('Hello world!54321', "5JdV75pqePpw9saQgqRBwLCmDvEauDVwT98WM7Q3o33UyqhckXg", { "user": "p2" })
        console.log('Added encrypted file contents:', res)
    })

    it('add encryptedFile', async () => {
        const proof = await client.add.encryptedFile('/linden/temp/photo1.jpg', "5KQayTDGKgWPZjEehoQxQDvqVuNgiVXYkzsgAcg72P36Qr1AMzG", { "user": "p2", "memo": "aaa,photo,2" })
        console.log('Added encryptedFile proof:', proof.cid)

        assert(proof.resource.size == 3705067, "add file error.")
    })

    it('add file', async () => {
        const res = await client.add.file('/linden/temp/photo1.jpg', { "user": "p4" })

        console.log('Added file contents:', res)

        assert(res.cid == "QmPTMZ8pPnTNdaU3koBGxTPCr2d9YJPx3SrWgGw7T4A75G", "add file error.")
    })

    // it('add addr', async () => {
    //     const res = await client.add.addr('/linden/linden-newpc/dist')
    //     assert(res, "add addr error.")
    //     console.log('Added addr contents:', res)

    //     // assert(res.cid == "QmeHztEDdupsiARZxboLSj9w1PjtRYFHA6ByEPKgzU9KK3", "add file error.")
    // })

    it('add proof', async () => {
        const proof = await client.add.proof("bafyreiaktbuswsqmpzdp26poq44izyvxnu4tor7lg6aj6xvx3vgmnqwuzy",
            "5KQayTDGKgWPZjEehoQxQDvqVuNgiVXYkzsgAcg72P36Qr1AMzG", "EOS5sbwx2AcMEfwcYvkSn3ePk2gF97FMP4uL6ppygLhkidESHwzao", { "user": "p3" , "memo":  "testType_2" })
        console.log('Added encryptedFile proof:', proof)
    })
})