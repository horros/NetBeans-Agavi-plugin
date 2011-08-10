/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package php.agavi.builder;

import java.io.File;

/**
 *
 * @author mle
 */
public class TokenReplacingFileCopier {
    
    TokenReplacer replacer;
    
    public void setTokenReplacer(TokenReplacer replacer) {
        this.replacer = replacer;
    }

    public void walk(String path) {
        
        File root = new File(path);
        File[] list = root.listFiles();
        
        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath());                
            } else {
                System.err.println("File:" + f.getAbsoluteFile());
            }
        }
        
    }
    
}
