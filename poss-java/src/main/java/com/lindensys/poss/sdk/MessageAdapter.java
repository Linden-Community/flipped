package com.lindensys.poss.sdk;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Base64;

/**
 * @author Jiang Shunzhi
 * @date 2022/5/13
 */
public class MessageAdapter extends TypeAdapter<byte[]> {
    @Override
    public void write(JsonWriter jsonWriter, byte[] bytes) throws IOException {
        jsonWriter.jsonValue(Base64.getEncoder().encodeToString(bytes));
    }

    @Override
    public byte[] read(JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return Base64.getDecoder().decode(value);
    }
}
