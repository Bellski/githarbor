package ru.githarbor.shared.rpc;

import jsinterop.annotations.JsType;

@JsType
public class DeleteFavoriteRepository extends  UserManagerRequest {
    public String name;

    public DeleteFavoriteRepository(String name) {
        super(DeleteFavoriteRepository.class.getName());
        this.name = name;
    }
}
