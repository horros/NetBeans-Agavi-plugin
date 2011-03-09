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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.php.api.phpmodule.PhpInterpreter;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpProgram.InvalidPhpProgramException;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleExtender;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import php.agavi.ui.wizard.NewProjectConfigurationPanel;

/**
 * Provides support for extending the PHP module with the Agavi framework
 * 
 * @author Markus Lervik <markus.lervik@necora.fi>
 */
class AgaviPhpModuleExtender extends PhpModuleExtender {

    private NewProjectConfigurationPanel panel = null;    

    AgaviPhpModuleExtender(PhpModule phpModule) {
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        getPanel().addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        getPanel().removeChangeListener(listener);
    }

    @Override
    public JComponent getComponent() {
        return getPanel();
    }

    @Override
    public HelpCtx getHelp() {
        return null;
    }

    @Override
    public boolean isValid() {
        return getErrorMessage() == null;
    }

    @Override
    public String getErrorMessage() {
        try {
            PhpInterpreter.getDefault();
        } catch (InvalidPhpProgramException ex) {
            return ex.getLocalizedMessage();
        }
        return getPanel().getErrorMessage();
    }

    @Override
    public String getWarningMessage() {
        return getPanel().getWarningMessage();
    }

    private synchronized NewProjectConfigurationPanel getPanel() {
        if (panel == null) {
            panel = new NewProjectConfigurationPanel();
        }
        return panel;
    }
    
    
    /**
     * Extend the project with the Agavi support. This is only called for new
     * projects, so this is the place to run Agavi's "project-create" -target.
     * 
     * @param phpModule
     * @return A set of configuration files
     * @throws org.netbeans.modules.php.spi.phpmodule.PhpModuleExtender.ExtendingException 
     */
    @Override
    public Set<FileObject> extend(PhpModule phpModule) throws ExtendingException {
        
        Set<FileObject> files = new HashSet<FileObject>();
        
        AgaviScript agaviScript = null;
                
        try {
            agaviScript = AgaviScript.getDefault();
        } catch (InvalidPhpProgramException ex) {
            // should not happen, must be handled in the wizard
            Exceptions.printStackTrace(ex);
        }

        try {
            agaviScript.initProject(phpModule, new String[] {"project-create"});
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            throw new ExtendingException(ex.getMessage());
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
            throw new ExtendingException(ex.getMessage());
        }
        
        FileObject routing = AgaviPhpFrameworkProvider.locate(phpModule.getSourceDirectory(), "config/routing.xml", true); // NOI18N
        if (routing != null) {
            files.add(routing);
        }
        
        FileObject settings = AgaviPhpFrameworkProvider.locate(phpModule.getSourceDirectory(), "config/settings.xml", true); // NOI18N
        if (settings != null) {
            files.add(settings);
        }

        return files;
        
    }


}
