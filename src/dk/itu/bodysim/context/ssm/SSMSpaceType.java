package dk.itu.bodysim.context.ssm;

public enum SSMSpaceType {
    
    WORLD_SPACE,
    PERCEPTION_SPACE,
    RECOGNIZABLE_SET,
    EXAMINABLE_SET,
    ACTION_SPACE,
    SELECTED_SET,
    MANIPULATED_SET,
    UNKNOWN;
    
    public static SSMSpaceType fromString(final String spaceName) {
        
        if(spaceName == null) {
            return UNKNOWN;
        }
        
        final String unifiedName = spaceName.toUpperCase();
        
        if("WORLDSPACE".equals(unifiedName)) {
            return WORLD_SPACE;
        } else if("PERCEPTIONSPACE".equals(unifiedName)) {
            return PERCEPTION_SPACE;
        } else if("RECOGNIZABLESET".equals(unifiedName)) {
            return RECOGNIZABLE_SET;
        } else if("EXAMINABLESET".equals(unifiedName)) {
            return EXAMINABLE_SET;
        } else if("ACTIONSPACE".equals(unifiedName)) {
            return ACTION_SPACE;
        } else if("SELECTEDSET".equals(unifiedName)) {
            return SELECTED_SET;
        } else if("MANIPULATEDSET".equals(unifiedName)) {
            return MANIPULATED_SET;
        } else {
            return UNKNOWN;
        }
    }
}
