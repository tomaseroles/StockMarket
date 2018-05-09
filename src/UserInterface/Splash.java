/*
 * Copyright (C) 2018 Tomas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package UserInterface;

import com.alee.laf.WebLookAndFeel;
import stockmarket.Consola;
import stockmarket.GameMain;
import stockmarket.Player;

/**
 *
 * @author Tomas
 */
public class Splash extends javax.swing.JFrame {

    /**
     * Creates new form Splash
     */
    public Splash() {
        setUndecorated(true);
        initComponents();
    }
    
    /* ---------------------------------------------------------------------------
    Funciones desarrolladas
    --------------------------------------------------------------------------  */ 

    private void Login(String usuario, String password) throws Exception{
        String titulo = "Autenticación";
        System.out.println("En Login");
        try{
            System.out.println("Still login");
            if (Player.LogIn(usuario, password)){
                GameMain.hAccesoAPI hAPI=new GameMain.hAccesoAPI();
                hAPI.start();
                Principal p = new Principal();
                setVisible(false);
                p.setVisible(true);
                p.setUser(usuario);
            } else {
                System.out.println("Usuario y/o contraseña incorrectos");
            }
        } catch(Exception ex){
            System.err.println("Error en autenticación de usuario" + ex.getMessage());
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        cmdSignup = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        txtCloseWindow = new javax.swing.JLabel();
        cmdSignIn = new javax.swing.JButton();
        cmdGuestEnter = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UserInterface/stockmarketbig.jpg"))); // NOI18N
        jLabel1.setText("jLabel1");

        cmdSignup.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        cmdSignup.setText("Sign Up");
        cmdSignup.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmdSignupMouseClicked(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel2.setLabelFor(txtUsername);
        jLabel2.setText("Username:");

        txtUsername.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel3.setLabelFor(txtPassword);
        jLabel3.setText("Password:");

        txtPassword.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N

        txtCloseWindow.setText("X");
        txtCloseWindow.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCloseWindowMouseClicked(evt);
            }
        });

        cmdSignIn.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        cmdSignIn.setText("Sign In");
        cmdSignIn.setBorder(null);
        cmdSignIn.setBorderPainted(false);
        cmdSignIn.setContentAreaFilled(false);
        cmdSignIn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        cmdSignIn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmdSignInMouseClicked(evt);
            }
        });

        cmdGuestEnter.setText("Enter as Guest");
        cmdGuestEnter.setBorderPainted(false);
        cmdGuestEnter.setContentAreaFilled(false);
        cmdGuestEnter.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cmdGuestEnter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmdGuestEnterMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cmdSignIn, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 72, Short.MAX_VALUE)
                        .addComponent(cmdGuestEnter, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtPassword)
                    .addComponent(txtUsername)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmdSignup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(30, 30, 30)
                .addComponent(txtCloseWindow, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addComponent(jLabel2)
                .addGap(4, 4, 4)
                .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(jLabel3)
                .addGap(4, 4, 4)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(155, 155, 155)
                .addComponent(cmdSignup)
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdSignIn, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdGuestEnter, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addComponent(txtCloseWindow)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmdSignupMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmdSignupMouseClicked
        // TODO add your handling code here:
        try{
            System.out.println("Usuario:    " + txtUsername.getText());
            System.out.println("Contraseña: " + txtPassword.getPassword().toString());
            Login(txtUsername.getText(),txtPassword.getPassword().toString());
        } catch (Exception ex){
            System.err.println("Error de autenticación.\n"+ex.getMessage());
        }
    }//GEN-LAST:event_cmdSignupMouseClicked

    private void cmdSignInMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmdSignInMouseClicked
        // TODO add your handling code here:
        UserData ud = new UserData();
        ud.setVisible(true);
    }//GEN-LAST:event_cmdSignInMouseClicked

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        setLocationRelativeTo(null);
    }//GEN-LAST:event_formWindowOpened

    private void txtCloseWindowMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCloseWindowMouseClicked
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_txtCloseWindowMouseClicked

    private void cmdGuestEnterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmdGuestEnterMouseClicked
        // TODO add your handling code here:
        try{
            Login("guest","");
        } catch (Exception ex){
            System.err.println("Error en entrada de guest.\n"+ex.getMessage());
        }

    }//GEN-LAST:event_cmdGuestEnterMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Splash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Splash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Splash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Splash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Splash().setVisible(true);
                WebLookAndFeel.initializeManagers ();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdGuestEnter;
    private javax.swing.JButton cmdSignIn;
    private javax.swing.JButton cmdSignup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel txtCloseWindow;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables

}
