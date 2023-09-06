package org.yunghegel.gdx.gizmo.core.transform;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.yunghegel.gdx.gizmo.core.Gizmo;
import org.yunghegel.gdx.gizmo.core.GizmoGroup;
import org.yunghegel.gdx.gizmo.core.GizmoTarget;
import org.yunghegel.gdx.gizmo.core.GizmoType;
import org.yunghegel.gdx.gizmo.graph.Spatial;

import java.util.ArrayList;

public abstract class TransformGizmo extends Gizmo implements GizmoGroup<TransformGizmo> {

    public TransformGizmoTarget target;

    static ArrayList<TransformGizmo> gizmos = new ArrayList<>();

    public TransformGizmo(InputMultiplexer inputMultiplexer, ModelBatch batch, Camera camera, Viewport viewport, int toggleKey, GizmoType type){
        super(inputMultiplexer, batch, camera, viewport, toggleKey,type);
        add(this);

        TransformGizmoTarget target = new TransformGizmoTarget(new Spatial(new Matrix4()));

        setTarget(target);
    }

    @Override
    public void enable(GizmoTarget target){

        disableGroup();
        enabled = true;
        this.target = (TransformGizmoTarget) target;

        inputs.addProcessor(this);

        System.out.println("Gizmo enabled");
    }

    public void setTarget(TransformGizmoTarget target){
        setTarget((GizmoTarget) target);
    }

    @Override
    public void disable(){
        enabled = false;
        inputs.removeProcessor(this);

        System.out.println("Gizmo disabled");
    }

    @Override
    public void add(TransformGizmo gizmo) {
        gizmos.add(gizmo);
    }

    @Override
    public void disableGroup() {
        for(TransformGizmo gizmo : gizmos){
            gizmo.disable();
        }
    }
}
