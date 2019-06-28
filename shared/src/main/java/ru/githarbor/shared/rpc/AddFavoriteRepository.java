package ru.githarbor.shared.rpc;

import jsinterop.annotations.JsType;

@JsType
public class AddFavoriteRepository extends UserManagerRequest {
    public String name;

    public AddFavoriteRepository(String name) {
        super(AddFavoriteRepository.class.getName());

        this.name = name;
    }
}
