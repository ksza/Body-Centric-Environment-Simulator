package dk.itu.bodysim.agent;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
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
import dk.itu.bodysim.EgocentricApp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author kszanto
 */
public class FirstPersonAgentAppState extends AbstractAppState implements ActionListener {

    private CharacterControl characterControl;
    private boolean left = false, right = false, up = false, down = false;
    private Vector3f agentWalkDirection = new Vector3f();

    /* Temporary vectors used on each frame. 
     * They here to avoid instanciating new vectors on each frame!
     */
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    private Node rootNode;
    private EgocentricApp app;
    private Camera cam;
    private AppStateManager stateManager;
    private AssetManager assetManager;
    private final PhysicsSpace physicsSpace;

    private Geometry mark;
    
    public FirstPersonAgentAppState(PhysicsSpace physicsSpace) {
        this.physicsSpace = physicsSpace;
    }

    /**
     * We set up collision detection for the player by creating a capsule
     * collision shape and a CharacterControl. The CharacterControl offers extra
     * settings for size, stepheight, jumping, falling, and gravity. We also put
     * the player in its starting position.
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); //To change body of generated methods, choose Tools | Templates.

        this.app = (EgocentricApp) app;
        this.cam = app.getCamera();
        this.stateManager = stateManager;
        this.assetManager = app.getAssetManager();
        this.rootNode = this.app.getRootNode();
        
        characterControl = new CharacterControl(new CapsuleCollisionShape(1.5f, 6f, 1), 0.05f);

        characterControl.setJumpSpeed(20);
        characterControl.setFallSpeed(30);
        characterControl.setGravity(30);
        characterControl.setPhysicsLocation(new Vector3f(0, 30, 30));

        physicsSpace.add(characterControl);

        initKeys(app.getInputManager());    
        initCrossHairs(); // a "+" in the middle of the screen to help aiming
        initMark();       // a red sphere to mark the hit

    }

    /**
     * A centred plus sign to help the player aim.
     */
    protected void initCrossHairs() {
        app.setDisplayStatView(false);
        final BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(font, false);
        ch.setSize(font.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
                app.getSettings().getWidth() / 2 - ch.getLineWidth() / 2, app.getSettings().getHeight() / 2 + ch.getLineHeight() / 2, 0);
        app.getGuiNode().attachChild(ch);
    }

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
     * Initialize the keys used to control this agent
     *
     * @param inputManager the apps input manager instance
     */
    private void initKeys(final InputManager inputManager) {

                inputManager.addMapping("Shoot",
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // left-button click
        inputManager.addListener(shootListener, "Shoot");
        
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Jump");
    }

    /**
     * These are our custom actions triggered by key presses. We do not walk
     * yet, we just keep track of the direction the user pressed.
     */
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("Left")) {
            left = isPressed;
        } else if (name.equals("Right")) {
            right = isPressed;
        } else if (name.equals("Up")) {
            up = isPressed;
        } else if (name.equals("Down")) {
            down = isPressed;
        } else if (name.equals("Jump")) {
            if (isPressed) {
                characterControl.jump();
            }
        }
    }

    /**
     * Should be called from application's simple update method! Actual walking
     * happens here. We check in which direction the player is walking by
     * interpreting the camera direction forward (camDir) and to the side
     * (camLeft). The setWalkDirection() command is what lets a
     * physics-controlled player walk. We also make sure here that the camera
     * moves with player.
     */
    @Override
    public void update(float tpf) {
        super.update(tpf);

        camDir.set(cam.getDirection()).multLocal(0.6f);
        camLeft.set(cam.getLeft()).multLocal(0.4f);
        agentWalkDirection.set(0, 0, 0);
        if (left) {
            agentWalkDirection.addLocal(camLeft);
        }
        if (right) {
            agentWalkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            agentWalkDirection.addLocal(camDir);
        }
        if (down) {
            agentWalkDirection.addLocal(camDir.negate());
        }
        characterControl.setWalkDirection(agentWalkDirection);
        cam.setLocation(characterControl.getPhysicsLocation());
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
                app.getEnvironmentScene().collideWith(ray, results);
                // 4. Print the results

                /* !! ALL Visible items! */
                int x = 0;
                for (Spatial g : app.getEnvironmentScene().getChildren()) {
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
}
