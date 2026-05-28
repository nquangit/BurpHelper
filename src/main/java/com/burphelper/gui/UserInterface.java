/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burphelper.gui;

/**
 *
 * @author ASUS
 */
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import burp.api.montoya.MontoyaApi;

public class UserInterface {

    private JPanel ui;
    MontoyaApi api;

    public UserInterface(MontoyaApi api) {
        this.api = api;
        this.ui = new JPanel();
        this.ui.setLayout(new GridLayout(1, 1));

        this.ui.add(this.addTabPane());
        api.persistence().extensionData().setBoolean("headerAuthentication", true);
    }

    private JTabbedPane addTabPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Copy Authentication", (new CopyAuthenticationPanel(api)).initCopyAuthenticationPanel());

        return tabbedPane;
    }

    public JPanel getUI() {
        return this.ui;
    }

}
