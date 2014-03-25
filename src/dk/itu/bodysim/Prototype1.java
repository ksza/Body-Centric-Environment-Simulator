package dk.itu.bodysim;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import dk.itu.bodysim.agent.FirstPersonAgent;
import dk.itu.bodysim.environment.SimpleEnvironment;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author kszanto
 */
public class Prototype1 extends SimpleApplication {

    private FirstPersonAgent agent;
    private Node environmentScene;
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;

    public static void main(String[] args) {
        Prototype1 app = new Prototype1();
        app.start();
    }
    private Geometry mark;

    @Override
    public void simpleInitApp() {
        /**
         * Set up Physics
         */
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.getPhysicsSpace().enableDebug(assetManager);

        // We re-use the flyby camera for rotation, while positioning is handled by physics
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        flyCam.setMoveSpeed(100);

        agent = new FirstPersonAgent();

        initCrossHairs(); // a "+" in the middle of the screen to help aiming
        initKeys();       // load custom key mappings
        initMark();       // a red sphere to mark the hit
        environmentScene = new SimpleEnvironment(assetManager);

        
        // We set up collision detection for the scene by creating a
        // compound collision shape and a static RigidBodyControl with mass zero.
        CollisionShape sceneShape =
                CollisionShapeFactory.createMeshShape(environmentScene);
        landscape = new RigidBodyControl(sceneShape, 0);
        environmentScene.addControl(landscape);

        // We attach the scene and the player to the rootnode and the physics space,
        // to make them appear in the game world.
        rootNode.attachChild(environmentScene);
        bulletAppState.getPhysicsSpace().add(landscape);
        bulletAppState.getPhysicsSpace().add(agent);
    }

    private void computeWorldSpace(final Node node, final Set<Spatial> worldSpace) {

        for (Spatial element : node.getChildren()) {
//            if (g.getLastFrustumIntersection() == Camera.FrustumIntersect.Outside) {
//            } else {
//                
//            }

            if (element.getCullHint() != Spatial.CullHint.Never) {

                final List<Spatial> children = node.getChildren();
                if (children != null && children.size() > 0) {

                    for (final Spatial child : children) {

                        if (Geometry.class.isAssignableFrom(child.getClass())) {
                            worldSpace.add(element);
                        } else if (Node.class.isAssignableFrom(child.getClass())) {
                            computeWorldSpace((Node) child, worldSpace);
                        }
                    }
                }
            }
        }
    }

    /**
     * Declaring the "Shoot" action and mapping to its triggers.
     */
    private void initKeys() {
        inputManager.addMapping("Shoot",
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // left-button click
        inputManager.addListener(shootListener, "Shoot");

        agent.initKeys(inputManager);
    }
    /**
     * Defining the "Shoot" action: Determine what was hit and how to respond.
     */
    private ActionListener shootListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Shoot") && !keyPressed) {

                final Set<Spatial> worldSpace = new HashSet<Spatial>();
                computeWorldSpace(rootNode, worldSpace);
                for (final Spatial element : worldSpace) {
                    System.out.println("--> " + element.getName());
                }

                // 1. Reset results list.
                CollisionResults results = new CollisionResults();
                // 2. Aim the ray from cam loc to cam direction.
                Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                // 3. Collect intersections between Ray and Shootables in results list.
                environmentScene.collideWith(ray, results);
                // 4. Print the results

                /* !! ALL Visible items! */
                int x = 0;
                for (Spatial g : environmentScene.getChildren()) {
                    if (g.getLastFrustumIntersection() == Camera.FrustumIntersect.Outside) {
                    } else {
                        x++;
                    }
                }

//                System.out.println(x + "  ----- Collisions? " + results.size() + "-----");
                for (int i = 0; i < results.size(); i++) {
                    // For each hit, we know distance, impact point, name of geometry.
                    float dist = results.getCollision(i).getDistance();
                    Vector3f pt = results.getCollision(i).getContactPoint();
                    String hit = results.getCollision(i).getGeometry().getName();
//                    System.out.println("* Collision #" + i);
//                    System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
                }
                // 5. Use the results (we mark the hit object)
                if (results.size() > 0) {
                    // The closest collision point is what was truly hit:
                    CollisionResult closest = results.getClosestCollision();
                    // Let's interact - we mark the hit with a red dot.
                    mark.setLocalTranslation(closest.getContactPoint());
                    rootNode.attachChild(mark);
                } else {
                    // No hits? Then remove the red mark.
                    rootNode.detachChild(mark);
                }
            }
        }
    };

    /**
     * A red ball that marks the last spot that was "hit" by the "shot".
     */
    protected void initMark() {
        Sphere sphere = new Sphere(30, 30, 0.2f);
        mark = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
    }

    /**
     * A centred plus sign to help the player aim.
     */
    protected void initCrossHairs() {
        setDisplayStatView(false);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
                settings.getWidth() / 2 - ch.getLineWidth() / 2, settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
    }

    @Override
    public void simpleUpdate(float tpf) {
        agent.onUpdate(cam, tpf);
    }
}