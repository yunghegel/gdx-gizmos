package org.yunghegel.gdx.gizmo.core;


public enum GizmoCategory{
    TRANSFORM {
        public GizmoType[] getTypes(){
            return new GizmoType[]{GizmoType.TRANSLATE, GizmoType.ROTATE, GizmoType.SCALE, GizmoType.NULL};

            }
        },
    UTILITY{
        public GizmoType[] getTypes(){
            return new GizmoType[]{GizmoType.COMPASS};
            }
        };


    public abstract GizmoType[] getTypes();
}

