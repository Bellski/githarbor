package ru.githarbor.frontend.vue.component.searchrepository;

import com.axellience.vuegwt.core.annotations.component.*;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import elemental2.core.JsArray;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsMethod;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.vue.component.openrepository.OpenRepositoryComponent;

import static elemental2.dom.DomGlobal.window;

@Component(components = OpenRepositoryComponent.class)
public class RepositoryListComponent implements IsVueComponent, HasCreated {

    @Prop
    public boolean openNewWindow;

    @Prop
    public Repository[] repositories;

    @Prop
    public boolean editable;

    @Prop
    public double currentIndex;

    @Data
    public double currentOverIndex = -1;

    @Ref
    public JsArray<HTMLElement> repositoryElements;

    @PropDefault("openNewWindow")
    public boolean defaultOpenNewWindow() {
        return true;
    }

    @PropDefault("editable")
    public boolean defaultEditable() {
        return false;
    }

    @PropDefault("repositories")
    public Repository[] defaultRepositories() {
        return new Repository[0];
    }

    @PropDefault("currentIndex")
    public double getCurrentIndex() {
        return -1;
    }

    @JsMethod
    public void onRepositoryClick(Repository repository, double index) {
        if (openNewWindow) {
            window.open("/" + repository.nameWithOwner, "_blank");

            return;
        }

        window.location.setHref("/" + repository.nameWithOwner);
    }


    @JsMethod
    public void onDelete(String name) {
        vue().$emit("delete", name);
    }

    @JsMethod
    public void onMouseOver(double index) {
        currentOverIndex = index;
    }

    @JsMethod
    public void onMouseOut() {
        currentOverIndex = -1;
    }

    @Override
    public void created() {
        vue().$watch(() -> currentIndex, (newIndex, oldIndex) -> {
            if (newIndex >= 0) {
                repositoryElements.getAt(newIndex.intValue())
                        .scrollIntoView(Element.ScrollIntoViewTopUnionType.of(
                                JsPropertyMap.of("block", "nearest")
                        ));
            }
        });
    }
}
