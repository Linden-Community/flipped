'use strict'

const createClient = require('ipfs-http-client')
const ipldDagPb = require('ipld-dag-pb')
const { DAGNode } = ipldDagPb

const aes = require("./aes")

module.exports = (options) => {

  const client = createClient(options)

  const addData = async function (privateKey, publicKey, data) {
    const encryptData = aes.encrypt(privateKey, publicKey, data)
    const rst = await client.dag.put(encryptData, { format: 'dag-cbor', hashAlg: 'sha2-256', pin: true })
    return rst
  }

  const getData = async function (privateKey, publicKey, cid) {
    let encryptData = await client.dag.get(cid)
    encryptData = encryptData.value
    const data = aes.decrypt(privateKey, publicKey, encryptData)
    return data
  }

  const addFile = async function (privateKey, publicKey, data, encryptKey) {
    let encryptInfo = aes.encrypt(privateKey, publicKey, encryptKey)
    let saveDate = new DAGNode('v0')
    saveDate.addLink(data)
    saveDate.encryptInfo = encryptInfo
    const rst = await client.dag.put(saveDate, { format: 'dag-cbor', hashAlg: 'sha2-256', pin: true })
    return rst
  }

  const dag = {
    put: client.dag.put,
    get: client.dag.get,
    addData: addData,
    getData: getData,
    addFile: addFile
  }

  return dag
}