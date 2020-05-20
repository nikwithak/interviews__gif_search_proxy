package com.nikwithak.hideme;

import mockit.Expectations;
import mockit.Mocked;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class IOStreamCouplerTest {
    final static int DATA_LENGTH = 256 * 1024 * 1024; // 256MB

    byte[] fromData = new byte[DATA_LENGTH];

    @Before
    public void setup() {
        // Fill the fake input stream with random data. We'll compare in the test that it's the same as what
        // we write to the fake output stream.
        new Random().nextBytes(fromData);
    }

    @Test
    public void testIOStreamCoupler() {
        ByteArrayInputStream in = new ByteArrayInputStream(fromData);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        new IOStreamCoupler(in, out).run();

        Assert.assertArrayEquals(fromData, out.toByteArray());
    }

    @Test
    public void testExceptionIsSwallowed(@Mocked InputStream in, @Mocked OutputStream out) throws IOException {
        new Expectations() {{
            out.flush(); result = new IOException();
        }};

        new IOStreamCoupler(in, out).run();

        // Expect no exceptions
    }
}
