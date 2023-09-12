package org.yunghegel.gdx.gizmo.core.transform;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class BasicTransformTarget implements TransformTarget{

    private static Matrix4 tempMat = new Matrix4();

    private Vector3 localPosition;
    private Quaternion localRotation;
    private Vector3 localScale;
    private Matrix4 combined;

    private Matrix4 parent;


    public BasicTransformTarget(Matrix4 transform){
        localPosition = new Vector3();
        localRotation = new Quaternion();
        localScale = new Vector3(1, 1, 1);
        combined = transform;

        transform.getRotation(localRotation);
        transform.getTranslation(localPosition);
        transform.getScale(localScale);
    }

    public BasicTransformTarget(Matrix4 transform, Matrix4 parent){
        this(transform);
        this.parent = parent;
    }

    @Override
    public void translate(Vector3 vec) {
        localPosition.add(vec);
    }

    @Override
    public void rotate(Quaternion quat) {
        localRotation.mulLeft(quat);
    }

    @Override
    public void scale(Vector3 vec) {
        localScale.scl(vec);
    }

    @Override
    public Matrix4 getTransform() {
        combined.set(localPosition, localRotation, localScale);
        if(parent != null){
            combined.mulLeft(parent);
        }
        return combined;
    }

    @Override
    public Vector3 getLocalPosition(Vector3 out) {
        return out.set(localPosition);
    }

    @Override
    public void apply() {
        getTransform();
    }
}
