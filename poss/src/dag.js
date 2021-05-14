'use strict'

const createClient = require('ipfs-http-client')
const ipldDagPb = require('ipld-dag-pb')
const { DAGNode } = ipldDagPb

const aes = require("./utils/keyTools")

module.exports = (options) => {

  const client = createClient(options)

  const addProof = async function (privateKey, publicKey, resource, aesKey) {
    let proof = new DAGNode('v1')
    proof.grantor = aes.privateToPublic(privateKey)
    proof.grantee = publicKey
    proof.encryptInfo = aes.encrypt(privateKey, publicKey, aesKey)
    proof.addLink(resource)
    const rst = await client.dag.put(proof, { format: 'dag-cbor', hashAlg: 'sha2-256', pin: true })
    return rst
  }

  const getProof = async function (privateKey, publicKey, cid) {
    const data = await client.dag.get(cid)
    const aesKey = aes.decrypt(privateKey, publicKey, data.value.encryptInfo)
    delete data.value.encryptInfo
    data.value.aesKey = aesKey
    return data.value
  }

  const dag = {
    put: client.dag.put,
    get: client.dag.get,
    addProof: addProof,
    getProof: getProof
  }

  return dag
}