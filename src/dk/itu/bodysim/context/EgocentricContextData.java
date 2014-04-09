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
    private static final String INTERACTION_TYPE_SAVE_TAG = "INTERACTION_TYPE";
    private static final String PERCEPTION_DISTANCE_SAVE_TAG = "PERCEPTION_DISTANCE";
    private static final String RECOGNITION_DISTANCE_SAVE_TAG = "RECOGNITION_DISTANCE";
    private static final String EXAMINATION_DISTANCE_SAVE_TAG = "EXAMINATION_DISTANCE";
    private static final String ACTION_DISTANCE_SAVE_TAG = "ACTION_DISTANCE";
    private static final String LAST_MEASURED_DISTANCE_SAVE_TAG = "LAST_MEASURED_DISTANCE_SAVE_TAG";
    private static final String WEIGHT_SAVE_TAG = "WEIGHT";
    private static final String SURFACE_TAG = "SURFACE";
    
    private String id = UUID.randomUUID().toString();
    private ObjectType type = ObjectType.PHYSICAL;
    /**
     * Distances expressed in World Units (WU)
     */
    private InteractionType interactionType = InteractionType.PICK_UP;
    /**
     * If you can place other objects on top of this.
     */
    private boolean surface = true;
    
    private float perceptionDistance = Float.MAX_VALUE;
    private float recognitionDistance = perceptionDistance;
    private float examinationDistance = 20; // gotta be really close
    /**
     * Some objects don't fit into the normal range distance
     */
    private float actionDistance = 13;
    private float weight;
    
    private float lastMeasuredDistance;

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

    public InteractionType getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(InteractionType interactionType) {
        this.interactionType = interactionType;
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

    public float getActionDistance() {
        return actionDistance;
    }

    public void setActionDistance(float actionDistance) {
        this.actionDistance = actionDistance;
    }
    
    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void setSurface(boolean surface) {
        this.surface = surface;
    }

    public boolean isSurface() {
        return surface;
    }
    
    public float getLastMeasuredDistance() {
        return lastMeasuredDistance;
    }

    public void setLastMeasuredDistance(float lastMeasuredDistance) {
        this.lastMeasuredDistance = lastMeasuredDistance;
    }

    public void write(JmeExporter ex) throws IOException {
        final OutputCapsule capsule = ex.getCapsule(this);

        capsule.write(id, ID_SAVE_TAG, null);
        capsule.write(type, OBJECT_TYPE_SAVE_TAG, null);
        capsule.write(interactionType, INTERACTION_TYPE_SAVE_TAG, null);
        capsule.write(perceptionDistance, PERCEPTION_DISTANCE_SAVE_TAG, perceptionDistance);
        capsule.write(recognitionDistance, RECOGNITION_DISTANCE_SAVE_TAG, recognitionDistance);
        capsule.write(examinationDistance, EXAMINATION_DISTANCE_SAVE_TAG, examinationDistance);
        capsule.write(actionDistance, ACTION_DISTANCE_SAVE_TAG, actionDistance);
        capsule.write(lastMeasuredDistance, LAST_MEASURED_DISTANCE_SAVE_TAG, 0);
        capsule.write(surface, SURFACE_TAG, surface);
        capsule.write(weight, WEIGHT_SAVE_TAG, weight);
    }

    public void read(JmeImporter im) throws IOException {
        final InputCapsule ic = im.getCapsule(this);

        id = ic.readString(ID_SAVE_TAG, UUID.randomUUID().toString());
        type = ic.readEnum(OBJECT_TYPE_SAVE_TAG, ObjectType.class, ObjectType.PHYSICAL);
        interactionType = ic.readEnum(INTERACTION_TYPE_SAVE_TAG, InteractionType.class, interactionType);
        perceptionDistance = ic.readFloat(PERCEPTION_DISTANCE_SAVE_TAG, Float.MAX_VALUE);
        recognitionDistance = ic.readFloat(RECOGNITION_DISTANCE_SAVE_TAG, perceptionDistance);
        examinationDistance = ic.readFloat(EXAMINATION_DISTANCE_SAVE_TAG, 10);
        actionDistance = ic.readFloat(ACTION_DISTANCE_SAVE_TAG, 13);
        lastMeasuredDistance = ic.readFloat(LAST_MEASURED_DISTANCE_SAVE_TAG, 0);
        surface = ic.readBoolean(SURFACE_TAG, true);
        weight = ic.readFloat(WEIGHT_SAVE_TAG, 0);
    }
}
