package ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.javaview.usages;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsMethod;
import ru.githarbor.frontend.monaco.Usage;

@Component
public class UsagesPopupComponent implements IsVueComponent, HasCreated, HasMounted {

    @Prop
    public String usageOf;

    @Prop
    public Usage[] usages;

    @Prop
    public UsagesPosition position;

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
    public void onUsageSelect(Usage usage) {
        vue().$emit("usage-select", usage);

        final CSSStyleDeclaration style = vue().<HTMLElement>$el().style;
        style.display = "none";
    }
}
