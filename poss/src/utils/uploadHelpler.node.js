'use strict'

const createClient = require('ipfs-http-client')
const globSource = require("./glob-source")

module.exports = (options) => {
    const client = createClient(options)

    const uploadFile = async function (path) {
        const file = globSource(path)
        return await client.add(file)
        // return await client.add(file, {progress:(a,b)=>{console.log(a,b)}})
    }

    const uploadDir = async function (path) {
        const addr = globSource(path, { recursive: true })
        return await client.add(addr)
    }

    const uploadEncryptedFile = async function (path, aesKey) {
        const options = { aesKey: aesKey }
        const file = globSource(path, options)
        const resource = await client.add(file)
        // const resource = await client.add(file, {progress:(a,b)=>{console.log(a,b)}})
        return resource
    }

    return {
        uploadFile: uploadFile,
        uploadDir: uploadDir,
        uploadEncryptedFile: uploadEncryptedFile
    }
}