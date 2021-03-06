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
package php.agavi.ui.wizard.module;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.Project;
import org.netbeans.modules.php.api.phpmodule.PhpProgram.InvalidPhpProgramException;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import php.agavi.AgaviScript;

/**
 * Wizard Iterator for creating a new module
 * 
 * @author Markus Lervik
 */
public final class NewAgaviModuleWizardIterator implements WizardDescriptor.AsynchronousInstantiatingIterator {

    private int index;
    private WizardDescriptor wizard;
    private WizardDescriptor.Panel[] panels;

    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            panels = new WizardDescriptor.Panel[]{
                        new NewAgaviModuleWizardPanel1(),
                        new NewAgaviModuleWizardPanel2(),
                        new NewAgaviModuleWizardPanel3()
                    };
            String[] steps = createSteps();
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                if (steps[i] == null) {
                    // Default step name to component name of panel. Mainly
                    // useful for getting the name of the target chooser to
                    // appear in the list of steps.
                    steps[i] = c.getName();
                }
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, new Integer(i));
                    // Sets steps names for a panel
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
                }
            }
        }
        return panels;
    }

    /**
     * Generate the module. This is called when the user presses "Finish" on the
     * last panel. 
     * 
     * TODO: Fix the return value so that NetBeans opens the module folder when 
     * returning.
     * 
     * @return Empty set
     * @throws IOException if there's a problem generating the module
     */
    @Override
    public Set instantiate() throws IOException {

        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, "Generating module, actions and views...");
        Runnable generateModule = new GenerateModule();
        try {
            generateModule.run();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("E_UNEXPECTED")) {
                wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
                wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, "Something went horribly wrong!");
                DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(
                            "Something went horribly wrong while generating the module!\n" +
                            "Are you sure this is a proper Agavi project and the Agavi script\n"+
                            "is properly installed and working?",
                            NotifyDescriptor.ERROR_MESSAGE
                        )
                );
            }
        }

        return Collections.EMPTY_SET;

    }

    @Override
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }

    @Override
    public void uninitialize(WizardDescriptor wizard) {
        panels = null;
    }

    @Override
    public WizardDescriptor.Panel current() {
        return getPanels()[index];
    }

    @Override
    public String name() {
        return index + 1 + ". from " + getPanels().length;
    }

    @Override
    public boolean hasNext() {
        return index < getPanels().length - 1;
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    // You could safely ignore this method. Is is here to keep steps which were
    // there before this wizard was instantiated. It should be better handled
    // by NetBeans Wizard API itself rather than needed to be implemented by a
    // client code.
    private String[] createSteps() {
        String[] beforeSteps = null;
        Object prop = wizard.getProperty(WizardDescriptor.PROP_CONTENT_DATA);
        if (prop != null && prop instanceof String[]) {
            beforeSteps = (String[]) prop;
        }

        if (beforeSteps == null) {
            beforeSteps = new String[0];
        }

        String[] res = new String[(beforeSteps.length - 1) + panels.length];
        for (int i = 0; i < res.length; i++) {
            if (i < (beforeSteps.length - 1)) {
                res[i] = beforeSteps[i];
            } else {
                res[i] = panels[i - beforeSteps.length + 1].getComponent().getName();
            }
        }
        return res;
    }

    /**
     * Thread for creating the module in the background instead of the UI thread
     */
    private class GenerateModule implements Runnable {

        public GenerateModule() {
        }

        @Override
        public void run() {

            ProgressHandle progress = ProgressHandleFactory.createHandle("Generating module");

            String moduleName = (String) wizard.getProperty("moduleName");
            String[] actions = (String[]) wizard.getProperty("actions");
            @SuppressWarnings("unchecked")
            Map<String, String[]> actionViews = (Map<String, String[]>) wizard.getProperty("actionViews");



            // Calculate how many steps we have to do to get a finished
            // module (for the progress bar)
            int steps = actions.length + 1;

            for (String action : actions) {
                String[] views = actionViews.get(action);
                steps += views.length;
            }


            progress.start(steps);

            try {

                AgaviScript script;
                try {
                    script = AgaviScript.getDefault();
                } catch (InvalidPhpProgramException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
                Project project = Templates.getProject(wizard);

                FileObject sd = project.getProjectDirectory();
                File sourceDirectory = FileUtil.toFile(sd);
                
                try {
                    boolean retVal = script.moduleWizardCommand(sourceDirectory, moduleName, actions, actionViews, progress);
                    if (retVal == false) {
                        // Just a dummy exception to flag the main thread we had a hickup
                        // TODO: Fix this to use an Agavi specific exception
                        throw new IOException("E_UNEXPECTED");
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex.getMessage());
                }

            } catch (InterruptedException ex) {
                throw new RuntimeException(ex.getMessage());
            } finally {
                progress.finish();
            }

        }
    }
}
