package ru.githarbor.frontend.vue.component.loader;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.annotations.component.PropDefault;
import com.axellience.vuegwt.core.client.component.IsVueComponent;

@Component
public class LoaderComponent implements IsVueComponent {

    @Prop
    protected String text;

    @Prop
    protected boolean error;

    @PropDefault("error")
    public boolean defaultError() {
        return false;
    }
}
