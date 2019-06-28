package ru.githarbor.frontend.harbor.jslib.monaco;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import ru.githarbor.frontend.harbor.core.github.core.File;


@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class ITextModel implements Disposable {
    public native void setValue(String value);
    public native String getValue();
    public native String getValueInRange(IRange iRange);
    public native FindMatch[] findMatches(String query);
    public native String getLineContent(double line);
    public native WordPosition getWordAtPosition(Position position);
    public native int getLineLastNonWhitespaceColumn(double line);

    @JsProperty
    public native _URI getUri();

    @Override
    public native void dispose();

    @JsProperty
    public native void setGitHubFile(File gitHubFile);

    @JsProperty
    public native File getGitHubFile();
}
