package ru.githarbor.frontend.harbor.vue.component.sidebar;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import jsinterop.annotations.JsMethod;
import ru.githarbor.frontend.harbor.event.Events;
import ru.githarbor.shared.User;

import javax.inject.Inject;

@Component
public class SideBarTabComponent implements IsVueComponent, HasCreated {

    @Inject
    public User user;

    @Prop
    protected double index;

    @Prop
    protected String title;

    @Prop
    protected JsArray<String> icon;

    @Inject
    @Data
    protected SideBarState sideBarState;

    @Override
    public void created() {

    }

    @JsMethod
    public void handleTabClick() {
        if (sideBarState.sideBarTabIndex  == index) {
            sideBarState.sideBarTabContentWidth = 0;
            sideBarState.sideBarContentWidth = 100;
        }

        if (sideBarState.sideBarTabIndex == 0) {
            sideBarState.sideBarTabContentWidth = sideBarState.oldSideBarTabContentWidth;
            sideBarState.sideBarContentWidth = sideBarState.oldSideBarContentWidth;
        }

        sideBarState.sideBarTabIndex = index == sideBarState.sideBarTabIndex ? 0 : index;

        vue().$nextTick(() -> {
            vue().$root().vue().$emit(Events.MAIN_SIDEBAR_RESIZED);

            DomGlobal.setTimeout(new DomGlobal.SetTimeoutCallbackFn() {
                @Override
                public void onInvoke(Object... p0) {
                    vue().$root().vue().$emit(Events.MAIN_SIDEBAR_RESIZED);
                }
            }, 50);
        });
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
