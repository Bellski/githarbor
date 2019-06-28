package ru.githarbor.frontend.harbor.vue.harbor.sourcetabs;

import elemental2.core.JsArray;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.data.SourceTab;

public class SourceTabsState {
    public JsArray<SourceTab> tabs = new JsArray<>();
    public String activeCodeTab = null;
}
