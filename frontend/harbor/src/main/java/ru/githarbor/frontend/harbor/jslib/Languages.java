package ru.githarbor.frontend.harbor.jslib;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Languages {

    @Inject
    public Languages() {
    }

    public String[] getExtensions(String languageName) {
        final String[] language = Js.uncheckedCast(getLanguages().get(languageName));

        if (language == null) {
            return new String[0];
        }

        return language;
    }

    @JsProperty(namespace = JsPackage.GLOBAL)
    private native static JsPropertyMap<String[]> getLanguages();
}
