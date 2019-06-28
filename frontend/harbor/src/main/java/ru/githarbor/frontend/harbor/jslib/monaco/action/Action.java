package ru.githarbor.frontend.harbor.jslib.monaco.action;

import elemental2.core.JsArray;
import jsinterop.annotations.JsProperty;

public class Action {

    @JsProperty
    private String id;

    @JsProperty
    private String label;

    @JsProperty
    private String alias;

    @JsProperty
    private String contextMenuGroupId;

    @JsProperty
    private ActionFunction run;

    @JsProperty
    private JsArray<Integer> keybindings = new JsArray<>();

    public Action(String id, String label, String alias, String contextMenuGroupId, ActionFunction run, Integer... keys) {
        this.id = id;
        this.label = label;
        this.alias = alias;
        this.contextMenuGroupId = contextMenuGroupId;
        this.run = run;
        this.keybindings.push(keys);
    }
}
