package ru.githarbor.frontend.monaco;

public class Usage {
    public final String className;
    public final double line;
    public String usageText;
    public final IRange range;

    public Usage(String className, double line, String usageText, IRange range) {
        this.className = className;
        this.line = line;
        this.range = range;

        this.usageText = usageText;
    }

}
