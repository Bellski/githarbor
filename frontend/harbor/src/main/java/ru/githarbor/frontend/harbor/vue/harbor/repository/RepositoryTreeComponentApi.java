package ru.githarbor.frontend.harbor.vue.harbor.repository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RepositoryTreeComponentApi {

    private RepositoryTreeComponent api;

    @Inject
    public RepositoryTreeComponentApi() {
    }

    void setApi(RepositoryTreeComponent api) {
        this.api = api;
    }

    public void revealKey(String key) {
        api.revealKey(key);
    }
}
