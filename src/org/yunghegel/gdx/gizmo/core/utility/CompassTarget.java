package org.yunghegel.gdx.gizmo.core.utility;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import org.yunghegel.gdx.gizmo.core.GizmoTarget;

public class CompassTarget extends GizmoTarget<PerspectiveCamera> {

    float xPos;
    float yPos;

    public CompassTarget(PerspectiveCamera target,float xPos,float yPos) {
        super(target);
        this.xPos = xPos;
        this.yPos = yPos;
    }

    @Override
    public void apply() {

    }
}
