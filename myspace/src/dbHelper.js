const createClient = require('ipfs-http-client')
const OrbitDB = require('orbit-db')

const clinet = createClient("http://csg.cipfs.cn/poss/v1/testdb");

module.exports = async () => {
    const orbitdb = await OrbitDB.createInstance(clinet);

    const options = {
        accessController: {
            write: [
                // orbitdb.identity.id
                "*"
            ]
        }
    }

    const db = await orbitdb.kvstore("dirInfo", options)
    await db.load()

    console.log(db.identity);
    console.log(db.id);
    return db
}