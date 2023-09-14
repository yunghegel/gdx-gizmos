package org.yunghegel.gdx.gizmo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.yunghegel.gdx.gizmo.core.transform.GizmoHandle;
import org.yunghegel.gdx.picking.PickerColorEncoder;


import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class ToolPicker implements Disposable {

    protected FrameBuffer fbo;

    private Texture fboTexture;

    public ToolPicker() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        try {
            fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
        } catch (Exception e) {

            width *= 0.9f;
            height *= 0.9f;
            try {
                fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
            } catch (Exception ee) {
                Logger.getGlobal().warning("Failed to create FrameBuffer for ToolHandlePicker");
            }
        }
    }

    public GizmoHandle pick(Viewport viewport, ModelBatch batch, Camera cam, int screenX, int screenY, GizmoHandle[] handles) {
        begin(viewport);
        renderPickableScene(batch, cam,handles);
        end();
        Pixmap pm = getFrameBufferPixmap(viewport);

        int x = screenX - viewport.getScreenX();
        int y = screenY - (Gdx.graphics.getHeight() - (viewport.getScreenY() + viewport.getScreenHeight()));

        int id = PickerColorEncoder.decode(pm.getPixel(x, y));

        System.out.println("Picked id: " + id);

        for (GizmoHandle handle : handles) {
            if (handle.getId() == id) {
                return handle;
            }
        }

        return null;
    }

    public Texture getFboTexture(){
        if(fboTexture == null){
            fboTexture = fbo.getColorBufferTexture();
        }
        return fboTexture;
    }

    protected void begin(Viewport viewport) {
        // Per LibGDX WIKI, we need to set to Pixels temporarily when capturing Scene2D elements otherwise we run into
        // issues on retina screens. Observed issues on Mac with tools not detecting clicks
        // https://libgdx.com/wiki/graphics/opengl-utils/frame-buffer-objects
        HdpiUtils.setMode(HdpiMode.Pixels);
        fbo.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        HdpiUtils.glViewport(viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(),
                viewport.getScreenHeight());
    }

    protected void end() {
        fbo.end();
        HdpiUtils.setMode(HdpiMode.Logical);
    }

    public Pixmap getFrameBufferPixmap(Viewport viewport) {
        int w = viewport.getScreenWidth();
        int h = viewport.getScreenHeight();
        int x = viewport.getScreenX();
        int y = viewport.getScreenY();
        final ByteBuffer pixelBuffer = BufferUtils.newByteBuffer(w * h * 4);

        Gdx.gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, fbo.getFramebufferHandle());
        Gdx.gl.glReadPixels(x, y, w, h, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, pixelBuffer);
        Gdx.gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, 0);

        final int numBytes = w * h * 4;
        byte[] imgLines = new byte[numBytes];
        final int numBytesPerLine = w * 4;
        for (int i = 0; i < h; i++) {
            pixelBuffer.position((h - i - 1) * numBytesPerLine);
            pixelBuffer.get(imgLines, i * numBytesPerLine, numBytesPerLine);
        }

        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        BufferUtils.copy(imgLines, 0, pixmap.getPixels(), imgLines.length);

        return pixmap;

    }



    private void renderPickableScene(ModelBatch batch, Camera cam, GizmoHandle[] handles){
        batch.begin(cam);
        for (GizmoHandle handle : handles) {
            handle.renderPick(batch);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        fbo.dispose();
    }

}
