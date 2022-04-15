const assert = require('assert').strict;
const createClient = require('../src/index')

describe('test v2', function () {
    this.timeout(60 * 1000)

    let client

    before(() => {
        client = createClient.create({ clientID: "testnet", possVersion: "v2" })
    })

    let token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ilh3OTBZSTJCZGN4b3F4WFBwWHVvdndaUDNEc0Q0eEczczZ6bzFkcU0zTXMifQ.eyJqdGkiOiJFcFpjeHc1OHAyRzgtYmZuc1I1V0QiLCJpYXQiOjE2NDQ1NzE4MTAsImV4cCI6MTY0NTc4MTQxMCwic2NvcGUiOiJzdG9yZTp0ZXN0bmV0OioiLCJpc3MiOiJodHRwczovL2xpbmRlbi5hdXRoaW5nLmNuL29pZGMiLCJhdWQiOiI2MjA2Mjk0MWVmOTJhOWQxNzA5Y2Y2MDUiLCJhenAiOiI2MTdhNDM5Y2FmNThhYWZjMGY5M2NiOGUifQ.oHzKGFyUgoJ5EipyFiAJHLnfAUFwdnHK7TL3XIcVyx6Q9s1sRsTkOZnq9RTfEhqKLHagzSmFIKcqCVbtHQ19XKXRW8Iht-g46SUyhm9w6smE51T-LuWLucjVcxjRtMLLW-nqdLKS0Y0N8-lT2qAz0wqe4mtCtP0f0H71Rbd329Hm_VayzEME8StnEKZ-XKeQ0a2cjmW-3vN_em-neR691b3YChegpXqzQ1jz6iIM6utYbAkaWTJcshLTRNOxxctTWeeUxh5pjXBHhdys69FYN2h4Qp7mHT347QSzj3F2n2sgd4it2xCq4iHu6g-fHfjORt-WxcnE6b_f5ggzQ5oAIA"

    it('add data', async () => {
        const res = await client.add.data('Hello world!1234565', { "user": "p1", "access-token": token })
        console.log('Added file contents:', res)

        assert(res.cid == "QmebsgayqHpYSzSGZDZLu8dhkfyu1j1EBKseqit1gyvaqc", "add data error.")
    })

    it('get data', async () => {
        const rst = await client.get.data("QmebsgayqHpYSzSGZDZLu8dhkfyu1j1EBKseqit1gyvaqc",  {"access-token": token })
        console.log(rst.toString())
    })

    // it('get encryptedFile', async () => {
    //     const privateKey = "5Jyb691zn225jivM4VVUrQ2JdZydq5Jpn3yjNznonaSXfmFMB4U"
    //     await client.get.encryptedFile("/linden/temp/aaa.txt", "bafyreighe2no2wbgbcjnjtyz2ky4n7u6ucv7pwzv62tvfwav6vwwhqapta", privateKey)
    // })

    // it('add proof', async () => {
    //     const proof = await client.add.proof("bafyreift2xn2kjfae2norpbsirjwxwf6vgbdpncfvk74fx3u3sixztc4d4",
    //         "5Jyb691zn225jivM4VVUrQ2JdZydq5Jpn3yjNznonaSXfmFMB4U", "EOS5PqCaKBWH6qYDqvVvbBCpwVwmLX1QvJJJLYgP49C8bZDhGiYMu", { "user": "0x9fA87f581D3821d4A51f167F56fE78cE7e0bA9B9" , "memo":  "testType_2", "access-token": token})
    //     console.log('Added encryptedFile proof:', proof)
    // })
})