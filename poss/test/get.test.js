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
        for await (const file of client.get.file("QmebsgayqHpYSzSGZDZLu8dhkfyu1j1EBKseqit1gyvaqc")) {
            console.log(file.type, file.path)
            if (!file.content) continue;
            const content = []
            for await (const chunk of file.content) {
                content.push(chunk)
            }
            console.log(content.toString())
            assert(file.path == "QmebsgayqHpYSzSGZDZLu8dhkfyu1j1EBKseqit1gyvaqc", "get data error.")
        }
    })


})