/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package php.agavi.builder;

/**
 *
 * @author mle
 */
public class AgaviPaths {
    
    public String SEPARATOR = System.getProperty("file.separator");
    
    public String AGAVI_INSTALL_PATH = "";
    
    public String BUILD;
    public String TEMPLATES;
    
    public String app = "";
    public String config = "";
    public String models = "";
    public String modules = "";
    public String cache = "";
    public String lib = "";
    public String log = "";
    public String templates = "";
    public String dev = "";
    public String dev_pub = "";
    public String libs = "";
    public String pub = "";
    
    public AgaviPaths(String agaviInstallPath) {
        this.AGAVI_INSTALL_PATH = agaviInstallPath;
        this.BUILD              = AGAVI_INSTALL_PATH + SEPARATOR + "build";
        this.TEMPLATES          = BUILD + SEPARATOR + "templates";
        this.app                = "app";
    }
    
    public void setAppDir(String appDir) {
        app = appDir;
        config    = app + SEPARATOR + "config";
        models    = app + SEPARATOR + "models";
        modules   = app + SEPARATOR + "modules";
        cache     = app + SEPARATOR + "cache";
        lib       = app + SEPARATOR + "lib";
        log       = app + SEPARATOR + "log";
        templates = app + SEPARATOR + "templates";
        dev       = app + SEPARATOR + "dev";
        dev_pub   = dev + SEPARATOR + "pub";
        libs      = app + SEPARATOR + "libs";
        pub       = app + SEPARATOR + "pub";
        
    }
    
    

}
