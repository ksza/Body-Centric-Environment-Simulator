package dk.itu.bodysim.context;

import dk.itu.bodysim.context.ssm.SSMBundle;
import dk.itu.bodysim.context.server.api.ContextApiServer;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dk.itu.bodysim.context.server.view.ContextViewServer;
import dk.itu.bodysim.context.ssm.SSMSpaceType;
import dk.itu.bodysim.context.ssm.WorldSpaceVisitor;
import dk.itu.bodysim.notifications.NotificationsStateManager;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.restlet.Component;
import org.restlet.data.Protocol;

/**
 *
 * @author kszanto
 */
public class EgocentricContextManager extends AbstractAppState {

    private SimpleApplication app;
    private Node rootNode;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private InputManager inputManager;
    private ViewPort viewPort;
    private BulletAppState physics;
    private Component serverComponent;
    private Camera cam;
    private static EgocentricContextManager instance;
    private boolean computing = false;
    private SSMBundle ssmBundle;
    private Set<Spatial> worldSpace;
    private SSMClassifier ssmClassifier;

    public static EgocentricContextManager getInstance() {
        return instance;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        instance = this;

        this.app = (SimpleApplication) app;
        this.cam = this.app.getCamera();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.viewPort = this.app.getViewPort();
        this.physics = this.stateManager.getState(BulletAppState.class);

        /* compute world space */
        rootNode.depthFirstTraversal(new WorldSpaceVisitor(this.app));

        /* start up the rest server */
        serverComponent = new Component();

        // Add a new HTTP server listening on port 8182.
        serverComponent.getServers().add(Protocol.HTTP, 8182);

        serverComponent.getDefaultHost().attach("/context/api",
                new ContextApiServer());

        serverComponent.getDefaultHost().attach("/context/view",
                new ContextViewServer());

        ssmBundle = SSMBundle.getInstance();
        worldSpace = ssmBundle.getSet(SSMSpaceType.WORLD_SPACE);

        try {
            // Start the component.
            serverComponent.start();
        } catch (Exception ex) {
            Logger.getLogger(EgocentricContextManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public synchronized boolean isComputing() {
        return computing;
    }

    public synchronized void setComputing(boolean computing) {
        this.computing = computing;
    }

    public void determineSpaces(final Node node) {

        if (ssmClassifier == null || ssmClassifier.isDone()) {
            ssmClassifier = new SSMClassifier(worldSpace, cam.clone(), rootNode);
            new Thread(ssmClassifier).start();
        }
    }

    public void pickedUp(final Spatial spatial) {

        ssmBundle.updateSet(SSMSpaceType.SELECTED_SET, spatial);
        ssmBundle.updateSet(SSMSpaceType.MANIPULATED_SET, spatial);
    }

    public void droppedDown(final Spatial spatial) {

        ssmBundle.removeFromSet(SSMSpaceType.SELECTED_SET, spatial);
        ssmBundle.removeFromSet(SSMSpaceType.MANIPULATED_SET, spatial);
    }

    public void log(final String setName, final Set<Spatial> result) {
        final StringBuilder sb = new StringBuilder(setName).append(": ");
        for (final Spatial elem : result) {
            sb.append(elem.getName()).append("; ");
        }

        stateManager.getState(NotificationsStateManager.class).addNotification(sb.toString());
    }

    @Override
    public void cleanup() {
        super.cleanup();

        if (serverComponent != null && serverComponent.isStarted()) {
            try {
                serverComponent.stop();
            } catch (Exception ex) {
                Logger.getLogger(EgocentricContextManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
