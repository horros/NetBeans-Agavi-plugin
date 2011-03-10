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
package php.agavi.ui.options;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.swing.UIManager;
import org.netbeans.modules.php.api.util.UiUtils;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import php.agavi.AgaviScript;

/**
 * The panel where to select the Agavi script (under the IDE's Tools => Options)
 * 
 * @author Markus Lervik
 */
final class AgaviOptionsPanel extends javax.swing.JPanel {

    private final AgaviOptionsPanelController controller;
    private boolean isValid = false;

    AgaviOptionsPanel(AgaviOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        errorLabel.setText("");
    }

    public String getAgavi() {
        return agaviScriptLocation.getText();
    }

    public void setAgavi(String agavi) {
        agaviScriptLocation.setText(agavi);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        agaviScriptLocation = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        searchButton = new javax.swing.JButton();
        agaviVersion = new javax.swing.JLabel();
        errorLabel = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(AgaviOptionsPanel.class, "AgaviOptionsPanel.jLabel1.text")); // NOI18N

        agaviScriptLocation.setText(org.openide.util.NbBundle.getMessage(AgaviOptionsPanel.class, "AgaviOptionsPanel.agaviScriptLocation.text")); // NOI18N
        agaviScriptLocation.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                scriptLocationKeyReleased(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(AgaviOptionsPanel.class, "AgaviOptionsPanel.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(searchButton, org.openide.util.NbBundle.getMessage(AgaviOptionsPanel.class, "AgaviOptionsPanel.searchButton.text")); // NOI18N
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(agaviVersion, org.openide.util.NbBundle.getMessage(AgaviOptionsPanel.class, "AgaviOptionsPanel.agaviVersion.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, org.openide.util.NbBundle.getMessage(AgaviOptionsPanel.class, "AgaviOptionsPanel.errorLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(errorLabel)
                    .addComponent(agaviVersion)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(agaviScriptLocation, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(agaviScriptLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton)
                    .addComponent(searchButton))
                .addGap(18, 18, 18)
                .addComponent(agaviVersion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 160, Short.MAX_VALUE)
                .addComponent(errorLabel)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        File agaviScript = new FileChooserBuilder(AgaviOptionsPanel.class.getName()).setTitle("Select the Agavi script").setFilesOnly(true).showOpenDialog();
        if (agaviScript != null) {
            agaviScript = FileUtil.normalizeFile(agaviScript);
            agaviScriptLocation.setText(agaviScript.getAbsolutePath());
            testAgaviFile();
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed

        String agaviScript = UiUtils.SearchWindow.search(new UiUtils.SearchWindow.SearchWindowSupport() {

            @Override
            public List<String> detect() {
                List<String> scripts = AgaviScript.detectAgaviScript();
                return scripts;
            }

            @Override
            public String getWindowTitle() {
                return "Agavi scripts";
            }

            @Override
            public String getListTitle() {
                return "Agavi scripts";
            }

            @Override
            public String getPleaseWaitPart() {
                return "Agavi scripts";
            }

            @Override
            public String getNoItemsFound() {
                return "No Agavi scripts found";
            }
        });
        if (agaviScript != null) {
            agaviScriptLocation.setText(agaviScript);
            try {
                String version = AgaviScript.detectAgaviVersion(agaviScript);
                agaviVersion.setText("Agavi version detected: " + version);
                this.isValid = true;
            } catch (FileNotFoundException ex) {
                agaviVersion.setText("Invalid Agavi script");
            } catch (IOException ex) {
                agaviVersion.setText("Cannot detect Agavi version");
                Exceptions.printStackTrace(ex);
            }

        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void scriptLocationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_scriptLocationKeyReleased
        testAgaviFile();

    }

    private void testAgaviFile() {
        File f = new File(agaviScriptLocation.getText());
        if (f == null || !f.isFile()) {
            setError("Invalid script location");
            agaviVersion.setText("Cannot detect Agavi version");
            isValid = false;

        } else {
            try {
                String version = AgaviScript.detectAgaviVersion(agaviScriptLocation.getText());
                agaviVersion.setText("Agavi version detected: " + version);
                setError("");
                isValid = false;
            } catch (FileNotFoundException ex) {
                agaviVersion.setText("Invalid Agavi script");
            } catch (IOException ex) {
                agaviVersion.setText("Cannot detect Agavi version");
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_scriptLocationKeyReleased

    void load() {
        // TODO read settings and initialize GUI
        // Example:        
        // someCheckBox.setSelected(Preferences.userNodeForPackage(AgaviPanel.class).getBoolean("someFlag", false));
        // or for org.openide.util with API spec. version >= 7.4:
        // someCheckBox.setSelected(NbPreferences.forModule(AgaviPanel.class).getBoolean("someFlag", false));
        // or:
        // someTextField.setText(SomeSystemOption.getDefault().getSomeStringProperty());
    }

    void store() {
        // TODO store modified settings
        // Example:
        // Preferences.userNodeForPackage(AgaviPanel.class).putBoolean("someFlag", someCheckBox.isSelected());
        // or for org.openide.util with API spec. version >= 7.4:
        // NbPreferences.forModule(AgaviPanel.class).putBoolean("someFlag", someCheckBox.isSelected());
        // or:
        // SomeSystemOption.getDefault().setSomeStringProperty(someTextField.getText());
    }

    boolean valid() {
        // TODO check whether form is consistent and complete
        return isValid;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField agaviScriptLocation;
    private javax.swing.JLabel agaviVersion;
    private javax.swing.JButton browseButton;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton searchButton;
    // End of variables declaration//GEN-END:variables

    public void setError(String message) {
        errorLabel.setText(" "); // NOI18N
        errorLabel.setForeground(UIManager.getColor("nb.errorForeground")); // NOI18N
        errorLabel.setText(message);
    }
}
