package com.burphelper.feature.excelcopy;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpRequestResponse;
import java.util.List;

public class ExcelCopyMenuActionHandler {

    private final MontoyaApi api;

    public ExcelCopyMenuActionHandler(MontoyaApi api) {
        this.api = api;
    }

    public void handleCopyToExcel(HttpRequestResponse requestResponse) {
        if (requestResponse == null) {
            api.logging().logToOutput("PCopy: RequestResponse is null.");
            return;
        }
        handleCopyToExcel(List.of(requestResponse));
    }

    public void handleCopyToExcelNoBody(HttpRequestResponse requestResponse) {
        if (requestResponse == null) {
            api.logging().logToOutput("PCopy: RequestResponse is null.");
            return;
        }
        handleCopyToExcelNoBody(List.of(requestResponse));
    }

    public void handleCopyToExcel(List<HttpRequestResponse> requestResponses) {
        if (requestResponses == null || requestResponses.isEmpty()) {
            api.logging().logToOutput("PCopy: No RequestResponse selected.");
            return;
        }
        String data = ExcelFormatterUtils.formatRequestResponseForExcel(requestResponses);
        ExcelFormatterUtils.copyToClipboard(data);
        api.logging().logToOutput("PCopy: Copied " + requestResponses.size() + " row(s) to clipboard.");
    }

    public void handleCopyToExcelNoBody(List<HttpRequestResponse> requestResponses) {
        if (requestResponses == null || requestResponses.isEmpty()) {
            api.logging().logToOutput("PCopy: No RequestResponse selected.");
            return;
        }
        String data = ExcelFormatterUtils.formatRequestResponseForExcelNoBody(requestResponses);
        ExcelFormatterUtils.copyToClipboard(data);
        api.logging().logToOutput("PCopy: Copied " + requestResponses.size() + " row(s) to clipboard (no body).");
    }
}
