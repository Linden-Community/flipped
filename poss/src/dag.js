'use strict'

const createClient = require('ipfs-http-client')
const { globSource } = createClient

const aes = require("./aes")

module.exports = (options) => {

  const client = createClient(options)

  const addData = async function (privateKey, publicKey, data) {
    const encryptData = aes.encrypt(privateKey, publicKey, data)
    const rst = await client.dag.put(encryptData, { format: 'dag-cbor', hashAlg: 'sha2-256' })
    return rst
  }

  const getData = async function (privateKey, publicKey, cid) {
    let encryptData = await client.dag.get(cid)
    encryptData = encryptData.value
    const data = aes.decrypt(privateKey, publicKey, encryptData)
    return data
  }

  const dag = {
    put: client.dag.put,
    get: client.dag.get,
    addData: addData,
    getData: getData
  }

  return dag
}