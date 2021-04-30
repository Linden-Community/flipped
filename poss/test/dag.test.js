const assert = require('assert').strict;
const createClient = require('../src/index')

describe('.dag', function () {
    let client

    before(() => {
        client = createClient.create()
    })

    it('test dag-cbor', async () => {
        const cbor = { 'foo': 'dag-cbor-bar' }
        let cid = await client.dag.put(cbor, { format: 'dag-cbor', hashAlg: 'sha2-256' })

        //expect(cid.codec).to.equal('dag-cbor')
        console.log(cid.codec)

        cid = cid.toBaseEncodedString('base32')
        // expect(cid).to.equal('bafyreic6f672hnponukaacmk2mmt7vs324zkagvu4hcww6yba6kby25zce')
        console.log(cid)

        const result = await client.dag.get(cid)

        // expect(result.value).to.deep.equal(cbor)
        console.log(result.value)
    })

    let cid = "bafyreihhvhjaojikaf77uo2rwgur7goxcbcdwgvzo3aoai2pwbzv6sk46u"
    it('add data', async () => {
        const msg = "abcd1234"
        const privateKey = "5KQayTDGKgWPZjEehoQxQDvqVuNgiVXYkzsgAcg72P36Qr1AMzG"
        const publicKey = "EOS5Rm1VBzzHMM7qD3xCBUFh9qGfpUi9eJcgzaLoiKHGdHBD8erqa"
        cid = await client.dag.addData(privateKey, publicKey, msg)
        console.log(cid.codec)
        console.log(cid.toV1().toBaseEncodedString('base32'))
    })

    it('get data', async () => {
        const privateKey = "5KQayTDGKgWPZjEehoQxQDvqVuNgiVXYkzsgAcg72P36Qr1AMzG"
        const publicKey = "EOS5Rm1VBzzHMM7qD3xCBUFh9qGfpUi9eJcgzaLoiKHGdHBD8erqa"
        let data = await client.dag.getData(privateKey, publicKey, cid)
        console.log(data.toString())
    })

    it("add file", async () => {
        const data = { "cid": "QmTgKghvimxUPwVPiTwgiATDQhqUxcWrm1bM6M2cdK7ycM", "Name": "aaa.txt", "size": 15 }
        const privateKey = "5KQayTDGKgWPZjEehoQxQDvqVuNgiVXYkzsgAcg72P36Qr1AMzG"
        const publicKey = "EOS5Rm1VBzzHMM7qD3xCBUFh9qGfpUi9eJcgzaLoiKHGdHBD8erqa"
        let cid = await client.dag.addFile(privateKey, publicKey, data, "sldkjfei")
        console.log(cid.codec)
        console.log(cid.toV1().toBaseEncodedString('base32'))
    })
})