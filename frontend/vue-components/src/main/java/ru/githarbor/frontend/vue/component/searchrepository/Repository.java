package ru.githarbor.frontend.vue.component.searchrepository;

public class Repository {
    public final String nameWithOwner;
    public final String description;
    public final String languageColor;
    public final String languageName;
    public final String stars;
    public final String updatedAt;

    public Repository(String nameWithOwner, String description, String languageColor, String languageName, String stars, String updatedAt) {
        this.nameWithOwner = nameWithOwner;
        this.description = description;
        this.languageColor = languageColor;
        this.languageName = languageName;
        this.stars = stars;
        this.updatedAt = updatedAt;
    }
}
