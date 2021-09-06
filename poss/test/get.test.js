const assert = require('assert').strict;
const createClient = require('../src/index')

describe('.get', function () {
    this.timeout(60 * 1000)

    let client

    before(() => {
        client = createClient.create({clientID: "testnet"})
    })

    it('get data', async () => {
        const rst = await client.get.data("QmebsgayqHpYSzSGZDZLu8dhkfyu1j1EBKseqit1gyvaqc")
        console.log(rst.toString())
    })

    it('get file', async () => {
        await client.get.file("/linden/temp/photo1.jpg", "QmPTMZ8pPnTNdaU3koBGxTPCr2d9YJPx3SrWgGw7T4A75G")
    })

    it('get encryptedData', async () => {
        const privateKey = "5JdV75pqePpw9saQgqRBwLCmDvEauDVwT98WM7Q3o33UyqhckXg"
        const data = await client.get.encryptedData("bafyreidzkpn52aun4fslqdxpzlq4ajaq5qjahbhddubgyvzhhsobmtjxsq", privateKey)
        console.log(data)
    })

    it('get encryptedFile1', async () => {
        const privateKey = "5KQayTDGKgWPZjEehoQxQDvqVuNgiVXYkzsgAcg72P36Qr1AMzG"
        await client.get.encryptedFile("/linden/temp/photo2.jpg", "bafyreihhwoc7nqqj7xlyiy42b2rgmesvvnnd46gymeluozo3zndlj27dxu", privateKey)
    })

    it('get encryptedFile2', async () => {
        const privateKey = "5HzCbxiPR88yzNo8VJnz2fhDEphhYEByh8aKd8No8RpYZCdDSfw"
        await client.get.encryptedFile("/linden/temp/photo3.jpg", "bafyreih55flvn3ki5qjkvramxgvqovolg57xe4ofdymjjntg7m7q543yfa", privateKey)
    })

    it('get encryptedBuffer', async () => {
        const privateKey = "5JdV75pqePpw9saQgqRBwLCmDvEauDVwT98WM7Q3o33UyqhckXg"
        const buf = await client.get.encryptedBuffer("bafyreibufx6rtr5vwpq7flssskp5kodeymfxgbsv5utptnkkttr6meqsxa", privateKey)
        console.log(buf.length)
        assert(buf.length == 3703889, "add file error.")
    })

})