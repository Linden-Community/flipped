const assert = require('assert').strict;
const createClient = require('../src/index')

describe('.get', function () {
    this.timeout(60 * 1000)

    let client

    before(() => {
        client = createClient.create()
    })

    it('get data', async () => {
        for await (const chunk of client.get.data("QmebsgayqHpYSzSGZDZLu8dhkfyu1j1EBKseqit1gyvaqc")) {
            console.info(chunk.toString())
            assert(chunk.toString() == "Hello world!1234565", "get data error.")
        }
    })

    it('get file', async () => {
        await client.get.file("C:/linden/temp/photo1.jpg", "QmPTMZ8pPnTNdaU3koBGxTPCr2d9YJPx3SrWgGw7T4A75G")
    })

    it('get encryptedFile', async () => {
        await client.get.encryptedFile("C:/linden/temp/photo2.jpg", "QmditSjDQnysiXbLf4DJGd625H7KPfeYrRgLztyG3TVLLc", "123456")
    })


})