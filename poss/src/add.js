'use strict'

const createClient = require('ipfs-http-client')
const globSource = require("./utils/glob-source")
const keyTools = require("./utils/keyTools")
const Dag = require("./dag")

module.exports = (options) => {
  const dag = Dag(options)
  const client = createClient(options)

  const addFile = async function (path) {
    const file = globSource(path)
    return await client.add(file)
  }

  const encryptAndUpload = async function (path, aesKey) {
    const options = {}
    options.aesKey = aesKey
    const file = globSource(path, options)
    return await client.add(file)
  }

  const addEncryptedFile = async function (path, privateKey, publicKey) {
    const aesKey = await keyTools.createPrivateKey()
    const resource = await encryptAndUpload(path, aesKey)
    const cid = await dag.addProof(privateKey, publicKey, resource, aesKey)
    return { cid: cid, resource: resource }
  }

  return {
    data: client.add,
    file: addFile,
    encryptedFile: addEncryptedFile
  }
}