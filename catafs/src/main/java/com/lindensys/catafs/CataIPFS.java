package com.lindensys.catafs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lindensys.catafs.bin.CataNode;
import com.lindensys.catafs.bin.FileType;
import com.lindensys.catafs.bin.Item;
import com.lindensys.catafs.bin.MfsNode;
import io.ipfs.api.*;
import io.ipfs.cid.Cid;
import io.ipfs.multiaddr.MultiAddress;
import io.ipfs.multihash.Multihash;
import org.apache.logging.log4j.util.Strings;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CataIPFS extends IPFS {
    protected static final String PREFIX = "/catafs";
    protected static final String INFO_FILE = "/.catalogInfo";
    protected static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 10000;
    protected static final int DEFAULT_READ_TIMEOUT_MILLIS = 60000;
    protected final String version;
    protected final int connectTimeoutMillis;
    protected final int readTimeoutMillis;
    public final CataIPFS.Files files;

    public CataIPFS(String host, int port) {
        this(host, port, "/api/v0/", false);
    }

    public CataIPFS(String multiaddr) {
        this(new MultiAddress(multiaddr));
    }

    public CataIPFS(MultiAddress addr) {
        this(addr.getHost(), addr.getTCPPort(), "/api/v0/", detectSSL(addr));
    }

    public CataIPFS(String host, int port, String version, boolean ssl) {
        this(host, port, version, 10000, 60000, ssl);
    }

    public CataIPFS(String host, int port, String version, int connectTimeoutMillis, int readTimeoutMillis, boolean ssl) {
        super(host, port, version, connectTimeoutMillis, readTimeoutMillis, ssl);
        if (connectTimeoutMillis < 0) {
            throw new IllegalArgumentException("connect timeout must be zero or positive");
        } else if (readTimeoutMillis < 0) {
            throw new IllegalArgumentException("read timeout must be zero or positive");
        } else {
            this.connectTimeoutMillis = connectTimeoutMillis;
            this.readTimeoutMillis = readTimeoutMillis;
        }

        this.version = version;
        this.files = new CataIPFS.Files();
    }


    protected static boolean detectSSL(MultiAddress multiaddress) {
        return multiaddress.toString().contains("/https");
    }

    protected Map retrieveMap(String path) throws IOException {
        return (Map) this.retrieveAndParse(path);
    }

    protected Object retrieveAndParse(String path) throws IOException {
        byte[] res = this.retrieve(path);
        return JSONParser.parse(new String(res));
    }

    protected byte[] retrieve(String path) throws IOException {
        URL target = new URL(this.protocol, this.host, this.port, this.version + path);
        return get(target, this.connectTimeoutMillis, this.readTimeoutMillis);
    }

    protected static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    protected static String decodeValue(String value) {
        try {
            value = value.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            value = value.replaceAll("\\\\u00", "%");
            return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    protected static byte[] get(URL target, int connectTimeoutMillis, int readTimeoutMillis) throws IOException {
        HttpURLConnection conn = configureConnection(target, "POST", connectTimeoutMillis, readTimeoutMillis);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        InputStream in;
        try {
            OutputStream out = conn.getOutputStream();
            out.write(new byte[0]);
            out.flush();
            out.close();
            in = conn.getInputStream();
            ByteArrayOutputStream resp = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];

            int r;
            while ((r = in.read(buf)) >= 0) {
                resp.write(buf, 0, r);
            }

            return resp.toByteArray();
        } catch (ConnectException var9) {
            throw new RuntimeException("Couldn't connect to IPFS daemon at " + target + "\n Is IPFS running?");
        } catch (IOException var10) {
            in = conn.getErrorStream();
            String err = in == null ? var10.getMessage() : new String(readFully(in));
            throw new RuntimeException(err);
        }
    }

    protected static HttpURLConnection configureConnection(URL target, String method, int connectTimeoutMillis, int readTimeoutMillis) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) target.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(connectTimeoutMillis);
        conn.setReadTimeout(readTimeoutMillis);
        return conn;
    }

    protected static final byte[] readFully(InputStream in) {
        try {
            ByteArrayOutputStream resp = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];

            int r;
            while ((r = in.read(buf)) >= 0) {
                resp.write(buf, 0, r);
            }

            return resp.toByteArray();
        } catch (IOException var4) {
            throw new RuntimeException("Error reading InputStrean", var4);
        }
    }

    public class Files {
        public Files() {
        }

        public Map<String, Object> stat(String path) throws IOException {
            path = convertCataPath(path);
            return retrieveMap("files/stat?arg=" + encodeValue(path));
        }

        public void mkdir(String path) throws IOException {
            path = convertCataPath(path);
            String str = new String(retrieve("files/mkdir?arg=" + encodeValue(path)));
            recursionUpdate(path);
        }

        public void mkdirWithParents(String path) throws IOException {
            path = convertCataPath(path);
            String str = new String(retrieve("files/mkdir?arg=" + encodeValue(path) + "&parents=true"));
            recursionUpdate(path);
        }

        public CataNode cp(String from, String to) throws IOException {
            return cpWithInfo(from, to, null, null, null);
        }

        public CataNode cpWithInfo(String from, String to, FileType fileType, String memo, String origin) throws IOException {
            from = convertCataPath(from);
            to = convertCataPath(to);

            String cataFilePath = to.substring(0, to.lastIndexOf("/")) + INFO_FILE;
            List<CataNode> cataNodes = getCatalogInfo(cataFilePath);
            String fileName = to.substring(to.lastIndexOf("/") + 1);
            if (cataNodes.stream().filter(cataNode -> cataNode.getName().equals(fileName)).count() != 0) {
                throw new RuntimeException("cp: cannot put node in path " + to + ": directory already has entry by that name");
            }

            if (from.startsWith("/proof/")) {
                _cpProof(from, to, fileType, memo, origin, cataFilePath, cataNodes, fileName);
            } else if ((from.startsWith("/ipfs/"))) {
                _cpIPFS(from, to, fileType, memo, origin, cataFilePath, cataNodes, fileName);
            } else {
                _cpItem(from, to, fileType, memo, origin, cataFilePath, cataNodes, fileName);
            }

            return lsItem(to);
        }

        private void _cpItem(String from, String to, FileType fileType, String memo, String origin, String cataFilePath, List<CataNode> cataNodes, String fileName) throws IOException {
            CataNode cataNode = lsItem(from);
            if (cataNode == null) {
                throw new RuntimeException("cp: file does not exist. from:" + from);
            } else {
                cataNode.setName(fileName);
            }
            if (!Strings.isEmpty(origin)) {
                cataNode.setOrigin(origin);
            }
            cataNode.setTime(System.currentTimeMillis());
            cataNodes.add(cataNode);
            String infsStr = JSONArray.toJSONString(cataNodes);
            if (cataNode.getItem() == Item.MFS) {
                _cp(from, to);
            }
            _write(cataFilePath, infsStr.getBytes());
            recursionUpdate(to);
        }

        private void _cpIPFS(String from, String to, FileType fileType, String memo, String origin, String cataFilePath, List<CataNode> cataNodes, String fileName) throws IOException {
            CataNode result = CataNode.builder()
                    .name(fileName)
                    .cid("")
                    .item(Item.MFS)
                    .fileType(fileType)
                    .memo(memo)
                    .origin(origin)
                    .time(System.currentTimeMillis()).build();
            cataNodes.add(result);
            String infsStr = JSONArray.toJSONString(cataNodes);
            _cp(from, to);
            _write(cataFilePath, infsStr.getBytes());
            recursionUpdate(to);
        }

        private void _cpProof(String from, String to, FileType fileType, String memo, String origin, String cataFilePath, List<CataNode> cataNodes, String fileName) throws IOException {
            String str = new String(dag.get(Cid.decode(from.substring("/proof/".length()))));
            JSONObject jsonObject = JSON.parseObject(str);
            if (jsonObject.getJSONObject("encryptInfo") == null) {
                throw new RuntimeException("proof CID error: " + from + "Is not a valid proof CID address.");
            }

            String cid = from.substring(from.lastIndexOf("/") + 1);
            CataNode result = CataNode.builder()
                    .name(fileName)
                    .cid(cid)
                    .item(Item.PROOF)
                    .size(jsonObject.getJSONArray("Links").getJSONObject(0).getLong("Tsize"))
                    .count(-1)
                    .fileType(fileType)
                    .memo(memo)
                    .origin(origin)
                    .time(System.currentTimeMillis()).build();
            cataNodes.add(result);
            String infsStr = JSONArray.toJSONString(cataNodes);
            _write(cataFilePath, infsStr.getBytes());
            recursionUpdate(to);
        }

        public void _cp(String from, String to) throws IOException {
//            String str = new String(retrieve("files/cp?arg=" + encodeValue(from) + "&arg=" + encodeValue(to) + "&parents=true"));
            String str = new String(retrieve("files/cp?arg=" + encodeValue(from) + "&arg=" + encodeValue(to)));
            System.out.println(str);
        }

        protected void _write(String path, byte[] object) throws IOException {
//            long start = System.currentTimeMillis();
            String prefix = protocol + "://" + host + ":" + port + version;
            Multipart m = new Multipart(prefix + "files/write?arg=" + encodeValue(path) + "&parents=true&create=true&truncate=true", "UTF-8");
            m.addFilePart("data", Paths.get(""), new NamedStreamable.ByteArrayWrapper(object));
            String res = m.finish();
//            long end = System.currentTimeMillis();
//            System.out.println("_write spend time is :" + (end - start));
        }

        public void write(String path, byte[] object) throws IOException {
            path = convertCataPath(path);
            _write(path, object);
            recursionUpdate(path);
        }

        public byte[] read(String path) throws IOException {
            if (path.startsWith("/ipfs/")) {
                String cid = path.split("/")[2];
                String subPath = path.substring(path.indexOf("/", 6));
                return cat(Multihash.fromBase58(cid), subPath);
            }
            path = convertCataPath(path);
            return retrieve("files/read?arg=" + encodeValue(path));
        }

        public void rm(String path) throws IOException {
            path = convertCataPath(path);
            String upperPath = path.substring(0, path.lastIndexOf("/"));
            if (!PREFIX.equals(upperPath)) {
                String cataFilePath = upperPath + INFO_FILE;
                List<CataNode> cataNodes = getCatalogInfo(cataFilePath);
                String leaf = path.substring(path.lastIndexOf("/") + 1);
                CataNode cataNode = cataNodes.stream().filter(node -> node.getName().equals(leaf)).findFirst().get();
                cataNodes.remove(cataNode);
                _write(cataFilePath, JSONArray.toJSONString(cataNodes).getBytes());

                if (cataNode.getItem() != Item.MFS) {
                    recursionUpdate(path);
                    return;
                }
            }
            byte[] retrieve = retrieve("files/rm?recursive=true&arg=" + encodeValue(path));
            System.out.println(new String(retrieve));
            recursionUpdate(upperPath);
        }


        public CataNode mv(String from, String to) throws IOException {
            return mvWithOrigin(from, to, null);
        }

        public CataNode mvWithOrigin(String from, String to, String origin) throws IOException {
            if (to.startsWith(from + "/")) {
                throw new RuntimeException("The destination folder is a subfolder of the source folder.");
            }
            CataNode cataNode = cpWithInfo(from, to, null, null, origin);
            rm(from);
            return cataNode;
        }

        public List<CataNode> ls(String path) throws IOException {
            path = convertCataPath(path);
            if (PREFIX.equals(path)) {
                List<MfsNode> mfsNodes = lsMfs(path);
                return mfsNodes.stream()
                        .map(mfsNode ->
                                CataNode.builder()
                                        .name(mfsNode.getName())
                                        .cid(mfsNode.getHash())
                                        .size(mfsNode.getSize())
                                        .item(Item.MFS)
                                        .build()
                        ).collect(Collectors.toList());
            }
            try {
                if ("directory".equals(stat(path).get("Type"))) {
                    return lsDir(path);
                }
            } catch (Exception e) {
                System.err.println("lsDir error.");
            }

            ArrayList<CataNode> cataNodes = new ArrayList<>();
            CataNode cataNode = lsItem(path);
            if (cataNode != null) {
                cataNodes.add(cataNode);
            }
            return cataNodes;
        }

        private List<CataNode> lsDir(String path) throws IOException {
            String cataFilePath = path + INFO_FILE;
            List<MfsNode> mfsNodes = lsMfs(path);
            List<CataNode> originNodes = getCatalogInfo(cataFilePath);
            List<CataNode> cataNodes = updateCatalogInfo(mfsNodes, originNodes);
            if (!path.equals(PREFIX) && !originNodes.equals(cataNodes)) {
                _write(cataFilePath, JSONArray.toJSONString(cataNodes).getBytes());
            }
            return cataNodes;
        }

        private CataNode lsItem(String path) throws IOException {
            String cataFilePath = path.substring(0, path.lastIndexOf("/")) + INFO_FILE;
            String fileName = path.substring(path.lastIndexOf("/") + 1);

            List<MfsNode> mfsNodes = lsMfs(path.substring(0, path.lastIndexOf("/")));
            List<CataNode> cataNodes = getCatalogInfo(cataFilePath);
            CataNode cataNode = cataNodes.stream().filter(node -> node.getName().equals(fileName)).findFirst().orElse(null);
            MfsNode mfsNode = mfsNodes.stream().filter(node -> node.getName().equals(fileName)).findFirst().orElse(null);
            return convertCataNode(mfsNode, cataNode);
        }

        protected List<MfsNode> lsMfs(String path) throws IOException {
            List<MfsNode> mfsNodes = Collections.EMPTY_LIST;
            try {
                Map map = retrieveMap("files/ls?long=true&arg=" + encodeValue(path));
                JSONObject json = new JSONObject(map);
                JSONArray entries = json.getJSONArray("Entries");
                entries.stream().map(node -> {
                    Map data = (Map)node;
                    String name = data.get("Name").toString();
                    name = decodeValue(name);
                    data.put("Name", name);
                    return node;
                }).collect(Collectors.toList());
                mfsNodes = JSONArray.parseArray(entries.toJSONString(), MfsNode.class);
            } catch (RuntimeException e) {
            }
            return mfsNodes;
        }

        protected List<CataNode> getCatalogInfo(String cataFilePath) {
            List<CataNode> cataNodes = new ArrayList<>();
            try {
                String data = new String(read(cataFilePath));
                cataNodes = JSONArray.parseArray(data, CataNode.class);
            } catch (RuntimeException e) {
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return cataNodes;
        }

        protected String convertCataPath(String path) {
            if (Strings.isEmpty(path) || "/".equals(path)) {
                return PREFIX;
            }
            if (path.startsWith("/ipfs") || path.startsWith("/ipns")
                    || path.startsWith("/proof") || path.startsWith(PREFIX)) {
                return path;
            }
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            return PREFIX + path;
        }

        protected List<CataNode> updateCatalogInfo(List<MfsNode> mfsNodes, List<CataNode> cataNodes) {
            Map<String, CataNode> map = cataNodes.stream().collect(Collectors.toMap(CataNode::getName, o -> o));
            List<CataNode> nodes = mfsNodes.stream()
                    .filter(mfsNode -> !INFO_FILE.endsWith(mfsNode.getName()))
                    .map(mfsNode -> {
                        CataNode cataNode = map.get(mfsNode.getName());
                        map.remove(mfsNode.getName());
                        return convertCataNode(mfsNode, cataNode);
                    })
                    .collect(Collectors.toList());

            nodes.addAll(map.values());
            return nodes;
        }

        protected CataNode convertCataNode(MfsNode mfsNode, CataNode cataNode) {
            if (mfsNode == null) {
                return cataNode;
            }
            if (cataNode == null) {
                return CataNode.builder()
                        .name(mfsNode.getName())
                        .cid(mfsNode.getHash())
                        .size(mfsNode.getSize())
                        .time(System.currentTimeMillis())
                        .item(Item.MFS)
                        .count(mfsNode.getType() == 0 ? -1 : getCount("/ipfs/" + mfsNode.getHash()))
                        .build();
            }
            if (!cataNode.getCid().equals(mfsNode.getHash())) {
                //clone cataNode
                cataNode = JSON.parseObject(JSON.toJSONString(cataNode), CataNode.class);
                cataNode.setCid(mfsNode.getHash());
                cataNode.setSize(mfsNode.getSize());
                cataNode.setCount(mfsNode.getType() == 0 ? -1 : getCount("/ipfs/" + mfsNode.getHash()));
                cataNode.setTime(System.currentTimeMillis());
            }
            return cataNode;
        }

        protected int getCount(String path) {
            int count = 0;
            List<CataNode> cataNodes = getCatalogInfo(path + INFO_FILE);
            count = cataNodes.size();
            if (count != 0) {
                return count;
            }

            try {
                if (path.startsWith("/ipfs/")) {
                    String cid = path.split("/")[2];
                    List<MerkleNode> list = CataIPFS.this.ls(Multihash.fromBase58(cid));
                    count = (int) list.stream().filter(merkleNode -> !INFO_FILE.endsWith(merkleNode.name.get())).count();
                    return count;
                }
                List<MfsNode> mfsNodes = lsMfs(path);
                count = (int) mfsNodes.stream().filter(mfsNode -> !INFO_FILE.endsWith(mfsNode.getName())).count();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return count;
        }

        protected void recursionUpdate(String path) throws IOException {
            if (PREFIX.equals(path)) {
                return;
            }
            ls(path);
            path = path.substring(0, path.lastIndexOf("/"));
            recursionUpdate(path);
        }
    }
}