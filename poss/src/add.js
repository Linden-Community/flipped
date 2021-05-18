'use strict'

const createClient = require('ipfs-http-client')
const globSource = require("./utils/glob-source")
const keyTools = require("./utils/keyTools")
const crypto = require("./utils/crypto")
const Dag = require("./dag")

module.exports = (options) => {
  const dag = Dag(options)
  const client = createClient(options)

  const addFile = async function (path) {
    const file = globSource(path)
    return await client.add(file)
  }

  const addEncryptedData = async function (data, privateKey) {
    const aesKey = await keyTools.createPrivateKey()
    const encryptedData = crypto.encrypt(data, aesKey)
    const resource = await client.add(encryptedData)
    const publicKey = keyTools.privateToPublic(privateKey)
    const cid = await dag.addProof(privateKey, publicKey, resource, aesKey)
    return { cid: cid, resource: resource }
  }

  const addEncryptedFile = async function (path, privateKey) {
    const options = { aesKey: await keyTools.createPrivateKey() }
    const file = globSource(path, options)
    const resource = await client.add(file)
    const publicKey = keyTools.privateToPublic(privateKey)
    const cid = await dag.addProof(privateKey, publicKey, resource, options.aesKey)
    return { cid: cid, resource: resource }
  }

  const grant = async function (oldProof, privateKey, publicKey) {
    const proof = await dag.getProof(oldProof, privateKey)
    const resource = proof.Links[0].Hash
    const aesKey = proof.aesKey
    const newProof = await dag.addProof(privateKey, publicKey, resource, aesKey)
    return { cid: newProof, resource: resource }
  }

  return {
    data: client.add,
    encryptedData: addEncryptedData,
    file: addFile,
    encryptedFile: addEncryptedFile,
    proof: grant
  }
}