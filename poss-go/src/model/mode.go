package model

import (
	"bytes"
	"encoding/json"
	"fmt"
	"github.com/ipld/go-ipld-prime/codec/dagjson"
	"github.com/ipld/go-ipld-prime/datamodel"
	"github.com/ipld/go-ipld-prime/node/basicnode"
)

type ProofInfo struct {
	Data        string      `json:"Data"`
	FileInfo    [1]FileInfo `json:"Links"`
	EncryptInfo *EncryptRes `json:"encryptInfo"`
	Grantee     string      `json:"grantee"`
	Grantor     string      `json:"grantor"`
}

type FileInfo struct {
	Hash  Hash   `json:"Hash"`
	Name  string `json:"Name"`
	TSize int64  `json:"Tsize"`
}

type Hash struct {
	Cid string `json:"/"`
}

type EncryptRes struct {
	Nonce    Long   `json:"nonce"`
	Message  []byte `json:"message"`
	CheckSum int64  `json:"checksum"`
}

type Long struct {
	High     int64 `json:"high"`
	Low      int64 `json:"low"`
	Unsigned bool  `json:"unsigned"`
}

func GetCborProof(info *ProofInfo) (value []byte, error error) {

	np := basicnode.Prototype.Any
	nb := np.NewBuilder()
	ma, _ := nb.BeginMap(5)

	// data
	ma.AssembleKey().AssignString("Data")
	ma.AssembleValue().AssignString("djE=")

	// Links
	ma.AssembleKey().AssignString("Links")
	ma.AssembleValue().AssignNode(getLinks(info.FileInfo[0]))

	// encryptInfo
	ma.AssembleKey().AssignString("encryptInfo")
	ma.AssembleValue().AssignNode(getEncryptInfo(*info.EncryptInfo))

	// grantor
	ma.AssembleKey().AssignString("grantor")
	ma.AssembleValue().AssignString(info.Grantor)

	// grantee
	ma.AssembleKey().AssignString("grantee")
	ma.AssembleValue().AssignString(info.Grantee)

	ma.Finish()
	n := nb.Build()

	buf := new(bytes.Buffer)

	error = dagjson.Encode(n, buf)
	//error = dagcbor.Encode(n, buf)

	if error != nil {
		return nil, error
	}

	return buf.Bytes(), nil
}

func GetJsonProof(info *ProofInfo) (proof []byte, error error) {
	info.Data = "djE="
	marshalJSON, err := info.MarshalJSON()
	if err != nil {
		fmt.Println("GetFileProof fail", "转化json发生错误")
	}
	//fmt.Println("转化结果为:", (string)(marshalJSON))
	return marshalJSON, nil
}

func (n *ProofInfo) MarshalJSON() ([]byte, error) {

	hash := map[string]interface{}{
		"/": n.FileInfo[0].Hash.Cid,
	}

	var links = map[string]interface{}{
		"Hash":  hash,
		"Name":  n.FileInfo[0].Name, // utf8
		"TSize": n.FileInfo[0].TSize,
	}
	nonce := map[string]interface{}{
		"high":     n.EncryptInfo.Nonce.High,
		"low":      n.EncryptInfo.Nonce.Low,
		"unsigned": n.EncryptInfo.Nonce.Unsigned,
	}

	encryptInfo := map[string]interface{}{
		"checksum": n.EncryptInfo.CheckSum,
		"message":  n.EncryptInfo.Message,
		"nonce":    nonce,
	}

	linkArray := make([]map[string]interface{}, 1)
	linkArray[0] = links
	out := map[string]interface{}{
		"Data":        n.Data,
		"Links":       linkArray,
		"encryptInfo": encryptInfo,
		"grantee":     n.Grantee,
		"grantor":     n.Grantor,
	}

	return json.Marshal(out)
}

// cbor 凭证格式
func getEncryptInfo(encryptRes EncryptRes) datamodel.Node {

	np := basicnode.Prototype.Any

	nb := np.NewBuilder()
	ma, _ := nb.BeginMap(3)

	ma.AssembleKey().AssignString("checksum")
	ma.AssembleValue().AssignInt(encryptRes.CheckSum)

	ma.AssembleKey().AssignString("message")
	ma.AssembleValue().AssignBytes(encryptRes.Message)

	ma.AssembleKey().AssignString("nonce")
	ma.AssembleValue().AssignNode(getNonce(encryptRes.Nonce))

	ma.Finish()
	return nb.Build()
}

func getNonce(long Long) datamodel.Node {
	np := basicnode.Prototype.Any
	nb := np.NewBuilder()
	ma, _ := nb.BeginMap(3)

	ma.AssembleKey().AssignString("high")
	ma.AssembleValue().AssignInt(long.High)

	ma.AssembleKey().AssignString("low")
	ma.AssembleValue().AssignInt(long.Low)

	ma.AssembleKey().AssignString("unsigned")
	ma.AssembleValue().AssignBool(long.Unsigned)

	ma.Finish()
	return nb.Build()
}

func getLinks(file FileInfo) datamodel.Node {
	npList := basicnode.Prototype.Any
	nbList := npList.NewBuilder()
	list, _ := nbList.BeginList(1)

	beginMap, _ := list.AssembleValue().BeginMap(3)

	// hash
	beginMap.AssembleKey().AssignString("Hash")
	beginMap.AssembleValue().AssignNode(getHash(file.Hash.Cid))

	// name
	beginMap.AssembleKey().AssignString("Name")
	beginMap.AssembleValue().AssignString(file.Name)

	// tsize
	beginMap.AssembleKey().AssignString("Tsize")
	beginMap.AssembleValue().AssignInt(file.TSize)

	beginMap.Finish()
	list.Finish()

	return nbList.Build()
}

func getHash(cid string) datamodel.Node {

	np := basicnode.Prototype.Any
	nb := np.NewBuilder()
	ma, _ := nb.BeginMap(1)

	ma.AssembleKey().AssignString("/")
	ma.AssembleValue().AssignString(cid)

	ma.Finish()
	return nb.Build()
}
