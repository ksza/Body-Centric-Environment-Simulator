package dk.itu.bodysim.environment;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import dk.itu.bodysim.context.EgocentricContextData;
import dk.itu.bodysim.context.InteractionType;
import dk.itu.bodysim.context.ObjectType;

/**
 * A really simple scene resembling the structure of a room. It comprises of
 * floor, walls and several items placed arround the room.
 *
 * @author kszanto
 */
public class SimpleEnvironment extends Node {

    private static final String SCENE_NAME = "SIMPLE_ENVIRONMENT";
    private final AssetManager assetManager;

    public SimpleEnvironment(final AssetManager assetManager) {

        super(SCENE_NAME);

        this.assetManager = assetManager;

//        this.attachChild(makeCube("Table", -2f, 0f, 1f, createContextData(ObjectType.PHYSICAL, false)));
//        this.attachChild(makeCube("TV", 1f, -2f, 0f, createContextData(ObjectType.MEDIATOR, true)));
//        this.attachChild(makeCube("Chair", 0f, 1f, -2f, createContextData(ObjectType.PHYSICAL, true)));
//        this.attachChild(makeCube("Dude", 1f, 0f, -4f, createContextData(ObjectType.PHYSICAL, false)));
//        this.attachChild(makeFloor());        
//        this.attachChild(makeCharacter());
        final Geometry table1 = makeTable(false, "Table1", 0, 0, -30, ColorRGBA.Blue, createContextData("Table1", ObjectType.PHYSICAL, 30, 500, 500, 200));
        this.attachChild(table1);
        
        final EgocentricContextData penData = createContextData("Pen", ObjectType.PHYSICAL, 0.05f, 200, 80, 50);
        penData.setSurface(false);        
        this.attachChild(makeCylinder("Pen", -1, 2.1f, -30.1f, ColorRGBA.Red, penData));
        
        final EgocentricContextData statueData = createContextData("Statue", ObjectType.PHYSICAL, 1, 350, 150, 70);
        statueData.setSurface(false);  
        statueData.setInteractionType(InteractionType.CUSTOM);
        this.attachChild(makeCharacter(2f, 3f, -30.5f, statueData));
        
        this.attachChild(makeFloor());
        final Geometry table2 = makeTable(true, "Table2", 50, 0, 10, ColorRGBA.Blue, createContextData("Table2", ObjectType.PHYSICAL, 30, 500, 500, 200));
        this.attachChild(table2);
        
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        this.addLight(dl);
    }

    private EgocentricContextData createContextData(final String id, final ObjectType type, final float weight, final float perceptionDistance, final float recognitionDistance, final float examinationDistance) {
        final EgocentricContextData data = new EgocentricContextData();

        data.setId(id);
        data.setType(type);
        data.setPerceptionDistance(perceptionDistance);
        data.setExaminationDistance(examinationDistance);
        data.setRecognitionDistance(recognitionDistance);
        data.setWeight(weight);

        return data;
    }

    private Geometry makeCylinder(String name, float x, float y, float z, ColorRGBA color, EgocentricContextData data) {
        Cylinder cylinder = new Cylinder(30, 30, 0.1f, 1);

        Geometry cylinderGeom = new Geometry(name, cylinder);
        cylinderGeom.setLocalTranslation(x, y, z);
        
        cylinderGeom.setModelBound(new BoundingBox());
        /* Create a 90-degree-pitch Quaternion. */
        Quaternion pitch90 = new Quaternion();
        pitch90.fromAngleAxis(FastMath.PI/2, new Vector3f(0,1,0));
        /* Apply the rotation to the object */
        cylinderGeom.setLocalRotation(pitch90);
        /* Update the model. Now it's vertical. */
        cylinderGeom.updateModelBound();
        cylinderGeom.updateGeometricState();
        
        Material mat1 = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", color);
        cylinderGeom.setMaterial(mat1);
        cylinderGeom.setUserData(EgocentricContextData.TAG, data);
        return cylinderGeom;
    }
    
    private Geometry makeTable(boolean rotated, String name, float x, float y, float z, ColorRGBA color, EgocentricContextData data) {
        Box box = rotated ? new Box(2, 2, 4) : new Box(4, 2, 2);
        Geometry cube = new Geometry(name, box);
        cube.setLocalTranslation(x, y, z);
        Material mat_brick = new Material(
                assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_brick.setTexture("ColorMap",
                assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg"));
        cube.setMaterial(mat_brick);
        cube.setUserData(EgocentricContextData.TAG, data);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1, 0, -2).normalizeLocal());
        sun.setColor(ColorRGBA.White);

        return cube;
    }

    private Geometry makeSphere(String name, float x, float y, float z, ColorRGBA color, EgocentricContextData data) {
        Sphere sphere = new Sphere(30, 30, 0.1f);
        Geometry sphereGeom = new Geometry(name, sphere);
        sphereGeom.setLocalTranslation(x, y, z);
        Material mat1 = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", color);
        sphereGeom.setMaterial(mat1);
        sphereGeom.setUserData(EgocentricContextData.TAG, data);
        return sphereGeom;
    }

    private Geometry makeCube(String name, float x, float y, float z, EgocentricContextData data) {
        Box box = new Box(1, 1, 1);
        Geometry cube = new Geometry(name, box);
        cube.setLocalTranslation(x, y, z);
        Material mat1 = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.randomColor());
        cube.setMaterial(mat1);
        cube.setUserData(EgocentricContextData.TAG, data);
        return cube;
    }

    private Spatial makeFloor() {

        final Material floorMaterial = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
        floorMaterial.setTexture("Alpha", assetManager.loadTexture(
                "Textures/Terrain/splat/alphamap.png"));

        final Texture floorTexture = assetManager.loadTexture("Textures/floor_wood.jpg");
        floorTexture.setWrap(WrapMode.Repeat);

        floorMaterial.setTexture("Tex1", floorTexture);
        floorMaterial.setTexture("Tex2", floorTexture);
        floorMaterial.setTexture("Tex3", floorTexture);

        final TerrainQuad terrain = new TerrainQuad("Floor", 65, 513, null);
        terrain.setMaterial(floorMaterial);
        terrain.setLocalTranslation(0, -4, -5);

        return terrain;
    }

    private Spatial makeCharacter(final float x, final float y, final float z, final EgocentricContextData data) {
        // load a character from jme3test-test-data
        Spatial golem = assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        golem.scale(0.2f);
        golem.setLocalTranslation(x, y, z);

        golem.setUserData(EgocentricContextData.TAG, data);

        // We must add a light to make the model visible
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        golem.addLight(sun);
        return golem;
    }
}
