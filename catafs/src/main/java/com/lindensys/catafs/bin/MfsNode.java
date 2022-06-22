package com.lindensys.catafs.bin;

import lombok.Data;

@Data
public class MfsNode {
    String Name;
    String Hash;
    Long Size;
    Integer Type;
}
