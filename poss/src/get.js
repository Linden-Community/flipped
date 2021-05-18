'use strict'

const createClient = require('ipfs-http-client')
const fs = require('fs-extra')
const crypto = require("./utils/crypto")
const Dag = require("./dag")

module.exports = (options) => {
    const dag = Dag(options)
    const client = createClient(options)

    const getData = async (cid) => {
        const content = []
        for await (const chunk of client.cat(cid)) {
            content.push(chunk)
        }
        return Buffer.concat(content)
    }

    const getFile = async (path, cid) => {
        for await (const file of client.get(cid)) {
            if (file.type != "file") console.error("this dag node is not a file")
            let ws = fs.createWriteStream(path);
            for await (const chunk of file.content) {
                ws.write(chunk)
            }
            ws.end()
        }
    }

    const getAndDecrypt = async (path, cid, aesKey) => {
        for await (const file of client.get(cid)) {
            if (file.type != "file") console.error("this dag node is not a file")

            const block = 262160
            let buf = Buffer.alloc(0);
            let ws = fs.createWriteStream(path);
            for await (const chunk of file.content) {
                if (buf.length + chunk.length < block) {
                    buf = Buffer.concat([buf, chunk])
                } else {
                    let data = Buffer.concat([buf, chunk], block)
                    data = crypto.decrypt(data, aesKey)
                    ws.write(data)
                    buf = chunk.slice(block - buf.length)
                }
            }
            buf = crypto.decrypt(buf, aesKey)
            ws.write(buf)
            ws.end()
        }
    }

    const getEncryptedData = async (cid, privateKey) => {
        const proof = await dag.getProof(cid, privateKey)
        const resourceCid = proof.Links[0].Hash
        const aesKey = proof.aesKey
        const encryptedData = await getData(resourceCid)
        return crypto.decrypt(encryptedData, aesKey).toString()
    }

    const getEncryptedFile = async (path, cid, privateKey) => {
        const proof = await dag.getProof(cid, privateKey)
        const resourceCid = proof.Links[0].Hash
        const aesKey = proof.aesKey
        await getAndDecrypt(path, resourceCid, aesKey)
    }

    return {
        data: getData,
        encryptedData: getEncryptedData,
        file: getFile,
        encryptedFile: getEncryptedFile
    }
}