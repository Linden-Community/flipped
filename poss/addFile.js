const createClient = require('ipfs-http-client')
const { globSource } = createClient

async function main() {
  const client = createClient('http://poss.cipfs.cn/v1/add/xoado/api/v0')

  const { cid } = await client.add('Hello world!1234565')
  console.log('Added file contents:', cid)

  const obj = await client.add(globSource('c:/Users/xieyu/Pictures/8A4A1163.jpg'))
  console.log('Added file contents:', obj.cid)
}

main()