package ru.githarbor.frontend.vue.component.searchrepository;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.annotations.component.PropDefault;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import jsinterop.annotations.JsMethod;
import ru.githarbor.frontend.vue.component.openrepository.OpenRepositoryComponent;

import static elemental2.dom.DomGlobal.window;

@Component(components = OpenRepositoryComponent.class)
public class RepositoryListComponent implements IsVueComponent {

    @Prop
    public Repository[] repositories;

    @Prop
    public boolean editable;

    @PropDefault("editable")
    public boolean defaultEditable() {
        return false;
    }

    @PropDefault("repositories")
    public Repository[] defaultRepositories() {
        return new Repository[0];
    }

    @JsMethod
    public void onNewWindow(String name) {
        window.open("/github/" + name, "_blank");
    }

    @JsMethod
    public void onThisWindow(String name) {
        window.location.setHref("/github/" + name);
    }

    @JsMethod
    public void onDelete(String name) {
        vue().$emit("delete", name);
    }
}
