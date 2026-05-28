package com.burphelper.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import burp.api.montoya.ui.hotkey.HotKeyContext;
import burp.api.montoya.ui.hotkey.HotKeyHandler;
import burp.api.montoya.ui.hotkey.HotKeyEvent;
import com.burphelper.feature.excelcopy.ExcelCopyMenuActionHandler;
import com.burphelper.feature.screenshot.legacy.LegacyScreenshotMenuActionHandler;
import com.burphelper.feature.screenshot.ScreenshotUtils;
import com.burphelper.feature.request.ApplyCookieChangeActionMenuHandler;
import com.burphelper.feature.request.AuthenticationCopyPasteActionMenuHandler;
import com.burphelper.feature.request.GenerateMenuHandler;

public class ContextMenu implements ContextMenuItemsProvider {

    private final MontoyaApi api;
    private final ExcelCopyMenuActionHandler excelCopyMenuActionHandler;
    private final LegacyScreenshotMenuActionHandler legacyScreenshotMenuActionHandler;
    private final ScreenshotUtils screenshotUtils;
    private final AuthenticationCopyPasteActionMenuHandler authenticationCopyActionMenuHandler;
    private final ApplyCookieChangeActionMenuHandler applyCookieChangeActionMenuHandler;
    private final GenerateMenuHandler generateMenuHandler;

    public ContextMenu(MontoyaApi api) {
        this.api = api;
        this.excelCopyMenuActionHandler = new ExcelCopyMenuActionHandler(api);
        this.legacyScreenshotMenuActionHandler = new LegacyScreenshotMenuActionHandler(api);
        this.screenshotUtils = new ScreenshotUtils(api);
        this.authenticationCopyActionMenuHandler = new AuthenticationCopyPasteActionMenuHandler(api);
        this.applyCookieChangeActionMenuHandler = new ApplyCookieChangeActionMenuHandler(api);
        this.generateMenuHandler = new GenerateMenuHandler(api);
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        List<Component> menuItems = new ArrayList<>();

        JMenu screenshotMenu = new JMenu("Screenshot");

        JMenuItem annotateComponent = new JMenuItem("Annotate Component (Normal) - Ctrl+Shift+S");
        annotateComponent.addActionListener(e -> screenshotUtils.handleNormalScreenshot());

        JMenuItem annotateFull = new JMenuItem("Annotate Full Req/Res (Full) - Ctrl+Shift+Space");
        annotateFull.addActionListener(e -> screenshotUtils.handleFullScreenshot());

        screenshotMenu.add(annotateComponent);
        screenshotMenu.add(annotateFull);

        menuItems.add(screenshotMenu);

        JMenu legacyScreenshotMenu = new JMenu("Screenshot (Legacy)");

        JMenuItem normalMenuItem = new JMenuItem("Normal");
        JMenuItem fullMenuItem = new JMenuItem("Full");
        JMenuItem fullEditedMenuItem = new JMenuItem("Full - Edited Request");
        JMenuItem fullOriginalMenuItem = new JMenuItem("Full - Original Request");

        normalMenuItem.addActionListener(e -> legacyScreenshotMenuActionHandler.handleNormalScreenshot());
        fullMenuItem.addActionListener(e -> legacyScreenshotMenuActionHandler.handleFullScreenshot());
        fullEditedMenuItem.addActionListener(e -> legacyScreenshotMenuActionHandler.handleEditedScreenshot());
        fullOriginalMenuItem.addActionListener(e -> legacyScreenshotMenuActionHandler.handleOriginalScreenshot());

        legacyScreenshotMenu.add(normalMenuItem);
        legacyScreenshotMenu.add(fullMenuItem);

        if (event.isFromTool(ToolType.PROXY)) {
            legacyScreenshotMenu.add(fullEditedMenuItem);
            legacyScreenshotMenu.add(fullOriginalMenuItem);
        }

        menuItems.add(legacyScreenshotMenu);

        List<HttpRequestResponse> selectedRequestResponses = new ArrayList<>();
        MessageEditorHttpRequestResponse editorReqRes = event.messageEditorRequestResponse().orElse(null);
        if (editorReqRes != null) {
            selectedRequestResponses.add(editorReqRes.requestResponse());
        } else {
            selectedRequestResponses.addAll(event.selectedRequestResponses());
        }
        HttpRequestResponse requestResponse = selectedRequestResponses.isEmpty()
                ? null
                : selectedRequestResponses.get(0);

        // PCopy
        if (!selectedRequestResponses.isEmpty()) {
            JMenu pCopy = new JMenu("PCopy");
            String countLabel = selectedRequestResponses.size() > 1
                    ? " (" + selectedRequestResponses.size() + " items)"
                    : "";
            JMenuItem pCopyhasBody = new JMenuItem("PCopy has body" + countLabel);
            JMenuItem pCopynoBody = new JMenuItem("PCopy no body" + countLabel);

            pCopyhasBody.addActionListener(e -> excelCopyMenuActionHandler.handleCopyToExcel(selectedRequestResponses));
            pCopynoBody.addActionListener(e -> excelCopyMenuActionHandler.handleCopyToExcelNoBody(selectedRequestResponses));

            pCopy.add(pCopyhasBody);
            pCopy.add(pCopynoBody);

            menuItems.add(pCopy);
        }


        // Copy Authentication
        if (requestResponse != null) {
            String key = "Copy Authentication";
            JMenuItem copyAuthen = new JMenuItem(key);
            copyAuthen.addActionListener(e -> authenticationCopyActionMenuHandler.handleCopyAuthentication(requestResponse));
            KeyStroke copyAuthenKey = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK);
            copyAuthen.setAccelerator(copyAuthenKey);
            menuItems.add(copyAuthen);
        }

        if (editorReqRes != null) {
            JMenuItem pasteAuthen = new JMenuItem("Paste Authentication");
            pasteAuthen.addActionListener(e -> authenticationCopyActionMenuHandler.handlePasteAuthentication(event));
            pasteAuthen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
            menuItems.add(pasteAuthen);

            JMenuItem applyCookieChange = new JMenuItem("Apply Cookie Change");
            applyCookieChange.addActionListener(e -> applyCookieChangeActionMenuHandler.applyCookieChange(event));
            applyCookieChange.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
            menuItems.add(applyCookieChange);
        }

        // Generate
        String generateMenuKey = "Generate";
        JMenuItem generateMenu = new JMenu(generateMenuKey);

        JMenuItem generateUUID = new JMenuItem("Gen UUIDv4");
        generateUUID.addActionListener(e -> generateMenuHandler.generateUUIDv4(event));

        generateMenu.add(generateUUID);
        menuItems.add(generateMenu);

        return menuItems;
    }

    public void registerHotkey() {
        api.userInterface().registerHotKeyHandler(HotKeyContext.HTTP_MESSAGE_EDITOR, "Ctrl+Shift+S", new HotKeyHandler() {
            @Override
            public void handle(HotKeyEvent evt) {
                screenshotUtils.handleNormalScreenshot();
            }
        });

        api.userInterface().registerHotKeyHandler(HotKeyContext.HTTP_MESSAGE_EDITOR, "Ctrl+Shift+Space", new HotKeyHandler() {
            @Override
            public void handle(HotKeyEvent evt) {
                screenshotUtils.handleFullScreenshot();
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
