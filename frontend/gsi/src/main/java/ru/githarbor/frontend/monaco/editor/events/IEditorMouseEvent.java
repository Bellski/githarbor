package ru.githarbor.frontend.monaco.editor.events;

import elemental2.dom.MouseEvent;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import ru.githarbor.frontend.monaco.IRange;
import ru.githarbor.frontend.monaco.Position;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class IEditorMouseEvent {

    @JsProperty(name = "event.ctrlKey")
    public boolean getCtrlKey;

    @JsProperty(name = "target.position")
    public Position position;

    @JsProperty(name = "target.range")
    public IRange range;

    @JsProperty(name = "event.browserEvent")
    public MouseEvent browserEvent;

    @JsProperty(name = "event.editorPos.x")
    public int editorPosX;

    @JsProperty(name = "event.editorPos.y")
    public int editorPosY;

    @JsProperty(name = "event.posx")
    public int posx;

    @JsProperty(name = "event.posy")
    public int posy;
}
