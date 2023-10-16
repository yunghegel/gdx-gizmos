package org.yunghegel.gdx.gizmo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.yunghegel.gdx.gizmo.core.Gizmo;
import org.yunghegel.gdx.gizmo.core.GizmoCategory;
import org.yunghegel.gdx.gizmo.core.GizmoTarget;
import org.yunghegel.gdx.gizmo.core.GizmoType;
import org.yunghegel.gdx.gizmo.core.transform.RotateGizmo;
import org.yunghegel.gdx.gizmo.core.transform.ScaleGizmo;
import org.yunghegel.gdx.gizmo.core.transform.TransformGizmoTarget;
import org.yunghegel.gdx.gizmo.core.transform.TranslateGizmo;
import org.yunghegel.gdx.gizmo.core.utility.CompassGizmo;

import java.util.EnumMap;

public class GizmoManager {

    public int TOGGLE_TRANSLATE_GIZMO_KEY = Input.Keys.T;
    public int TOGGLE_ROTATE_GIZMO_KEY = Input.Keys.R;
    public int TOGGLE_SCALE_GIZMO_KEY = Input.Keys.S;
    public int TOGGLE_COMPASS_GIZMO_KEY = -1;

    private TransformGizmoTarget target;
    private GizmoTarget gizmoTarget;
    private InputMultiplexer inputMultiplexer;

    private TranslateGizmo translateGizmo;
    private RotateGizmo rotateGizmo;
    private ScaleGizmo scaleGizmo;
    private CompassGizmo compassGizmo;
    private ModelBatch batch = new ModelBatch();

    private Gizmo[] gizmos;
    private EnumMap<GizmoType, Gizmo> gizmoMap = new EnumMap<>(GizmoType.class);

    private Viewport viewport;


    public GizmoManager(InputMultiplexer inputMultiplexer, Camera camera, Viewport viewport){
        this.inputMultiplexer = inputMultiplexer;
        this.viewport = viewport;

        translateGizmo = new TranslateGizmo(inputMultiplexer, batch, camera, viewport, TOGGLE_TRANSLATE_GIZMO_KEY);
        rotateGizmo = new RotateGizmo(inputMultiplexer, batch, camera, viewport, TOGGLE_ROTATE_GIZMO_KEY);
        scaleGizmo = new ScaleGizmo(inputMultiplexer, batch, camera, viewport,TOGGLE_SCALE_GIZMO_KEY);
        compassGizmo = new CompassGizmo(inputMultiplexer, batch, camera, viewport,TOGGLE_COMPASS_GIZMO_KEY);

        gizmos = new Gizmo[]{translateGizmo, rotateGizmo, scaleGizmo};
        gizmoMap.put(GizmoType.NULL, null);

        for(Gizmo gizmo : gizmos){
            gizmoMap.put(gizmo.getType(), gizmo);
        }
    }

    public GizmoManager(InputMultiplexer inputMultiplexer, Camera camera, Viewport viewport, ModelBatch batch){
        this.inputMultiplexer = inputMultiplexer;
        this.batch = batch;
        this.viewport = viewport;

        translateGizmo = new TranslateGizmo(inputMultiplexer, batch, camera, viewport, TOGGLE_TRANSLATE_GIZMO_KEY);
        rotateGizmo = new RotateGizmo(inputMultiplexer, batch, camera, viewport, TOGGLE_ROTATE_GIZMO_KEY);
        scaleGizmo = new ScaleGizmo(inputMultiplexer, batch, camera, viewport,TOGGLE_SCALE_GIZMO_KEY);
        compassGizmo = new CompassGizmo(inputMultiplexer, batch, camera, viewport,TOGGLE_COMPASS_GIZMO_KEY);

        gizmos = new Gizmo[]{translateGizmo, rotateGizmo, scaleGizmo};
        gizmoMap.put(GizmoType.NULL, null);

        for(Gizmo gizmo : gizmos){
            gizmoMap.put(gizmo.getType(), gizmo);
        }
    }

    public Gizmo getGizmo(GizmoType type){
        return gizmoMap.get(type);
    }

    public Gizmo[] getGizmos(){
        return gizmos;
    }

    public void setTransformGizmoTarget(TransformGizmoTarget target){
        this.target = target;
        setTarget(target, GizmoType.TRANSLATE, GizmoType.ROTATE, GizmoType.SCALE);
    }

    public void setTarget(GizmoType gizmoType, GizmoTarget target){
        gizmoTarget = target;
        if(target instanceof TransformGizmoTarget){
            this.target = (TransformGizmoTarget) target;
        }

        switch (gizmoType)
        {
            case TRANSLATE:
                translateGizmo.setTarget(target);
                translateGizmo.setTarget(target);
                break;
            case ROTATE:
                rotateGizmo.setTarget(target);
                break;
            case SCALE:
                scaleGizmo.setTarget(target);
                break;

        }
    }

    public void setTarget(GizmoTarget target,GizmoType... types){
        for(GizmoType type : types){
            setTarget(type, target);
        }
    }



    public void update(){

        for(Gizmo gizmo : gizmos){
            if(gizmo.enabled){
                gizmo.update(gizmoTarget);
            }
        }
    }

    public void render(){
        for(Gizmo gizmo : gizmos){
            if(gizmo.enabled){
                gizmo.render(batch);
            }
        }

    }

    public void render(ModelBatch batch){
        for(Gizmo gizmo : gizmos){
            if(gizmo.enabled){
                gizmo.render(batch);
            }
        }
    }

    public boolean transformGizmosIdle(){
        return translateGizmo.state== Gizmo.TransformState.IDLE && rotateGizmo.state== Gizmo.TransformState.IDLE && scaleGizmo.state== Gizmo.TransformState.IDLE;
    }



    public void toggleGizmo(GizmoType gizmoType){
        switch(gizmoType){
            case TRANSLATE:
                translateGizmo.toggle(target);
                break;
            case ROTATE:
                rotateGizmo.toggle(target);
                break;
            case SCALE:
                scaleGizmo.toggle(target);
                break;


        }
    }

    public void processInput(){
        if(target != null){
            for(Gizmo gizmo : gizmos){
                if(Gdx.input.isKeyJustPressed(gizmo.getToggleKey())){
                    gizmo.toggle(target);
                }
            }
        }
    }

    public void dispose(){
        for(Gizmo gizmo : gizmos){
            gizmo.dispose();
        }
    }

    public void disableAll(){
        for(Gizmo gizmo : gizmos){
            gizmo.disable();
        }
    }

    public void assignToggleKey(GizmoType type,int key){
        switch(type){
            case TRANSLATE:
                translateGizmo.setToggleKey(key);
                break;
            case ROTATE:
                rotateGizmo.setToggleKey(key);
                break;
            case SCALE:
                scaleGizmo.setToggleKey(key);
                break;


        }
    }

    public void enableGizmo(GizmoType type,GizmoTarget target){
        switch(type){
            case TRANSLATE:
                translateGizmo.enable(target);
                break;
            case ROTATE:
                rotateGizmo.enable(target);
                break;
            case SCALE:
                scaleGizmo.enable(target);
                break;
                case NULL:
                    disableAll();
        }
    }

    public void disableGizmo(GizmoType type){
        switch(type){
            case TRANSLATE:
                translateGizmo.disable();
                break;
            case ROTATE:
                rotateGizmo.disable();
                break;
            case SCALE:
                scaleGizmo.disable();
                break;
            case COMPASS:
                compassGizmo.disable();
                break;
        }
    }

    public void disableCategory(GizmoCategory category){
        GizmoType[] types = category.getTypes();
        for(GizmoType type : types){
            disableGizmo(type);
        }
    }

    public void setCurrentGizmo(GizmoType type){
        disableAll();
        switch(type){
            case TRANSLATE:
                translateGizmo.enable(target);
                break;
            case ROTATE:
                rotateGizmo.enable(target);
                break;
            case SCALE:
                scaleGizmo.enable(target);
                break;

        }
    }

    public void setIfNotCurrent(GizmoType type){
        if(!gizmoMap.get(type).enabled){
            setCurrentGizmo(type);
        }
    }

    public void setSharedTarget(GizmoTarget target){
        this.gizmoTarget = target;
        for(Gizmo gizmo : gizmos){
            gizmo.setTarget(target);
        }
    }



}
