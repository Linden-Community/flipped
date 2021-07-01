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

let dirInfo = {
  "nocheck": true,
  "type": "directory",
  "name": "BACKUP",
  "children": [
    {
      "nocheck": true,
      "type": "directory",
      "name": "hr-201.9-dbbackup",
      "children": [
        {
          "nocheck": true,
          "type": "file",
          "name": "uxinhr.bak"
        },
        {
          "nocheck": true,
          "type": "file",
          "name": "zknet10.bak"
        }
      ]
    },
    {
      "nocheck": true,
      "type": "file",
      "name": "rsyncd - 副本.rar"
    },
    {
      "nocheck": true,
      "type": "file",
      "name": "部署系统-websites-20160113.rar"
    }
  ]
}

let db;
(async () => {
  db = await createDB()
  const hash = await db.put("user1", fileList);
  dirInfo = db.get("user1");
  console.log(dirInfo)
})()