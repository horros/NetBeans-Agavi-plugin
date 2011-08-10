/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package php.agavi.builder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author mle
 */
public class PropertyWriter {

    File propertiesFile;
    
    public PropertyWriter(File f) {
        propertiesFile = f;
    }
    
    public void writeProperties(HashMap<String, String> properties) throws IOException {
       
        PrintStream stream = new PrintStream(propertiesFile);
        
        Set<String> keys = properties.keySet();
        
        for (String key : keys) {
            String prop = properties.get(key);
            stream.println(key + "=" + prop);
        }
        
        
    }
    
}
