package dk.itu.bodysim.environment;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import dk.itu.bodysim.context.EgocentricContextData;
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

        this.attachChild(makeCube("Table", -15f, 0f, 1f, createContextData(ObjectType.PHYSICAL, false)));
        this.attachChild(makeCube("TV", 1f, -2f, 30f, createContextData(ObjectType.MEDIATOR, true)));
        this.attachChild(makeCube("Chair", 0f, 1f, -2f, createContextData(ObjectType.PHYSICAL, true)));
        this.attachChild(makeCube("Dude", 20f, 0f, -4f, createContextData(ObjectType.PHYSICAL, false)));
        this.attachChild(makeFloor());        
        this.attachChild(makeCharacter());
        
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        this.addLight(dl);
    }

    private EgocentricContextData createContextData(final ObjectType type, final boolean canBeMoved) {
        final EgocentricContextData data = new EgocentricContextData();
        data.setType(type);
        data.setCanBeMoved(canBeMoved);
        
        return data;
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
        
        final TerrainQuad terrain = new TerrainQuad("Floor", 129, 1025, null);
        terrain.setMaterial(floorMaterial);
        terrain.setLocalTranslation(0, -4, -5);

        return terrain;
    }

    private Spatial makeCharacter() {
        // load a character from jme3test-test-data
        Spatial golem = assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        golem.scale(0.5f);
        golem.setLocalTranslation(-1.0f, -1.5f, -0.6f);

        // We must add a light to make the model visible
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        golem.addLight(sun);
        return golem;
    }
}
