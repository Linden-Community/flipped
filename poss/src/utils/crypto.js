const createHash = require('create-hash')
const crypto = require('browserify-aes')

const CryptoHelper = {
    encrypt: (data, aesKey) => {
        const encryption_key = createHash('sha512').update(aesKey).digest()
        const iv = encryption_key.slice(32, 48)
        const key = encryption_key.slice(0, 32)

        const cipher = crypto.createCipheriv('aes-256-cbc', key, iv)
        data = Buffer.concat([cipher.update(data), cipher.final()])
        return data
    },
    decrypt: (data, aesKey) => {
        const encryption_key = createHash('sha512').update(aesKey).digest()
        const iv = encryption_key.slice(32, 48)
        const key = encryption_key.slice(0, 32)

        const decipher = crypto.createDecipheriv('aes-256-cbc', key, iv)
        data = Buffer.concat([decipher.update(data), decipher.final()])
        return data
    }
}

module.exports = CryptoHelper