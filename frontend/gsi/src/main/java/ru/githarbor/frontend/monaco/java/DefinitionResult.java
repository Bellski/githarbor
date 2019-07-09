package ru.githarbor.frontend.monaco.java;


import ru.githarbor.frontend.monaco.IRange;

public class DefinitionResult {
    public static final DefinitionResult UNRESOLVED = new DefinitionResult(DefinitionType.UNRESOLVED);

    private final DefinitionType type;
    private IRange[] ranges;
    private UsageRanges usageRanges;


    public DefinitionResult(DefinitionType type, IRange... ranges) {
        this.type = type;
        this.ranges = ranges;
    }

    public DefinitionResult(DefinitionType type, UsageRanges usageRanges) {
        this.type = type;
        this.usageRanges  = usageRanges;
    }

    public DefinitionType getType() {
        return type;
    }

    public IRange[] getRanges() {
        return ranges;
    }

    public UsageRanges getUsageRanges() {
        return usageRanges;
    }

    public boolean isEmpty() {
        return ranges == null || ranges.length == 0;
    }
}
