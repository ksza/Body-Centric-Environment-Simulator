package dk.itu.bodysim.context;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import java.io.IOException;


/**
 *
 * @author kszanto
 */
public class ContextData implements Savable {

    /**
     * Expressed in grams.
     */
    private final int weight;

    public ContextData(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public void write(JmeExporter ex) throws IOException {
    }

    public void read(JmeImporter im) throws IOException {
    }
}
