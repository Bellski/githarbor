package ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.javaview.usages;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.annotations.component.Ref;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import elemental2.core.JsArray;
import elemental2.dom.CSSProperties;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.DomGlobal;
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

    @Ref
    public JsArray<HTMLElement> classNameElements;

    @Ref
    public JsArray<HTMLElement> usageTextElements;

    @Override
    public void created() {
        vue().$watch(() -> position, (newValue, oldValue) -> {
            final CSSStyleDeclaration style = vue().<HTMLElement>$el().style;

            style.top = newValue.y + "px";
            style.left = newValue.x + "px";

            style.display = "block";

            vue().$nextTick(() -> {
                double elementWidth = 0;

                for (int i = 0; i < classNameElements.length; i++) {
                    final HTMLElement classNameElement = classNameElements.getAt(i);
                    final HTMLElement usageTextElement = usageTextElements.getAt(i);

                    double liWith = classNameElement.clientWidth + usageTextElement.clientWidth;

                    if (liWith > elementWidth) {
                        elementWidth = liWith;
                    }
                }

                style.width = CSSProperties.WidthUnionType.of((elementWidth + 20)+ "px");
            });

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
