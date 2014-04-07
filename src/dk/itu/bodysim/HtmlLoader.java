/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.bodysim;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HtmlLoader implements AssetLoader {

    @Override
    public Object load(AssetInfo assetInfo) throws IOException {

        final StringBuilder builder = new StringBuilder();
        InputStream in = assetInfo.openStream();
        if (in != null) {

            BufferedReader bufRead = null;

            try {
                bufRead = new BufferedReader(new InputStreamReader(in));
                String line = bufRead.readLine();
                while (line != null) {
                    builder.append(line);
                    line = bufRead.readLine();
                }
            } finally {
                if (bufRead != null) {
                    bufRead.close();
                }
            }
        }
        return builder.toString();
    }
}