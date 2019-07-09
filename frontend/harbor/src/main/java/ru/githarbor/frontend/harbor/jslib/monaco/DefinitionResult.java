package ru.githarbor.frontend.harbor.jslib.monaco;

import ru.githarbor.frontend.monaco.IRange;
import ru.githarbor.frontend.monaco.java.DefinitionType;
import ru.githarbor.frontend.monaco.java.UsageRanges;
import ru.githarbor.frontend.harbor.core.github.core.File;

public class DefinitionResult extends ru.githarbor.frontend.monaco.java.DefinitionResult {
    public static final DefinitionResult UNRESOLVED = new DefinitionResult(DefinitionType.UNRESOLVED);

    private File[] externalReferences;

    public DefinitionResult(DefinitionType type, IRange... ranges) {
        super(type, ranges);
    }

    public DefinitionResult(DefinitionType type, UsageRanges usageRanges) {
        super(type, usageRanges);
    }


    public DefinitionResult(DefinitionType type, File[] files) {
        super(type);

        this.externalReferences = files;
    }

    public File[] getExternalReferences() {
        return externalReferences;
    }
}
