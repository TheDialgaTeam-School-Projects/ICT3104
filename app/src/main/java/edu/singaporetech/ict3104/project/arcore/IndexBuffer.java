package edu.singaporetech.ict3104.project.arcore;

import android.opengl.GLES30;

import java.io.Closeable;
import java.nio.IntBuffer;

public class IndexBuffer implements Closeable {

    private final GpuBuffer buffer;

    public IndexBuffer(IntBuffer entries) {
        buffer = new GpuBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, GpuBuffer.INT_SIZE, entries);
    }

    public void set(IntBuffer entries) {
        buffer.set(entries);
    }

    @Override
    public void close() {
        buffer.free();
    }

    int getBufferId() {
        return buffer.getBufferId();
    }

    int getSize() {
        return buffer.getSize();
    }

}
