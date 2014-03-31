package dk.itu.bodysim.context.ssm;

import com.jme3.scene.Spatial;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This resource is share by many execution threads!
 * 
 * @author kszanto
 */
public class SSMBundle {
    
    private static SSMBundle instance;
    
    public static synchronized SSMBundle getInstance() {
        
        if(instance == null) {
            instance = new SSMBundle();
        }
        
        return instance;
    }

    private SSMBundle() {
        
        ssmSpaces.put(SSMSpaceType.WORLD_SPACE, new HashSet<Spatial>());
        ssmSpaces.put(SSMSpaceType.PERCEPTION_SPACE, new HashSet<Spatial>());
        ssmSpaces.put(SSMSpaceType.RECOGNIZABLE_SET, new HashSet<Spatial>());
        ssmSpaces.put(SSMSpaceType.EXAMINABLE_SET, new HashSet<Spatial>());
        ssmSpaces.put(SSMSpaceType.ACTION_SPACE, new HashSet<Spatial>());
        ssmSpaces.put(SSMSpaceType.SELECTED_SET, new HashSet<Spatial>());
        ssmSpaces.put(SSMSpaceType.MANIPULATED_SET, new HashSet<Spatial>());
        ssmSpaces.put(SSMSpaceType.UNKNOWN, new HashSet<Spatial>());
    }        
    
    /* SSM Spaces */
    private Map<SSMSpaceType, Set<Spatial>> ssmSpaces = new ConcurrentHashMap<SSMSpaceType, Set<Spatial>>();

    public synchronized Set<Spatial> getSet(final String setName) {
        
        return ssmSpaces.get(SSMSpaceType.fromString(setName));
    }
    
    public synchronized Set<Spatial> getSet(final SSMSpaceType setType) {
        
        return ssmSpaces.get(setType);
    }
    
    public synchronized void putSet(final SSMSpaceType setType, final Set<Spatial> setValue) {
        
        ssmSpaces.put(setType, setValue);
    }
    
    public synchronized void updateSet(final SSMSpaceType setType, final Spatial element) {
        
        final Set<Spatial> setValue = ssmSpaces.get(setType);
        setValue.add(element);
        ssmSpaces.put(setType, setValue);
    }
    
    public synchronized void clearSet(final SSMSpaceType setType) {
        
        ssmSpaces.get(setType).clear();
    }
}
