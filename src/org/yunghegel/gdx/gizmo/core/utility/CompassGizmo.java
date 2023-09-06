package org.yunghegel.gdx.gizmo.core.utility;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ArrowShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.yunghegel.gdx.gizmo.core.Gizmo;
import org.yunghegel.gdx.gizmo.core.GizmoTarget;
import org.yunghegel.gdx.gizmo.core.GizmoType;

public class CompassGizmo extends Gizmo {

    public ModelInstance compass;
    ModelInstance compassSphere;
    CompassTarget target;
    SpriteBatch spriteBatch;
    BitmapFont font;
    ShapeRenderer shapeRenderer;

    public boolean useArrows = true;
    public boolean drawSphere = false;




    private float ARROW_LENGTH = 0.06f;
    private float ARROW_THIKNESS = .3f;
    private float ARROW_CAP_SIZE = .15f;
    private float SPHERE_SIZE = 0.13f;
    private int ARROW_DIVISIONS = 15;

    public Vector3 position = new Vector3(.9f,.9f,0);

    Vector3 tempv3 = new Vector3();
    public PerspectiveCamera localCam = new PerspectiveCamera();



    public CompassGizmo(InputMultiplexer inputMultiplexer, ModelBatch batch, Camera camera, Viewport viewport, int toggleKey) {
        super(inputMultiplexer, batch, camera, viewport, toggleKey, GizmoType.COMPASS);
        shapeRenderer = new ShapeRenderer();

        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.translate(position.x,position.y,position.z);
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        localCam.viewportWidth = Gdx.graphics.getWidth();
        localCam.viewportHeight = Gdx.graphics.getHeight();

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("compass", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder.setColor(COLOR_X);
        ArrowShapeBuilder.build(builder,0, 0, 0, ARROW_LENGTH, 0, 0, ARROW_CAP_SIZE,ARROW_THIKNESS,ARROW_DIVISIONS);
        builder.setColor(COLOR_Y);
        ArrowShapeBuilder.build(builder,0, 0, 0, 0, ARROW_LENGTH, 0, ARROW_CAP_SIZE,ARROW_THIKNESS,ARROW_DIVISIONS);
        builder.setColor(COLOR_Z);
        ArrowShapeBuilder.build(builder,0, 0, 0, 0, 0, ARROW_LENGTH, ARROW_CAP_SIZE,ARROW_THIKNESS,ARROW_DIVISIONS);
        compass = new ModelInstance(modelBuilder.end());

        compassSphere = new ModelInstance(modelBuilder.createSphere(SPHERE_SIZE, SPHERE_SIZE, SPHERE_SIZE, 10, 10, new Material(ColorAttribute.createDiffuse(1,1,1,0.2f),new BlendingAttribute(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA,.1f),new IntAttribute(IntAttribute.CullFace,GL20.GL_FRONT_FACE)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));

        compass.transform.setTranslation(position);
        compassSphere.transform.setTranslation(position);

        enabled = true;


    }

    public void setPosition(float x, float y){
        position.x=x;
        position.y=y;
        compass.transform.setTranslation(position);
        compassSphere.transform.setTranslation(position);
    }


    Vector3 projCompassPos = new Vector3();
    @Override
    public void render(ModelBatch batch) {
        if(!enabled)return;


        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);

        if(useArrows){
            renderArrows();
        } else
            renderSquares();
    }

    @Override
    public void enable(GizmoTarget target) {
        if(target instanceof CompassTarget){
            this.target = (CompassTarget) target;
        } else{
            Gdx.app.error("CompassGizmo","Target must be of type CompassTarget");
        }
        enabled = true;
    }

    void renderArrows(){



        batch.begin(localCam);
//        localCam.viewportWidth = Gdx.graphics.getWidth();
//        localCam.viewportHeight = Gdx.graphics.getHeight();
        Gdx.gl.glViewport(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        batch.render(compass);
        if(drawSphere)
            batch.render(compassSphere);
        batch.end();

    }

    void renderSquares(){
        Gdx.gl.glDisable(GL20.GL_BLEND);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        float ARROW_LENGTH = 0.03f;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setProjectionMatrix(camera.combined);
//        shapeRenderer.identity();
        shapeRenderer.getTransformMatrix().getTranslation(tempv3);
        shapeRenderer.getTransformMatrix().set(camera.view);
        shapeRenderer.getTransformMatrix().setTranslation(.9f, .85f,0);

        Vector3 compassPos = new Vector3(Gdx.input.getX(), -Gdx.input.getY(), 0);
//        camera.project(compassPos);
        compassPos.x = compassPos.x / Gdx.graphics.getWidth();
        compassPos.y = compassPos.y / Gdx.graphics.getHeight();
        compassPos.x = compassPos.x * 2 - 1;
        compassPos.y = compassPos.y * 2 - 1;
        compassPos.z = 0;

        Gdx.gl.glLineWidth(2);
        shapeRenderer.setColor(COLOR_X);
        shapeRenderer.line(0f, 0f,0, ARROW_LENGTH, 0, 0);

        shapeRenderer.setColor(COLOR_Y);
        shapeRenderer.line(0, 0, 0, 0f, .04f,0);

        shapeRenderer.setColor(COLOR_Z);
        shapeRenderer.line(0, 0, 0, 0, 0, ARROW_LENGTH);

        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(COLOR_X);
        shapeRenderer.box(ARROW_LENGTH, ARROW_LENGTH/4, -ARROW_LENGTH/4, ARROW_LENGTH/2, -ARROW_LENGTH/2, -ARROW_LENGTH/2);

        shapeRenderer.setColor(COLOR_Y);
        shapeRenderer.box(-ARROW_LENGTH/4, ARROW_LENGTH*2, -ARROW_LENGTH/4, ARROW_LENGTH/2, -ARROW_LENGTH/2, -ARROW_LENGTH/2);
        shapeRenderer.setColor(COLOR_Z);
        shapeRenderer.box(-ARROW_LENGTH/4, ARROW_LENGTH/4, ARROW_LENGTH, ARROW_LENGTH/2, -ARROW_LENGTH/2, -ARROW_LENGTH/2);
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1);
        Gdx.gl.glEnable(GL20.GL_BLEND);
    }

    @Override
    public void disable() {
        enabled = false;

    }


    public static Vector2 vec = new Vector2();

    @Override
    public void update() {

        compass.transform.getTranslation(tempv3);

        compass.transform.set(camera.view);
        compass.transform.setTranslation(tempv3);
        compassSphere.transform.setTranslation(tempv3);
        localCam.viewportHeight= camera.viewportHeight;
        localCam.viewportWidth= camera.viewportWidth;
//        localCam.update();
    }

    @Override
    public void dispose() {
        compass.model.dispose();

    }
}
