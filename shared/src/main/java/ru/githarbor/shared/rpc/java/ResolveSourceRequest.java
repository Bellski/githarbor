package ru.githarbor.shared.rpc.java;

import jsinterop.annotations.JsType;

@JsType
public class ResolveSourceRequest {

    public final String methodName;
    public final String ownerWithName;
    public final String path;

    public ResolveSourceRequest(String ownerWithName, String path) {
        this.methodName = ResolveSourceRequest.class.getName();
        this.ownerWithName = ownerWithName;
        this.path = path;
    }
}
