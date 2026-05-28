package com.burphelper.feature.screenshot;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;

public final class ClipboardUtils {
    private ClipboardUtils() {
    }

    public static void copyToClipboard(String data) {
        if (data == null) {
            return;
        }
        StringSelection stringSelection = new StringSelection(data);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public static boolean copyImageToClipboard(BufferedImage image) {
        if (image == null) {
            return false;
        }
        try {
            ImageSelection imageSelection = new ImageSelection(image);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(imageSelection, null);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }
}
