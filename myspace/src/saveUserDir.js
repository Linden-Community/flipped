const createDB = require('./dbHelper');

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
    const hash = await db.put("user1", dirInfo);
    dirInfo = db.get("user1");
    console.log(dirInfo)
})()