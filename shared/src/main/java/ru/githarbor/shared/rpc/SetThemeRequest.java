package ru.githarbor.shared.rpc;

import jsinterop.annotations.JsType;

@JsType
public class SetThemeRequest extends UserManagerRequest {
    public boolean dark;

    public SetThemeRequest(boolean dark) {
        super(SetThemeRequest.class.getName());

        this.dark = dark;
    }
}
