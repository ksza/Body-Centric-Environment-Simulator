package dk.itu.bodysim.context;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import java.io.IOException;
import java.util.UUID;


/**
 *
 * @author kszanto
 */
public class EgocentricContextData implements Savable {

    public static final String TAG = "EGOCENTRIC_CONTEXT_DATA";
    
    private String id = UUID.randomUUID().toString();
    private ObjectType type = ObjectType.PHYSICAL;
    private boolean canBeMoved = false;
    private float perceptionDistance = Float.MAX_VALUE;
    private float recognitionDistance = perceptionDistance;
    private float examinationDistance = 1; // gotta be really close
    private float weight;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }        
    
    public ObjectType getType() {
        return type;
    }
    public void setType(ObjectType type) {
        this.type = type;
    }

    public boolean isCanBeMoved() {
        return canBeMoved;
    }
    public void setCanBeMoved(boolean canBeMoved) {
        this.canBeMoved = canBeMoved;
    }

    public float getPerceptionDistance() {
        return perceptionDistance;
    }
    public void setPerceptionDistance(float perceptionDistance) {
        this.perceptionDistance = perceptionDistance;
    }

    public float getRecognitionDistance() {
        return recognitionDistance;
    }
    public void setRecognitionDistance(float recognitionDistance) {
        this.recognitionDistance = recognitionDistance;
    }

    public float getExaminationDistance() {
        return examinationDistance;
    }
    public void setExaminationDistance(float examinationDistance) {
        this.examinationDistance = examinationDistance;
    }

    public float getWeight() {
        return weight;
    }
    public void setWeight(float weight) {
        this.weight = weight;
    }
             
    public void write(JmeExporter ex) throws IOException {
    }
    public void read(JmeImporter im) throws IOException {
    }
}
