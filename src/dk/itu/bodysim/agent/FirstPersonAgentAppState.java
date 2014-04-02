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
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dk.itu.bodysim.EgocentricApp;
import dk.itu.bodysim.notifications.NotificationsStateManager;
import dk.itu.bodysim.context.EgocentricContextData;
import dk.itu.bodysim.context.EgocentricContextManager;

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
     * Initialize the keys used to control this agent
     *
     * @param inputManager the apps input manager instance
     */
    private void initKeys(final InputManager inputManager) {

        inputManager.addMapping("Pick",
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // left-button click
        inputManager.addListener(pickListener, "Pick");

        inputManager.addMapping("ComputeSpaces",
                new MouseAxisTrigger(MouseInput.AXIS_X, true),
                new MouseAxisTrigger(MouseInput.AXIS_X, false),
                new MouseAxisTrigger(MouseInput.AXIS_Y, true),
                new MouseAxisTrigger(MouseInput.AXIS_Y, false),
                new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false),
                new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true),
                new KeyTrigger(KeyInput.KEY_A),
                new KeyTrigger(KeyInput.KEY_D),
                new KeyTrigger(KeyInput.KEY_W),
                new KeyTrigger(KeyInput.KEY_S));
        inputManager.addListener(computeSpacesListener, "ComputeSpaces");

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
    private AnalogListener computeSpacesListener = new AnalogListener() {

        public void onAnalog(String name, float value, float tpf) {
            if (name.equals("ComputeSpaces")) {
                stateManager.getState(EgocentricContextManager.class).determineSpaces(environment);
            }
        }
    };
    private ActionListener pickListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Pick") && !keyPressed) {

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

//                        // we cheat Model differently with simple Geometry
//                        // s.parent is Oto-ogremesh when s is Oto_geom-1 and that is what we need
//                        if (s.getName().equals("Oto-geom-1")) {
//                            s = s.getParent();
//                        }

                        while (!s.getUserDataKeys().contains(EgocentricContextData.TAG) && !s.equals(environment)) {
                            s = s.getParent();
                        }

                        final EgocentricContextData data = s.getUserData(EgocentricContextData.TAG);
                        /* take into consideration only objects having contextual data */
                        if (data != null && !s.equals(environment)) {

                            if (data.isCanBeMoved()) {

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
