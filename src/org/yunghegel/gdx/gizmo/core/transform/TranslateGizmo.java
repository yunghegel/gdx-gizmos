package org.yunghegel.gdx.gizmo.core.transform;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.yunghegel.gdx.gizmo.core.Gizmo;
import org.yunghegel.gdx.gizmo.core.GizmoType;

public class TranslateGizmo extends TransformGizmo {
    private static final float ARROW_THICKNESS = 0.25f;
    private static final float ARROW_CAP_SIZE = 0.1f;
    private static final int ARROW_DIVISIONS = 12;

    private final Vector3 lastPos = new Vector3();
    private boolean globalSpace = true;
    private boolean initTranslate = true;
    private final Vector3 temp0 = new Vector3();
    private final Vector3 temp1 = new Vector3();
    private final Matrix4 tempMat0 = new Matrix4();

    private final TranslateGizmoHandle xHandle;
    private final TranslateGizmoHandle yHandle;
    private final TranslateGizmoHandle zHandle;
    private final TranslateGizmoHandle xyzHandle;

    private TranslateGizmoHandle[] handlesArray = new TranslateGizmoHandle[4];

    private final Array<TranslateGizmoHandle> handles = new Array<>();







    public TranslateGizmo(InputMultiplexer inputs, ModelBatch batch, Camera camera, Viewport viewport,int toggleKey){
        super(inputs,batch,camera,viewport,toggleKey, GizmoType.TRANSLATE);
        ModelBuilder modelBuilder = new ModelBuilder();
        ColorAttribute PBRColorAttribute;
        Model xHandleModel = modelBuilder.createArrow(0 , 0 , 0 , .5f , 0 , 0 , ARROW_CAP_SIZE , ARROW_THICKNESS, ARROW_DIVISIONS , GL20.GL_TRIANGLES , new Material(ColorAttribute.createDiffuse(COLOR_X)) , VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        Model yHandleModel = modelBuilder.createArrow(0 , 0 , 0 , 0 , .5f , 0 , ARROW_CAP_SIZE , ARROW_THICKNESS, ARROW_DIVISIONS , GL20.GL_TRIANGLES , new Material(ColorAttribute.createDiffuse(COLOR_Y)) , VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        Model zHandleModel = modelBuilder.createArrow(0 , 0 , 0 , 0 , 0 , .5f , ARROW_CAP_SIZE , ARROW_THICKNESS, ARROW_DIVISIONS , GL20.GL_TRIANGLES , new Material(ColorAttribute.createDiffuse(COLOR_Z)) , VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        Model xyzHandleModel = modelBuilder.createSphere(.25f/3 , .25f/3 , .25f/3 , 20 , 20 , new Material(ColorAttribute.createDiffuse(COLOR_XYZ)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        xHandle = new TranslateGizmoHandle(xHandleModel, Gizmo.X_HANDLE_ID);
        yHandle = new TranslateGizmoHandle(yHandleModel, Gizmo.Y_HANDLE_ID);
        zHandle = new TranslateGizmoHandle(zHandleModel, Gizmo.Z_HANDLE_ID);
        xyzHandle = new TranslateGizmoHandle(xyzHandleModel, Gizmo.XYZ_HANDLE_ID);

        handles.add(xHandle,yHandle,zHandle,xyzHandle);
        handlesArray[0] = xHandle;
        handlesArray[1] = yHandle;
        handlesArray[2] = zHandle;
        handlesArray[3] = xyzHandle;
    }

    @Override
    public void render(ModelBatch batch) {
        if(!enabled) return;
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        batch.begin(camera);

        for (TranslateGizmoHandle handle : handles) {
            Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

            handle.render(batch);
        }
        batch.end();
//        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }

    @Override
    public void update(){

        for (TranslateGizmoHandle handle : handles) {
            handle.update((target.getTransform()), camera);
        }

//        if (state == TransformState.IDLE) return;

        Ray ray = camera.getPickRay(Gdx.input.getX(),Gdx.input.getY());
        Vector3 rayEnd= new Vector3();
        target.getLocalPosition(rayEnd);
        float dst = camera.position.dst(rayEnd);
        rayEnd = ray.getEndPoint(rayEnd,dst);

        if (initTranslate) {
            initTranslate = false;
            lastPos.set(rayEnd);
        }

        boolean modified = false;
        Vector3 vec = new Vector3();
        if(state==TransformState.X){
            vec.set(rayEnd.x - lastPos.x, 0, 0);
            modified = true;
        } else if(state==TransformState.Y){
            vec.set(0, rayEnd.y - lastPos.y, 0);
            modified = true;
        } else if(state==TransformState.Z){
            vec.set(0, 0, rayEnd.z - lastPos.z);
            modified = true;
        }


        target.translate(vec);
        target.apply();
        lastPos.set(rayEnd);

    }



    @Override
    public void dispose() {
        for (TranslateGizmoHandle handle : handles) {
            handle.dispose();
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if (button == 0 && target !=null) {

            TranslateGizmoHandle handle = (TranslateGizmoHandle)picker.pick(viewport,batch,camera,screenX,screenY,handlesArray);
            if (handle != null) {
                state = TransformState.IDLE;
                int id = handle.getId();
                Gdx.input.setInputProcessor(this);
                switch(id) {
                    case Gizmo.X_HANDLE_ID:
                        state = TransformState.X;
                        initTranslate = true;
                        handle.setColor(COLOR_X_SELECTED);
                        break;
                    case Gizmo.Y_HANDLE_ID:
                        state = TransformState.Y;
                        initTranslate = true;
                        handle.setColor(COLOR_Y_SELECTED);
                        break;
                    case Gizmo.Z_HANDLE_ID:
                        state = TransformState.Z;
                        initTranslate = true;
                        handle.setColor(COLOR_Z_SELECTED);
                        break;
                    case Gizmo.XYZ_HANDLE_ID:
                        state = TransformState.XYZ;
                        initTranslate = true;
                        handle.setColor(COLOR_XYZ_SELECTED);
                        break;
                }
            }
        }
        System.out.println(state);
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);
        state = TransformState.IDLE;
        for(TranslateGizmoHandle handle : handles){
           handle.restoreColor();
        }
        Gdx.input.setInputProcessor(inputs);

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (state != TransformState.IDLE) return true;


        return super.touchDragged(screenX, screenY, pointer);
    }


    private static class TranslateGizmoHandle extends GizmoHandle {

        public TranslateGizmoHandle(Model handleModel,int id) {
            super(handleModel,id);
        }


        @Override
        void render(ModelBatch batch){
            batch.render(handle);
        }
    }

}
