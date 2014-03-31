package dk.itu.bodysim.context.ssm;

import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author kszanto
 */
public abstract class AbstractSSMSpaceComputationStrategy implements SSMSpaceComputationStrategy {

    protected final Camera camera;

    public AbstractSSMSpaceComputationStrategy(final Camera camera) {
        this.camera = camera;
    }

    public Set<Spatial> determineSet(final Set<Spatial> spatials) {

        final Set<Spatial> result = new HashSet<Spatial>();

        for (final Spatial element : spatials) {
            if (isInSet(element)) {
                result.add(element);
            }
        }

        return result;
    }

    public abstract boolean isInSet(final Spatial element);
}
