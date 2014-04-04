package dk.itu.bodysim.context.ssm;

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

        final EgocentricContextData data = element.getUserData(EgocentricContextData.TAG);
        final float distance = data.getLastMeasuredDistance();

        return distance > 0 && distance <= getReferenceDistance(data);
    }

    protected abstract float getReferenceDistance(final EgocentricContextData data);
}