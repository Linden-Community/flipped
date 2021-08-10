const assert = require('assert').strict;
const createClient = require('../src/index')

describe('.dag', function () {
    let client

    before(() => {
        client = createClient.create({ clientID: "testnet" })
    })

    it('test dag-cbor', async () => {
        const cbor = { foo: 'dag-cbor-bar' }
        let cid = await client.dag.put(cbor, { format: 'dag-cbor', hashAlg: 'sha2-256' })

        assert(cid.codec == "dag-cbor", "cid.codec error.")

        cid = cid.toBaseEncodedString('base32')
        assert(cid == "bafyreic6f672hnponukaacmk2mmt7vs324zkagvu4hcww6yba6kby25zce", "cid error.")
        console.log(cid)

        const result = await client.dag.get(cid)
        console.log(result.value)
        console.log(result)
    })

    let cid = "bafyreib62h2bcjdy3wllp32j62mpquz6ny6j4gancpi52zljguwsngncjm"

    it("add proof", async () => {
        const data = { "cid": "QmTgKghvimxUPwVPiTwgiATDQhqUxcWrm1bM6M2cdK7ycM", "path": "aaa.txt", "size": 15 }
        const privateKey = "5KQayTDGKgWPZjEehoQxQDvqVuNgiVXYkzsgAcg72P36Qr1AMzG"
        const publicKey = "EOS5Rm1VBzzHMM7qD3xCBUFh9qGfpUi9eJcgzaLoiKHGdHBD8erqa"
        cid = await client.dag.addProof(privateKey, publicKey, data, "sldkjfei", { "user": "p3" })
        console.log(cid.codec)
        console.log(cid.toV1().toBaseEncodedString('base32'))
    })

    it('get proof', async () => {
        const privateKey = "5KQayTDGKgWPZjEehoQxQDvqVuNgiVXYkzsgAcg72P36Qr1AMzG"
        let data = await client.dag.getProof(cid, privateKey)
        console.log(JSON.stringify(data))
    })
})