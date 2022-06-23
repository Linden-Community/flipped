package src

import (
	"encoding/json"
	"fmt"
	"liblindencloud/src/global"
	"liblindencloud/src/model"
)

// addProof  添加凭证 grantor grantee
func addProof(p *model.ProofInfo) (proofCid string, error error) {

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

// getProof 通过凭证cid获取凭证json
func getProof(cid string) (value model.ProofInfo, error error) {

	proof := make(map[string]interface{})
	err := global.GetShell().DagGet(cid, &proof)
	if err != nil {
		fmt.Printf("get proof fail...")
		return value, err
	}

	model, err1 := proofToModel(proof)
	if err1 != nil {
		return value, err1
	}

	return model, nil
}

// uploadProof 上传凭证
func uploadProof(proof []byte) (proofCid string, error error) {
	// 		InputCodec: "dag-json",
	//		StoreCodec: "dag-cbor",
	put, err := global.GetShell().DagPut(proof, "dag-cbor", "dag-cbor")

	// TODO pin

	if err != nil {
		PrintLog("addProof fail", "添加凭证失败")
		return "", err
	}

	return put, nil
}

// proofToModel 凭证json转化为model
func proofToModel(proof map[string]interface{}) (value model.ProofInfo, error error) {
	// 将原始凭证数据转化为json
	bytes, _ := json.Marshal(proof)
	//fmt.Println("转化后结果为", string(bytes))
	err := json.Unmarshal(bytes, &value) //第二个参数要地址传递
	if err != nil {
		return value, err
	}

	return value, nil
}

// proofToString 凭证json转化string
func proofToString(proof map[string]interface{}) (str string, error error) {
	// 将原始凭证数据转化为json
	bytes, err := json.Marshal(proof)
	if err != nil {
		return "", err
	}
	return string(bytes), nil
}
