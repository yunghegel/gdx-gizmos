package org.yunghegel.gdx.gizmo.core;

public enum GizmoType {
    NULL{
        public String toString(){
            return "DISABLED";
        }},
    TRANSLATE,
    ROTATE,
    SCALE,
    COMPASS,


}
