package com.lindensys.catafs.bin;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FileType {
    /**
     *
     */
    OTHER, IMAGE, VIDEOS, DOCS, AUDIO, APPS, ARCHIVES;

    @JsonValue
    public int getOrdinal() {
        return ordinal();
    }
}
