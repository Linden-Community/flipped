const assert = require('assert').strict;
const createClient = require('../src/index')

describe('.add', function () {
    this.timeout(60 * 1000)

    let client

    before(() => {
        client = createClient.create()
    })

    it('add data', async () => {
        const res = await client.add.data('Hello world!1234565')
        console.log('Added file contents:', res)

        assert(res.cid == "QmebsgayqHpYSzSGZDZLu8dhkfyu1j1EBKseqit1gyvaqc", "add data error.")
    })

    
    it('add encryptedFile', async () => {
        const res = await client.add.encryptedFile('C:/linden/temp/photo1.jpg', "123456")
        console.log('Added encryptedFile contents:', res.cid)

        assert(res.cid == "QmditSjDQnysiXbLf4DJGd625H7KPfeYrRgLztyG3TVLLc", "add file error.")
    })

    it('add file', async () => {
        const res = await client.add.file('C:/linden/temp/photo1.jpg')
        console.log('Added file contents:', res.cid)

        assert(res.cid == "QmPTMZ8pPnTNdaU3koBGxTPCr2d9YJPx3SrWgGw7T4A75G", "add file error.")
    })
})