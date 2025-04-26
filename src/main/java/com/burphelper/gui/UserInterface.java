/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burphelper.gui;

/**
 *
 * @author ASUS
 */
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import burp.api.montoya.MontoyaApi;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserInterface {

    private JPanel ui;
    MontoyaApi api;

    public UserInterface(MontoyaApi api) {
        this.api = api;
        this.ui = new JPanel();
        this.ui.setLayout(new GridLayout(1, 1));

        this.ui.add(this.addTabPane());
    }

    private JTabbedPane addTabPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Copy Authentication", initCopyAuthenticationPanel());

        return tabbedPane;
    }

    private javax.swing.JButton saveBtn;
    private javax.swing.JCheckBox headerAuthenticationCheckBox;
    private javax.swing.JCheckBox bodyAuthenticationCheckBox;
    private javax.swing.JComboBox<String> bodyTypeComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField customHeaderTextField;
    private javax.swing.JTextField bodyIdentifierTextField;

    private JPanel initCopyAuthenticationPanel() {

        Boolean headerAuthentication = api.persistence().extensionData().getBoolean("headerAuthentication");
        Boolean bodyAuthentication = api.persistence().extensionData().getBoolean("bodyAuthentication");
        String bodyType = api.persistence().extensionData().getString("bodyType");
        String bodyField = api.persistence().extensionData().getString("bodyField");
        String customHeaderAuthentication = api.persistence().extensionData().getString("customHeaderAuthentication");

        JPanel copyAuthenticationPanel = new JPanel();
        headerAuthenticationCheckBox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        bodyIdentifierTextField = new javax.swing.JTextField();
        bodyAuthenticationCheckBox = new javax.swing.JCheckBox();
        bodyTypeComboBox = new javax.swing.JComboBox<>();
        saveBtn = new javax.swing.JButton();
        customHeaderTextField = new javax.swing.JTextField();

        if (headerAuthentication != null) {
            headerAuthenticationCheckBox.setSelected(headerAuthentication.booleanValue());

        } else {
            headerAuthenticationCheckBox.setSelected(true);

        }

        headerAuthenticationCheckBox.setText("Cookie/Authorization header");

        jLabel1.setText("Body Authentication handle");

        if (bodyField != null) {
            bodyIdentifierTextField.setText(bodyField);

        } else {
            bodyIdentifierTextField.setText("null");

        }

        bodyAuthenticationCheckBox.setText("Enable");
        if (bodyAuthentication != null) {
            bodyAuthenticationCheckBox.setSelected(bodyAuthentication.booleanValue());

        } else {
            bodyAuthenticationCheckBox.setSelected(false);

        }

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

        if (customHeaderAuthentication != null) {
            customHeaderTextField.setText(customHeaderAuthentication);
        } else {
            customHeaderTextField.setText("Custom-Header1, Custom-Header2");
        }

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
        
        if (customHeader != null && !customHeader.strip().equals("")) {
            api.persistence().extensionData().setString("customHeaderAuthentication", customHeader);
        }

        api.persistence().extensionData().setBoolean("headerAuthentication", headerAuthen);

        api.persistence().extensionData().setBoolean("bodyAuthentication", bodyAuthen);
        if (bodyAuthen) {
            api.persistence().extensionData().setString("bodyType", bodyType);
            api.persistence().extensionData().setString("bodyField", bodyField);
        }
        api.logging().logToOutput("Saved config");
//        api.logging().logToOutput(headerAuthen);
//        api.logging().logToOutput(bodyAuthen);
//        api.logging().logToOutput(bodyType);
//        api.logging().logToOutput(bodyField);

    }

    public JPanel getUI() {
        return this.ui;
    }

}
