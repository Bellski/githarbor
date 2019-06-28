package ru.githarbor.frontend.harbor.vue.harbor.window.codesearch.list;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import jsinterop.annotations.JsMethod;
import ru.githarbor.frontend.harbor.vue.harbor.window.codesearch.item.CodeSearchItem;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;

@Component(components = {
        LoaderComponent.class
})
public class CodeSearchItemListComponent implements IsVueComponent, HasMounted {

    @Prop
    public CodeSearchItem[] items;

    @Data
    public double currentIndex = 0;

    @Prop
    public boolean loadMore;

    @Prop
    public boolean loadingMore;

    @Override
    public void mounted() {
        onItemSelect(currentIndex, items[(int) currentIndex]);
    }

    @JsMethod
    public void onItemSelect(double index, CodeSearchItem item) {
        this.currentIndex = index;

        vue().$emit("item-select", item);
    }

    @JsMethod
    public void onLoadMore() {
        vue().$emit("load-more");
    }
}
