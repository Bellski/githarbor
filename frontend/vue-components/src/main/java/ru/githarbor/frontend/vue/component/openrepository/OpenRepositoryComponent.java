package ru.githarbor.frontend.vue.component.openrepository;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.annotations.component.Watch;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasDestroyed;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import jsinterop.annotations.JsMethod;

import static elemental2.dom.DomGlobal.window;

@Component
public class OpenRepositoryComponent implements IsVueComponent, HasMounted, HasDestroyed {

    @Prop
    public String name;

    @Data
    public boolean visible;


    @Watch("visible")
    public void watchVisible(boolean newVisible) {

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

    @Override
    public void destroyed() {

    }
}
