package ru.githarbor.shared.rpc.go;

import jsinterop.annotations.JsType;

@JsType
public class GoResolveSourceRequest {
    public final String methodName;
    public final String ownerWithName;
    public final String path;

    public GoResolveSourceRequest(String ownerWithName, String path) {
        this.methodName = GoResolveSourceRequest.class.getName();
        this.ownerWithName = ownerWithName;
        this.path = path;
    }
}
