# flipped-POSS

[![standard-readme compliant](https://img.shields.io/badge/standard--readme-OK-green.svg?style=flat-square)](https://github.com/RichardLitt/standard-readme)
TODO: Put more badges here.

A private OSS client based on IPFS.

## Table of Contents

- [Install](#install)
- [Usage](#usage)
- [Maintainers](#maintainers)
- [Contributing](#contributing)
- [License](#license)

## Install

```
npm install --save linden-poss

```

## Usage

```
const createClient = require('linden-poss');
const client = createClient.create({ clientID: "testnet" });

// add data
(async function(){
    const res = await client.add.data('Hello world!')
    console.log('Added data contents:', res)
})();

// add file
(async function(){
    const res = await client.add.file('c:/Users/xieyu/Pictures/94156e5566c82ae7233655505653d674.mp4')
    console.log('Added file:', res)
})();

//get data
(async function() {
    for await (const chunk of client.get.data("QmebsgayqHpYSzSGZDZLu8dhkfyu1j1EBKseqit1gyvaqc")) {
        console.info('geted data:', chunk.toString())
    }
})();

// add web site
(async function(){
    const res = await client.add.addr('C:/linden/linden-newpc/dist')
    console.log('Added addr contents:', res)
})();


```

## Maintainers

[@do-nothing](https://github.com/do-nothing)

## Contributing

PRs accepted.

Small note: If editing the README, please conform to the [standard-readme](https://github.com/RichardLitt/standard-readme) specification.

## License

MIT © 2021 lindensys.com
