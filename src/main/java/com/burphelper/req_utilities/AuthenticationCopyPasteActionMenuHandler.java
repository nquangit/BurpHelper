/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burphelper.req_utilities;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import java.awt.event.ActionEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import burp.api.montoya.ui.hotkey.HotKeyEvent;
import java.awt.Toolkit;
import java.util.List;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

/**
 *
 * @author ASUS
 */
public class AuthenticationCopyPasteActionMenuHandler {

    MontoyaApi api;

    public AuthenticationCopyPasteActionMenuHandler(MontoyaApi api) {
        this.api = api;
    }

    public void handleCopyAuthentication(HttpRequestResponse requestResponse) {
        // Request Header
        List<HttpHeader> headersRequest = requestResponse.request().headers();

        // Get the Cookies or Authorization header
        // 2. Search for Authorization or Cookie header
        String data = "";
        for (HttpHeader header : headersRequest) {
            String name = header.name();

            if ("Authorization".equalsIgnoreCase(name) || "Cookie".equalsIgnoreCase(name)) {
                data += header.toString();
            }
        }
        StringSelection stringSelection = new StringSelection(data);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
    
    public void handleGlobalCopyAuthentication(HotKeyEvent event) {
        HttpRequestResponse requestResponse = event.messageEditorRequestResponse().isPresent()
                ? event.messageEditorRequestResponse().get().requestResponse() : null;
        if (requestResponse == null) {
            api.logging().logToError("Null request");
            return;
        }
        // Request Header
        List<HttpHeader> headersRequest = requestResponse.request().headers();

        // Get the Cookies or Authorization header
        // 2. Search for Authorization or Cookie header
        String data = "";
        for (HttpHeader header : headersRequest) {
            String name = header.name();

            if ("Authorization".equalsIgnoreCase(name) || "Cookie".equalsIgnoreCase(name)) {
                data += header.toString();
            }
        }
        StringSelection stringSelection = new StringSelection(data);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
    
    public void handlePasteAuthentication(ContextMenuEvent event) {
        MessageEditorHttpRequestResponse editor = event.messageEditorRequestResponse().get();
        Transferable clipboard = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        String content = null;
        if (clipboard != null && clipboard.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                content = (String) clipboard.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception ex) {
                api.logging().logToError(ex.toString());
                return;
            }
        }
        if (content == null) {
            api.logging().logToError("cannot read clipboard");
            return;
        }
        var originalRequest = editor.requestResponse().request();
        // Multiple header
        String[] clipboardHeaders = content.split("\n");
        for (String i: clipboardHeaders) {
            String[] header = i.split(": ");
            originalRequest = originalRequest.withRemovedHeader(header[0]).withAddedHeader(header[0], header[1]);
        }
        editor.setRequest(originalRequest);
    }
    
    public void handleGlobalPasteAuthentication(HotKeyEvent event) {
        MessageEditorHttpRequestResponse editor = event.messageEditorRequestResponse().get();
        Transferable clipboard = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        String content = null;
        if (clipboard != null && clipboard.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                content = (String) clipboard.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception ex) {
                api.logging().logToError(ex.toString());
                return;
            }
        }
        if (content == null) {
            api.logging().logToError("cannot read clipboard");
            return;
        }
        var originalRequest = editor.requestResponse().request();
        // Multiple header
        String[] clipboardHeaders = content.split("\n");
        for (String i: clipboardHeaders) {
            String[] header = i.split(": ");
            originalRequest = originalRequest.withRemovedHeader(header[0]).withAddedHeader(header[0], header[1]);
        }
        editor.setRequest(originalRequest);
    }

}
