/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NotApplicableWindow.java
 *
 * Created on 27.10.2010, 13:26:32
 */

package php.agavi.ui.xmlpalette.items;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;

/**
 *
 * @author mle
 */
public class NotApplicableWindow extends javax.swing.JPanel {
    private boolean dialogOK;
    private DialogDescriptor descriptor;
    private Dialog dialog;

    /** Creates new form NotApplicableWindow */
    public NotApplicableWindow() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel1 = new javax.swing.JLabel();

    jLabel1.setText(org.openide.util.NbBundle.getMessage(NotApplicableWindow.class, "NotApplicableWindow.jLabel1.text_1")); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel jLabel1;
  // End of variables declaration//GEN-END:variables

   public boolean showDialog(String text) {

        dialogOK = false;
        
        this.jLabel1.setText(text);

        descriptor = new DialogDescriptor(this, "Target not applicable", true,
                DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.OK_OPTION,
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        if (descriptor.getValue().equals(DialogDescriptor.OK_OPTION)) {
                            dialogOK = true;
                        }
                        dialog.dispose();
                    }
                });

        dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        dialog.setVisible(true);
        repaint();

        return dialogOK;

    }    
    
}
