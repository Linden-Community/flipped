package com.lindensys.catafs.bin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CataNode {
    String name;
    String cid;
    Item item;
    Long size;
    Long time;
    Integer count;
    FileType fileType;
    String memo;
    String origin;
}
