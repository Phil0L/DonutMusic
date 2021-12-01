package com.pl.donut.music.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Vector;

public class StreamTest {

  public static void main(String[] args) throws IOException {
    Vector<InputStream> streams = new Vector<>();
    streams.add(new ByteArrayInputStream("Lorem dolor ipsum ned üosiehfouASEHFGÖILUehfiouHGIULFGsiluefghilushgfiludrhg\n".getBytes()));
    streams.add(new ByteArrayInputStream("Lorem dolor ipsum ned üosiehfouASEHFGÖILUehfiouHGIULFGsiluefghilushgfiludrhg\n".getBytes()));
    streams.add(new ByteArrayInputStream("Lorem dolor ipsum ned üosiehfouASEHFGÖILUehfiouHGIULFGsiluefghilushgfiludrhg\n".getBytes()));
    streams.add(new ByteArrayInputStream("Lorem dolor ipsum ned üosiehfouASEHFGÖILUehfiouHGIULFGsiluefghilushgfiludrhg\n".getBytes()));
    SequenceInputStream stream = new SequenceInputStream(streams.elements());

    int oneByte;
    while ((oneByte = stream.read()) != -1) {
      System.out.write(oneByte);
      System.out.flush();
    }

  }
}
