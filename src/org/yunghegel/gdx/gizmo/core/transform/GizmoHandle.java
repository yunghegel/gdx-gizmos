package org.yunghegel.gdx.gizmo.core.transform;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import org.yunghegel.gdx.gizmo.core.GizmoTarget;
import org.yunghegel.gdx.picking.PickerColorEncoder;
import org.yunghegel.gdx.picking.PickerIDAttribute;
import org.yunghegel.gdx.picking.PickerShader;


public abstract class GizmoHandle<T extends GizmoTarget<T>> implements Disposable {

    public Vector3 position,euler,scale;
    public Quaternion rotation;
    public ModelInstance handle;
    public Model handleModel;
    private PickerIDAttribute attr=new PickerIDAttribute();
    public final int id;
    private boolean selected=false;
    private Color startColor;
    public float scaleFactor=0.25f;
    public float scl=0;
    public Vector3 tmpScale=new Vector3(0,0,0);



    public GizmoHandle(Model handleModel,int id){
        this.handleModel = handleModel;
        this.id = id;
        position = new Vector3();
        rotation = new Quaternion();
        euler = new Vector3();
        scale = new Vector3(1,1,1);

        handle = new ModelInstance(handleModel);

        PickerColorEncoder.encodeRaypickColorId(id,attr);
        handle.materials.first().set(attr);
    }

    public void setColor(Color color){
        startColor = getColor();
        handle.materials.first().set(ColorAttribute.createDiffuse(color));
    }

    public void restoreColor(){
        if (startColor == null) {
            return;
        }
        handle.materials.first().set(ColorAttribute.createDiffuse(startColor));

    }

    public Color getColor(){
        ColorAttribute diffuse = (ColorAttribute) handle.materials.first().get(ColorAttribute.Diffuse);
        return diffuse.color;
    }

    public void setPosition(Vector3 position){
        this.position.set(position);
    }

    public void setRotation(Quaternion rotation){
        this.rotation.set(rotation);
    }

    public void setScale(Vector3 scale){
        this.scale.set(scale);
    }

    public void scale(float scale){
        setScale(new Vector3(scl,scl,scl).add(scale));
    }

    public void scale(Vector3 scale){
        setScale(new Vector3(scl,scl,scl).add(scale));
    }

    abstract void render(ModelBatch batch);

    public void update(Matrix4 target, Camera cam){
        target.getTranslation(position);
        float dst = cam.position.dst(position);
        scl = dst*scaleFactor;
        Vector3 sclamnt = new Vector3(scl,scl,scl).add(tmpScale);
        setScale(sclamnt);
        setPosition(position);

//        handle.transform.setToTranslation(position);
        applyTransform();


    }

    public void applyTransform(){
        handle.transform.set(position,rotation,scale);
    }

    public void renderPick(ModelBatch batch){
        batch.render(handle, PickerShader.getInstance());
    }

    @Override
    public void dispose() {
        handleModel.dispose();
    }

    public int getId(){
        return id;
    }
}
