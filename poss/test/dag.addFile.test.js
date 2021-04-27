const assert = require('assert').strict;
const createClient = require('../src/index')

describe('.dag', function () {
    let client

    before(() => {
        client = createClient.create()
    })

    it("add file", async () => {
        const data = {"cid":"QmTgKghvimxUPwVPiTwgiATDQhqUxcWrm1bM6M2cdK7ycM","Name":"aaa.txt","size":15}
        const privateKey = "5KQayTDGKgWPZjEehoQxQDvqVuNgiVXYkzsgAcg72P36Qr1AMzG"
        const publicKey = "EOS5Rm1VBzzHMM7qD3xCBUFh9qGfpUi9eJcgzaLoiKHGdHBD8erqa"
        let cid = await client.dag.addFile(privateKey, publicKey, data, "sldkjfei")
        console.log(cid.codec)
        console.log(cid.toV1().toBaseEncodedString('base32'))
    })
})