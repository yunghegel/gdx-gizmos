package org.yunghegel.gdx.gizmo.core.transform;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import org.yunghegel.gdx.gizmo.core.GizmoTarget;
import org.yunghegel.gdx.gizmo.graph.Spatial;

public class TransformGizmoTarget extends GizmoTarget<Spatial> {

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
        this(target.getTransform());
        this.target = target;
    }

    public TransformGizmoTarget(Spatial target){
        super(target);
        transform = target.getTransform();
    }

    public TransformGizmoTarget(Matrix4 target){
        super(new Spatial(target));
        transform = target;
    }

    public TransformGizmoTarget(Vector3 pos){
        super(new Spatial(new Matrix4().setToTranslation(pos)));
        transform = target.getTransform();
    }

    public TransformGizmoTarget(Spatial spatial, TransformAction action){
        super(spatial);
        transform = target.getTransform();
        setAction(action);
    }

    public TransformGizmoTarget(Matrix4 transform, TransformAction action){
        super(new Spatial(transform));
        this.transform = transform;
        setAction(action);
    }

    public TransformGizmoTarget(Vector3 pos, TransformAction action){
        super(new Spatial(new Matrix4().setToTranslation(pos)));
        transform = target.getTransform();
        setAction(action);
    }

    public void setAction(TransformAction action){
        this.action = action;
    }

    @Override
    public void apply() {
        target.apply();
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
