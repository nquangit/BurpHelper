package com.burphelper.feature.excelcopy;

import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.Cookie;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.http.message.params.ParsedHttpParameter;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public final class ExcelFormatterUtils {
    private static final int MAX_EXCEL_CELL_LENGTH = 29000;
    private static final String EXCEL_SEPARATOR = "\t";
    private static final String REDACTED_TEXT = "REDACTED";
    private static final String BINARY_DATA_TEXT = "[BINARY DATA]";
    private static final double BINARY_THRESHOLD = 0.1;

    private ExcelFormatterUtils() {
    }

    public static String formatRequestResponseForExcel(HttpRequestResponse requestResponse) {
        return formatRequestResponse(requestResponse, true);
    }

    public static String formatRequestResponseForExcel(List<HttpRequestResponse> requestResponses) {
        if (requestResponses == null || requestResponses.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < requestResponses.size(); i++) {
            if (i > 0) {
                result.append("\n");
            }
            result.append(formatRequestResponse(requestResponses.get(i), true));
        }
        return result.toString();
    }

    public static String formatRequestResponseForExcelNoBody(HttpRequestResponse requestResponse) {
        return formatRequestResponse(requestResponse, false);
    }

    public static String formatRequestResponseForExcelNoBody(List<HttpRequestResponse> requestResponses) {
        if (requestResponses == null || requestResponses.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < requestResponses.size(); i++) {
            if (i > 0) {
                result.append("\n");
            }
            result.append(formatRequestResponse(requestResponses.get(i), false));
        }
        return result.toString();
    }

    public static void copyToClipboard(String data) {
        if (data == null) {
            return;
        }
        StringSelection stringSelection = new StringSelection(data);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public static boolean isBinaryContent(byte[] data) {
        if (data == null || data.length == 0) {
            return false;
        }

        int nonPrintableCount = 0;
        int checkLength = Math.min(data.length, 8192);
        for (int i = 0; i < checkLength; i++) {
            byte b = data[i];
            if (b == 0 || (b < 32 && b != 9 && b != 10 && b != 13) || b == 127) {
                nonPrintableCount++;
            }
        }

        double ratio = (double) nonPrintableCount / checkLength;
        return ratio > BINARY_THRESHOLD;
    }

    public static boolean isBinaryContent(String data) {
        if (data == null || data.isEmpty()) {
            return false;
        }
        return isBinaryContent(data.getBytes(StandardCharsets.UTF_8));
    }

    public static String excelFormat(String data) {
        if (data == null) {
            return "";
        }
        data = data.stripLeading();
        if (data.length() > MAX_EXCEL_CELL_LENGTH) {
            data = data.substring(0, MAX_EXCEL_CELL_LENGTH);
        }

        StringBuilder formattedData = new StringBuilder();
        for (char c : data.toCharArray()) {
            switch (c) {
                case '\t':
                    formattedData.append("\\t");
                    break;
                case '\n':
                case '\r':
                    formattedData.append(c);
                    break;
                case '"':
                    formattedData.append("\"\"");
                    break;
                case '<':
                    formattedData.append("<");
                    break;
                case '>':
                    formattedData.append(">");
                    break;
                case '&':
                    formattedData.append("&");
                    break;
                case '\'':
                    formattedData.append("&#39;");
                    break;
                default:
                    if (c >= 32 && c != 127) {
                        formattedData.append(c);
                    }
                    break;
            }
        }
        return "\"" + formattedData.toString() + "\"";
    }

    private static String sanitizeMultipartBody(String bodyString, String boundary) {
        if (bodyString == null || boundary == null || boundary.isEmpty()) {
            return bodyString;
        }

        String delimiter = "--" + boundary;
        String[] parts = bodyString.split(delimiter);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            if (part.equals("--") || part.equals("--\r\n") || part.equals("--\n")) {
                result.append(delimiter).append("--");
                continue;
            }

            if (part.trim().isEmpty() && i == 0) {
                continue;
            }

            if (i > 0) {
                result.append(delimiter);
            }

            int headerEndIndex = part.indexOf("\r\n\r\n");
            String separator = "\r\n\r\n";
            if (headerEndIndex == -1) {
                headerEndIndex = part.indexOf("\n\n");
                separator = "\n\n";
            }

            if (headerEndIndex != -1) {
                String partHeaders = part.substring(0, headerEndIndex);
                String partContent = part.substring(headerEndIndex + separator.length());

                result.append(partHeaders);
                result.append(separator);

                if (isBinaryContent(partContent)) {
                    result.append(BINARY_DATA_TEXT);
                    result.append("\n");
                } else {
                    result.append(partContent);
                }
            } else {
                result.append(part);
            }
        }

        return result.toString();
    }

    private static String extractBoundary(String contentType) {
        if (contentType == null) {
            return null;
        }

        String boundaryPrefix = "boundary=";
        int boundaryIndex = contentType.indexOf(boundaryPrefix);
        if (boundaryIndex == -1) {
            return null;
        }

        String boundary = contentType.substring(boundaryIndex + boundaryPrefix.length());
        if (boundary.startsWith("\"") && boundary.endsWith("\"")) {
            boundary = boundary.substring(1, boundary.length() - 1);
        }

        int semicolonIndex = boundary.indexOf(';');
        if (semicolonIndex != -1) {
            boundary = boundary.substring(0, semicolonIndex);
        }

        return boundary.trim();
    }

    private static String processBodyContent(byte[] bodyBytes, String contentType) {
        if (bodyBytes == null || bodyBytes.length == 0) {
            return "";
        }

        String bodyString = new String(bodyBytes, StandardCharsets.UTF_8);

        if (contentType != null && contentType.toLowerCase().contains("multipart/")) {
            String boundary = extractBoundary(contentType);
            if (boundary != null) {
                return sanitizeMultipartBody(bodyString, boundary);
            }
        }

        if (isBinaryContent(bodyBytes)) {
            return BINARY_DATA_TEXT;
        }

        return prettyPrintJson(bodyString);
    }

    private static String prettyPrintJson(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return "";
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return jsonObject.toString(2);
        } catch (JSONException e) {
            return jsonString;
        }
    }

    private static String formatRequestResponse(HttpRequestResponse requestResponse, boolean includeBody) {
        if (requestResponse == null) {
            return "";
        }

        StringBuilder data = new StringBuilder();

        String method = requestResponse.request().method();
        data.append(excelFormat(method)).append(EXCEL_SEPARATOR);

        HttpService httpService = requestResponse.httpService();
        String host = httpService.host() + ":" + httpService.port();
        data.append(excelFormat(host)).append(EXCEL_SEPARATOR);

        String path = requestResponse.request().path();
        int questionMarkIndex = path.indexOf('?');
        if (questionMarkIndex != -1) {
            path = path.substring(0, questionMarkIndex);
        }
        data.append(excelFormat(path)).append(EXCEL_SEPARATOR);

        String fullPath = requestResponse.request().path();
        String requestSection = formatRequestSection(requestResponse, method, fullPath, includeBody);
        data.append(excelFormat(requestSection)).append(EXCEL_SEPARATOR);

        String responseSection = formatResponseSection(requestResponse, includeBody);
        data.append(excelFormat(responseSection)).append(EXCEL_SEPARATOR);

        String requestBody;
        if (!includeBody) {
            requestBody = REDACTED_TEXT;
        } else {
            byte[] requestBodyBytes = requestResponse.request().body().getBytes();
            String requestContentType = requestResponse.request().headerValue("Content-Type");
            requestBody = processBodyContent(requestBodyBytes, requestContentType);
        }
        data.append(excelFormat(requestBody)).append(EXCEL_SEPARATOR);

        String responseBody;
        if (!includeBody) {
            responseBody = REDACTED_TEXT;
        } else {
            byte[] responseBodyBytes = requestResponse.response().body().getBytes();
            String responseContentType = requestResponse.response().headerValue("Content-Type");
            responseBody = processBodyContent(responseBodyBytes, responseContentType);
        }
        data.append(excelFormat(responseBody)).append(EXCEL_SEPARATOR);

        String rawSummary = buildRawSummary(requestResponse);
        data.append(excelFormat(rawSummary));

        return data.toString();
    }

    private static String formatRequestSection(HttpRequestResponse requestResponse, String method, String path, boolean includeBody) {
        List<HttpHeader> headers = requestResponse.request().headers();
        String headersString = headers.stream()
                .map(HttpHeader::toString)
                .collect(Collectors.joining("\n"));

        String firstLine = method + " " + path + " " + requestResponse.request().httpVersion() + "\n";

        if (includeBody) {
            byte[] bodyBytes = requestResponse.request().body().getBytes();
            String contentType = requestResponse.request().headerValue("Content-Type");
            String prettyBody = "\n" + processBodyContent(bodyBytes, contentType);
            return firstLine + headersString + "\n" + prettyBody;
        }

        return firstLine + headersString + "\n\n" + REDACTED_TEXT;
    }

    private static String formatResponseSection(HttpRequestResponse requestResponse, boolean includeBody) {
        List<HttpHeader> headers = requestResponse.response().headers();
        String headersString = headers.stream()
                .map(HttpHeader::toString)
                .collect(Collectors.joining("\n"));

        String firstLine = requestResponse.response().toString().split("\n", 2)[0];

        if (includeBody) {
            byte[] bodyBytes = requestResponse.response().body().getBytes();
            String contentType = requestResponse.response().headerValue("Content-Type");
            String prettyBody = "\n" + processBodyContent(bodyBytes, contentType);
            return firstLine + "\n" + headersString + "\n" + prettyBody;
        }

        return firstLine + "\n" + headersString + "\n\n" + REDACTED_TEXT;
    }

    private static String buildRawSummary(HttpRequestResponse requestResponse) {
        StringBuilder summary = new StringBuilder();

        summary.append("______ REQUEST ______\n");

        summary.append("GET Params\n");
        appendParameterNames(summary, requestResponse.request().parameters(HttpParameterType.URL));

        summary.append("POST Params\n");
        appendParameterNames(summary, requestResponse.request().parameters(HttpParameterType.BODY));

        summary.append("HEADERS\n");
        appendHeaderNames(summary, requestResponse.request().headers());

        summary.append("Cookie\n");
        appendParameterNames(summary, requestResponse.request().parameters(HttpParameterType.COOKIE));

        summary.append("\n______ RESPONSE ______\n");

        summary.append("HEADERS\n");
        appendHeaderNames(summary, requestResponse.response().headers());

        summary.append("COOKIES\n");
        appendCookieNames(summary, requestResponse.response().cookies());

        summary.append("\n______ RAW ______\n");

        return summary.toString();
    }

    private static void appendParameterNames(StringBuilder summary, List<ParsedHttpParameter> params) {
        for (int i = 0; i < params.size(); i++) {
            summary.append(i + 1).append(".").append(params.get(i).name()).append(" | ");
        }
        if (!params.isEmpty()) {
            summary.setLength(summary.length() - 3);
        }
        summary.append("\n");
    }

    private static void appendHeaderNames(StringBuilder summary, List<HttpHeader> headers) {
        for (int i = 0; i < headers.size(); i++) {
            summary.append(i + 1).append(".").append(headers.get(i).name()).append(" | ");
        }
        if (!headers.isEmpty()) {
            summary.setLength(summary.length() - 3);
        }
        summary.append("\n");
    }

    private static void appendCookieNames(StringBuilder summary, List<Cookie> cookies) {
        for (int i = 0; i < cookies.size(); i++) {
            summary.append(i + 1).append(".").append(cookies.get(i).name()).append(" | ");
        }
        if (!cookies.isEmpty()) {
            summary.setLength(summary.length() - 3);
        }
        summary.append("\n");
    }
}
