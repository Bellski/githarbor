package ru.githarbor.frontend.harbor.jslib.monaco.java;


import ru.githarbor.frontend.harbor.jslib.monaco.IRange;

public class UsageRange {
    public final String inClass;
    public final IRange range;

    public UsageRange(String inClass, IRange range) {
        this.inClass = inClass;
        this.range = range;
    }
}
