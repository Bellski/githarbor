package ru.githarbor.frontend.vue.component.loader;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.client.component.IsVueComponent;

@Component
public class LoaderComponent implements IsVueComponent {

    @Prop
    protected String text;
}
