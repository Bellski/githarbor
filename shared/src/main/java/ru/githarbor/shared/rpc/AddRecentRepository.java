package ru.githarbor.shared.rpc;

import jsinterop.annotations.JsType;

@JsType
public class AddRecentRepository extends UserManagerRequest {
    public String name;
    public long timestamp;

    public AddRecentRepository(String name, long timestamp) {
        super(AddRecentRepository.class.getName());

        this.name = name;
        this.timestamp = timestamp;
    }
}
