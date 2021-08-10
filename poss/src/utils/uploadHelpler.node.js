'use strict'

const createClient = require('ipfs-http-client')
const globSource = require("./glob-source")

module.exports = (options) => {
    const client = createClient(options)

    const uploadFile = async function (path, options = {}) {
        const file = globSource(path)
        return await client.add(file, options)
        // return await client.add(file, {progress:(a,b)=>{console.log(a,b)}})
    }

    const uploadDir = async function (path) {
        const addr = globSource(path, { recursive: true })
        return await client.add(addr)
    }

    const uploadEncryptedFile = async function (path, aesKey, options = {}) {

        const file = globSource(path, { aesKey: aesKey })
        const resource = await client.add(file, options)
        // const resource = await client.add(file, {progress:(a,b)=>{console.log(a,b)}})
        return resource
    }

    return {
        uploadFile: uploadFile,
        uploadDir: uploadDir,
        uploadEncryptedFile: uploadEncryptedFile
    }
}