package dk.itu.bodysim.context.ssm;

import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import dk.itu.bodysim.context.EgocentricContextData;

/**
 * Entity is considered to be in the ACTION_SET if it is within reach
 * and the agent is looking directly at it!
 * 
 * @author kszanto
 */
public class ActionSpaceStrategy extends AbstractSSMSpaceComputationStrategy {

    private static final float ACTION_SPACE_DISTANCE = 7;

    public ActionSpaceStrategy(Camera camera) {
        super(camera);
    }

    @Override
    public boolean isInSet(Spatial element) {

        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(camera.getLocation(), camera.getDirection());
        element.collideWith(ray, results);

        if (results.size() > 0) {

            final EgocentricContextData data = element.getUserData(EgocentricContextData.TAG);
            if (data.getLastMeasuredDistance() <= ACTION_SPACE_DISTANCE) {
                return true;
            }
        }

        return false;
    }
}
