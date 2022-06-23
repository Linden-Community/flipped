package src

import (
	"fmt"
	"io"
	"liblindencloud/src/cache"
	"liblindencloud/src/global"
	"liblindencloud/src/model"
	"os"
)

type DefaultPossClient struct{}

func (client DefaultPossClient) AddByPath(filePath string) (cid string, err error) {

	reader, err := os.Open(filePath)
	if err != nil {
		PrintLog("addByPath", "打开文件错误")
		return "", err
	}

	cid, err = add(reader)
	if err != nil {
		PrintLog("addByPath", "上传文件错误")
		return "", err
	}

	return cid, nil
}

func (client DefaultPossClient) AddByReader(reader io.Reader) (cid string, err error) {
	cid, err = add(reader)
	if err != nil {
		PrintLog("addByPath", "上传文件错误")
		return "", err
	}

	return cid, nil
}

func (client DefaultPossClient) AddEncryptByPath(path, privateKey, randomFileID string) (cid string, err error) {

	aesKey := randomString(51)

	cid, fileFormat, err := uploadEncryptedFile(path, aesKey, randomFileID)
	if nil != err {
		return "", err
	}

	publicK, err := PrivateKeyToPublicByEos(privateKey)
	if nil != err {
		return "", err
	}

	secret, err := GetSharedSecretByEos(privateKey, publicK)

	encryptRes, err := EcdhEncrypt(secret, []byte(aesKey))

	// 上传凭证
	proof := new(model.ProofInfo)
	proof.EncryptInfo = &encryptRes

	proof.FileInfo[0].Hash.Cid = cid
	proof.FileInfo[0].Name = fileFormat.Name
	proof.FileInfo[0].TSize = fileFormat.TSize
	proof.Grantee = publicK
	proof.Grantor = publicK
	proofCid, err1 := addProof(proof)
	if nil != err1 {
		return "", err
	}

	return proofCid, nil
}

func (client DefaultPossClient) Get(cid string) (bytes []byte, error error) {

	cat, err := global.GetShell().Cat(global.DefaultBaseUrl + cid)

	if err != nil {
		PrintLog("download file", "下载文件异常")
		return nil, err
	}

	bytes, error = readerToBytes(cat)
	if error != nil {
		PrintLog("download file readToBytes", "转化异常")
		return nil, err
	}

	return bytes, nil

}

func (client DefaultPossClient) GetEncrypt(proofCid, privateKey string) ([]byte, error) {
	// 通过凭证cid获取凭证model
	proof, err := getProof(proofCid)
	if err != nil {
		PrintLog("GetEncrypted fail", "获取凭证失败")
		return nil, err
	}

	// 文件cid
	cid := proof.FileInfo[0].Hash.Cid

	// 下载加密文件数据
	encryptFile, err := client.Get(cid)
	if err != nil {
		PrintLog("Download fail", "下载加密文件失败")
		return nil, err
	}

	// 解密aesKey
	aesKey := EcdhDecrypt(privateKey, proof)

	// 解密文件
	return decrypt(encryptFile, aesKey), nil

}

func (client DefaultPossClient) Grant(proofCid, privateKey, publicKey string) (newProofCid string, err error) {
	// 下载凭证
	oldProof, err := getProof(proofCid)
	if err != nil {
		PrintLog("Grant", "下载凭证失败")
	}

	SharedSecret, err1 := GetSharedSecretByEos(privateKey, publicKey)
	if err1 != nil {
		PrintLog("Grant", "获取共享密钥失败")
	}

	aesKey := EcdhDecrypt(privateKey, oldProof)

	// 获取encryptInfo 信息
	encryptRes, err := EcdhEncrypt(SharedSecret, aesKey)

	p := new(model.ProofInfo)
	p.EncryptInfo = &encryptRes
	p.FileInfo[0].Hash.Cid = oldProof.FileInfo[0].Hash.Cid
	p.FileInfo[0].Name = oldProof.FileInfo[0].Name
	p.FileInfo[0].TSize = oldProof.FileInfo[0].TSize
	p.Grantor = oldProof.Grantor
	p.Grantee = publicKey

	// 上传凭证
	cid, err2 := addProof(p)

	if err2 != nil {
		PrintLog("Grant", "上传凭证失败")
		return "", err2
	}

	return cid, nil
}

func (client DefaultPossClient) AddProof(cid, name, privateKey, aesKey string, size int64) (string, error) {

	publicK, err := PrivateKeyToPublicByEos(privateKey)
	if nil != err {
		return "", err
	}

	secret, err := GetSharedSecretByEos(privateKey, publicK)

	encryptRes, err := EcdhEncrypt(secret, []byte(aesKey))

	// 上传凭证
	p := new(model.ProofInfo)
	p.EncryptInfo = &encryptRes
	p.FileInfo[0].Hash.Cid = cid
	p.FileInfo[0].Name = name
	p.FileInfo[0].TSize = size
	p.Grantee = publicK
	p.Grantor = publicK

	// 获取标准凭证格式
	proof, err := model.GetJsonProof(p)
	if err != nil {
		PrintLog("GetFileProof fail", "获取凭证格式失败")
		return "", err
	}

	// 上传凭证
	value, err := uploadProof(proof)
	if err != nil {
		PrintLog("UploadProof fail", "上传凭证失败")
		return "", err
	}

	return value, nil
}

func (client DefaultPossClient) GetProof(proofCid string) (str string, error error) {

	proof := make(map[string]interface{})
	err := global.GetShell().DagGet(proofCid, &proof)
	if err != nil {
		fmt.Printf("get proof fail...")
		return "", err
	}

	model, err1 := proofToString(proof)
	if err1 != nil {
		return "", err1
	}

	return model, nil
}

func (client DefaultPossClient) UploadProgress(fileId string) string {
	return cache.View(fileId)
}

func (client DefaultPossClient) GetRandomFileID() string {
	// 获取一个随机的文件id
	return getTimeNowStr()
}
