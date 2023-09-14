package org.yunghegel.gdx.gizmo.core;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.yunghegel.gdx.gizmo.ToolPicker;


public abstract class Gizmo extends InputAdapter implements Disposable {

    protected static Color COLOR_X = new Color(.8f,0,0,1);
    protected static Color COLOR_Y = new Color(0,.8f,0,1);
    protected static Color COLOR_Z = Color.valueOf("#0074F7");
    protected static Color COLOR_XYZ = new Color(0,.8f,.8f,1);

    protected static Color COLOR_X_SELECTED = Color.RED;
    protected static Color COLOR_Y_SELECTED = Color.GREEN;
    protected static Color COLOR_Z_SELECTED = Color.BLUE;
    protected static Color COLOR_XYZ_SELECTED = Color.CYAN;


    protected static final int X_HANDLE_ID = 1;
    protected static final int Y_HANDLE_ID = 2;
    protected static final int Z_HANDLE_ID = 3;
    protected static final int XYZ_HANDLE_ID = 4;

    public static int TOGGLE_KEY=-1;



    public GizmoTarget target;

    public enum TransformState{
        X,Y,Z,XYZ,IDLE
    }

    public TransformState state = TransformState.IDLE;
    public ToolPicker picker = new ToolPicker();

    protected InputMultiplexer inputs;
    protected ModelBatch batch;
    protected Camera camera;
    protected Viewport viewport;
    private GizmoType type;

    public boolean enabled=false;

    protected Array<InputProcessor> tmp = new Array<>();
    protected InputProcessor[] processors;

    private int toggleKey;

    public Gizmo(InputMultiplexer inputMultiplexer, ModelBatch batch, Camera camera, Viewport viewport, int toggleKey, GizmoType type){
        this.inputs = inputMultiplexer;
        this.batch = batch;
        this.camera = camera;
        this.viewport = viewport;
        this.toggleKey = toggleKey;
        this.type = type;
    }

    public GizmoType getType(){
        return type;
    }

    public abstract void render(ModelBatch batch);

    public abstract void enable(GizmoTarget target);

    public void enable(){
        enabled = true;
    }

    public void setTarget(GizmoTarget target){
        this.target = target;
    }

    public abstract void disable();

    public abstract void update();

    public void toggle(GizmoTarget target){
        if(enabled){
            disable();
            target = null;
        }else{
            enable(target);
        }
    }

    public void setToggleKey(int toggleKey){
        this.toggleKey = toggleKey;
    }

    public int getToggleKey(){
        return toggleKey;
    }







}
