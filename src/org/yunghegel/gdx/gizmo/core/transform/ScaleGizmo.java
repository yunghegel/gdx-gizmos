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
import com.badlogic.gdx.utils.viewport.Viewport;
import org.yunghegel.gdx.gizmo.Util;
import org.yunghegel.gdx.gizmo.core.Gizmo;
import org.yunghegel.gdx.gizmo.core.GizmoTarget;
import org.yunghegel.gdx.gizmo.core.GizmoType;

public class ScaleGizmo extends TransformGizmo {

    private final Vector3 lastPos = new Vector3();
    private final Vector3 temp0 = new Vector3();
    private final Vector3 temp1 = new Vector3();
    private final Matrix4 tempMat0 = new Matrix4();

    public boolean initScale = true;


    private final ScaleHandle xHandle,yHandle,zHandle,xyzHandle;

    private final ScaleHandle[] handlesArray = new ScaleHandle[4];

    public ScaleGizmo(InputMultiplexer inputMultiplexer, ModelBatch batch, Camera camera, Viewport viewport,int toggleKey) {
        super(inputMultiplexer, batch, camera, viewport, toggleKey, GizmoType.SCALE);

    Model xHandleModel = Util.createArrowStub(new Material(ColorAttribute.createDiffuse(COLOR_X)),Vector3.Zero,new Vector3(0.5f , 0 , 0));
    Model yHandleModel = Util.createArrowStub(new Material(ColorAttribute.createDiffuse(COLOR_Y)),Vector3.Zero,new Vector3(0 , 0.5f , 0));
    Model zHandleModel = Util.createArrowStub(new Material(ColorAttribute.createDiffuse(COLOR_Z)),Vector3.Zero,new Vector3(0 , 0 , 0.5f));
    ModelBuilder modelBuilder = new ModelBuilder();
    Model xyzPlaneHandleModel = modelBuilder.createBox(0.08f , 0.08f , 0.08f , new Material(ColorAttribute.createDiffuse(COLOR_XYZ)) , VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal| VertexAttributes.Usage.TextureCoordinates);

    xHandle = new ScaleHandle(xHandleModel , X_HANDLE_ID);
    yHandle = new ScaleHandle(yHandleModel , Y_HANDLE_ID);
    zHandle = new ScaleHandle(zHandleModel , Z_HANDLE_ID);
    xyzHandle = new ScaleHandle(xyzPlaneHandleModel , XYZ_HANDLE_ID);

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
        for(ScaleHandle handle : handlesArray){
            Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
            handle.render(batch);
        }
        batch.end();
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }

    @Override
    public void update(GizmoTarget gizmoTarget) {
        this.target = (TransformGizmoTarget) gizmoTarget;

        if(!enabled) return;
        for(ScaleHandle handle : handlesArray){

            handle.update(target.getTransform(),camera);

        }


        if(state == TransformState.IDLE) return;
        Ray ray = camera.getPickRay(Gdx.input.getX(),Gdx.input.getY());
        Vector3 rayEnd= new Vector3();
        target.getLocalPosition(rayEnd);
        float dst = camera.position.dst(rayEnd);
        rayEnd = ray.getEndPoint(rayEnd,dst);

        if(initScale){
            initScale = false;
            lastPos.set(rayEnd);
        }
        boolean modified = false;
        Vector3 vec = new Vector3(0,0,0);

        if(state==TransformState.X){
            vec.set(1 + rayEnd.x - lastPos.x , 1 , 1);
            handlesArray[0].tmpScale.add( rayEnd.x - lastPos.x , 0 , 0);
            modified = true;
        } else if(state==TransformState.Y){
            vec.set(1 , 1 + rayEnd.y - lastPos.y , 1);
            modified = true;
        } else if(state==TransformState.Z){
            vec.set(1 , 1 , 1 + rayEnd.z - lastPos.z);
            modified = true;
        } else if(state==TransformState.XYZ){
            vec.set(1 + rayEnd.x - lastPos.x , 1 + rayEnd.y - lastPos.y , 1 + rayEnd.z - lastPos.z);
            float avg = ( vec.x + vec.y + vec.z ) / 3;
            vec.set(avg , avg , avg);
            modified = true;
        }

        //ensure non zero scale
        if (vec.x == 0) vec.x = 1;
        if (vec.y == 0) vec.y = 1;
        if (vec.z == 0) vec.z = 1;

        target.scale(vec);
        target.apply();
        lastPos.set(rayEnd);



    }

    @Override
    public void dispose() {
        for(ScaleHandle handle : handlesArray){
            handle.dispose();
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == 0 && target !=null) {
            interacting = true;
                ScaleHandle handle = (ScaleHandle)picker.pick(viewport,batch,camera,screenX,screenY,handlesArray);
            if (handle != null) {
                state = TransformState.IDLE;
                int id = handle.getId();
                Gdx.input.setInputProcessor(this);
                switch(id) {
                    case Gizmo.X_HANDLE_ID:
                        state = TransformState.X;
                        initScale = true;
                        handle.setColor(COLOR_X_SELECTED);
                        break;
                    case Gizmo.Y_HANDLE_ID:
                        state = TransformState.Y;
                        initScale = true;
                        handle.setColor(COLOR_Y_SELECTED);
                        break;
                    case Gizmo.Z_HANDLE_ID:
                        state = TransformState.Z;
                        initScale = true;
                        handle.setColor(COLOR_Z_SELECTED);
                        break;
                    case Gizmo.XYZ_HANDLE_ID:
                        state = TransformState.XYZ;
                        initScale = true;
                        handle.setColor(COLOR_XYZ_SELECTED);
                        break;
                }
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);
        interacting = false;
        state = TransformState.IDLE;
        for(ScaleHandle handle : handlesArray){
            handle.restoreColor();
            handle.tmpScale.set(0,0,0);
        }
        Gdx.input.setInputProcessor(inputs);

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (state != TransformState.IDLE) return true;


        return super.touchDragged(screenX, screenY, pointer);
    }

    private static class ScaleHandle extends GizmoHandle {

        public ScaleHandle(Model handleModel, int id) {
            super(handleModel, id);
        }

        @Override
        void render(ModelBatch batch){
            batch.render(handle);
        }

        @Override
        public void update(Matrix4 target, Camera cam){
            target.getTranslation(position);
            float dst = cam.position.dst(position);
            scl = dst*scaleFactor;
            Vector3 sclamnt = new Vector3(scl,scl,scl).add(tmpScale);


            setScale(new Vector3(scl,scl,scl));
//            System.out.println(handle.nodes.size+"");

            setPosition(position);

//        handle.transform.setToTranslation(position);
            applyTransform();
        }
    }


}
