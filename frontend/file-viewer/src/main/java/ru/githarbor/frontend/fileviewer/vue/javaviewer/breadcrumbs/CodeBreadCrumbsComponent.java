package ru.githarbor.frontend.fileviewer.vue.javaviewer.breadcrumbs;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import jsinterop.annotations.JsMethod;

@Component
public class CodeBreadCrumbsComponent implements IsVueComponent, HasCreated {

    @Prop
    public CodeBreadCrumb[] breadCrumbs;

    @Data
    public double currentIndex = -1;

    @Override
    public void created() {
        vue().$watch(() -> breadCrumbs, (newValue, oldValue) -> {
            currentIndex = -1;
        });
    }

    @JsMethod
    public void onBreadCrumbSelect(CodeBreadCrumb codeBreadCrumb, double index) {
        this.currentIndex = index;

        vue().$emit("select", codeBreadCrumb);
    }
}
