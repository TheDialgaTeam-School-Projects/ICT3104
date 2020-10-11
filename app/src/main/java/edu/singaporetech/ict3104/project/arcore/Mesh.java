package edu.singaporetech.ict3104.project.arcore;

import android.opengl.GLES30;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;

public class Mesh implements Closeable {

    private static final String TAG = Mesh.class.getSimpleName();

    private final int[] vertexArrayId = {0};
    private final PrimitiveMode primitiveMode;
    private final IndexBuffer indexBuffer;
    private final VertexBuffer[] vertexBuffers;

    public Mesh(PrimitiveMode primitiveMode, IndexBuffer indexBuffer, VertexBuffer[] vertexBuffers) {
        if (vertexBuffers == null || vertexBuffers.length == 0) {
            throw new IllegalArgumentException("Must pass at least one vertex buffer");
        }

        this.primitiveMode = primitiveMode;
        this.indexBuffer = indexBuffer;
        this.vertexBuffers = vertexBuffers;

        try {
            // Create vertex array
            GLES30.glGenVertexArrays(1, vertexArrayId, 0);
            GLError.maybeThrowGLException("Failed to generate a vertex array", "glGenVertexArrays");

            // Bind vertex array
            GLES30.glBindVertexArray(vertexArrayId[0]);
            GLError.maybeThrowGLException("Failed to bind vertex array object", "glBindVertexArray");

            if (indexBuffer != null) {
                GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getBufferId());
            }

            for (int i = 0; i < vertexBuffers.length; ++i) {
                // Bind each vertex buffer to vertex array
                GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vertexBuffers[i].getBufferId());
                GLError.maybeThrowGLException("Failed to bind vertex buffer", "glBindBuffer");
                GLES30.glVertexAttribPointer(i, vertexBuffers[i].getNumberOfEntriesPerVertex(), GLES30.GL_FLOAT, false, 0, 0);
                GLError.maybeThrowGLException("Failed to associate vertex buffer with vertex array", "glVertexAttribPointer");
                GLES30.glEnableVertexAttribArray(i);
                GLError.maybeThrowGLException("Failed to enable vertex buffer", "glEnableVertexAttribArray");
            }
        } catch (Throwable t) {
            close();
            throw t;
        }
    }

    public static Mesh createFromAsset(SurfaceViewRenderer surfaceViewRenderer, String assetFileName) throws IOException {
        try (InputStream inputStream = surfaceViewRenderer.getAssets().open(assetFileName)) {
            Obj obj = ObjUtils.convertToRenderable(ObjReader.read(inputStream));

            // Obtain the data from the OBJ, as direct buffers:
            IntBuffer vertexIndices = ObjData.getFaceVertexIndices(obj, /*numVerticesPerFace=*/ 3);
            FloatBuffer localCoordinates = ObjData.getVertices(obj);
            FloatBuffer textureCoordinates = ObjData.getTexCoords(obj, /*dimensions=*/ 2);
            FloatBuffer normals = ObjData.getNormals(obj);

            VertexBuffer[] vertexBuffers = {
                    new VertexBuffer(3, localCoordinates),
                    new VertexBuffer(2, textureCoordinates),
                    new VertexBuffer(3, normals),
            };

            IndexBuffer indexBuffer = new IndexBuffer(vertexIndices);

            return new Mesh(Mesh.PrimitiveMode.TRIANGLES, indexBuffer, vertexBuffers);
        }
    }

    @Override
    public void close() {
        if (vertexArrayId[0] != 0) {
            GLES30.glDeleteVertexArrays(1, vertexArrayId, 0);
            GLError.maybeLogGLError(Log.WARN, TAG, "Failed to free vertex array object", "glDeleteVertexArrays");
        }
    }

    void draw() {
        if (vertexArrayId[0] == 0) {
            throw new IllegalStateException("Tried to draw a freed Mesh");
        }

        GLES30.glBindVertexArray(vertexArrayId[0]);
        GLError.maybeThrowGLException("Failed to bind vertex array object", "glBindVertexArray");
        if (indexBuffer == null) {
            // Sanity check for debugging
            int numberOfVertices = vertexBuffers[0].getNumberOfVertices();
            for (int i = 1; i < vertexBuffers.length; ++i) {
                if (vertexBuffers[i].getNumberOfVertices() != numberOfVertices) {
                    throw new IllegalStateException("Vertex buffers have mismatching numbers of vertices");
                }
            }
            GLES30.glDrawArrays(primitiveMode.glesEnum, 0, numberOfVertices);
            GLError.maybeThrowGLException("Failed to draw vertex array object", "glDrawArrays");
        } else {
            GLES30.glDrawElements(primitiveMode.glesEnum, indexBuffer.getSize(), GLES30.GL_UNSIGNED_INT, 0);
            GLError.maybeThrowGLException("Failed to draw vertex array object with indices", "glDrawElements");
        }
    }

    public enum PrimitiveMode {
        POINTS(GLES30.GL_POINTS),
        LINE_STRIP(GLES30.GL_LINE_STRIP),
        LINE_LOOP(GLES30.GL_LINE_LOOP),
        LINES(GLES30.GL_LINES),
        TRIANGLE_STRIP(GLES30.GL_TRIANGLE_STRIP),
        TRIANGLE_FAN(GLES30.GL_TRIANGLE_FAN),
        TRIANGLES(GLES30.GL_TRIANGLES);

        final int glesEnum;

        PrimitiveMode(int glesEnum) {
            this.glesEnum = glesEnum;
        }
    }

}
