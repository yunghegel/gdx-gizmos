package org.yunghegel.gdx.gizmo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Vector3;

public class Util {
    private static final MeshPartBuilder.VertexInfo v0 = new MeshPartBuilder.VertexInfo();
    private static final MeshPartBuilder.VertexInfo v1 = new MeshPartBuilder.VertexInfo();

    private static Color[] colors = new Color[] {
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.PURPLE
    };

    public static Color randomColor() {
        return colors[(int) (Math.random() * colors.length)];
    }

    public static Model torus(Material mat, float width, float height, int divisionsU, int divisionsV) {

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("torus", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position, mat);
        // builder.setColor(Ansi.LIGHT_GRAY);

        MeshPartBuilder.VertexInfo curr1 = v0.set(null, null, null, null);
        curr1.hasUV = curr1.hasNormal = false;
        curr1.hasPosition = true;

        MeshPartBuilder.VertexInfo curr2 = v1.set(null, null, null, null);
        curr2.hasUV = curr2.hasNormal = false;
        curr2.hasPosition = true;
        short i1, i2, i3 = 0, i4 = 0;

        int i, j, k;
        double s, t, twopi;
        twopi = 2 * Math.PI;

        for (i = 0; i < divisionsV; i++) {
            for (j = 0; j <= divisionsU; j++) {
                for (k = 1; k >= 0; k--) {
                    s = (i + k) % divisionsV + 0.5;
                    t = j % divisionsU;

                    curr1.position.set(
                            (float) ((width + height * Math.cos(s * twopi / divisionsV))
                                    * Math.cos(t * twopi / divisionsU)),
                            (float) ((width + height * Math.cos(s * twopi / divisionsV))
                                    * Math.sin(t * twopi / divisionsU)),
                            (float) (height * Math.sin(s * twopi / divisionsV)));
                    k--;
                    s = (i + k) % divisionsV + 0.5;
                    curr2.position.set(
                            (float) ((width + height * Math.cos(s * twopi / divisionsV))
                                    * Math.cos(t * twopi / divisionsU)),
                            (float) ((width + height * Math.cos(s * twopi / divisionsV))
                                    * Math.sin(t * twopi / divisionsU)),
                            (float) (height * Math.sin(s * twopi / divisionsV)));
                    // curr2.uv.set((float) s, 0);
                    i1 = builder.vertex(curr1);
                    i2 = builder.vertex(curr2);
                    builder.rect(i4, i2, i1, i3);
                    i4 = i2;
                    i3 = i1;
                }
            }
        }

        return modelBuilder.end();
    }
    public static ModelInstance createAxes(float step) {
        final float GRID_MIN = -100f;
        final float GRID_MAX = 100f;
        final float GRID_STEP = step;
        ModelBuilder modelBuilder = new ModelBuilder();
        Material mat = new Material();
        mat.set(new BlendingAttribute(GL20.GL_SRC_ALPHA , GL20.GL_ONE_MINUS_SRC_ALPHA , .6f));
        Color color = new Color(Color.valueOf("7f7f7f"));
        float lightness = 0.3f;
        float alpha = 0.3f;
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("grid" , GL20.GL_LINES , VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked , mat);
        builder.setColor(color);
        for (float t = GRID_MIN; t <= GRID_MAX; t += GRID_STEP) {
            if (t == 0) continue;
            builder.line(t , 0 , GRID_MIN , t , 0 , GRID_MAX);
            builder.line(GRID_MIN , 0 , t , GRID_MAX , 0 , t);
        }
        builder = modelBuilder.part("axes" , GL20.GL_LINES , VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked , new Material(new BlendingAttribute(GL20.GL_SRC_ALPHA , GL20.GL_ONE_MINUS_SRC_ALPHA , .5f)));
        builder.setColor(Color.RED);
        builder.line(-300 , 0f , 0 , 300 , 0 , 0);
        builder.setColor(Color.GREEN);
        builder.line(0 , 0f , 0 , 0 , 300 , 0);
        builder.setColor(Color.BLUE);
        builder.line(0 , 0f , -300 , 0 , 0 , 300);
        Model axesModel = modelBuilder.end();
        ModelInstance axesInstance = new ModelInstance(axesModel);

        return axesInstance;
    }
    public static ModelInstance createGrid(float step,float alpha){
        final float GRID_MIN = -300f;
        final float GRID_MAX = 300f;
        final float GRID_STEP = step;
        ModelBuilder modelBuilder = new ModelBuilder();
        Material mat = new Material();
        mat.set(new BlendingAttribute(GL20.GL_SRC_ALPHA , GL20.GL_ONE_MINUS_SRC_ALPHA , .5f));

        Color color = new Color(Color.valueOf("7f7f7f"));

        float lightness = 0.3f;

        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("axis" , GL20.GL_LINES , VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.ColorUnpacked, mat);

        //first create colored axis lines at the origin
        builder.setColor(Color.RED);
        builder.line(0 , 0f , 0 , 300 , 0 , 0);
        builder.setColor(Color.GREEN);
        builder.line(0 , 0f , 0 , 0 , 300 , 0);
        builder.setColor(Color.BLUE);
        builder.line(0 , 0f , 0 , 0 , 0 , 300);

        builder = modelBuilder.part("grid" , GL20.GL_LINES , VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked , new Material());

        //then create the grid lines,skip the lines that are on the axis
        builder.setColor(color);
        for (float t = GRID_MIN; t <= GRID_MAX; t += GRID_STEP) {
            if (t == 0) continue;
            builder.line(t , 0 , GRID_MIN , t , 0 , GRID_MAX);
            builder.line(GRID_MIN , 0 , t , GRID_MAX , 0 , t);
        }


        Model axesModel = modelBuilder.end();
        ModelInstance axesInstance = new ModelInstance(axesModel);

        return axesInstance;
    }

    public static Model createArrowStub(Material mat , Vector3 from , Vector3 to) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder meshBuilder;
        // line
        meshBuilder = modelBuilder.part("line" , GL20.GL_LINES , VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates |VertexAttributes.Usage.Normal, mat);
        meshBuilder.line(from.x , from.y , from.z , to.x , to.y , to.z);
        //rectangular prism
        Node node1 = modelBuilder.node();
//        node1.translation.set(to.cpy().sub(from).scl(0.5f).add(from));
        node1.translation.set(from.cpy().add(to).scl(0.5f));
        meshBuilder = modelBuilder.part("rectangularPrism" , GL20.GL_TRIANGLES , VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates |VertexAttributes.Usage.Normal, mat);
        BoxShapeBuilder.build(meshBuilder , 0.08f/8+to.x , 0.08f/8+to.y , 0.08f/8+to.z);


        // stub
        Node node2 = modelBuilder.node();
        node2.translation.set(to.x , to.y , to.z);
        meshBuilder = modelBuilder.part("stub" , GL20.GL_TRIANGLES , VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal , mat);
        BoxShapeBuilder.build(meshBuilder , 0.025f , 0.0251f , 0.025f);
        return modelBuilder.end();
    }

    public static Vector3 convertNormalizedScreenSpaceToScreenCoordinates(Vector3 screenSpaceCoordinates) {
        Vector3 screenCoordinates = new Vector3();
        screenCoordinates.x = (screenSpaceCoordinates.x + 1) *  Gdx.graphics.getWidth() / 2;
        screenCoordinates.y = (screenSpaceCoordinates.y + 1) * Gdx.graphics.getHeight() / 2;
        screenCoordinates.z = screenSpaceCoordinates.z;
        return screenCoordinates;
    }
}
