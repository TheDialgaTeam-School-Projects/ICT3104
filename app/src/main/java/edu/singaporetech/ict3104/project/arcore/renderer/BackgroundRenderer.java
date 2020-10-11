package edu.singaporetech.ict3104.project.arcore.renderer;

import com.google.ar.core.Coordinates2d;
import com.google.ar.core.Frame;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import edu.singaporetech.ict3104.project.arcore.Mesh;
import edu.singaporetech.ict3104.project.arcore.Shader;
import edu.singaporetech.ict3104.project.arcore.SurfaceViewRenderer;
import edu.singaporetech.ict3104.project.arcore.Texture;
import edu.singaporetech.ict3104.project.arcore.VertexBuffer;

public class BackgroundRenderer {

    private static final String CAMERA_VERTEX_SHADER_NAME = "shaders/background_show_camera.vert";
    private static final String CAMERA_FRAGMENT_SHADER_NAME = "shaders/background_show_camera.frag";

    private static final int FLOAT_SIZE = 4;

    private static final float[] QUAD_COORDS_ARRAY = {
            /*0:*/ -1f, -1f, /*1:*/ +1f, -1f, /*2:*/ -1f, +1f, /*3:*/ +1f, +1f,
    };

    private static final FloatBuffer QUAD_COORDS_BUFFER =
            ByteBuffer.allocateDirect(QUAD_COORDS_ARRAY.length * FLOAT_SIZE)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();

    static {
        QUAD_COORDS_BUFFER.put(QUAD_COORDS_ARRAY);
    }

    private final FloatBuffer texCoords =
            ByteBuffer.allocateDirect(QUAD_COORDS_ARRAY.length * FLOAT_SIZE)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();

    private final Mesh mesh;
    private final VertexBuffer texCoordsVertexBuffer;
    private final Shader cameraShader;
    private final Texture cameraTexture;

    public BackgroundRenderer(SurfaceViewRenderer renderer) throws IOException {
        cameraTexture = new Texture(Texture.Target.TEXTURE_EXTERNAL_OES, Texture.WrapMode.CLAMP_TO_EDGE);
        cameraShader = Shader.createFromAssets(renderer, CAMERA_VERTEX_SHADER_NAME, CAMERA_FRAGMENT_SHADER_NAME, null)
                .setTexture("u_Texture", cameraTexture)
                .setDepthTest(false)
                .setDepthWrite(false);

        VertexBuffer localCoordsVertexBuffer = new VertexBuffer(2, QUAD_COORDS_BUFFER);
        texCoordsVertexBuffer = new VertexBuffer(2, null);
        VertexBuffer[] vertexBuffers = {
                localCoordsVertexBuffer, texCoordsVertexBuffer,
        };
        mesh = new Mesh(Mesh.PrimitiveMode.TRIANGLE_STRIP, null, vertexBuffers);
    }

    public void draw(SurfaceViewRenderer renderer, Frame frame) {
        // If display rotation changed (also includes view size change), we need to re-query the uv
        // coordinates for the screen rect, as they may have changed as well.
        if (frame.hasDisplayGeometryChanged()) {
            QUAD_COORDS_BUFFER.rewind();
            frame.transformCoordinates2d(Coordinates2d.OPENGL_NORMALIZED_DEVICE_COORDINATES, QUAD_COORDS_BUFFER, Coordinates2d.TEXTURE_NORMALIZED, texCoords);
            texCoordsVertexBuffer.set(texCoords);
        }

        if (frame.getTimestamp() == 0) {
            // Suppress rendering if the camera did not produce the first frame yet. This is to avoid
            // drawing possible leftover data from previous sessions if the texture is reused.
            return;
        }

        renderer.draw(mesh, cameraShader);
    }

    public int getTextureId() {
        return cameraTexture.getTextureId();
    }

}
