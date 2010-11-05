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

package php.agavi.ui.actions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.spi.actions.GoToActionAction;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import php.agavi.util.AgaviUtils;

/**
 *
 * @author Markus Lervik <markus.lervik@necora.fi>
 */
public class AgaviGoToActionAction extends GoToActionAction {

    private final FileObject fo;
    private static final Pattern ACTION_METHOD_NAME = Pattern.compile("^execute\\w+");
    /*
     * The fo-object passed is a view or a template
     */

    public AgaviGoToActionAction(FileObject fo) {
        this.fo = fo;

    }

    /**
     * Given a view or a template in the file referenced by the fo-object,
     * attempt to determine that it really is a view or a template, and from
     * there try to find the action file
     * 
     * @return boolean true if the action was found, false otherwise
     */
    @Override
    public boolean goToAction() {

        FileObject action = AgaviUtils.getAction(fo);
        if (action != null) {
            UiUtils.open(action, getActionMethodOffset(action));
            return true;
        }
        return false;
    }

    private int getActionMethodOffset(FileObject action) {
        String actionMethodName = getActionMethodName(fo.getName());
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        for (PhpClass phpClass : editorSupport.getClasses(action)) {
            if (actionMethodName != null) {
                for (PhpClass.Method method : phpClass.getMethods()) {
                    if (actionMethodName.equals(method.getName())) {
                        return method.getOffset();
                    }
                }
            }
            return phpClass.getOffset();
        }
        return DEFAULT_OFFSET;
    }

    static String getActionMethodName(String filename) {
        Matcher matcher = ACTION_METHOD_NAME.matcher(filename);
        if (matcher.find()) {
            String group = matcher.group(1);
            return AgaviUtils.ACTION_METHOD_PREFIX + group.substring(0, 1).toUpperCase() + group.substring(1);
        }
        return null;

    }
}
