package ru.githarbor.frontend.harbor.jslib.monaco.java;


import ru.githarbor.frontend.harbor.core.github.core.File;
import ru.githarbor.frontend.harbor.jslib.monaco.IRange;

public class DefinitionResult {
    public static final DefinitionResult UNRESOLVED = new DefinitionResult(DefinitionType.UNRESOLVED);

    private final DefinitionType type;
    private IRange[] ranges;
    private File externalReference;
    private UsageRanges usageRanges;

    public DefinitionResult(DefinitionType type, File externalReference) {
        this.type = type;
        this.externalReference = externalReference;
    }

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

    public File getExternalReference() {
        return externalReference;
    }

    public boolean isEmpty() {
        return ranges.length == 0;
    }
}
