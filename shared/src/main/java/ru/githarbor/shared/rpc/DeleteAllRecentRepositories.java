package ru.githarbor.shared.rpc;

import jsinterop.annotations.JsType;

@JsType
public class DeleteAllRecentRepositories extends UserManagerRequest {

    public DeleteAllRecentRepositories() {
        super(DeleteAllRecentRepositories.class.getName());
    }
}
