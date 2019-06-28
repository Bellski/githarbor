package ru.githarbor.frontend.harbor.vue.component.sidebar;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import elemental2.core.JsArray;
import jsinterop.annotations.JsMethod;

import javax.inject.Inject;

@Component
public class SideBarTabComponent implements IsVueComponent {

    @Prop
    protected double index;

    @Prop
    protected String title;

    @Prop
    protected JsArray<String> icon;

    @Inject
    @Data
    protected SideBarState sideBarState;

    @JsMethod
    public void handleTabClick() {
        sideBarState.sideBarTabIndex = index;
    }

    @Computed
    public boolean getIsActive() {
        return sideBarState.sideBarTabIndex == index;
    }

    @Computed
    public boolean getHasIconPrefix() {
        return icon.length > 1;
    }

    @Computed
    public String getIconWithoutPrefix() {
        return icon.getAt(0);
    }

    @Computed
    public JsArray<String> getIconWithPrefix() {
        return icon;
    }
}
