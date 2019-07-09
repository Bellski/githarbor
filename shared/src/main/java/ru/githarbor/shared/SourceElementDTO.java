package ru.githarbor.shared;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;


@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class SourceElementDTO {

    public int[] p;

    @JsOverlay
    public static SourceElementDTO create(int index,
                                          int nameIndex,
                                          int startLine,
                                          int endLine,
                                          int startColumn,
                                          int endColumn,
                                          int type,
                                          int parentIndex,
                                          int clazz) {

        final SourceElementDTO sourceElement = new SourceElementDTO();

        sourceElement.p = new int[10];
        sourceElement.p[0] = index;
        sourceElement.p[1] = nameIndex;
        sourceElement.p[2] = startLine;
        sourceElement.p[3] = endLine;
        sourceElement.p[4] = startColumn;
        sourceElement.p[5] = endColumn;
        sourceElement.p[6] = type;
        sourceElement.p[7] = 0;
        sourceElement.p[8] = parentIndex;
        sourceElement.p[9] = clazz;

        return sourceElement;
    }


    @JsOverlay
    public final int getIndex() {
        return p[0];
    }

    @JsOverlay
    public final int getNameIndex() {
        return p[1];
    }

    @JsOverlay
    public final int getStartLine() {
        return p[2];
    }

    @JsOverlay
    public final int getEndLine() {
        return p[3];
    }

    @JsOverlay
    public final int getStartColumn() {
        return p[4];
    }

    @JsOverlay
    public final int getEndColumn() {
        return p[5];
    }

    @JsOverlay
    public final JavaElementType getType() {
        return JavaElementType.valueOf(p[6]);
    }

    @JsOverlay
    public final boolean isThisExpr() {
        return p[7] == 1;
    }

    @JsOverlay
    public final void setThisExpr(boolean thisExpr) {
        this.p[7] = thisExpr ? 1 : 0;
    }

    @JsOverlay
    public final int getParentIndex() {
        return p[8];
    }

    @JsOverlay
    public final int getClazzIndex() {
        return p[9];
    }
}
