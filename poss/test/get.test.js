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

    it('get encryptedFile1', async () => {
        const privateKey = "5KZuwbXgRVmkKfT8f44ysZXmExs2j4W5Fsa7sWhKdU68w9SawSS"
        await client.get.encryptedFile("C:/linden/temp/photo2.jpg", "bafyreifpi7rzrcvxnfvvvptupnwt4f4fqop2te5dc7ealwd4z5qz53bmuq", privateKey)
    })

    it('get encryptedFile2', async () => {
        const privateKey = "5HzCbxiPR88yzNo8VJnz2fhDEphhYEByh8aKd8No8RpYZCdDSfw"
        await client.get.encryptedFile("C:/linden/temp/photo3.jpg", "bafyreiff2whan3wc6enndjqdznp56wwluynsp5olnpacnhzrc2fe4wujry", privateKey)
    })

})