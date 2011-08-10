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

package php.agavi.ui.modules;

import java.awt.Image;
import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.util.ImageUtilities;
import php.agavi.AgaviPhpFrameworkProvider;
import php.agavi.ui.wizard.module.NewAgaviModuleWizardAction;

/**
 * A module node representing a virtual "folder" containing Agavi modules.
 * The icon is a folder overlaid with the small library-badge
 * 
 * @author Markus Lervik
 */
class ModuleNode extends FilterNode {

    private static Image smallImage =
            ImageUtilities.loadImage("/org/netbeans/modules/php/project/ui/resources/libraries.gif"); // NOI18N

    public ModuleNode (Project proj) throws DataObjectNotFoundException {
        super(DataObject.find(AgaviPhpFrameworkProvider.locateInSrc(proj.getProjectDirectory(), "modules", true)).getNodeDelegate());
    }

    @Override
    public String getDisplayName() {
        return "Agavi modules";
    }

    @Override
    public Image getIcon(int type) {
        DataFolder root = DataFolder.findFolder(org.openide.filesystems.FileUtil.getConfigRoot());
        Image original = root.getNodeDelegate().getIcon(type);
        return ImageUtilities.mergeImages(original, smallImage, 7, 7);
    }

    @Override
    public Image getOpenedIcon(int type) {
        DataFolder root = DataFolder.findFolder(org.openide.filesystems.FileUtil.getConfigRoot());
        Image original = root.getNodeDelegate().getIcon(type);
        return ImageUtilities.mergeImages(original, smallImage, 7, 7);
    }

    
    @Override
    public Action[] getActions(boolean context) {
        
        return NewAgaviModuleWizardAction.createActions();
        
    }
    
    
    
}
