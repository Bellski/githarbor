package ru.githarbor.frontend.harbor.vue.component.menu;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsMethod;

@Component
public class ContextMenuComponent implements IsVueComponent, HasCreated, HasMounted {

    @Prop
    public Action[] actions;

    @Prop
    public Position position;

    @Override
    public void created() {
        vue().$watch(() -> position, (newValue, oldValue) -> {
            final CSSStyleDeclaration style = vue().<HTMLElement>$el().style;

            style.top = newValue.y + "px";
            style.left = newValue.x + "px";

            style.display = "block";

            vue().$el().focus();
        });

    }

    @Override
    public void mounted() {
        vue().$el().addEventListener("blur", evt -> {
            final CSSStyleDeclaration style = vue().<HTMLElement>$el().style;
            style.display = "none";
        });
    }

    @JsMethod
    public void onClick(Action action) {
        action.run.run();

        final CSSStyleDeclaration style = vue().<HTMLElement>$el().style;
        style.display = "none";
    }
}
