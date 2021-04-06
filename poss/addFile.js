const createClient = require('ipfs-http-client')
const { globSource } = createClient

async function main() {
  const client = createClient('http://poss.cipfs.cn/v1/add/xoado/api/v0')

  const { cid } = await client.add('Hello world!1234565')
  console.log('Added file contents:', cid)

  const obj = await client.add(globSource('c:/Users/xieyu/Pictures/8A4A1163.jpg'))
  console.log('Added file contents:', obj.cid)

  const obj1 = await client.add(globSource('c:/Users/xieyu/Pictures/94156e5566c82ae7233655505653d674.mp4'))
  console.log('Added file contents:', obj1.cid)
}

main()