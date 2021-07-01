# flipped-MySpace

[![standard-readme compliant](https://img.shields.io/badge/standard--readme-OK-green.svg?style=flat-square)](https://github.com/RichardLitt/standard-readme)
TODO: Put more badges here.

A IPFS-backed cloud disk.

## Table of Contents

- [Install](#install)
- [Initia](#initia)
- [Usage](#usage)
- [Maintainers](#maintainers)
- [Contributing](#contributing)
- [License](#license)

## Install

```
npm i linden-poss --save
npm i orbit-db --save

```

## Initia
vim dbHelper.js
```
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

```

## Usage

```
const createDB = require('./dbHelper');

let fileList = {
  image: [
    {
      "Name": "8A4A1163.jpg",
      "Hash": "QmWVLSaULcHpqzx7aSmVF4rtSZVmbC17DomYFVVR3oJ9QC",
      "Size": "2837821"
    },
    {
      "Name": "photo1.jpg",
      "Hash": "QmPTMZ8pPnTNdaU3koBGxTPCr2d9YJPx3SrWgGw7T4A75G",
      "Size": "3704828"
    }

  ],
  video: [],
  note: [],
  file: []
}
let db;
(async () => {
  db = await createDB()
  const hash = await db.put("user1", fileList);
  dirInfo = db.get("user1");
  console.log(dirInfo)
})()

```

## Maintainers

[@do-nothing](https://github.com/do-nothing)

## Contributing

PRs accepted.

Small note: If editing the README, please conform to the [standard-readme](https://github.com/RichardLitt/standard-readme) specification.

## License

MIT © 2021 lindensys.com
