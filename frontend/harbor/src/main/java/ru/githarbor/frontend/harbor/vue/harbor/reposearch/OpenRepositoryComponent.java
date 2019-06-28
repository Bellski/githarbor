package ru.githarbor.frontend.harbor.vue.harbor.reposearch;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.annotations.component.Watch;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import elemental2.dom.EventListener;
import elemental2.dom.FocusEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;

import static elemental2.dom.DomGlobal.*;

@Component
public class OpenRepositoryComponent implements IsVueComponent, HasMounted {

    @Prop
    public String name;

    @Data
    public boolean visible;

    private String popoverId;

    public EventListener blurEventListener = null;

    @Watch("visible")
    public void watchVisible(boolean newVisible) {
        if (newVisible) {

            if (blurEventListener == null) {
                final HTMLElement htmlElement = Js.cast(vue().<HTMLElement>$el().querySelector("[aria-describedby]"));
                popoverId = htmlElement.getAttribute("aria-describedby");

                document.getElementById(popoverId).addEventListener("blur", blurEventListener = evt -> {
                    final HTMLElement target = Js.cast(((FocusEvent) evt).relatedTarget);

                    if (!(target instanceof HTMLButtonElement)) {
                        visible = false;
                    }
                });
            }

            setTimeout(p0 -> {
                document.getElementById(popoverId).focus();
            }, 50);
        } else {
            document.getElementById(popoverId).removeEventListener("blur", blurEventListener);
            blurEventListener = null;
        }
    }

    @JsMethod
    public void onNewWindow() {
        visible = false;

        window.open("/github/" + name, "_blank");
    }

    @JsMethod
    public void onThisWindow() {
        window.location.setHref("/github/" + name);
    }

    @Override
    public void mounted() {

    }
}
