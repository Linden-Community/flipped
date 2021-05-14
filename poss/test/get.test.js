const assert = require('assert').strict;
const createClient = require('../src/index')

describe('.get', function () {
    this.timeout(60 * 1000)

    let client

    before(() => {
        client = createClient.create()
    })

    it('get data', async () => {
        const rst = await client.get.data("QmebsgayqHpYSzSGZDZLu8dhkfyu1j1EBKseqit1gyvaqc")
        console.log(rst.toString())
    })

    it('get file', async () => {
        await client.get.file("C:/linden/temp/photo1.jpg", "QmPTMZ8pPnTNdaU3koBGxTPCr2d9YJPx3SrWgGw7T4A75G")
    })

    it('get encryptedFile', async () => {
        const privateKey = "5KQayTDGKgWPZjEehoQxQDvqVuNgiVXYkzsgAcg72P36Qr1AMzG"
        const publicKey = "EOS5Rm1VBzzHMM7qD3xCBUFh9qGfpUi9eJcgzaLoiKHGdHBD8erqa"
        await client.get.encryptedFile("C:/linden/temp/photo2.jpg", "bafyreicl4ifhznc4m2bbhcgghowcozc5rm2ckb6pewzzu5yhi6ztqwtlky", privateKey, publicKey)
    })

})