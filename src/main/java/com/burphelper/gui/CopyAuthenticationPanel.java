/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burphelper.gui;

import burp.api.montoya.MontoyaApi;
import com.burphelper.config.ExtensionConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author ASUS
 */
public class CopyAuthenticationPanel {

    private javax.swing.JButton saveBtn;
    private javax.swing.JCheckBox headerAuthenticationCheckBox;
    private javax.swing.JCheckBox bodyAuthenticationCheckBox;
    private javax.swing.JComboBox<String> bodyTypeComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField customHeaderTextField;
    private javax.swing.JTextField bodyIdentifierTextField;
    MontoyaApi api;

    public CopyAuthenticationPanel(MontoyaApi api) {
        this.api = api;
    }

    public JPanel initCopyAuthenticationPanel() {

        ExtensionConfig config = new ExtensionConfig(api);
        boolean headerAuthentication = config.isHeaderAuthenticationEnabled();
        boolean bodyAuthentication = config.isBodyAuthenticationEnabled();
        String bodyType = config.getBodyType();
        String bodyField = config.getBodyField();
        String customHeaderAuthentication = config.getCustomHeaderAuthentication();

        JPanel copyAuthenticationPanel = new JPanel();
        headerAuthenticationCheckBox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        bodyIdentifierTextField = new javax.swing.JTextField();
        bodyAuthenticationCheckBox = new javax.swing.JCheckBox();
        bodyTypeComboBox = new javax.swing.JComboBox<>();
        saveBtn = new javax.swing.JButton();
        customHeaderTextField = new javax.swing.JTextField();

        headerAuthenticationCheckBox.setSelected(headerAuthentication);
        headerAuthenticationCheckBox.setText("Cookie/Authorization header");

        jLabel1.setText("Body Authentication handle");

        bodyIdentifierTextField.setText(bodyField);

        bodyAuthenticationCheckBox.setText("Enable");
        bodyAuthenticationCheckBox.setSelected(bodyAuthentication);

        List<String> bodyTypeArr = new ArrayList<>(Arrays.asList("json", "x-url-form-encoded", "xml"));
        String[] bodyTypes = bodyTypeArr.toArray(String[]::new);
        bodyTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(bodyTypes));
        int index = bodyTypeArr.indexOf(bodyType);
        bodyTypeComboBox.setSelectedIndex(index);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(bodyIdentifierTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(bodyAuthenticationCheckBox)
                                                .addGap(18, 18, 18)
                                                .addComponent(bodyTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 693, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(bodyAuthenticationCheckBox)
                                        .addComponent(bodyTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(bodyIdentifierTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(25, Short.MAX_VALUE))
        );

        saveBtn.setText("Save");
        saveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        customHeaderTextField.setText(customHeaderAuthentication);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(copyAuthenticationPanel);
        copyAuthenticationPanel.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(headerAuthenticationCheckBox)
                                                        .addComponent(jLabel1)
                                                        .addComponent(saveBtn))
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addComponent(customHeaderTextField))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(headerAuthenticationCheckBox)
                                .addGap(10, 10, 10)
                                .addComponent(customHeaderTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(saveBtn)
                                .addContainerGap(511, Short.MAX_VALUE))
        );
        return copyAuthenticationPanel;
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        boolean headerAuthen = this.headerAuthenticationCheckBox.isSelected();
        boolean bodyAuthen = this.bodyAuthenticationCheckBox.isSelected();
        String bodyType = (String) this.bodyTypeComboBox.getSelectedItem();
        String bodyField = this.bodyIdentifierTextField.getText();
        String customHeader = this.customHeaderTextField.getText();

        ExtensionConfig config = new ExtensionConfig(api);

        if (customHeader != null) {
            config.setCustomHeaderAuthentication(customHeader);
        }

        config.setHeaderAuthenticationEnabled(headerAuthen);
        config.setBodyAuthenticationEnabled(bodyAuthen);
        if (bodyAuthen) {
            config.setBodyType(bodyType);
            config.setBodyField(bodyField);
        }
        api.logging().logToOutput("Saved config");
    }
}
