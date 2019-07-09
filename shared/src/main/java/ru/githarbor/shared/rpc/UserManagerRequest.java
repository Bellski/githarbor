package ru.githarbor.shared.rpc;

import jsinterop.annotations.JsType;

@JsType
public class UserManagerRequest {
    public String methodName;

    public UserManagerRequest(String methodName) {
        this.methodName = methodName;
    }
}
