/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burphelper.req_utilities;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.Cookie;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import burp.api.montoya.ui.hotkey.HotKeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ASUS
 */
public class ApplyCookieChangeActionMenuHandler {

    MontoyaApi api;

    public ApplyCookieChangeActionMenuHandler(MontoyaApi api) {
        this.api = api;
    }

    private void applyCookieChangeHandler(MessageEditorHttpRequestResponse editor, HttpRequestResponse requestResponse) {
        List<Cookie> responseCookies = requestResponse.response().cookies();

        Map<String, String> cookieMap = new HashMap<>();
        if (requestResponse.request().hasHeader("Cookie")) {
            HttpHeader cookies = requestResponse.request().header("Cookie");
            for (String cookiePair : cookies.value().split(";")) {
                if (!cookiePair.strip().isEmpty()) {
                    String[] cookie = cookiePair.strip().split("=");
                    cookieMap.put(cookie[0], cookie[1]);
                }
            }
        }

        for (Cookie newCookie : responseCookies) {
            cookieMap.put(newCookie.name(), newCookie.value());
        }

        List<String> newCookieList = new ArrayList<>();
        for (Map.Entry<String, String> e : cookieMap.entrySet()) {
            newCookieList.add(e.getKey() + "=" + e.getValue());
        }
        String newCookieValue = String.join("; ", newCookieList);

        var newReq = requestResponse.request().withRemovedHeader("Cookie").withAddedHeader("Cookie", newCookieValue);
        editor.setRequest(newReq);
    }

    public void applyCookieChange(ContextMenuEvent event) {
        MessageEditorHttpRequestResponse editor = event.messageEditorRequestResponse().get();
        HttpRequestResponse requestResponse = event.messageEditorRequestResponse().isPresent()
                ? event.messageEditorRequestResponse().get().requestResponse()
                : event.selectedRequestResponses().get(0);
        this.applyCookieChangeHandler(editor, requestResponse);
    }

    public void applyCookieChangeGlobal(HotKeyEvent event) {
        MessageEditorHttpRequestResponse editor = event.messageEditorRequestResponse().get();
        HttpRequestResponse requestResponse = event.messageEditorRequestResponse().isPresent()
                ? event.messageEditorRequestResponse().get().requestResponse() : null;

        if (requestResponse != null) {
            this.applyCookieChangeHandler(editor, requestResponse);
        }
    }
}
