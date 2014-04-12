package dk.itu.bodysim;

import com.jme3.audio.AudioNode;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dk.itu.bodysim.agent.Agent;
import dk.itu.bodysim.context.EgocentricContextData;
import dk.itu.bodysim.environment.ALFEnvironment;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kszanto
 */
public class ALFTask extends EgocentricApp {
    
    public static void main(String[] args) {
        ALFTask app = new ALFTask();
        app.start();
    }
    private static final List<String> pianoNotes = new ArrayList<String>();

    static {
        pianoNotes.add("Sounds/Piano/c.wav");
        pianoNotes.add("Sounds/Piano/d.wav");
        pianoNotes.add("Sounds/Piano/e.wav");
        pianoNotes.add("Sounds/Piano/f.wav");
        pianoNotes.add("Sounds/Piano/g.wav");
        pianoNotes.add("Sounds/Piano/a.wav");
        pianoNotes.add("Sounds/Piano/b.wav");
        pianoNotes.add("Sounds/Piano/cc.wav");
    };
    
    private int nextKeyToPlay = 0;
    private final List<AudioNode> pianoKeys = new ArrayList<AudioNode>();
    private AudioNode noEmailsNotification;
    private AudioNode pouringLiquidNotification;
    
    @Override
    public void simpleInitApp() {
        
        super.simpleInitApp();
        
        for (final String pianoNote : pianoNotes) {
            pianoKeys.add(createAudioNode(pianoNote));
        }
        
        noEmailsNotification = createAudioNode("Sounds/Notifications/no_emails.wav");
        pouringLiquidNotification = createAudioNode("Sounds/Notifications/pouring_liquid.wav");
    }
    
    private AudioNode createAudioNode(final String pianoKey) {
        
        final AudioNode pianoSoundNode = new AudioNode(assetManager, pianoKey, false);
        pianoSoundNode.setPositional(false);
        pianoSoundNode.setLooping(false);
        pianoSoundNode.setVolume(3);
        rootNode.attachChild(pianoSoundNode);
        
        return pianoSoundNode;
    }
    
    @Override
    protected Node createEnvironmentScene() {
        
        return new ALFEnvironment(getAssetManager());
    }
    
    @Override
    protected Agent getAgentConfiguration() {
        return new Agent(new Vector3f(-55, 26, 70), 25, 15);
    }
    
    @Override
    public void onCustomInteraction(Spatial spatial) {
        
        final EgocentricContextData data = spatial.getUserData(EgocentricContextData.TAG);
        if ("Piano".equals(data.getId())) {
            
            pianoKeys.get(nextKeyToPlay++).playInstance();
            if (nextKeyToPlay >= pianoKeys.size()) {
                nextKeyToPlay = 0;
            }
        } else if("Laptop".equals(data.getId())) {
            
            noEmailsNotification.playInstance();
            super.onCustomInteraction(spatial);
        } else {
            super.onCustomInteraction(spatial);
        }
    }

    @Override
    public void onCombinedInteraction(Spatial pickedUpObject, Spatial withObject) {
        super.onCombinedInteraction(pickedUpObject, withObject);    
        
        final EgocentricContextData data1 = pickedUpObject.getUserData(EgocentricContextData.TAG);
        final EgocentricContextData data2 = withObject.getUserData(EgocentricContextData.TAG);

        if("Pot".equals(data1.getId()) && "Cup".equals(data2.getId())) {
            pouringLiquidNotification.playInstance();
        }
    }
}
