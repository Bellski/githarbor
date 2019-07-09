package ru.githarbor.frontend.monaco.java;

public class UsageRanges {
    public final String of;
    public final UsageRange[] usageRanges;

    public UsageRanges(String of, UsageRange[] usageRanges) {
        this.of = of;
        this.usageRanges= usageRanges;
    }
}
