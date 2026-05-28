package com.burphelper.feature.request;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import burp.api.montoya.ui.hotkey.HotKeyEvent;
import com.burphelper.config.ExtensionConfig;
import java.awt.Toolkit;
import java.util.List;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONObject;

public class AuthenticationCopyPasteActionMenuHandler {

    MontoyaApi api;

    public AuthenticationCopyPasteActionMenuHandler(MontoyaApi api) {
        this.api = api;
    }

    public void handleCopyAuthentication(HttpRequestResponse requestResponse) {
        ExtensionConfig config = new ExtensionConfig(api);
        boolean headerAuthentication = config.isHeaderAuthenticationEnabled();
        boolean bodyAuthentication = config.isBodyAuthenticationEnabled();

        String customHeader = config.getCustomHeaderAuthentication();
        String bodyType = null;
        String bodyField = null;

        String data = "";
        if (headerAuthentication) {
            List<String> headerList = new ArrayList<>();
            if (customHeader != null && !customHeader.strip().equals("")) {
                for (String h : customHeader.split(",")) {
                    String trimmed = h.trim();
                    if (!trimmed.isEmpty()) {
                        headerList.add(trimmed);
                    }
                }
            }
            if (!headerList.contains("Authorization")) {
                headerList.add("Authorization");
            }
            if (!headerList.contains("Cookie")) {
                headerList.add("Cookie");
            }

            for (String header : headerList) {
                if (requestResponse.request().hasHeader(header)) {
                    data += requestResponse.request().header(header).toString() + "\n";
                }
            }
        }

        if (bodyAuthentication) {
            bodyType = config.getBodyType();
            bodyField = config.getBodyField();

            if (bodyType != null && bodyType.equalsIgnoreCase("json") && bodyField != null) {
                String[] jq = bodyField.split("\\.");
                String body = requestResponse.request().bodyToString();

                try {
                    var jo = new JSONObject(body);

                    Object value = jo;
                    for (String key : jq) {
                        if (value instanceof JSONObject) {
                            value = ((JSONObject) value).opt(key);

                            if (value == null) {
                                api.logging().logToError("bodyField cannot find key: " + key);
                                break;
                            }
                        } else {
                            api.logging().logToError("bodyField isn't json at key: " + key);
                            break;
                        }
                    }
                    if (value != null) {
                        data += value.toString();
                    }
                } catch (Exception ex) {
                    api.logging().logToError(ex.toString());
                }
            }
        }

        StringSelection stringSelection = new StringSelection(data);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public void handleGlobalCopyAuthentication(HotKeyEvent event) {
        HttpRequestResponse requestResponse = event.messageEditorRequestResponse().isPresent()
                ? event.messageEditorRequestResponse().get().requestResponse()
                : null;
        if (requestResponse == null) {
            api.logging().logToError("Null request");
            return;
        }
        this.handleCopyAuthentication(requestResponse);
    }

    private void handlePaste(MessageEditorHttpRequestResponse editor) {
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
        String[] clipboardHeaders = content.split("\n");
        ExtensionConfig config = new ExtensionConfig(api);
        for (String i : clipboardHeaders) {
            String[] header = i.split(": ");
            if (header.length < 2) {
                String bodyType = config.getBodyType();
                String bodyField = config.getBodyField();
                if ("json".equalsIgnoreCase(bodyType) && bodyField != null && !bodyField.isBlank()) {

                    try {
                        String[] keys = bodyField.split("\\.");
                        String body = originalRequest.bodyToString();
                        JSONObject jo = new JSONObject(body);

                        Object parent = jo;
                        boolean error = false;
                        for (var idx = 0; idx < keys.length - 1; idx++) {
                            if (parent instanceof JSONObject) {
                                parent = ((JSONObject) parent).opt(keys[idx]);

                                if (parent == null) {
                                    api.logging().logToError("Object not found at key: " + keys[idx]);
                                    error = true;
                                    break;
                                }
                            } else {
                                api.logging().logToError("Body isn't json at key: " + keys[idx]);
                                error = true;
                                break;
                            }

                        }
                        if (!error && parent != null) {
                            String lastKey = keys[keys.length - 1];
                            ((JSONObject) parent).put(lastKey, header[0]);

                            String newBody = jo.toString();
                            originalRequest = originalRequest.withBody(newBody);
                        }
                    } catch (Exception ex) {
                        api.logging().logToError("Error updating JSON body: " + ex.toString());
                    }
                }

                continue;
            }
            originalRequest = originalRequest.withRemovedHeader(header[0]).withAddedHeader(header[0], header[1]);
        }
        editor.setRequest(originalRequest);
    }

    public void handlePasteAuthentication(ContextMenuEvent event) {
        MessageEditorHttpRequestResponse editor = event.messageEditorRequestResponse().get();
        this.handlePaste(editor);
    }

    public void handleGlobalPasteAuthentication(HotKeyEvent event) {
        MessageEditorHttpRequestResponse editor = event.messageEditorRequestResponse().get();
        this.handlePaste(editor);
    }

}
