package org.yunghegel.gdx.gizmo.core;

public abstract class GizmoTarget<T> {

    public T target;

    public GizmoTarget(T target){
        this.target = target;
    }

    public void setTarget(T target){
        this.target = target;
    }

    public abstract void apply();


}
