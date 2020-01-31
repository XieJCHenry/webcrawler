package org.jccode.webcrawler.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * IOUtils
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/1/30 21:29
 * @Version 1.0
 **/
public class IOUtils {
    private IOUtils() {
    }

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;


    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy(input,baos);
        return baos.toByteArray();
    }

    public static long copy(InputStream input, OutputStream out) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int i;
        while ((i = input.read(buffer)) != -1) {
            out.write(buffer, 0, i);
            count += i;
        }
        return count;
    }


}
