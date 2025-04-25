/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.burphelper.intergrate;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.hotkey.HotKeyEvent;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Base64;

/**
 *
 * @author ASUS
 */
public class IntergrateMenuActionHandler {

    MontoyaApi api;
    private final ExecutorService executor;

    public IntergrateMenuActionHandler(MontoyaApi api) {
        this.api = api;
        this.executor = Executors.newCachedThreadPool();
    }

    /**
     * Được gọi khi user chọn "Send to Integration" từ context menu.
     */
    public void handleSendReqToIntegration(HttpRequestResponse requestResponse) {
        HttpRequest original = requestResponse.request();

        String httpVersion = original.httpVersion();
        String url = Base64.getEncoder().encodeToString(original.url().getBytes());
        
        String method = original.method();
        // Method 1: builder với URL tuyệt đối
        HttpRequest serviceReq = HttpRequest
                .httpRequestFromUrl("http://localhost:5000/api/burp/request?url=" + url + "&method=" + method + "&version=" + httpVersion)
                .withMethod("POST")
                .withBody(original.body())
                .withRemovedHeaders(original.headers())
                .withUpdatedHeaders(original.headers())
                .withAddedHeaders(original.headers());
        
//        this.api.logging().logToOutput(serviceReq.headers());

        executor.submit(() -> {
            try {
                HttpRequestResponse resp = api.http().sendRequest(serviceReq);  // :contentReference[oaicite:23]{index=23}
                int status = resp.response().statusCode();                     // :contentReference[oaicite:24]{index=24}

                api.logging().logToOutput("[Integration] Status: " + status); // :contentReference[oaicite:26]{index=26}
            } catch (Exception e) {
                api.logging().logToError("[Integration] Error: " + e.getMessage());
            }
        });
    }
    
    public void handleGlobalSendReqToIntegration(HotKeyEvent event) {
        HttpRequestResponse requestResponse = event.messageEditorRequestResponse().isPresent()
                ? event.messageEditorRequestResponse().get().requestResponse() : null;
        if (requestResponse == null) {
            api.logging().logToError("Null request");
            return;
        }
        HttpRequest original = requestResponse.request();

        String httpVersion = original.httpVersion();
        String url = Base64.getEncoder().encodeToString(original.url().getBytes());
        
        String method = original.method();
        // Method 1: builder với URL tuyệt đối
        HttpRequest serviceReq = HttpRequest
                .httpRequestFromUrl("http://localhost:5000/api/burp/request?url=" + url + "&method=" + method + "&version=" + httpVersion)
                .withMethod("POST")
                .withBody(original.body())
                .withRemovedHeaders(original.headers())
                .withUpdatedHeaders(original.headers())
                .withAddedHeaders(original.headers());
        
//        this.api.logging().logToOutput(serviceReq.headers());

        executor.submit(() -> {
            try {
                HttpRequestResponse resp = api.http().sendRequest(serviceReq);  // :contentReference[oaicite:23]{index=23}
                int status = resp.response().statusCode();                     // :contentReference[oaicite:24]{index=24}

                api.logging().logToOutput("[Integration] Status: " + status); // :contentReference[oaicite:26]{index=26}
            } catch (Exception e) {
                api.logging().logToError("[Integration] Error: " + e.getMessage());
            }
        });
    }

}
