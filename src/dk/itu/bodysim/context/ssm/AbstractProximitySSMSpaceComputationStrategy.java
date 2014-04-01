package dk.itu.bodysim.context.ssm;

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

    @Override
    public boolean isInSet(Spatial element) {

        if (element.getCullHint() != Spatial.CullHint.Never && element.checkCulling(camera)) {

            final EgocentricContextData data = element.getUserData(EgocentricContextData.TAG);
            
            final Vector3f objectLocation = element.getWorldTranslation();

            final float distance = camera.getLocation().distance(element.getLocalTranslation());
            if (distance > 0 && distance <= getReferenceDistance(data)) {

                return true;
            }
        }

        return false;
    }

    protected abstract float getReferenceDistance(final EgocentricContextData data);
}