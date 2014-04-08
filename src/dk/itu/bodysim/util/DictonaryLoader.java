package dk.itu.bodysim.util;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DictonaryLoader implements AssetLoader {

    @Override
    public Object load(AssetInfo assetInfo) throws IOException {

        Map<String, String> dictionary = null;
        final InputStream in = assetInfo.openStream();
        
        if (in != null) {
            dictionary = new HashMap<String, String>();

            BufferedReader bufRead = null;
            try {
                bufRead = new BufferedReader(new InputStreamReader(in));
                String line = bufRead.readLine();
                while (line != null) {
                    final String[] keyValuePair = line.split("=");
                    if(keyValuePair == null || keyValuePair.length != 2) {
                        throw new IllegalStateException("Properties file should be a propper java properties file!");
                    }
                    
                    dictionary.put(keyValuePair[0], keyValuePair[1]);
                    line = bufRead.readLine();
                }
            } finally {
                if (bufRead != null) {
                    bufRead.close();
                }
            }
        }
        return dictionary;
    }
}