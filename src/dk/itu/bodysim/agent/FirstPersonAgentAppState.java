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
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dk.itu.bodysim.EgocentricApp;
import dk.itu.bodysim.Util;
import dk.itu.bodysim.notifications.NotificationsStateManager;
import dk.itu.bodysim.context.EgocentricContextData;
import dk.itu.bodysim.context.EgocentricContextManager;
import dk.itu.bodysim.context.ObjectType;
import dk.itu.bodysim.context.ssm.SSMBundle;
import dk.itu.bodysim.context.ssm.SSMSpaceType;
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
    private Node inventory;
    private Vector3f oldPosition = null;
    private Vector3f oldScale = null;

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

        characterControl = new CharacterControl(new CapsuleCollisionShape(1.5f, this.app.getAgentHeight(), 1), 0.05f);

        characterControl.setJumpSpeed(20);
        characterControl.setFallSpeed(30);
        characterControl.setGravity(70);
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

        inputManager.addMapping("Interact",
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // left-button click
        inputManager.addListener(interactListener, "Interact");

        inputManager.addMapping("PutBack",
                new MouseButtonTrigger(MouseInput.BUTTON_RIGHT)); // right-button click
        inputManager.addListener(putBackListener, "PutBack");

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
//    private ActionListener computeSpacesListener = new ActionListener() {
//        public void onAction(String name, boolean isPressed, float tpf) {
//            if (name.equals("ComputeSpaces") && !isPressed) {
//                stateManager.getState(EgocentricContextManager.class).determineSpaces(environment);
//            }
//        }
//    };
    private AnalogListener computeSpacesListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals("ComputeSpaces")) {
                stateManager.getState(EgocentricContextManager.class).determineSpaces(environment);
            }
        }
    };
    private ActionListener interactListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Interact") && !keyPressed) {

                if (!inventory.getChildren().isEmpty()) {

                    CollisionResults results = new CollisionResults();
                    Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                    environment.collideWith(ray, results);

                    if (results.size() > 0) {
                        CollisionResult closest = results.getClosestCollision();
                        Spatial s = closest.getGeometry();

                        while (!s.getUserDataKeys().contains(EgocentricContextData.TAG) && !s.equals(environment)) {
                            s = s.getParent();
                        }

                        final EgocentricContextData data = s.getUserData(EgocentricContextData.TAG);
                        /* take into consideration only objects having contextual data */
                        if (data != null && !s.equals(environment)) {

                            final Set<Spatial> actionSpace = SSMBundle.getInstance().getSet(SSMSpaceType.ACTION_SPACE);

                            if (actionSpace.contains(s)) {

                                final BoundingBox targetBounds = (BoundingBox) s.getWorldBound();
                                final Vector3f newPosition = closest.getContactPoint();
                                float radius = ((BoundingBox) s.getWorldBound()).getYExtent();
                                newPosition.setY(targetBounds.getCenter().getY() + targetBounds.getYExtent() + radius * 2);
                                newPosition.setZ(targetBounds.getCenter().getZ());

                                Spatial s1 = inventory.getChild(0);
                                // scale back
                                s1.setLocalScale(oldScale);
                                s1.setLocalTranslation(newPosition);
                                inventory.detachAllChildren();
                                environment.attachChild(s1);
                                Util.highlightEntity(app, s1);

                                stateManager.getState(EgocentricContextManager.class).droppedDown(s1);
                            } else {
                                stateManager.getState(NotificationsStateManager.class).addNotification("(Drop-down) onto " + data.getId() + ", you are too far!");
                            }
                        } else {
                            stateManager.getState(NotificationsStateManager.class).addNotification("(Drop-down) Possible only on egocentric entities!");
                        }
                    }

                } else {
                    CollisionResults results = new CollisionResults();
                    Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                    environment.collideWith(ray, results);

                    if (results.size() > 0) {
                        CollisionResult closest = results.getClosestCollision();
                        Spatial s = closest.getGeometry();

                        while (!s.getUserDataKeys().contains(EgocentricContextData.TAG) && !s.equals(environment)) {
                            s = s.getParent();
                        }

                        final EgocentricContextData data = s.getUserData(EgocentricContextData.TAG);

                        final Set<Spatial> actionSpace = SSMBundle.getInstance().getSet(SSMSpaceType.ACTION_SPACE);

                        /* take into consideration only objects having contextual data */
                        if (data != null && !s.equals(environment)) {

                            if (actionSpace.contains(s)) {
                                if (data.getType() == ObjectType.PHYSICAL) {
                                    pickObjectUp(s, data);
                                } else if (data.getType() == ObjectType.MEDIATOR) {
                                    interactWithMediator(s, data);
                                }
                            } else {
                                stateManager.getState(NotificationsStateManager.class).addNotification("(Interact) " + data.getId() + ", you are too far!");
                            }
                        }
                    }
                }
            }
        }
    };
    private ActionListener putBackListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("PutBack") && !keyPressed) {

                if (!inventory.getChildren().isEmpty()) {

                    Spatial s1 = inventory.getChild(0);
                    // scale back
                    s1.setLocalScale(oldScale);
                    s1.setLocalTranslation(oldPosition);
                    inventory.detachAllChildren();
                    environment.attachChild(s1);
                    Util.highlightEntity(app, s1);

                    stateManager.getState(EgocentricContextManager.class).droppedDown(s1);
                }
            }
        }
    };

    private void pickObjectUp(final Spatial object, final EgocentricContextData data) {

        if (data.isCanBeMoved()) {

            Util.removeHightlight(app, object);

            oldPosition = object.getWorldTranslation().clone();
            oldScale = object.getWorldScale().clone();
            object.getWorldScale();
            environment.detachChild(object);
            inventory.attachChild(object);
            // make it bigger to see on the HUD
            object.scale(50f);
            // make it on the HUD center
            object.setLocalTranslation(app.getSettings().getWidth() / 2, app.getSettings().getHeight() / 2, 0);

            stateManager.getState(EgocentricContextManager.class).pickedUp(object);
        } else {
            stateManager.getState(NotificationsStateManager.class).addNotification("(Pick-up) " + data.getId() + ", can't be moved!");
        }
    }

    private void interactWithMediator(final Spatial object, final EgocentricContextData data) {
        stateManager.getState(NotificationsStateManager.class).addNotification("(Interaction) " + data.getId() + ", not yet implemented for mediators!");
    }
}
