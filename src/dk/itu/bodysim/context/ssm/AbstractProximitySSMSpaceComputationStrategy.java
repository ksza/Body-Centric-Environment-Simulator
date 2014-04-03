package dk.itu.bodysim.context.ssm;

import com.jme3.bounding.BoundingVolume;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import dk.itu.bodysim.context.EgocentricContextData;

/**
 *
 * @author kszanto
 */
public abstract class AbstractProximitySSMSpaceComputationStrategy extends AbstractSSMSpaceComputationStrategy {

    public AbstractProximitySSMSpaceComputationStrategy(Camera camera) {
        super(camera);
    }

    private boolean isOnScreen(Spatial s) {
        BoundingVolume bv = s.getWorldBound();
        int planeState = camera.getPlaneState();
        camera.setPlaneState(0);
        Camera.FrustumIntersect result = camera.contains(bv);
        camera.setPlaneState(planeState);
        return result == Camera.FrustumIntersect.Inside || result == Camera.FrustumIntersect.Intersects;
    }

    @Override
    public boolean isInSet(Spatial element) {

        if (isOnScreen(element)) {

            final EgocentricContextData data = element.getUserData(EgocentricContextData.TAG);

            final float distance = camera.getLocation().distance(element.getWorldTranslation());
            if (distance > 0 && distance <= getReferenceDistance(data)) {

                return true;
            }
        }
//        
//        if (element.getCullHint() != Spatial.CullHint.Never && element.checkCulling(camera)) {
//
//            final EgocentricContextData data = element.getUserData(EgocentricContextData.TAG);
//            
//            final Vector3f objectLocation = element.getWorldTranslation();
//
//            final float distance = camera.getLocation().distance(element.getWorldTranslation());
//            if (distance > 0 && distance <= getReferenceDistance(data)) {
//
//                return true;
//            }
//        }

        return false;
    }

    protected abstract float getReferenceDistance(final EgocentricContextData data);
}