package src

import (
	"bytes"
	"crypto/sha256"
	"crypto/sha512"
	"encoding/binary"
	"encoding/hex"
	"fmt"
	"github.com/enceve/crypto/dh/ecdh"
	"github.com/eoscanada/eos-go/btcsuite/btcd/btcec"
	"github.com/eoscanada/eos-go/btcsuite/btcutil"
	"github.com/eoscanada/eos-go/ecc"
	"github.com/ethereum/go-ethereum/crypto/secp256k1"
	"liblindencloud/src/model"
)

const cell uint64 = 1 << 32

func PrivateKeyToPublic(privateK string) (string, error) {

	kk := ecdh.GenericCurve(secp256k1.S256())
	priv_bytes, err := hex.DecodeString(privateK)
	if nil != err {
		return "", err
	}

	return hex.EncodeToString(kk.PublicKey(priv_bytes)), nil
}

func PrivateKeyToPublicByEos(privateK string) (string, error) {

	privKey, err := ecc.NewPrivateKey(privateK)

	if err != nil {
		PrintLog("EosPrivateKeyToPublic fail", "eos私钥转公钥失败")
	}

	pubKey := privKey.PublicKey()

	return pubKey.String(), nil

}

func GetSharedSecret(privateK, publicK string) (secret []byte, err error) {

	var (
		privK, pubK []byte
		keyExchange ecdh.KeyExchange
		shareSecret []byte
	)

	privK, err = hex.DecodeString(privateK)

	if nil != err {
		return nil, fmt.Errorf("resolve hex private key failed: %s", err.Error())
	}

	pubK, err = hex.DecodeString(publicK)
	if nil != err {
		return nil, fmt.Errorf("resolve hex private key failed: %s", err.Error())
	}

	keyExchange = ecdh.GenericCurve(secp256k1.S256())

	shareSecret = keyExchange.ComputeSecret(privK, pubK)
	// PrintLog("shareSecret", shareSecret)

	// 计算结果sha512加密
	secret_tmp := sha512.Sum512(shareSecret)
	secret = append(secret, secret_tmp[:]...)
	return secret, nil
}

func GetSharedSecretByEos(eosPrivateK, eosPublicK string) (secret []byte, err error) {
	wif, err := btcutil.DecodeWIF(eosPrivateK)
	privKey := wif.PrivKey
	pub, err := ecc.NewPublicKey(eosPublicK)
	publicKey, err := pub.Key()
	sharedSecret := btcec.GenerateSharedSecret(privKey, publicKey)
	// 获取共享密钥
	sum512 := sha512.Sum512(sharedSecret)

	return sum512[:], nil
}

func EcdhEncrypt(shareSecret []byte, message []byte) (encryptRes model.EncryptRes, err error) {

	var secret_little bytes.Buffer
	nonce := getUniqueNonce()
	binary.Write(&secret_little, binary.LittleEndian, nonce)
	//PrintLog("secret_little_nonce", secret_little.Bytes())

	binary.Write(&secret_little, binary.LittleEndian, shareSecret)
	//PrintLog("secret_little", secret_little.Bytes())

	encrypt_key := sha512.Sum512(secret_little.Bytes())
	//PrintLog("encrypt_key", encrypt_key[:])

	encryptRes.Nonce = convertUint64ToLong(nonce)
	//PrintLog("long nonce", encryptRes.Nonce)
	// message
	encryptRes.Message = aesEncrypt(message, encrypt_key[:])
	//PrintLog("encrypt message", encryptRes.Message)

	check := sha256.Sum256(encrypt_key[:])
	var check_little bytes.Buffer
	binary.Write(&check_little, binary.LittleEndian, check[0:4])
	var checksum int64
	binary.Read(&check_little, binary.LittleEndian, &checksum)

	//PrintLog("checksum", checksum)
	encryptRes.CheckSum = checksum
	return
}

func EcdhDecrypt(privateKey string, info model.ProofInfo) []byte {
	var secret_little bytes.Buffer
	// 获取共享密钥
	secret, _ := GetSharedSecretByEos(privateKey, info.Grantor)
	// nonce 转uint
	nonce := convertLongToUint64(info.EncryptInfo.Nonce)

	binary.Write(&secret_little, binary.LittleEndian, nonce)
	binary.Write(&secret_little, binary.LittleEndian, secret)

	encrypt_key := sha512.Sum512(secret_little.Bytes())

	// 字符串转化为[] byte
	//decodeString := hex.DecodeString(info.EncryptInfo.Message)

	decrypt := aesDecrypt(info.EncryptInfo.Message, encrypt_key[:])
	// 解密之后sha512加密
	aesKey := sha512.Sum512(decrypt)

	return aesKey[:]
	return nil
}

func convertUint64ToLong(value uint64) model.Long {
	var l model.Long
	l.Low = int64(value % cell)
	l.High = int64(value / cell)
	l.Unsigned = false

	return l
}

func convertLongToUint64(l model.Long) uint64 {
	var nonce_ori uint64
	if l.Low < 0 {
		nonce_ori = uint64(l.High+1)*cell + uint64(l.Low)
	} else {
		nonce_ori = uint64(l.High)*cell + uint64(l.Low)
	}

	return nonce_ori
}

func getUniqueNonce() uint64 {
	var cell int64 = 1 << 32
	var nonce_ori = int64(25191556+1)*cell + int64(-677065606)
	return uint64(nonce_ori)
}
