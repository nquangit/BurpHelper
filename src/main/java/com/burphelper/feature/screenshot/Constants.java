package com.burphelper.feature.screenshot;

import java.awt.Color;

public final class Constants {
    private Constants() {
    }

    public static final String SPLIT_VIEWER_PANE = "rrvSplitViewerSplitPane";
    public static final String REQUESTS_PANE = "rrvRequestsPane";
    public static final String RESPONSE_PANE = "rrvResponsePane";
    public static final String SYNTAX_TEXT_AREA = "syntaxTextArea";

    public static final int TOOLBAR_WIDTH = 140;
    public static final float STROKE_WIDTH = 3f;
    public static final int HIGHLIGHT_ALPHA = 200;
    public static final int EDITOR_MIN_WIDTH = 500;
    public static final int EDITOR_MIN_HEIGHT = 400;

    public static final int BORDER_THICKNESS = 5;
    public static final Color BORDER_COLOR = Color.BLACK;
    public static final Color BACKGROUND_COLOR = Color.WHITE;

    public static final String MODE_RECT = "RECT";
    public static final String MODE_LINE = "LINE";
    public static final String MODE_HIGHLIGHT = "HIGHLIGHT";

    public static final Color DEFAULT_ANNOTATION_COLOR = new Color(255, 0, 0, HIGHLIGHT_ALPHA);
}
