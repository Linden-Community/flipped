const assert = require('assert').strict;
const createClient = require('../src/index')

describe('.dag', function () {
    let client

    before(() => {
        client = createClient.create()
    })

    it('should be able to put and get a DAG node with format dag-cbor', async () => {
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

    it("add data", async () => {
        const msg = "abcd1234"
        const privateKey = "5KQayTDGKgWPZjEehoQxQDvqVuNgiVXYkzsgAcg72P36Qr1AMzG"
        const publicKey = "EOS5Rm1VBzzHMM7qD3xCBUFh9qGfpUi9eJcgzaLoiKHGdHBD8erqa"
        let cid = await client.dag.addData(privateKey, publicKey, msg)
        console.log(cid.codec)
        console.log(cid.toBaseEncodedString('base32'))
    })

    it("get data", async () => {
        const cid = "bafyreiclat55u2hriugc73fbujpy6a33i2piwoeuv6sxk2tfe65vzgqujm"
        const privateKey = "5KQayTDGKgWPZjEehoQxQDvqVuNgiVXYkzsgAcg72P36Qr1AMzG"
        const publicKey = "EOS5Rm1VBzzHMM7qD3xCBUFh9qGfpUi9eJcgzaLoiKHGdHBD8erqa"
        let data = await client.dag.getData(privateKey, publicKey, cid)
        console.log(data.toString())
    })
})