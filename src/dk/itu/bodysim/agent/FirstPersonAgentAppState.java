package dk.itu.bodysim.agent;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
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
import dk.itu.bodysim.notifications.NotificationsStateManager;
import dk.itu.bodysim.context.EgocentricContextData;
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
    private Node environment;
    private Geometry mark;
    private Node inventory;

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
        this.environment = this.app.getEnvironmentScene();
        this.cam = app.getCamera();
        this.stateManager = stateManager;
        this.assetManager = app.getAssetManager();
        this.rootNode = this.app.getRootNode();

        inventory = new Node("Inventory");
        this.app.getGuiNode().attachChild(inventory);

        characterControl = new CharacterControl(new CapsuleCollisionShape(1.5f, 6f, 1), 0.05f);

        characterControl.setJumpSpeed(20);
        characterControl.setFallSpeed(30);
        characterControl.setGravity(30);
        characterControl.setPhysicsLocation(this.app.getInitialAgentPosition());

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

        inputManager.addMapping("Pick",
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // left-button click
        inputManager.addListener(pickListener, "Pick");

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

    private void computePerceptionSpace(final Node node, final Set<Spatial> worldSpace) {

        for (Spatial element : node.getChildren()) {

            if (element.getCullHint() != Spatial.CullHint.Never) {

                final EgocentricContextData data = element.getUserData(EgocentricContextData.TAG);
                if (data != null && element.checkCulling(cam)) {
                    worldSpace.add(element);
                }

                final List<Spatial> children = node.getChildren();
                if (children != null && children.size() > 0) {

                    for (final Spatial child : children) {


                        if (Node.class.isAssignableFrom(child.getClass())) {
                            computePerceptionSpace((Node) child, worldSpace);
                        }
                    }
                }
            }
        }
    }
    private ActionListener pickListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Pick") && !keyPressed) {

                final Set<Spatial> worldSpace = new HashSet<Spatial>();
                computePerceptionSpace(environment, worldSpace);
                final StringBuilder sb = new StringBuilder();
                for(final Spatial elem: worldSpace) {
                   sb.append(elem).append("; ");
                }
                System.out.println("Perception Space: " + sb.toString());
                
                if (!inventory.getChildren().isEmpty()) {


                    CollisionResults results = new CollisionResults();
                    Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                    environment.collideWith(ray, results);

                    if (results.size() > 0) {
                        CollisionResult closest = results.getClosestCollision();
                        Spatial s = closest.getGeometry();
                        // we cheat Model differently with simple Geometry
                        // s.parent is Oto-ogremesh when s is Oto_geom-1 and that is what we need
                        if (s.getName().equals("Oto-geom-1")) {
                            s = s.getParent();
                        }

                        final Vector3f newPosition = closest.getContactPoint();
                        float radius = ((BoundingBox) s.getWorldBound()).getYExtent();
                        newPosition.setY(newPosition.getY() + radius * 2);

                        Spatial s1 = inventory.getChild(0);
                        // scale back
                        s1.scale(.02f);
                        s1.setLocalTranslation(newPosition);
                        inventory.detachAllChildren();
                        environment.attachChild(s1);
                    }

                } else {
                    CollisionResults results = new CollisionResults();
                    Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                    environment.collideWith(ray, results);

                    if (results.size() > 0) {
                        CollisionResult closest = results.getClosestCollision();
                        Spatial s = closest.getGeometry();

                        // we cheat Model differently with simple Geometry
                        // s.parent is Oto-ogremesh when s is Oto_geom-1 and that is what we need
                        if (s.getName().equals("Oto-geom-1")) {
                            s = s.getParent();
                        }

                        final EgocentricContextData data = s.getUserData(EgocentricContextData.TAG);
                        /* take into consideration only objects having contextual data */
                        if (data != null) {

                            if (data.canBeMoved()) {

                                environment.detachChild(s);
                                inventory.attachChild(s);
                                // make it bigger to see on the HUD
                                s.scale(50f);
                                // make it on the HUD center
                                s.setLocalTranslation(app.getSettings().getWidth() / 2, app.getSettings().getHeight() / 2, 0);
                            } else {
                                stateManager.getState(NotificationsStateManager.class).addNotification("(Pick-up) " + s.getName() + ", can't be moved!");
                            }
                        }
                    }
                }
            }
        }
    };
}
