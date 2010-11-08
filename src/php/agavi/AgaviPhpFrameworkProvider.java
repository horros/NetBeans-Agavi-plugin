/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package php.agavi;

import org.netbeans.modules.versioning.spi.VersioningSystem;
import php.agavi.commands.AgaviFrameworkCommandSupport;
import php.agavi.editor.AgaviEditorExtender;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.netbeans.modules.php.api.phpmodule.BadgeIcon;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.spi.commands.FrameworkCommandSupport;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpFrameworkProvider;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleActionsExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleCustomizerExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleIgnoredFilesExtender;
import org.netbeans.modules.versioning.VersioningManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ImageUtilities;

/**
 * Encapsulates the Agavi PHP framework.
 *
 * <p>This class provides support for the Agavi PHP frameworks. It is used
 * to find out whether a PHP module is already extended by the Agavi PHP 
 * framework and to retrieve a PHP framework's specific configuration files.</p>
 *
 * <p>Instances of this class are registered in the <code>{@value org.netbeans.modules.php.api.phpmodule.PhpFrameworks#FRAMEWORK_PATH}</code>
 * in the module layer, see {@link Registration}.</p>
 * 
 * <p>A special shout out goes to Thomas Mysik for the Symfony framework support,
 * as this is to a big degree modelled after it.</p>
 *
 * @author Markus Lervik <markus.lervik@necora.fi>
 */
public final class AgaviPhpFrameworkProvider extends PhpFrameworkProvider {

    private static final String CONFIG_FILE = "action_filters.xml";
    private static final String ICON_PATH = "php/agavi/ui/resources/agavi_badge_8.png"; // NOI18N
    
    private static final Set<String> CONFIG_FILE_EXTENSIONS = new HashSet<String>();
    private static AgaviPhpFrameworkProvider INSTANCE = new AgaviPhpFrameworkProvider();
    
    private final BadgeIcon badgeIcon;

    private AgaviPhpFrameworkProvider() {
        super("Agavi", "The Agavi PHP framework");
        badgeIcon = new BadgeIcon(
                ImageUtilities.loadImage(ICON_PATH),
                AgaviPhpFrameworkProvider.class.getResource("/" + ICON_PATH)); // NOI18N
    }
    
    @Override
    public BadgeIcon getBadgeIcon() {
        return badgeIcon;
    }    
    
    /**
     * Returns an instance of the class.
     * The @PhpFrameworkProvider.Registration annotation is what properly registers
     * the framework provider with the PHP plugin and makes the framework available
     * in eg. the new project -wizard
     * 
     * @return a singleton instance of this framework provider 
     */
    @PhpFrameworkProvider.Registration(position=100)
    public static AgaviPhpFrameworkProvider getInstance() {
        return INSTANCE;
    }
    
    // Agavi only uses XML-files as config files.
    // Any eg. Propel config files needs to be handled
    // by a Propel-plugin
    static {
        CONFIG_FILE_EXTENSIONS.add("xml");
    }
        
    /**
     * Try to locate (find) a <code>relativePath</code> in source directory.
     * Currently, it searches source dir and its subdirs (if <code>subdirs</code> equals {@code true}).
     * @return {@link FileObject} or {@code null} if not found
     */
    public static FileObject locate(FileObject startDir, String relativePath, boolean subdirs) {
        
         VersioningManager vManager = VersioningManager.getInstance();

        FileObject fileObject = startDir.getFileObject(relativePath);
        if (fileObject != null || !subdirs) {
            return fileObject;
        }
        for (FileObject child : startDir.getChildren()) {
            
            File childFile = FileUtil.toFile(child);
            
            if (childFile != null) {
                VersioningSystem owner = vManager.getOwner(childFile);
                if (!owner.getVisibilityQuery().isVisible(childFile)) {
                    continue;
                }
            }            
            
            fileObject = child.getFileObject(relativePath);
            
            if (fileObject != null) {
                return fileObject;
            } else if (child.isFolder()) {
                fileObject = locate(child, relativePath, subdirs);
                if (fileObject != null)
                    return fileObject;
            }
        }
        return null;
    }

    /**
     * Try to locate (find) files matching <code>searchPattern</code> in source directory.
     * Currently, it searches source dir and its subdirs (if <code>subdirs</code> equals {@code true}).
     * @return {@link FileObject} or {@code null} if not found
     */
    public static List<FileObject> locate(FileObject startDir, Pattern searchPattern, boolean subdirs) {

        VersioningManager vManager = VersioningManager.getInstance();
        
        
        List<FileObject> list = Collections.synchronizedList(new ArrayList<FileObject>());

        for (FileObject child : startDir.getChildren()) {
           
            File childFile = FileUtil.toFile(child);
            
            if (childFile != null) {
                VersioningSystem owner = vManager.getOwner(childFile);
                if (!owner.getVisibilityQuery().isVisible(childFile)) {
                    continue;
                }
            }
            
            if (searchPattern.matcher(child.getNameExt()).matches()) {
                list.add(child);
            }
            
            if (child.isFolder()) {
                List<FileObject> sublist = locate(child, searchPattern, subdirs);
                if (sublist != null && sublist.size() > 0) {
                    for (FileObject fileObject : sublist) {
                        System.out.println(fileObject.getNameExt());
                        list.add(fileObject);
                    }
                }
            }
        }
        if (list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }
    
    
    /**
     * Attempt to detect whether or not the PHP project is an Agavi project
     * 
     * @param phpModule the PHP project
     * @return true if this is an Agavi project, false otherwise
     */
    @Override
    public boolean isInPhpModule(PhpModule phpModule) {
        FileObject config = locate(phpModule.getSourceDirectory(), CONFIG_FILE, true);
        return config != null && config.isData();    
    }

    /**
     * Return an array of framework configuration files. These will turn up in the
     * project view under "Important files"
     * 
     * @param phpModule the current Agavi project
     * @return an array of XML files from the application's config-directory
     */
    @Override
    public File[] getConfigurationFiles(PhpModule phpModule) {
        List<File> files = new LinkedList<File>();
        FileObject appConfig = locate(phpModule.getSourceDirectory(), "app/config", true); // NOI18N
        if (appConfig != null) {
            List<FileObject> fileObjects = new LinkedList<FileObject>();
            Enumeration<? extends FileObject> children = appConfig.getChildren(false);
            while (children.hasMoreElements()) {
                FileObject child = children.nextElement();
                if (child.isData()
                        && (CONFIG_FILE_EXTENSIONS.contains(child.getExt().toLowerCase()) || FileUtils.isPhpFile(child))) {
                    fileObjects.add(child);
                }
            }
            Collections.sort(fileObjects, new Comparator<FileObject>() {
                @Override
                public int compare(FileObject o1, FileObject o2) {
                    // php files go last
                    boolean phpFile1 = FileUtils.isPhpFile(o1);
                    boolean phpFile2 = FileUtils.isPhpFile(o2);
                    if (phpFile1 && phpFile2) {
                        return o1.getNameExt().compareTo(o2.getNameExt());
                    } else if (phpFile1) {
                        return 1;
                    } else if (phpFile2) {
                        return -1;
                    }

                    // compare extensions, then full names
                    String ext1 = o1.getExt();
                    String ext2 = o2.getExt();
                    if (ext1.equals(ext2)) {
                        return o1.getNameExt().compareToIgnoreCase(o2.getNameExt());
                    }
                    return ext1.compareToIgnoreCase(ext2);
                }
            });

            for (FileObject fo : fileObjects) {
                files.add(FileUtil.toFile(fo));
            }
        }
        
        return files.toArray(new File[files.size()]);
    }

    /**
     * Creates a module extender for the Agavi framework and the given
     * PHP module (project)
     * 
     * @param  phpModule the PHP module to be extended; can be <code>null</code>, e.g., if the
     *         method is called while creating a new PHP application, in which
     *         case the module doesn't exist yet.
     * @return a new PHP module extender; can be <code>null</code> if the framework doesn't support
     *         extending (either PHP modules in general or the particular PHP module
     *         passed in the <code>phpModule</code> parameter).
     */
    @Override
    public PhpModuleExtender createPhpModuleExtender(PhpModule phpModule) {
        return new AgaviPhpModuleExtender();
    }
    /**
     * Get Agavi module properties for the given PHP module (e.g. web root
     * and test file locations).
     *
     * @param  phpModule the PHP module which properties are going to be changed
     * @return new PHP module properties
     */
    @Override
    public PhpModuleProperties getPhpModuleProperties(PhpModule phpModule) {
        PhpModuleProperties properties = new PhpModuleProperties();
        FileObject web = locate(phpModule.getSourceDirectory(), "pub", true); // NOI18N
        if (web != null) {
            properties = properties.setWebRoot(web);
        }
        FileObject testUnit = locate(phpModule.getSourceDirectory(), "test/unit", true); // NOI18N
        if (testUnit != null) {
            properties = properties.setTests(testUnit);
        }
        return properties;

    }

    /**
     * Get the Agavi framework actions extender which extends the PHP module
     * with actions specific for the Agavi framework
     * 
     * @param  phpModule the PHP module which actions are going to be extended
     * @return a new PHP module actions extender, can be <code>null</code> if the framework doesn't support
     *         extending of actions 
     */
    @Override
    public PhpModuleActionsExtender getActionsExtender(PhpModule phpModule) {
        return new AgaviPhpModuleActionsExtender();
    }

    /**
     * Get the Agavi-specific Ignored Files extender which can be used to tell
     * NetBeans to hide specific types of files (e.g. cache)
     * 
     * @param  phpModule the PHP module which ignored files are going to be extended
     * @return PHP module ignored files extender, can be <code>null</code> if the framework doesn't need
     *         to recommend to hide any files or folders
     */
    @Override
    public PhpModuleIgnoredFilesExtender getIgnoredFilesExtender(PhpModule phpModule) {
        return new AgaviPhpModuleIgnoredFilesExtender();
    }

    /**
     * Get the Agavi-specific framework command support for the given PHP module
     * 
     * @param  phpModule the PHP module for which framework command support is to be gotten
     * @return framework command support, can be <code>null</code> if the framework doesn't support
     *         running external commands
     */
    @Override
    public FrameworkCommandSupport getFrameworkCommandSupport(PhpModule phpModule) {
        return new AgaviFrameworkCommandSupport(phpModule);
    }

    /**
     * Get the Agavi-specific Editor Extender for the given PHP module
     * 
     * @param  phpModule the PHP module for which editor extender is to be gotten
     * @return editor extender, can be <code>null</code> if the framework doesn't provide
     *         any additional fields/classes etx. to code completion etc. 
     */
    @Override
    public EditorExtender getEditorExtender(PhpModule phpModule) {
        return new AgaviEditorExtender();
    }
   
    @Override
    public PhpModuleCustomizerExtender createPhpModuleCustomizerExtender(PhpModule phpModule) {
        return new AgaviPhpModuleCustomiserExtender(phpModule);
    }
    
    
}
