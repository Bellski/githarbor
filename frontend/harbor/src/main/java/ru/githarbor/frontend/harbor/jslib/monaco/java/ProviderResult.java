package ru.githarbor.frontend.harbor.jslib.monaco.java;

import jsinterop.annotations.JsType;
import ru.githarbor.frontend.harbor.jslib.monaco.Location;

@JsType
public class ProviderResult {
    private Location[] locations;

    public ProviderResult(Location[] locations) {
        this.locations = locations;
    }

    public Location[] get() {
        return locations;
    }
}
