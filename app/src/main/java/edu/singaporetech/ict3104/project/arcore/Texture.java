package edu.singaporetech.ict3104.project.arcore;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;

public class Texture implements Closeable {

    private static final String TAG = Texture.class.getSimpleName();

    private final int[] textureId = {0};
    private final Target target;

    public Texture(Target target, WrapMode wrapMode) {
        this.target = target;

        GLES30.glGenTextures(1, textureId, 0);
        GLError.maybeThrowGLException("Texture creation failed", "glGenTextures");

        try {
            GLES30.glBindTexture(target.glesEnum, textureId[0]);
            GLError.maybeThrowGLException("Failed to bind texture", "glBindTexture");
            GLES30.glTexParameteri(target.glesEnum, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLError.maybeThrowGLException("Failed to set texture parameter", "glTexParameteri");
            GLES30.glTexParameteri(target.glesEnum, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLError.maybeThrowGLException("Failed to set texture parameter", "glTexParameteri");

            GLES30.glTexParameteri(target.glesEnum, GLES30.GL_TEXTURE_WRAP_S, wrapMode.glesEnum);
            GLError.maybeThrowGLException("Failed to set texture parameter", "glTexParameteri");
            GLES30.glTexParameteri(target.glesEnum, GLES30.GL_TEXTURE_WRAP_T, wrapMode.glesEnum);
            GLError.maybeThrowGLException("Failed to set texture parameter", "glTexParameteri");
        } catch (Throwable t) {
            close();
            throw t;
        }
    }

    public static Texture createFromAsset(SurfaceViewRenderer surfaceViewRenderer, String assetFileName, WrapMode wrapMode) throws IOException {
        Texture texture = new Texture(Target.TEXTURE_2D, wrapMode);
        try {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture.getTextureId());
            Bitmap bitmap = BitmapFactory.decodeStream(surfaceViewRenderer.getAssets().open(assetFileName));
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
            GLError.maybeThrowGLException("Failed to populate texture data", "GLUtils.texImage2D");
            GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
            GLError.maybeThrowGLException("Failed to generate mipmaps", "glGenerateMipmap");
        } catch (Throwable t) {
            texture.close();
            throw t;
        }
        return texture;
    }

    @Override
    public void close() {
        if (textureId[0] != 0) {
            GLES30.glDeleteTextures(1, textureId, 0);
            GLError.maybeLogGLError(Log.WARN, TAG, "Failed to free texture", "glDeleteTextures");
            textureId[0] = 0;
        }
    }

    public int getTextureId() {
        return textureId[0];
    }

    Target getTarget() {
        return target;
    }

    public enum WrapMode {
        CLAMP_TO_EDGE(GLES30.GL_CLAMP_TO_EDGE),
        MIRRORED_REPEAT(GLES30.GL_MIRRORED_REPEAT),
        REPEAT(GLES30.GL_REPEAT);

        /* package-private */
        final int glesEnum;

        WrapMode(int glesEnum) {
            this.glesEnum = glesEnum;
        }
    }

    public enum Target {
        TEXTURE_2D(GLES30.GL_TEXTURE_2D),
        TEXTURE_EXTERNAL_OES(GLES11Ext.GL_TEXTURE_EXTERNAL_OES),
        TEXTURE_CUBE_MAP(GLES30.GL_TEXTURE_CUBE_MAP);

        final int glesEnum;

        Target(int glesEnum) {
            this.glesEnum = glesEnum;
        }
    }

}
