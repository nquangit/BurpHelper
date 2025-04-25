package com.burphelper;

import com.burphelper.gui.ContextMenu;
import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

public class BurpHelper implements BurpExtension {

    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("BurpHelper");

        api.logging().logToOutput("BurpHelper extension initialized");
        api.logging().logToOutput("Copyright @nquangit v1.0 - Forked from @toancse");

        ContextMenu contextMenu = new ContextMenu(api);
        contextMenu.registerHotkey();

        api.userInterface().registerContextMenuItemsProvider(contextMenu);

    }
}
