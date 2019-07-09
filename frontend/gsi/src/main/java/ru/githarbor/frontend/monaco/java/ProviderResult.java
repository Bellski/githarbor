package ru.githarbor.frontend.monaco.java;

import jsinterop.annotations.JsType;
import ru.githarbor.frontend.monaco.Location;

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
