package dk.itu.bodysim.context;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import java.io.IOException;
import java.util.UUID;

/**
 *
 * @author kszanto
 */
public class EgocentricContextData implements Savable {

    public static final String TAG = "EGOCENTRIC_CONTEXT_DATA";
    private static final String ID_SAVE_TAG = "ID";
    private static final String OBJECT_TYPE_SAVE_TAG = "OBJECT_TYPE";
    private static final String CAN_BE_MOVED_SAVE_TAG = "CAN_BE_MOVED";
    private static final String PERCEPTION_DISTANCE_SAVE_TAG = "PERCEPTION_DISTANCE";
    private static final String RECOGNITION_DISTANCE_SAVE_TAG = "RECOGNITION_DISTANCE";
    private static final String EXAMINATION_DISTANCE_SAVE_TAG = "EXAMINATION_DISTANCE";
    private static final String WEIGHT_SAVE_TAG = "WEIGHT";
    private String id = UUID.randomUUID().toString();
    private ObjectType type = ObjectType.PHYSICAL;
    private boolean canBeMoved = true;
    
    /**
     * Distances expressed in World Units (WU)
     */
    private float perceptionDistance = Float.MAX_VALUE;
    private float recognitionDistance = perceptionDistance;
    private float examinationDistance = 3; // gotta be really close
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
        final OutputCapsule capsule = ex.getCapsule(this);

        capsule.write(id, ID_SAVE_TAG, null);
        capsule.write(type, OBJECT_TYPE_SAVE_TAG, null);
        capsule.write(canBeMoved, CAN_BE_MOVED_SAVE_TAG, canBeMoved);
        capsule.write(perceptionDistance, PERCEPTION_DISTANCE_SAVE_TAG, perceptionDistance);
        capsule.write(recognitionDistance, RECOGNITION_DISTANCE_SAVE_TAG, recognitionDistance);
        capsule.write(examinationDistance, EXAMINATION_DISTANCE_SAVE_TAG, examinationDistance);
        capsule.write(weight, WEIGHT_SAVE_TAG, weight);
    }

    public void read(JmeImporter im) throws IOException {
        final InputCapsule ic = im.getCapsule(this);

        id = ic.readString(ID_SAVE_TAG, UUID.randomUUID().toString());
        type = ic.readEnum(OBJECT_TYPE_SAVE_TAG, ObjectType.class, ObjectType.PHYSICAL);
        canBeMoved = ic.readBoolean(CAN_BE_MOVED_SAVE_TAG, true);
        perceptionDistance = ic.readFloat(PERCEPTION_DISTANCE_SAVE_TAG, Float.MAX_VALUE);
        recognitionDistance = ic.readFloat(RECOGNITION_DISTANCE_SAVE_TAG, perceptionDistance);
        examinationDistance = ic.readFloat(EXAMINATION_DISTANCE_SAVE_TAG, 3);
        weight = ic.readFloat(WEIGHT_SAVE_TAG, 0);
    }
}
