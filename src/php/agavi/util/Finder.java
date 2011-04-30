/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package php.agavi.util;

import java.io.File;

/**
 *
 * @author mle
 */
class Finder {

    public static boolean walk(String path, String glob, CopierCallback callback) {

        
        File root = new File(path);
        File[] list = root.listFiles();

        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath(), glob, callback);
            } else {
                callback.copy(f);
            }
        }
        
        return true;
    }
}
