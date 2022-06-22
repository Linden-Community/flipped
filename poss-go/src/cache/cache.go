package cache

import (
	"fmt"
	"github.com/arriqaaq/flashdb"
	"strconv"
	"sync"
)

var (
	flashDbConfig = &flashdb.Config{Path: "/tmp", EvictionInterval: 10}
)

func View(str string) (value string) {
	// 初始化数据库
	db, err := flashdb.New(flashDbConfig)
	defer db.Close()
	if err != nil {
		fmt.Println(err)
		fmt.Println("获取失败")
		return "-1"
	}

	err1 := db.View(func(tx *flashdb.Tx) error {
		val, err := tx.Get(str)
		if err != nil {
			return err
		}
		value = val
		return nil
	})

	if err1 != nil {
		return "-1"
	}

	return
}

func Update(str string, i float64, mutex *sync.WaitGroup) {
	// 初始化数据库
	db, err := flashdb.New(flashDbConfig)
	defer db.Close()
	defer mutex.Done()
	if err != nil {
		fmt.Println(err)
		fmt.Println("获取失败")
	}

	err1 := db.Update(func(tx *flashdb.Tx) error {
		err := tx.Set(str, strconv.FormatFloat(i, 'f', 6, 64))
		return err
	})

	if err1 != nil {
		fmt.Println("修改失败")
	}
}
