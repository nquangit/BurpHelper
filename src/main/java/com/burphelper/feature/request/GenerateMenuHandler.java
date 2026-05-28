package com.burphelper.feature.request;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.UUID;

public class GenerateMenuHandler {

    MontoyaApi api;

    public GenerateMenuHandler(MontoyaApi api) {
        this.api = api;
    }

    public void generateUUIDv4(ContextMenuEvent evt) {
        String uuidv4 = UUID.randomUUID().toString();
        StringSelection stringSelection = new StringSelection(uuidv4);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
}
