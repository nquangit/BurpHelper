package com.burphelper.feature.excelcopy;

import java.util.ArrayList;

public class ExcelFormatter {
    private StringBuilder data;

    public ExcelFormatter() {
        this.data = new StringBuilder();
    }

    public void addData(String data) {
        this.data.append(data).append("\t");
    }

    public String getData() {
        return this.data.toString().stripTrailing();
    }

    public static String excelFormat(String data) {
        data = data.stripLeading();

        if (data.length() > 29000) {
            data = data.substring(0, 29000);
        }

        StringBuilder formattedData = new StringBuilder();

        for (char c : data.toCharArray()) {
            if (c < 0x20 && c != '\t' && c != '\n' && c != '\r') {
                continue;
            }
            switch (c) {
                case '\t':
                    formattedData.append("\\t");
                    break;
                case '"':
                    formattedData.append("\"\"");
                    break;
                case '<':
                    formattedData.append("&lt;");
                    break;
                case '>':
                    formattedData.append("&gt;");
                    break;
                case '&':
                    formattedData.append("&amp;");
                    break;
                case '\'':
                    formattedData.append("&#39;");
                    break;
                default:
                    formattedData.append(c);
                    break;
            }
        }

        if (formattedData.length() > 35000) {
            formattedData.setLength(35000);
        }

        return "\"" + formattedData.toString() + "\"";
    }

    public static byte[] filterValidASCII(byte[] data) {
        ArrayList<Byte> validBytes = new ArrayList<>();

        for (byte b : data) {
            if ((b >= 0x20 && b <= 0x7E) || b == '\n' || b == '\r') {
                validBytes.add(b);
            }
        }

        byte[] result = new byte[validBytes.size()];
        for (int i = 0; i < validBytes.size(); i++) {
            result[i] = validBytes.get(i);
        }
        return result;
    }
}
