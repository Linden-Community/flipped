const createClient = require('ipfs-http-client')
const OrbitDB = require('orbit-db')

const args = process.argv.splice(2)
const clinet = createClient(args[0] || "http://localhost:5001");

(async () => {
    const orbitdb = await OrbitDB.createInstance(clinet);
    const db = await orbitdb.open(args[1] || "/orbitdb/zdpuB3MTJ2HNesA3bbW2JwjhQS5DFJ8EAYmHZVd7WoPHGyg9i/dirInfo")
    await db.load()
    db.events.on("replicated", () => {
        console.log("replicated at: ", new Date().toLocaleString());
    });
    db.events.on('replicate.progress', (address, hash, entry, progress, have) => {
        console.log(entry.payload, progress, have);
    })
    console.log(db.identity);
})();