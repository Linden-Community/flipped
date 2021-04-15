const createClient = require('../src/index')
const client = createClient.create({ clientID: 'linden' })

async function main() {

    const obj = await client.add.data('Hello world!1234565')
    console.log('Added file contents:', obj)

    const obj1 = await client.add.file('c:/Users/xieyu/Pictures/94156e5566c82ae7233655505653d674.mp4')
    console.log('Added file contents:', obj1.cid)
}

main()