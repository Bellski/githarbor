package ru.githarbor.frontend.monaco.gsi;


import ru.githarbor.shared.SourceElementDTO;

public class Range implements Comparable<Range> {
    private double startLine;
    private double endLine;
    private double startColumn;
    private double endColumn;

    public Range(double startLine, double endLine, double startColumn, double endColumn) {
        this.startLine = startLine;
        this.endLine = endLine;
        this.startColumn = startColumn;
        this.endColumn = endColumn;
    }

    public static Range of(SourceElementDTO dto) {
        return new Range(
                dto.getStartLine(),
                dto.getEndLine(),
                dto.getStartColumn(),
                dto.getEndColumn()
        );
    }

    public static Range of(double line, double startColumn, double endColumn) {
        return new Range(line, line, startColumn, endColumn);
    }

    public double getStartLine() {
        return startLine;
    }

    public double getEndLine() {
        return endLine;
    }

    public double getStartColumn() {
        return startColumn;
    }

    public double getEndColumn() {
        return endColumn;
    }

    @Override
    public int compareTo(Range o) {
        if (startLine == o.startLine) {
            return Double.compare(startColumn, o.startColumn);
        }

        return Double.compare(startLine, o.startLine);
    }

    public boolean inRange(double line, double column) {
        boolean inRange = startLine <= line && endLine >= line;

        if (line == endLine) {
            inRange = endColumn >= column;
        }

        return inRange;
    }

    public boolean equalRange(double startLine, double startColumn) {
        return startLine == this.startLine && startColumn == this.startColumn;
    }

    public static boolean gtOrEq(Range range1, Range range2) {
        return range1.getStartLine() >= range2.getStartLine() && range1.getStartColumn() > range2.getEndColumn();
    }

    @Override
    public String toString() {
        return startLine + ":" + startColumn;
    }
}