package com.burphelper.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.Action;
import javax.swing.AbstractAction;

import javax.swing.JComponent;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.hotkey.HotKeyContext;
import burp.api.montoya.ui.hotkey.HotKeyHandler;
import burp.api.montoya.ui.hotkey.HotKeyEvent;
import com.burphelper.pcopy.PCopyMenuActionHandler;
import com.burphelper.screenshort.ScreenShortMenuActionHandler;
import com.burphelper.intergrate.IntergrateMenuActionHandler;
import com.burphelper.req_utilities.ApplyCookieChangeActionMenuHandler;
import com.burphelper.req_utilities.AuthenticationCopyPasteActionMenuHandler;

public class ContextMenu implements ContextMenuItemsProvider {

    private final MontoyaApi api;
    private final PCopyMenuActionHandler pCopyMenuActionHandler;
    private final ScreenShortMenuActionHandler screenShortMenuActionHandler;
    private final IntergrateMenuActionHandler intergrateMenuActionHandler;
    private final AuthenticationCopyPasteActionMenuHandler authenticationCopyActionMenuHandler;
    private final ApplyCookieChangeActionMenuHandler applyCookieChangeActionMenuHandler;

    public ContextMenu(MontoyaApi api) {
        this.api = api;
        this.pCopyMenuActionHandler = new PCopyMenuActionHandler(api);
        this.screenShortMenuActionHandler = new ScreenShortMenuActionHandler(api);
        this.intergrateMenuActionHandler = new IntergrateMenuActionHandler(api);
        this.authenticationCopyActionMenuHandler = new AuthenticationCopyPasteActionMenuHandler(api);
        this.applyCookieChangeActionMenuHandler = new ApplyCookieChangeActionMenuHandler(api);
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        List<Component> menuItems = new ArrayList<>();

        // Tạo JMenu thay vì JMenuItem để có thể chứa các mục con
        JMenu screenshotMenu = new JMenu("ScreenShort");

        JMenuItem normalMenuItem = new JMenuItem("Normal");
        JMenuItem fullMenuItem = new JMenuItem("Full");
        JMenuItem fullEditedMenuItem = new JMenuItem("Full - Edited Request");
        JMenuItem fullOriginalMenuItem = new JMenuItem("Full - Original Request");

        normalMenuItem.addActionListener(e -> screenShortMenuActionHandler.handleNormalScreenshot());
        fullMenuItem.addActionListener(e -> screenShortMenuActionHandler.handleFullScreenshot());
        fullEditedMenuItem.addActionListener(e -> screenShortMenuActionHandler.handleEditedScreenshot());
        fullOriginalMenuItem.addActionListener(e -> screenShortMenuActionHandler.handleOriginalScreenshot());

        screenshotMenu.add(normalMenuItem);
        screenshotMenu.add(fullMenuItem);

        if (event.isFromTool(ToolType.PROXY)) {
            screenshotMenu.add(fullEditedMenuItem);
            screenshotMenu.add(fullOriginalMenuItem);
        }

        menuItems.add(screenshotMenu);

        // PCopy
        JMenu pCopy = new JMenu("PCopy");
        JMenuItem pCopyhasBody = new JMenuItem("PCopy has body");
        JMenuItem pCopynoBody = new JMenuItem("PCopy no body");
        // Lấy HttpRequestResponse từ sự kiện
        HttpRequestResponse requestResponse = event.messageEditorRequestResponse().isPresent()
                ? event.messageEditorRequestResponse().get().requestResponse()
                : event.selectedRequestResponses().get(0);

        pCopyhasBody.addActionListener(e -> pCopyMenuActionHandler.handleCopyToExcel(requestResponse));
        pCopynoBody.addActionListener(e -> pCopyMenuActionHandler.handleCopyToExcelNoBody(requestResponse));

        pCopy.add(pCopyhasBody);
        pCopy.add(pCopynoBody);

        menuItems.add(pCopy);

        // Intergrate
        JMenu intergrate = new JMenu("Intergrate");
        JMenuItem intergrateAction = new JMenuItem("Send");
        intergrateAction.addActionListener(e -> intergrateMenuActionHandler.handleSendReqToIntegration(requestResponse));
        KeyStroke intergrateKey = KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK);
        intergrateAction.setAccelerator(intergrateKey);

        intergrate.add(intergrateAction);

        menuItems.add(intergrate);

        // Copy Authentication
        String key = "Copy Authentication";
        JMenuItem copyAuthen = new JMenuItem(key);
        copyAuthen.addActionListener(e -> authenticationCopyActionMenuHandler.handleCopyAuthentication(requestResponse));
        KeyStroke copyAuthenKey = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK);
        copyAuthen.setAccelerator(copyAuthenKey);
        menuItems.add(copyAuthen);

        JMenuItem pasteAuthen = new JMenuItem("Paste Authentication");
        pasteAuthen.addActionListener(e -> authenticationCopyActionMenuHandler.handlePasteAuthentication(event));
        pasteAuthen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
        menuItems.add(pasteAuthen);
        
        JMenuItem applyCookieChange = new JMenuItem("Apply Cookie Change");
        applyCookieChange.addActionListener(e -> applyCookieChangeActionMenuHandler.applyCookieChange(event));
        applyCookieChange.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
        menuItems.add(applyCookieChange);

        return menuItems;
    }

    public void registerHotkey() {
        api.userInterface().registerHotKeyHandler(HotKeyContext.HTTP_MESSAGE_EDITOR, "Ctrl+Q", new HotKeyHandler() {
            @Override
            public void handle(HotKeyEvent evt) {
                intergrateMenuActionHandler.handleGlobalSendReqToIntegration(evt);
            }
        });

        api.userInterface().registerHotKeyHandler(HotKeyContext.HTTP_MESSAGE_EDITOR, "Ctrl+Shift+C", new HotKeyHandler() {
            @Override
            public void handle(HotKeyEvent evt) {
                authenticationCopyActionMenuHandler.handleGlobalCopyAuthentication(evt);
            }
        });

        api.userInterface().registerHotKeyHandler(HotKeyContext.HTTP_MESSAGE_EDITOR, "Ctrl+Shift+V", new HotKeyHandler() {
            @Override
            public void handle(HotKeyEvent evt) {
                authenticationCopyActionMenuHandler.handleGlobalPasteAuthentication(evt);
            }
        });
        
        api.userInterface().registerHotKeyHandler(HotKeyContext.HTTP_MESSAGE_EDITOR, "Ctrl+Shift+X", new HotKeyHandler() {
            @Override
            public void handle(HotKeyEvent evt) {
                applyCookieChangeActionMenuHandler.applyCookieChangeGlobal(evt);
            }
        });
    }

}
