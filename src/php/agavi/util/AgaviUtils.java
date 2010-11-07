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


package php.agavi.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import php.agavi.AgaviPhpFrameworkProvider;
import php.agavi.ui.actions.ListViewsDialog;

/**
 *
 * @author Markus Lervik <markus.lervik@necora.fi>
 */
public final class AgaviUtils {

    public static final String DIR_VIEWS = "views";
    public static final String DIR_ACTIONS = "actions";
    public static final Pattern VIEW_NAME = Pattern.compile("^(\\w+)(?:Success|Input|Error)View");
    public static final Pattern VIEW_FILE = Pattern.compile("^(\\w+)(?:Success|Input|Error)View.class.php");
    public static final Pattern TEMPLATE_FILE = Pattern.compile("^(\\w+)(?:Success|Input|Error).php$");
    public static final Pattern ACTION_FILE = Pattern.compile("^(\\w+)Action.class.php$");
    public static final Pattern ACTION_NAME = Pattern.compile("^(\\w+)Action$");
    
    public static String ACTION_METHOD_PREFIX = "execute";
    
    public static FileObject getAction(FileObject viewFile) {
        
        
        Matcher viewMatcher = VIEW_FILE.matcher(viewFile.getNameExt());
        Matcher templateMatcher = TEMPLATE_FILE.matcher(viewFile.getNameExt());

        String prefix = null;
        String action = null;
        
        if (viewMatcher.matches()) {
            prefix = viewMatcher.group(1);
        } else if (templateMatcher.matches()) {
            prefix = templateMatcher.group(1);
        }

        if (prefix != null && prefix.length() > 0) {
            action = prefix + "Action.class.php";
        } else {
            return null;
        }
        
        FileObject parent = viewFile.getParent();
        
        PhpModule module = PhpModule.inferPhpModule();
        System.out.println("Attempting to find " + action);
        System.out.println("Source dir: " + module.getSourceDirectory().getName());
        System.out.println("In AgaviUtils.getAction(): " + parent.getName());
        // We don't really have a way to know if this is a sub-action or a
        // sub-sub-action or whatever, so we must attempt to traverse backwards
        // up the directory hierarchy and try to find an "actions"-folder.
        // If we do, try to find an action file within the folder or its subfolders
        do {
            System.out.println(parent.getName());
            FileObject f = AgaviPhpFrameworkProvider.locate(parent, action, true);
            if (f != null) {
                return f;
            } else {
                parent = parent.getParent();
            }
            
        } while(!module.getSourceDirectory().getName().equals(parent.getName()));
        
        
        return null;
    }
    
    /**
     * Attempt to determine if this file is a view
     * 
     * @param fo
     * @return 
     */
    public static boolean isView(FileObject fo) {
        
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        for (PhpClass phpClass : editorSupport.getClasses(fo)) {
            if (VIEW_NAME.matcher(phpClass.getName()).matches() && 
                VIEW_FILE.matcher(fo.getNameExt()).matches()) {
                return true;
                }
        }
        return false;
        
    }   
    
    public static boolean isViewWithAction(FileObject fo) {
        return isView(fo) && getAction(fo) != null;
    }
    
    public static boolean isActionWithView(FileObject fo) {
        return isAction(fo);
    }

    public static FileObject getView(FileObject fo, PhpBaseElement phpElement) {
        
        Matcher actionMatcher = ACTION_FILE.matcher(fo.getNameExt());
        System.out.println("getView: " + fo.getNameExt());
        if (actionMatcher.matches()) {
        
            FileObject parent = fo.getParent();
        
            PhpModule module = PhpModule.inferPhpModule();
            
            Pattern p = Pattern.compile(actionMatcher.group(1) + "(Success|Error|Input)View.class.php");
            
            List<FileObject> list = null;
            do {
                System.out.println(parent.getName());
                list = AgaviPhpFrameworkProvider.locate(parent, p, true);
                if (list != null) {
                    break;
                } else {
                    parent = parent.getParent();
                }
            
            } while(!module.getSourceDirectory().getName().equals(parent.getName()));
            
            if (list != null && !list.isEmpty()) {
                ListViewsDialog dialog = new ListViewsDialog(null, true, list);
                dialog.setVisible(true);
                return dialog.getSelectedFile();
                
            } else {
                return null;
            }
        }
        
        return null;

        
    }

    private static boolean isAction(FileObject fo) {
        
        System.out.println("isAction: " +fo.getNameExt());
        
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        for (PhpClass phpClass : editorSupport.getClasses(fo)) {
            System.out.println(phpClass.getName());
            if (ACTION_NAME.matcher(phpClass.getName()).matches() && 
                ACTION_FILE.matcher(fo.getNameExt()).matches()) {
                return true;
                }
        }
        return false;    
    
    }

    
}
