const assert = require('assert').strict;
const createClient = require('../src/index')

describe('.dag', function () {
    let client

    before(() => {
        client = createClient.create({ clientID: "xoado" })
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
})