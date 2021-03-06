/*
 * Copyright 2014.
 * Distributed under the terms of the GPLv3 License.
 *
 * Authors:
 *      Clemens Zeidler <czei002@aucklanduni.ac.nz>
 */
package nz.ac.auckland.lablet.misc;

import java.io.*;


public class AudioWavOutputStream extends OutputStream implements Closeable {
    private RandomAccessFile outFile;
    private long audioDataLength = 0;
    private long sampleRate = 0;
    final private int PCM_FORMAT = 1;
    final private int PCM_SUB_CHUNK_SIZE = 16;
    final private int BITS_PER_SAMPLE = 16;
    protected int channelCount;

    public AudioWavOutputStream(File file, int channelCount, int sampleRate) throws IOException {
        this.outFile = new RandomAccessFile(file, "rw");

        this.channelCount = channelCount;
        this.sampleRate = sampleRate;

        // set position just after the header
        outFile.seek(44);
    }

    @Override
    public void write(int i) throws IOException {
        outFile.write(i);
        audioDataLength++;
    }

    @Override
    public void write(byte[] buffer) throws java.io.IOException {
        outFile.write(buffer);
        audioDataLength += buffer.length;
    }

    @Override
    public void write(byte[] buffer, int offset, int count) throws java.io.IOException {
        outFile.write(buffer, offset, count);
        audioDataLength += count;
    }

    @Override
    public void close() throws IOException {
        writeHeader();
        outFile.close();
    }

    private void writeAsBigEndian(long value) throws IOException {
        outFile.writeByte((int)(value & 0xff));
        outFile.writeByte((int)((value >> 8) & 0xff));
        outFile.writeByte((int)((value >> 16) & 0xff));
        outFile.writeByte((int)((value >> 24) & 0xff));
    }

    private void writeHeader() throws IOException {
        long dataLength = audioDataLength + 36;

        outFile.seek(0);
        outFile.write('R');
        outFile.write('I');
        outFile.write('F');
        outFile.write('F');
        writeAsBigEndian(dataLength + 36);
        outFile.write('W');
        outFile.write('A');
        outFile.write('V');
        outFile.write('E');
        outFile.write('f');
        outFile.write('m');
        outFile.write('t');
        outFile.write(' ');
        outFile.writeByte(PCM_SUB_CHUNK_SIZE);
        outFile.writeByte(0);
        outFile.writeByte(0);
        outFile.writeByte(0);
        outFile.writeByte(PCM_FORMAT);
        outFile.writeByte(0);
        outFile.writeByte(channelCount);
        outFile.writeByte(0);
        writeAsBigEndian(sampleRate);
        long byteRate = sampleRate * 2 * channelCount;
        writeAsBigEndian(byteRate);
        outFile.writeByte(BITS_PER_SAMPLE / 2 * channelCount);
        outFile.writeByte(0);
        outFile.writeByte(BITS_PER_SAMPLE);
        outFile.writeByte(0);
        outFile.write('d');
        outFile.write('a');
        outFile.write('t');
        outFile.write('a');
        writeAsBigEndian(audioDataLength);
    }
}
