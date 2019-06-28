package ru.githarbor.shared.rpc;

import jsinterop.annotations.JsType;

@JsType
public class DeleteRecentRepository extends  UserManagerRequest {
    public String name;

    public DeleteRecentRepository(String name) {
        super(DeleteRecentRepository.class.getName());

        this.name = name;
    }
}
