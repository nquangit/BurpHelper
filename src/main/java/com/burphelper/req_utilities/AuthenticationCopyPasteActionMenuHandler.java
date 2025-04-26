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
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONObject;

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
        // List<HttpHeader> headersRequest = requestResponse.request().headers();

        Boolean headerAuthentication = api.persistence().extensionData().getBoolean("headerAuthentication");
        Boolean bodyAuthentication = api.persistence().extensionData().getBoolean("bodyAuthentication");

        String customeHeader = api.persistence().extensionData().getString("customeHeaderAuthentication");
        String bodyType = null;
        String bodyField = null;

        // Get the Cookies or Authorization header
        // 2. Search for Authorization or Cookie header
        String data = "";
        if (headerAuthentication != null && headerAuthentication.booleanValue()) {
            List<String> headerList = new ArrayList<>();
            if (customeHeader != null && !customeHeader.strip().equals("")) {
                headerList.addAll(Arrays.asList(customeHeader.split(",")));
            }

            if (headerList != null) {
                headerList = new ArrayList<>();
                headerList.add("Authorization");
                headerList.add("Cookie");
            } else {
                headerList.add("Authorization");
                headerList.add("Cookie");
            }

            for (String header : headerList) {
                if (requestResponse.request().hasHeader(header)) {
                    data += requestResponse.request().header(header).toString() + "\n";
                }
            }
        }

        if (bodyAuthentication != null && bodyAuthentication.booleanValue()) {
            bodyType = api.persistence().extensionData().getString("bodyType");
            bodyField = api.persistence().extensionData().getString("bodyField");

            if (bodyType.equalsIgnoreCase("json")) {
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
                            api.logging().logToError("bodyField isn't json at key: : " + key);
                            break;
                        }
                    }
                    if (value != null) {
                        data += "Body: " + value.toString();
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
        // Multiple header
        String[] clipboardHeaders = content.split("\n");
        for (String i : clipboardHeaders) {
            String[] header = i.split(": ");
            if (header[0].equalsIgnoreCase("body")) {
                String bodyType = api.persistence().extensionData().getString("bodyType");
                String bodyField = api.persistence().extensionData().getString("bodyField");
                if ("json".equalsIgnoreCase(bodyType) && bodyField != null && !bodyField.isBlank()) {

                    try {
                        // Phân tách đường dẫn field: ví dụ "auth.token"
                        String[] keys = bodyField.split("\\.");
                        // Đọc chuỗi body hiện tại
                        String body = originalRequest.bodyToString();
                        JSONObject jo = new JSONObject(body);

                        // Duyệt xuống tới JSONObject cha của trường cần cập nhật
                        Object parent = jo;
                        boolean error = false;
                        for (var idx = 0; idx < keys.length - 1; idx++) {
                            if (parent instanceof JSONObject) {
                                parent = ((JSONObject) parent).opt(keys[idx]);

                                if (parent == null) {
                                    api.logging().logToError("Không tìm thấy object tại: " + keys[idx]);
                                    error = true;
                                    break;
                                }
                            } else {
                                api.logging().logToError("Body isn't json at key: : " + keys[idx]);
                                error = true;
                                break;
                            }

                        }
                        if (!error && parent != null) {
                            // Gán giá trị mới cho trường cuối cùng
                            String lastKey = keys[keys.length - 1];
                            ((JSONObject) parent).put(lastKey, header[1]);

                            // Chuyển đổi lại thành chuỗi và cập nhật vào request
                            String newBody = jo.toString();
                            originalRequest = originalRequest.withBody(newBody);
                        }
                    } catch (Exception ex) {
                        api.logging().logToError("Lỗi khi cập nhật JSON body: " + ex.toString());
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
