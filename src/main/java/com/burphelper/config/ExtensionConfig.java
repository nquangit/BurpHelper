package com.burphelper.config;

import burp.api.montoya.MontoyaApi;

/**
 * Service to manage configuration persistence for the BurpHelper extension.
 */
public class ExtensionConfig {
    private final MontoyaApi api;

    // Keys
    public static final String KEY_HEADER_AUTHENTICATION = "headerAuthentication";
    public static final String KEY_BODY_AUTHENTICATION = "bodyAuthentication";
    public static final String KEY_BODY_TYPE = "bodyType";
    public static final String KEY_BODY_FIELD = "bodyField";
    public static final String KEY_CUSTOM_HEADER_AUTHENTICATION = "customHeaderAuthentication";

    public ExtensionConfig(MontoyaApi api) {
        this.api = api;
    }

    public boolean isHeaderAuthenticationEnabled() {
        Boolean value = api.persistence().extensionData().getBoolean(KEY_HEADER_AUTHENTICATION);
        return value != null ? value : true; // Default to true
    }

    public void setHeaderAuthenticationEnabled(boolean enabled) {
        api.persistence().extensionData().setBoolean(KEY_HEADER_AUTHENTICATION, enabled);
    }

    public boolean isBodyAuthenticationEnabled() {
        Boolean value = api.persistence().extensionData().getBoolean(KEY_BODY_AUTHENTICATION);
        return value != null ? value : false; // Default to false
    }

    public void setBodyAuthenticationEnabled(boolean enabled) {
        api.persistence().extensionData().setBoolean(KEY_BODY_AUTHENTICATION, enabled);
    }

    public String getBodyType() {
        String value = api.persistence().extensionData().getString(KEY_BODY_TYPE);
        return value != null ? value : "json"; // Default to json
    }

    public void setBodyType(String bodyType) {
        api.persistence().extensionData().setString(KEY_BODY_TYPE, bodyType);
    }

    public String getBodyField() {
        String value = api.persistence().extensionData().getString(KEY_BODY_FIELD);
        return value != null ? value : "null"; // Default to null
    }

    public void setBodyField(String bodyField) {
        api.persistence().extensionData().setString(KEY_BODY_FIELD, bodyField);
    }

    public String getCustomHeaderAuthentication() {
        String value = api.persistence().extensionData().getString(KEY_CUSTOM_HEADER_AUTHENTICATION);
        return value != null ? value : "Custom-Header1, Custom-Header2";
    }

    public void setCustomHeaderAuthentication(String customHeader) {
        api.persistence().extensionData().setString(KEY_CUSTOM_HEADER_AUTHENTICATION, customHeader);
    }
}
