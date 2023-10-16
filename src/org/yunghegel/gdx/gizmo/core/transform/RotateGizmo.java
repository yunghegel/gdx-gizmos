package org.yunghegel.gdx.gizmo.core.transform;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.yunghegel.gdx.gizmo.Util;
import org.yunghegel.gdx.gizmo.core.Gizmo;
import org.yunghegel.gdx.gizmo.core.GizmoTarget;
import org.yunghegel.gdx.gizmo.core.GizmoType;

public class RotateGizmo extends TransformGizmo {

    private final RotateHandle xHandle,yHandle,zHandle;
    private final RotateHandle[] handles = new RotateHandle[3];

    private Vector3 a = new Vector3();
    private Vector3 b = new Vector3();
    private Vector3 localPosition = new Vector3();
    private Vector3 position = new Vector3();
    private Matrix4 tmp = new Matrix4();
    private Quaternion tmpQ = new Quaternion();
    private Quaternion tmpQ2 = new Quaternion();

    public float degree = 0;
    public float rot = 0;
    public float lastRot = 0;
    double dst = 0;

    public boolean initRotate = true;


    public RotateGizmo(InputMultiplexer inputMultiplexer, ModelBatch batch, Camera camera, Viewport viewport,int toggleKey) {
        super(inputMultiplexer, batch, camera, viewport, toggleKey, GizmoType.ROTATE);

        Model xHandle = Util.torus(new Material(ColorAttribute.createDiffuse(COLOR_X)), .5f, 0.008f, 50, 50);
        Model yHandle = Util.torus(new Material(ColorAttribute.createDiffuse(COLOR_Y)), .5f, 0.008f, 50, 50);
        Model zHandle = Util.torus(new Material(ColorAttribute.createDiffuse(COLOR_Z)), .5f, 0.008f, 50, 50);

        this.xHandle = new RotateHandle(xHandle,X_HANDLE_ID);
        this.yHandle = new RotateHandle(yHandle,Y_HANDLE_ID);
        this.zHandle = new RotateHandle(zHandle,Z_HANDLE_ID);

        handles[0] = this.xHandle;
        handles[1] = this.yHandle;
        handles[2] = this.zHandle;

    }

    @Override
    public void render(ModelBatch batch) {
        if(!enabled) return;
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        batch.begin(camera);



        for (RotateHandle handle : handles) {
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
        for (RotateHandle handle : handles) {
            handle.update(target.getTransform(),camera);
        }

        calculateAngle();
        if (initRotate){
            initRotate = false;
        }

        rot = degree - lastRot;

        if(state == TransformState.X) {
            tmpQ.setEulerAngles(0,-rot,0);
            target.rotate(tmpQ);
        }
        else if(state == TransformState.Y) {
            tmpQ.setEulerAngles(rot,0,0);
            target.rotate(tmpQ);
        }
        else if(state == TransformState.Z) {
            tmpQ.setEulerAngles(0,0,-rot);
            target.rotate(tmpQ);
        }

        target.getTransform();
        target.apply();

        lastRot = degree;

    }

    public float calculateAngle(){
        Ray ray = viewport.getPickRay(Gdx.input.getX(), Gdx.input.getY());

        target.target.getLocalPosition(position);

        a = camera.project(position);
        b = camera.project(ray.origin);
        degree = (float)Math.toDegrees(Math.atan2(b.x-a.x, b.y-a.y));
        if (degree<0){
            degree = 360+degree;
        }
        return degree;
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == 0 && target !=null) {
            interacting = true;
            RotateHandle handle = (RotateHandle) picker.pick(viewport,batch,camera,screenX,screenY,handles);
            if (handle != null) {
                state = TransformState.IDLE;
                int id = handle.getId();
                Gdx.input.setInputProcessor(this);
                switch(id) {
                    case Gizmo.X_HANDLE_ID:
                        state = TransformState.X;
                        initRotate = true;
                        handle.setColor(COLOR_X_SELECTED);
                        break;
                    case Gizmo.Y_HANDLE_ID:
                        state = TransformState.Y;
                        initRotate = true;
                        handle.setColor(COLOR_Y_SELECTED);
                        break;
                    case Gizmo.Z_HANDLE_ID:
                        state = TransformState.Z;
                        initRotate = true;
                        handle.setColor(COLOR_Z_SELECTED);
                        break;
                }
            }
        }
        System.out.println(state);
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        state = TransformState.IDLE;
        for(RotateHandle handle : handles){
            handle.restoreColor();
        }
        interacting = false;
        Gdx.input.setInputProcessor(inputs);
        return super.touchUp(screenX, screenY, pointer, button);
    }

    private static class RotateHandle extends GizmoHandle {

        public RotateHandle(Model handleModel, int id) {
            super(handleModel, id);
        }

        @Override
        void render(ModelBatch batch) {
            batch.render(handle);
        }

        @Override
        public void update(Matrix4 target, Camera cam) {
            target.getTranslation(position);
            float dst = cam.position.dst(position);
            float scl = dst*scaleFactor;
            setScale(new Vector3(scl,scl,scl));
            setPosition(position);
            switch (id) {
                case X_HANDLE_ID:
                    this.euler.y = 90;
                    break;
                case Y_HANDLE_ID:
                    this.euler.x = 90;
                    break;
                case Z_HANDLE_ID:
                    this.euler.z = 90;
                    break;
            }
            rotation.setEulerAngles(euler.x,euler.y,euler.z);
            handle.transform.set(position,rotation,scale);
        }

//        @Override
//        public void update(Supplier<Matrix4> target, Camera cam) {
//            target.get().getTranslation(position);
//            float dst = cam.position.dst(position);
//            float scl = dst*scaleFactor;
//            setScale(new Vector3(scl,scl,scl));
//            setPosition(position);
//            switch (id) {
//                case X_HANDLE_ID:
//                    this.euler.y = 90;
//                    break;
//                case Y_HANDLE_ID:
//                    this.euler.x = 90;
//                    break;
//                case Z_HANDLE_ID:
//                    this.euler.z = 90;
//                    break;
//            }
//            rotation.setEulerAngles(euler.x,euler.y,euler.z);
//            handle.transform.set(position,rotation,scale);
//
//
//
//
//        }
    }

}
