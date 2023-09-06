package org.yunghegel.gdx.gizmo.core.transform;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public interface TransformTarget {

    void translate(Vector3 vec);

    void rotate(Quaternion quat);

    void scale(Vector3 vec);

    Matrix4 getTransform();

    Vector3 getLocalPosition(Vector3 out);

    void apply();
}
