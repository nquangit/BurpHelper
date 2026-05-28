package com.burphelper.feature.screenshot.legacy;

import com.burphelper.feature.screenshot.ComponentFinder;
import java.awt.Component;
import java.awt.Frame;
import java.util.List;

import burp.api.montoya.MontoyaApi;

public class LegacyScreenshotMenuActionHandler {
    private final MontoyaApi api;
    private final Action action;

    public LegacyScreenshotMenuActionHandler(MontoyaApi api) {
        this.api = api;
        this.action = new Action(api);
    }

    public void handleNormalScreenshot() {
        Frame frame = api.userInterface().swingUtils().suiteFrame();
        Component targetComponent = ComponentFinder.findComponentUnderMouse("rrvSplitViewerSplitPane", frame);
        action.takeScreenshot(targetComponent);
    }

    public void handleFullScreenshot() {
        try {
            Frame frame = api.userInterface().swingUtils().suiteFrame();
            Component targetComponent = ComponentFinder.findComponentUnderMouse("rrvSplitViewerSplitPane", frame);
            Component reqComp = ComponentFinder.getComponentByName(targetComponent, "rrvRequestsPane");
            Component resComp = ComponentFinder.getComponentByName(targetComponent, "rrvResponsePane");
            Component syntaxTextAreaReq = ComponentFinder.getComponentByName(reqComp, "syntaxTextArea");
            Component syntaxTextAreaRes = ComponentFinder.getComponentByName(resComp, "syntaxTextArea");

            action.takeScreenshotAndGetBufferImage(syntaxTextAreaReq);
            action.takeScreenshotAndGetBufferImage(syntaxTextAreaRes);
            action.takeScreenshot2();
        } catch (Exception ex) {
            api.logging().logToError("Error: " + ex.getMessage());
        }
    }

    public void handleEditedScreenshot() {
        captureScreenshot(1);
    }

    public void handleOriginalScreenshot() {
        captureScreenshot(0);
    }

    private void captureScreenshot(int index) {
        try {
            Frame frame = api.userInterface().swingUtils().suiteFrame();
            Component targetComponent = ComponentFinder.findComponentUnderMouse("rrvSplitViewerSplitPane", frame);
            Component reqComp = ComponentFinder.getComponentByName(targetComponent, "rrvRequestsPane");
            Component resComp = ComponentFinder.getComponentByName(targetComponent, "rrvResponsePane");

            List<Component> reqComponents = ComponentFinder.findAllComponentsByName(reqComp, "syntaxTextArea");
            List<Component> resComponents = ComponentFinder.findAllComponentsByName(resComp, "syntaxTextArea");

            if (reqComponents.size() <= index || resComponents.size() <= index) {
                api.logging().logToOutput("No syntaxTextArea found");
                action.clearImages();
                return;
            }

            Component syntaxTextAreaReq = reqComponents.get(index);
            Component syntaxTextAreaRes = resComponents.get(index);

            action.takeScreenshotAndGetBufferImage(syntaxTextAreaReq);
            action.takeScreenshotAndGetBufferImage(syntaxTextAreaRes);
            action.takeScreenshot2();
        } catch (Exception ex) {
            api.logging().logToError("Error: " + ex.getMessage());
        }
    }
}
