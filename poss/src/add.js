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
    // return await client.add(file, {progress:(a,b)=>{console.log(a,b)}})
  }

  const addAddr = async function (path) {
    const addr = globSource(path, { recursive: true })
    return await client.add(addr)
    // return await client.add(addr, {progress:(a,b)=>{console.log(a,b)}})
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
    // const resource = await client.add(file, {progress:(a,b)=>{console.log(a,b)}})
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
    addr: addAddr,
    encryptedFile: addEncryptedFile,
    proof: grant
  }
}