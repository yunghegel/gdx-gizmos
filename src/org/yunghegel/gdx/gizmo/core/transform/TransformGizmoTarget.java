package org.yunghegel.gdx.gizmo.core.transform;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import org.yunghegel.gdx.gizmo.core.GizmoTarget;

public class TransformGizmoTarget extends GizmoTarget<TransformTarget> {

    public Matrix4 transform;

    /**
     * Optionally provide your own action to apply to the transform supplied by the gizmo
     */
    public interface TransformAction {
        void apply(Matrix4 transform);
    }

    TransformAction action;

    TransformTarget target;


    public TransformGizmoTarget(TransformTarget target){
        super(target);
        this.target = target;
    }

    public TransformGizmoTarget(TransformTarget target, TransformAction action){
        super(target);
        this.target = target;
        this.action = action;
    }

    public void setAction(TransformAction action){
        this.action = action;
    }

    @Override
    public void apply() {
        target.apply();
        if(action != null) action.apply(transform);
    }

    public void translate(Vector3 vec) {
        target.translate(vec);
    }

    public void rotate(Quaternion quat) {
        target.rotate(quat);
    }

    public void scale(Vector3 vec) {
        target.scale(vec);
    }


    public Matrix4 getTransform() {
        return target.getTransform();
    }

    public Vector3 getLocalPosition(Vector3 out) {
        return target.getLocalPosition(out);
    }


}
