package ru.githarbor.frontend.harbor.jslib.monaco.editor.events;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import ru.githarbor.frontend.harbor.jslib.monaco.IRange;
import ru.githarbor.frontend.harbor.jslib.monaco.Position;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class IEditorMouseEvent {

    @JsProperty(name = "event.ctrlKey")
    public boolean getCtrlKey;

    @JsProperty(name = "target.position")
    public Position position;

    @JsProperty(name = "target.range")
    public IRange range;

    @JsProperty(name = "event.editorPos.x")
    public int editorPosX;

    @JsProperty(name = "event.editorPos.y")
    public int editorPosY;

    @JsProperty(name = "event.posx")
    public int posx;

    @JsProperty(name = "event.posy")
    public int posy;
}
