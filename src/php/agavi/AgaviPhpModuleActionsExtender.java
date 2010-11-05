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

import php.agavi.ui.actions.ClearCacheAction;
import php.agavi.util.AgaviUtils;
import php.agavi.ui.actions.AgaviGoToViewAction;
import php.agavi.ui.actions.AgaviGoToActionAction;
import java.util.Collections;
import java.util.List;
import javax.swing.Action;
import org.netbeans.modules.php.spi.actions.GoToActionAction;
import org.netbeans.modules.php.spi.actions.GoToViewAction;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleActionsExtender;
import org.openide.filesystems.FileObject;


/**
 * Extends the PHP module with support for Agavi-related actions
 * 
 * @author Markus Lervik <markus.lervik@necora.fi>
 */
class AgaviPhpModuleActionsExtender extends PhpModuleActionsExtender {

    // Only one action in the right-click-in-project menu for the time being,
    // "Clear cache", which doesn't currently do anything
    public static final List<Action> ACTIONS = Collections.<Action>singletonList(ClearCacheAction.getInstance());
    
    
    public AgaviPhpModuleActionsExtender() {
    }
    
    /**
     * The name of the menu item in the project's context menu
     * 
     * @return "Agavi", becuase that's what we'll call it
     */
    @Override
    public String getMenuName() {
        return "Agavi";
    }
    
    /**
     * Return a list of actions that will be available under the
     * "Agavi" context menu
     * 
     * @return list of actions
     */
    @Override
    public List<? extends Action> getActions() {
        return ACTIONS;
    }
    
    /**
     * Determine if the file passed as the FileObject -parameter is a view
     * 
     * @param fo the file currently open in the editor
     * @return true if the file is determined to be a view, false otherwise
     */
     @Override
    public boolean isViewWithAction(FileObject fo) {
         return AgaviUtils.isView(fo) && AgaviUtils.getAction(fo) != null;
    }
    
    /**
     * Get the Agavi-specific Go To Action implementation. Cannot return null
     * if the given FileObject is a view.
     * 
     * @param fo file object to get action for (the currently opened file in editor)
     * @param offset current offset in the file object
     * @return instance of framework specific Go To Action action or {@code null}
     * @see #isViewWithAction(FileObject)
     */ 
    @Override
    public GoToActionAction getGoToActionAction(FileObject fo, int offset) {
        return new AgaviGoToActionAction(fo);
    }     
    
    /**
     * Get instance of the Agavi specific Go To View action. It can return {@code null}
     * only if the given file object is not an {@link #isActionWithView(FileObject) <em>action</em>}.
     * <p>
     * The default implementation returns {@code null}.
     * @param fo file object to get action for (the currently opened file in editor)
     * @param offset current offset in the file object
     * @return instance of framework specific Go To View action or {@code null}
     * @see #isActionWithView(FileObject)
     */
    @Override
    public GoToViewAction getGoToViewAction(FileObject fo, int offset) {
        return new AgaviGoToViewAction(fo, offset);
    }

}
