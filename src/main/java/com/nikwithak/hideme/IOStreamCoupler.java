package com.nikwithak.hideme;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Arrays;

@Slf4j
public class IOStreamCoupler implements Runnable {
    final static private int BUFFER_SIZE = 8 * 1024; // 8KB

    static private int connections = 1;

    final private InputStream in;
    final private OutputStream out;

    public IOStreamCoupler(final InputStream in, final OutputStream out) {
        this.in = in;
        this.out = out;
    }

    public void run() {
        try (InputStream in = this.in; OutputStream out = this.out) {
            byte[] buf = new byte[BUFFER_SIZE];
            int bytes;
            while ((bytes = in.read(buf)) != -1) {
                out.write(buf, 0, bytes);
                out.flush();
            }
        } catch (IOException e) {}
    }
}
