# BurpHelper Extension

BurpHelper is a powerful, modular Burp Suite extension designed to streamline penetration testing workflows by offering utilities for screenshot capturing/annotation, copy-pasting to Excel (PCopy), integration with external API endpoints, advanced authentication token copying/pasting, and quick utilities like UUID generation.

This codebase has been restructured from a legacy monolith into a clean, modular design.

---

## Architecture Overview

The extension is organized into clear namespaces under `com.burphelper`:

*   **`com.burphelper.BurpHelper`**: The entry point implementing Montoya's `BurpExtension` interface.
*   **`com.burphelper.config`**: Contains `ExtensionConfig` which acts as a centralized service wrapper around Montoya's persistent store, offering typed accessors.
*   **`com.burphelper.gui`**: Contains the UI tab, panel configuration layout (`CopyAuthenticationPanel` and `CopyAuthenticationDesign`), and the main context menu registration (`ContextMenu`).
*   **`com.burphelper.http`**: Implements custom HTTP handlers (e.g., `CustomHTTPHandler`).
*   **`com.burphelper.feature`**: Modular sub-packages corresponding to core feature areas:
    *   `excelcopy` (formerly PCopy): Copies structured request/response metadata to clipboard formatted for Excel.
    *   `integration`: Sends selected HTTP request/response payloads to local or remote endpoints.
    *   `request`: Handles authentication copy/paste actions and cookie changes.
    *   `screenshot`: Handles capturing, annotating, and clipboard-copying screenshot ranges of HTTP requests/responses (with a legacy subsystem).

---

## Features & Usage

### 1. Screenshot & Annotation
*   **Normal / Component**: Capture and annotate a specific component in the Burp UI (`Ctrl+Shift+S`).
*   **Full Req/Res**: Capture and annotate the full Request/Response editor pane (`Ctrl+Shift+Space`).
*   *Legacy Screenshots*: Still available under the "Screenshot (Legacy)" context menu to handle original vs. edited requests (e.g., in the Proxy tab).

### 2. PCopy (Excel Copy)
*   Right-click one or more requests in the HTTP History or Message Editor.
*   Select **PCopy has body** or **PCopy no body** to format the requests into clean Excel columns.

### 3. Integrate
*   Select a request and choose **Integrate** -> **Send** (`Ctrl+Q`) to transmit the transaction details to external testing components or integration layers.

### 4. Copy / Paste Authentication
*   Configure which headers (e.g., `Authorization`, `Cookie`, or custom headers) and JSON body fields should be extracted via the **BurpHelper** Suite Tab.
*   Copy authentication tokens using `Ctrl+Shift+C`.
*   Paste them into any active request editor using `Ctrl+Shift+V`. It will automatically replace target headers or JSON body attributes as configured.

---

## Build & Installation

### Prerequisites
*   Java Development Kit (JDK) 21 or higher
*   Maven 3.x

### Building the Extension
The project includes a PowerShell script at the root (`build.ps1`) to compile and package the extension:
```powershell
.\build.ps1
```
This script sets up `JAVA_HOME` and runs `mvn clean install` pointing to the NetBeans bundled maven executable to generate the final assembly JAR.

### Loading into Burp Suite
1.  Open Burp Suite.
2.  Go to the **Extensions** tab -> **Installed**.
3.  Click **Add**.
4.  Set Extension type to **Java**.
5.  Select the generated JAR file: `target/BurpHelper-1.0-SNAPSHOT-jar-with-dependencies.jar`.

---

## License & Credits
Copyright @nquangit v1.0 - Forked from @toancse