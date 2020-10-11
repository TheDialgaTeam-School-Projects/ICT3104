package edu.singaporetech.ict3104.project.arcore;

import android.content.res.AssetManager;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SurfaceViewRenderer {

    private final AssetManager assetManager;

    public SurfaceViewRenderer(GLSurfaceView glSurfaceView, Renderer renderer, AssetManager assetManager) {
        this.assetManager = assetManager;

        glSurfaceView.setPreserveEGLContextOnPause(true);
        glSurfaceView.setEGLContextClientVersion(3);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glSurfaceView.setRenderer(
                new GLSurfaceView.Renderer() {
                    @Override
                    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                        GLES30.glEnable(GLES30.GL_BLEND);
                        renderer.onSurfaceCreated(SurfaceViewRenderer.this);
                    }

                    @Override
                    public void onSurfaceChanged(GL10 gl, int w, int h) {
                        GLES30.glViewport(0, 0, w, h);
                        renderer.onSurfaceChanged(SurfaceViewRenderer.this, w, h);
                    }

                    @Override
                    public void onDrawFrame(GL10 gl) {
                        GLES30.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
                        renderer.onDrawFrame(SurfaceViewRenderer.this);
                    }
                });
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        glSurfaceView.setWillNotDraw(false);
    }

    public void draw(Mesh mesh, Shader shader) {
        shader.use();
        mesh.draw();
    }

    public interface Renderer {
        void onSurfaceCreated(SurfaceViewRenderer surfaceViewRenderer);

        void onSurfaceChanged(SurfaceViewRenderer surfaceViewRenderer, int width, int height);

        void onDrawFrame(SurfaceViewRenderer surfaceViewRenderer);
    }

    AssetManager getAssets() {
        return assetManager;
    }

}
