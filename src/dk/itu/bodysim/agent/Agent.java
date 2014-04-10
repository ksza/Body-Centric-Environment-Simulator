package dk.itu.bodysim.agent;

import com.jme3.math.Vector3f;
import dk.itu.bodysim.context.EgocentricContextData;
import dk.itu.bodysim.context.InteractionType;

/**
 *
 * @author kszanto
 */
public class Agent {

    /**
     * The eyesight height
     */
    private final Vector3f initialAgentPosition;
    private final float agentHeight;
    private final float agentMaxCarryWeight;
    
    public Agent(Vector3f initialAgentPosition, float agentHeight, float agentMaxCarryWeight) {
        this.initialAgentPosition = initialAgentPosition;
        this.agentHeight = agentHeight;
        this.agentMaxCarryWeight = agentMaxCarryWeight;
    }

    public Vector3f getInitialAgentPosition() {
        return initialAgentPosition;
    }

    public float getAgentHeight() {
        return agentHeight;
    }

    /**
     * Compares the weight of the target object with the max carry weight of the
     * agent
     *
     * @param object the object to be moved
     * @return
     */
    public boolean canMove(final EgocentricContextData object) {

        if(object != null && object.getInteractionType() == InteractionType.PICK_UP) {
            
            return object.getWeight() <= agentMaxCarryWeight;
        }
        
        return false;
    }   
}
