package ru.githarbor.frontend.harbor.vue.component.sidebar;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Ref;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasDestroyed;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import com.axellience.vuegwt.core.client.component.hooks.HasRender;
import com.axellience.vuegwt.core.client.vnode.VNode;
import com.axellience.vuegwt.core.client.vnode.VNodeData;
import com.axellience.vuegwt.core.client.vnode.builder.VNodeBuilder;
import elemental2.core.JsArray;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

import javax.inject.Inject;
import java.util.Objects;

@Component(hasTemplate = false, components = {SideBarPaneComponent.class, SideBarTabComponent.class})
public class SideBarComponent implements IsVueComponent, HasRender, HasMounted, HasDestroyed {

    @Ref
    protected IsVueComponent splitPanes;

    @Data
    @Inject
    public SideBarState state;

    @Override
    public void mounted() {
        calculateFirstPane();
    }

    private void calculateFirstPane() {
        final HTMLElement containerElement = splitPanes.vue().$ref("container");

        final HTMLElement firstPane = (HTMLElement) containerElement.childNodes.item(0);
        firstPane.style.display = state.sideBarTabIndex == 0 ? "none" : null;
    }

    @Override
    public VNode render(VNodeBuilder builder) {
        final VNode tabsList = builder.el("ul");
        tabsList.setData(new VNodeData().setClassProp("g-sidebar-header-tab-list"));

        final VNode header = builder.el("div", tabsList);
        header.setData(new VNodeData().setClassProp("g-sidebar-header"));


        final VNode[] panes = Js.uncheckedCast(vue().$slots().get("default").filter((p0, p1, p2) -> Objects.nonNull(p0.getComponentOptions()) && p0.getComponentOptions().getTag().equals("side-bar-pane")));

        for (int i = 0; i < panes.length; i++) {
            final VNode pane = panes[i];

            final JsPropertyMap<Object> panePropsData = Js.cast(pane.getComponentOptions().getPropsData());
            panePropsData.set("index", i + 1);

            final VNode sideBarTab = builder.el(
                    "side-bar-tab",
                    VNodeData
                            .get()
                            .setProps(
                                    JsPropertyMap
                                            .of(
                                                    "title", panePropsData.get("title"),
                                                    "index", i + 1,
                                                    "icon", panePropsData.get("icon")
                                            )
                            )
            );


            tabsList.addChild(sideBarTab);
        }

        final VNode panels = builder.el(
                "div",
                VNodeData
                        .get()
                        .setClassProp("g-sidebar-content-container"),
                builder.el(
                        "split-panes",
                        VNodeData
                                .get()
                                .setAttrs(JsPropertyMap.of("watch-slots", true))
                                .on("resized", event -> {
                                    final JsArray<JsPropertyMap> evt = Js.cast(event);

                                    state.sideBarTabContentWidth = Js.cast(evt.getAt(0).get("width"));
                                    state.sideBarContentWidth = Js.cast(evt.getAt(1).get("width"));

                                    vue().$emit("splitter-moved", event);
                                })
                                .setRef("splitPanes"),
                        builder.el("div", VNodeData
                                        .get()
                                        .setClassProp("g-sidebar-panes")
                                        .setAttrs(JsPropertyMap.of("splitpanes-size", state.sideBarTabContentWidth, "splitpanes-min", "15")),
                                vue().$slots().get("default")
                        ),
                        builder.el("div", VNodeData
                                        .get()
                                        .setClassProp("g-sidebar-content")
                                        .setAttrs(JsPropertyMap.of("splitpanes-size", state.sideBarContentWidth)),
                                vue().$slots().get("content")
                        )
                )
        );

        return builder
                .el("div", header, panels)
                .setData(new VNodeData().setClassProp(JsPropertyMap.of("g-sidebar", true, "no-content", state.sideBarTabIndex == 0)));
    }

    @Override
    public void destroyed() {

    }
}
