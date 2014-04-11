package dk.itu.bodysim.context;

import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dk.itu.bodysim.context.ssm.ActionSpaceStrategy;
import dk.itu.bodysim.context.ssm.ExaminableSetStrategy;
import dk.itu.bodysim.context.ssm.PerceptionSpaceStrategy;
import dk.itu.bodysim.context.ssm.RecognizableSetStrategy;
import dk.itu.bodysim.context.ssm.SSMBundle;
import dk.itu.bodysim.context.ssm.SSMSpaceComputationStrategy;
import dk.itu.bodysim.context.ssm.SSMSpaceType;
import java.util.HashSet;
import java.util.Set;

/**
 * Classifies objects in the WorldSpace based on the current context
 *
 * @author kszanto
 */
public class SSMClassifier implements Runnable {

    private Boolean done = false;
    private final Set<Spatial> worldSpace;
    private final Camera cam;
    private final Node environment;
    private SSMBundle ssmBundle = SSMBundle.getInstance();
    private final SSMSpaceComputationStrategy perceptionSpaceStrategy;
    private final SSMSpaceComputationStrategy recognizableSetStrategy;
    private final SSMSpaceComputationStrategy examinableSetStrategy;
    private final SSMSpaceComputationStrategy actionSpaceStrategy;

    public SSMClassifier(final Set<Spatial> worldSpace, final Camera cam, final Node environment) {
        this.worldSpace = worldSpace;
        this.cam = cam;
        this.environment = environment;

        perceptionSpaceStrategy = new PerceptionSpaceStrategy(cam);
        recognizableSetStrategy = new RecognizableSetStrategy(cam);
        examinableSetStrategy = new ExaminableSetStrategy(cam);
        actionSpaceStrategy = new ActionSpaceStrategy(cam);

    }

    public void run() {

        final Set<Spatial> onScreenEntities = updateVisibleEntitiesContext();

        ssmBundle.putSet(SSMSpaceType.PERCEPTION_SPACE, perceptionSpaceStrategy.determineSet(onScreenEntities));
        ssmBundle.putSet(SSMSpaceType.RECOGNIZABLE_SET, recognizableSetStrategy.determineSet(onScreenEntities));
        ssmBundle.putSet(SSMSpaceType.EXAMINABLE_SET, examinableSetStrategy.determineSet(onScreenEntities));
        ssmBundle.putSet(SSMSpaceType.ACTION_SPACE, actionSpaceStrategy.determineSet(onScreenEntities));

        synchronized (done) {
            done = true;
        }
    }

    /**
     * Get the set of currently visible items and update the distance from the
     * agent!
     *
     * @return
     */
    private Set<Spatial> updateVisibleEntitiesContext() {

        final Set<Spatial> result = new HashSet<Spatial>();

        for (final Spatial element : worldSpace) {
            if (isOnScreen(element)) {

                final EgocentricContextData data = element.getUserData(EgocentricContextData.TAG);

                final float distance = cam.getLocation().distance(element.getWorldTranslation());
                data.setLastMeasuredDistance(distance);

                result.add(element);
            }
        }

        return result;
    }

    private boolean isOnScreen(Spatial spatial) {
        if (spatial == null) {
            return false;
        }

        BoundingVolume bv = spatial.getWorldBound();
        int planeState = cam.getPlaneState();
        cam.setPlaneState(0);
        Camera.FrustumIntersect result = cam.contains(bv);
        cam.setPlaneState(planeState);
        if (result == Camera.FrustumIntersect.Inside || result == Camera.FrustumIntersect.Intersects) {

            final Vector3f direction = spatial.getWorldTranslation().subtract(cam.getLocation());
            final Ray ray = new Ray(cam.getLocation(), direction);

            final CollisionResults collisionResults = new CollisionResults();
            environment.collideWith(ray, collisionResults);

            final CollisionResult closestResult = collisionResults.getClosestCollision();
            if (closestResult != null) {
                Spatial closestGeometry = closestResult.getGeometry();

                while (closestGeometry != null && !closestGeometry.getUserDataKeys().contains(EgocentricContextData.TAG)) {
                    closestGeometry = closestGeometry.getParent();
                }

                if (closestGeometry != null) {
                    final EgocentricContextData data = closestGeometry.getUserData(EgocentricContextData.TAG);
                    return data != null && closestGeometry.equals(spatial);
                }
            }

            return false;
        }

        return false;
    }

    public boolean isDone() {
        synchronized (done) {
            return done;
        }
    }
}
