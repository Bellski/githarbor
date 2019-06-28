package ru.githarbor.frontend.harbor.vue.component.sidebar;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.annotations.component.Watch;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasDestroyed;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import elemental2.core.JsArray;
import elemental2.dom.HTMLElement;

import javax.inject.Inject;

@Component
public class SideBarPaneComponent implements IsVueComponent, HasDestroyed, HasMounted {

    @Prop
    protected double index;

    @Prop
    protected String title;

    @Prop
    protected JsArray<String> icon;

    @Inject
    @Data
    public SideBarState sideBarState;

    @Watch("sideBarState.sideBarTabIndex")
    public void watchSideBarTabIndex(double newTabIndex) {
        final HTMLElement element = vue().$el();
        element.style.display = newTabIndex == index ? null : "none";
    }

    @Override
    public void destroyed() {
    }

    @Override
    public void mounted() {
        final HTMLElement element = vue().$el();

        element.style.display = sideBarState.sideBarTabIndex == index ? null : "none";
    }
}
