package ru.githarbor.frontend.harbor.jslib.monaco;

import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.harbor.jslib.monaco.editor.IEditor;
import ru.githarbor.frontend.harbor.jslib.monaco.editor.StandaloneDiffEditor;


@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class Monaco {

    @JsMethod(name = "editor.createModel")
    public static native ITextModel createModel(String text, String languageId);

    @JsMethod(name = "editor.getModels")
    public static native ITextModel[] getModels();

    @JsMethod(name = "editor.create")
    public static native IEditor createEditor(Element element, Options option);

    @JsOverlay
    public static IEditor createEditor(HTMLElement element) {
        return createEditor(element, MonacoOptions.INSTANCE);
    }

    @JsMethod(name = "editor.createDiffEditor")
    public static native StandaloneDiffEditor createDiffEditor(HTMLElement container, Options options);

    @JsMethod(name = "editor.setTheme")
    public static native void setTheme(String theme);

    @JsMethod(name = "languages.getLanguages")
    public static native ILanguageExtensionPoint[] getLanguages();

    @JsMethod(name = "editor.colorizeElement")
    public static native Promise<String> colorizeElement(HTMLElement element);

    @JsMethod(name = "editor.colorize")
    public static native elemental2.promise.Promise<String> colorize(String text, String langId);

    @JsMethod(name = "editor.colorizeModelLine")
    public static native String colorizeModelLine(ITextModel model, int lineNumber);

    @JsMethod(name = "editor.defineTheme")
    public static native void defineTheme(String name, JsPropertyMap<Object> theme);

    @JsMethod(name = "Uri.parse")
    public static native _URI uriParse(String uri);

    @JsMethod(name = "languages.typescript.typescriptDefaults.setMaximumWorkerIdleTime")
    public static native void setTypeScriptMaximumWorkerIdleTime(double value);

    @JsMethod(name = "languages.typescript.javascriptDefaults.setMaximumWorkerIdleTime")
    public static native void setJavaScriptMaximumWorkerIdleTime(double value);

    @JsMethod(name = "languages.typescript.javascriptDefaults.setEagerModelSync")
    public static native void setJavaScriptEagerModelSync(boolean value);

    @JsMethod(name = "languages.typescript.javascriptDefaults.setEagerModelSync")
    public static native void setJavaScriptDiagnosticsOptions(JsPropertyMap options);

    @JsMethod(name = "languages.typescript.getJavaScriptWorker")
    public static native Promise<Object> getJavaScriptWorker();

    @JsOverlay
    public static StandaloneDiffEditor createDiffEditor(HTMLElement container, ITextModel modified, ITextModel original) {
        final StandaloneDiffEditor editor = createDiffEditor(container, MonacoOptions.INSTANCE);
        final StandaloneDiffEditor.DiffModel diffModel = new StandaloneDiffEditor.DiffModel();
        diffModel.setOriginal(modified);
        diffModel.setModified(original);

        editor.setModel(diffModel);

        return editor;
    }

    @JsType(isNative = true)
    public static class KeyCode {
        public static int KEY_C;
    }

    @JsType(isNative = true)
    public static class KeyMod {
        public static int Shift;
        public static int CtrlCmd;
    }

    public static class MonacoOptions {
        public static Options INSTANCE = Options.create();
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Options {
        private ITextModel model;

        private boolean scrollBeyondLastLine;

        private boolean readOnly;

        private ScrollBar scrollbar;

        private int fontSize;

        public String theme;

        @JsOverlay
        public static Options create() {
            final Options options = new Options();
            options.model = null;
            options.scrollBeyondLastLine = true;
            options.readOnly = true;
            options.scrollbar = ScrollBar.create();
            options.fontSize = 12;

            return options;
        }

        @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
        public static class ScrollBar {

            private int verticalScrollbarSize;

            private int horizontalScrollbarSize;

            @JsOverlay
            public static ScrollBar create() {
                final ScrollBar scrollBar = new ScrollBar();
                scrollBar.verticalScrollbarSize = 6;
                scrollBar.horizontalScrollbarSize = 6;

                return scrollBar;
            }
        }
    }

}
