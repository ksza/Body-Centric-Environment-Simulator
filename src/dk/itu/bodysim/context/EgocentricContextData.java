package dk.itu.bodysim.context;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import java.io.IOException;


/**
 *
 * @author kszanto
 */
public class EgocentricContextData implements Savable {

    public static final String TAG = "EGOCENTRIC_CONTEXT_DATA";
    
    private ObjectType type = ObjectType.PHYSICAL;
    private boolean canBeMoved = false;
    
    public EgocentricContextData() {
    }

    public void setType(ObjectType type) {
        this.type = type;
    }

    public void setCanBeMoved(boolean canBeMoved) {
        this.canBeMoved = canBeMoved;
    }

    public ObjectType getType() {
        return type;
    }

    public boolean isCanBeMoved() {
        return canBeMoved;
    }

    public void write(JmeExporter ex) throws IOException {
    }

    public void read(JmeImporter im) throws IOException {
    }
}
