package com.lindensys.catafs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lindensys.catafs.bin.CataNode;
import com.lindensys.catafs.bin.FileType;
import io.ipfs.multiaddr.MultiAddress;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@TestMethodOrder(MethodOrderer.MethodName.class)
class CataIPFSTest {
    //ipfs version 0.12.2
    private final CataIPFS ipfs = new CataIPFS(new MultiAddress("/ip4/192.168.253.61/tcp/31944"));

    @Test
    public void a_mkdir() throws IOException {
        ipfs.files.rm("0xaB069f281a15a3D0C2876F939331F4B63601cDBE");
        ipfs.files.mkdirWithParents("0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/.recycleBin");
        ipfs.files.mkdirWithParents("0xaB069f281a15a3D0C2876F939331F4B63601cDBE/encryption/.recycleBin");
        ipfs.files.mkdirWithParents("0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/dir1");
        ipfs.files.mkdirWithParents("0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/a/b/c/d");
        ipfs.files.mkdirWithParents("0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/#dir1");
        ipfs.files.mkdirWithParents("0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/dir1（）");
        ipfs.files.mkdirWithParents("0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/aaa bbb");
        ipfs.files.mkdirWithParents("0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/*<(¦Q[▓▓  呼呼呼。。。。");
        ipfs.files.mkdirWithParents("0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal//?%#&+=:*\"<>|");
        try {
            ipfs.files.mkdir("0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/a1/b/c/d");
        }catch (Exception e){
            System.out.println("*****************error ->" + e.getMessage() + "<-");
        }
        ipfs.files.mkdirWithParents("0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/a/a&");
        ipfs.files.mkdirWithParents("0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/a/&");
        List<CataNode> ls = ipfs.files.ls("/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/a");
        System.out.println(JSONArray.toJSONString(ls));

        ipfs.files.mkdirWithParents("0x28874638e103d7957b07ab3c6ac006eb71168c18/normal/a a/b b/ c/ d");
    }

    @Test
    public void stat() throws IOException {
        Map<String, Object> stat = ipfs.files.stat("/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/dir1/1.txt");
        System.out.println(JSONArray.toJSONString(stat));
        Assertions.assertEquals("directory", stat.get("Type"));
        stat = ipfs.files.stat("/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/dir1/1.txt/1.txt");
        System.out.println(JSONArray.toJSONString(stat));
        Assertions.assertEquals("file", stat.get("Type"));
    }

    @Test
    public void cp() throws IOException {
        String path = "/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/dir1";
        ipfs.files.rm(path);
        CataNode cp = ipfs.files.cp("/ipfs/QmaRJjcGRfkJcYfpE8RDe5TmS83zLgQqW9s9qYGtLnBjLV", path);
        System.out.println(JSONArray.toJSONString(cp));
    }

    @Test
    public void rm() throws IOException {
        String path = "/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/.recycleBin/bbb.txt";
        ipfs.files.cp("/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/dir1/bbb.txt", path);
        ipfs.files.rm(path);
        Assertions.assertThrowsExactly(RuntimeException.class, () -> {
            ipfs.files.read(path);
        });
    }

    @Test
    public void mv() throws IOException {
        ipfs.files.mv("/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/dir1/aaa.txt",
                "/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/.recycleBin/aaa.txt");
        Assertions.assertThrowsExactly(RuntimeException.class, () -> {
            ipfs.files.read("/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/dir1/aaa.txt");
        });
        ipfs.files.mv("/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/.recycleBin/aaa.txt",
                "/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/dir1/aaa.txt");
    }

    @Test
    public void ls() throws IOException {
        List<CataNode> ls = ipfs.files.ls("/0xcE7cAac7Fe305317aedece86DaD39162E7217591");
        System.out.println(JSONArray.toJSONString(ls));
        /*List<CataNode> ls2 = ipfs.files.ls("/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/dir1");
        System.out.println(JSONArray.toJSONString(ls2));
        List<CataNode> ls3 = ipfs.files.ls("/");
        System.out.println(JSONArray.toJSONString(ls3));*/
    }

    @Test
    public void read() throws IOException {
        byte[] data = ipfs.files.read("/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/dir1/bbb.txt");
        Assertions.assertEquals("BBB",  new String(data));

        byte[] data2 = ipfs.files.read("/ipfs/QmaRJjcGRfkJcYfpE8RDe5TmS83zLgQqW9s9qYGtLnBjLV/bbb.txt");
        Assertions.assertEquals("BBB",  new String(data2));
    }

    @Test
    public void write() throws IOException {
        String path = "/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/dir1/aaa.txt";
        String str = "AAA";
        ipfs.files.write(path, str.getBytes());
        byte[] read = ipfs.files.read(path);
        Assertions.assertEquals(str, new String(read));
    }

    @Test
    public void lsEncrypt() throws IOException {
        List<CataNode> ls = ipfs.files.ls("/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/encryption/dir1/abc.jpg");
        System.out.println(JSONArray.toJSONString(ls));
        List<CataNode> ls2 = ipfs.files.ls("/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/encryption/dir1/");
        System.out.println(JSONArray.toJSONString(ls2));
    }

    @Test
    public void cpEncryptedFile() throws IOException {
        String path = "/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/encryption/dir1";
        CataNode cp = ipfs.files.cp("/proof/bafyreihhwoc7nqqj7xlyiy42b2rgmesvvnnd46gymeluozo3zndlj27dxu", path + "/abc.jpg");
        System.out.println(JSON.toJSONString(cp));
        ipfs.files.mkdir(path + "/dir1_1");
        CataNode cp1 = ipfs.files.cp(path + "/dir1_1", path + "/dir1_2");
        System.out.println(JSON.toJSONString(cp1));
    }

    @Test
    public void cpEncryptedFile2() throws IOException {
        String path = "/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/encryption/dir1";
        ipfs.files.cp(path + "/abc.jpg", path + "/bcd.jpg");
    }

    @Test
    public void rmEncrypt() throws IOException {
        String path = "/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/encryption/dir1";
        ipfs.files.rm(path + "/abc.jpg");
        List<CataNode> ls = ipfs.files.ls(path);
        System.out.println(JSONArray.toJSONString(ls));
        ipfs.files.rm(path + "/dir1_2");
        List<CataNode> ls1 = ipfs.files.ls(path);
        System.out.println(JSONArray.toJSONString(ls1));
    }

    @Test
    public void mvEncrypt() throws IOException {
        String path = "/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/encryption/dir1";
        ipfs.files.mv(path + "/abc.jpg",
                "/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/encryption/.recycleBin/abc.jpg");
        Assertions.assertThrowsExactly(RuntimeException.class, () -> {
            ipfs.files.read(path + "/abc.jpg");
        });
        ipfs.files.mv("/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/encryption/.recycleBin/abc.jpg",
                path + "/abc.jpg");
    }

    @Test
    public void cpWithInfo() throws IOException {
        String path = "/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/dir1/";
        CataNode ls = ipfs.files.cpWithInfo("/ipfs/QmanTKzihp1GTD9gufQpb9xttrfzAmSh2ZzEGeSBdsUq17", path + "b1.txt", FileType.DOCS, "test memo", "/normal/dir");
        System.out.println(ls);

        CataNode ls2 = ipfs.files.cpWithInfo(path + "b1.txt", path + "b2.txt", FileType.DOCS, "test memo", "/normal/dir");
        System.out.println(ls2);

        CataNode ls3 = ipfs.files.cpWithInfo("/proof/bafyreihhwoc7nqqj7xlyiy42b2rgmesvvnnd46gymeluozo3zndlj27dxu", path + "b3.txt", FileType.DOCS, "test memo", "/normal/dir");
        System.out.println(ls3);

        ipfs.files.rm(path + "b1.txt");
        ipfs.files.rm(path + "b2.txt");
//        ipfs.files.rm(path + "b3.txt");
    }

    @Test
    public void mvWithOrigin() throws IOException {
        String path = "/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/dir1/";
        ipfs.files.mvWithOrigin("/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/dir1/b3.txt", "/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/.recycleBin/b3.txt", "/normal/dir1/b3.txt");
        List<CataNode> ls3 = ipfs.files.ls("/0xaB069f281a15a3D0C2876F939331F4B63601cDBE/normal/.recycleBin");
        System.out.println(JSONArray.toJSONString(ls3));
    }
}