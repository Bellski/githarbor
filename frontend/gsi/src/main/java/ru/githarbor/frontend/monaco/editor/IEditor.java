package ru.githarbor.frontend.monaco.editor;

import elemental2.dom.Element;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import ru.githarbor.frontend.monaco.editor.events.ICursorPositionChangedEvent;
import ru.githarbor.frontend.monaco.editor.events.ICursorSelectionChangedEvent;
import ru.githarbor.frontend.monaco.editor.events.IEditorMouseEvent;
import ru.githarbor.frontend.monaco.editor.events.IKeyboardEvent;
import ru.githarbor.frontend.monaco.action.Action;
import ru.githarbor.frontend.monaco.*;


@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public abstract class IEditor implements Disposable {

    public native void focus();

    public native ITextModel getModel();

    public native void setModel(ITextModel model);

    public native void layout();

    public native void layout(IDimension dimension);

    public native void revealRangeInCenter(IRange range);

    public native void setSelection(IRange range);

    public native Disposable onDidChangeCursorSelection(EventListener<ICursorSelectionChangedEvent> listener);
    public native Disposable onDidChangeCursorPosition(EventListener<ICursorPositionChangedEvent> listener);
    public native Disposable onContextMenu(EventListener<IEditorMouseEvent> listener);
    public native Disposable onMouseMove(EventListener<IEditorMouseEvent> listener);
    public native Disposable onMouseUp(EventListener<IEditorMouseEvent> listener);
    public native Disposable onKeyUp(EventListener<IKeyboardEvent> listener);
    public native Disposable onKeyDown(EventListener<IKeyboardEvent> listener);

    public native Disposable addAction(Action action);

    public native void trigger(String anyString, String id);

    public native Position getPosition();

    public native Selection getSelection();

    public native void setPosition(Position position);
    public native void revealPositionInCenter(Position position);
    public native void revealLineInCenter(double line);

    public native void addOverlayWidget(Object widget);
    public native void addContentWidget(Object widget);
    public native void removeContentWidget(Object widget);

    public native Element getDomNode();

    public native String[] deltaDecorations(String[] old, IModelDeltaDecoration[] newDecorations);

    public native void setTheme(String theme);

    protected native String getEditorType();

    @JsOverlay
    public final boolean isDiff() {
        return getEditorType().equals("vs.editor.IDiffEditor");
    }
}