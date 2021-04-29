const assert = require('assert').strict;
const createClient = require('../src/index')

describe('.add', function () {
    this.timeout(60 * 1000)

    let client

    before(() => {
        client = createClient.create({ url: "http://192.168.0.91:6001/poss/v1/test" })
    })

    it('add data', async () => {
        const res = await client.add.data('Hello world!1234565')
        // console.log('Added file contents:', res)

        assert(res.cid == "QmebsgayqHpYSzSGZDZLu8dhkfyu1j1EBKseqit1gyvaqc", "add data error.")
    })

    it('add file', async () => {
        const res = await client.add.file('c:/Users/xieyu/Pictures/94156e5566c82ae7233655505653d674.mp4')
        // console.log('Added file contents:', res.cid)

        assert(res.cid == "QmTtwQFX85t9VdJpCRqB2ZdKxTaNiKpY9QRLrA8f1TpFkL", "add file error.")
    })
})