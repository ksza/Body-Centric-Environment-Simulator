package dk.itu.bodysim.context.ssm;

import com.jme3.scene.Spatial;
import java.util.Set;

/**
 *
 * @author kszanto
 */
public interface SSMSpaceComputationStrategy {
 
    Set<Spatial> determineSet(final Set<Spatial> spatials);
}
