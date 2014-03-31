package dk.itu.bodysim.context.ssm;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import dk.itu.bodysim.context.EgocentricContextData;

/**
 *
 * @author kszanto
 */
public class ExaminableSetStrategy extends AbstractSSMSpaceComputationStrategy {

    public ExaminableSetStrategy(Camera camera) {
        super(camera);
    }

    @Override
    public boolean isInSet(Spatial element) {

        final EgocentricContextData data = element.getUserData(EgocentricContextData.TAG);

        final Vector3f objectLocation = element.getWorldTranslation();

        final float distance = camera.distanceToNearPlane(objectLocation);
        if (distance > 0 && distance <= data.getExaminationDistance()) {
            return true;
        }

        return false;
    }
}
