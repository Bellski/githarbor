package ru.githarbor.frontend.monaco.gsi.java.impl.statement;

import ru.githarbor.frontend.monaco.gsi.GsiElement;
import ru.githarbor.frontend.monaco.gsi.Range;
import ru.githarbor.frontend.monaco.gsi.java.GsiJavaStatement;
import ru.githarbor.frontend.monaco.gsi.java.impl.BaseGsiElement;
import ru.githarbor.shared.JavaElementType;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GsiJavaStatementImpl extends BaseGsiElement implements GsiJavaStatement {

    public GsiJavaStatementImpl(Range range, JavaElementType type, GsiElement parent) {
        super(range, type, parent);
    }

    @Override
    public String getText() {
        if (getType() == JavaElementType.LAMBDA_STATEMENT) {

            final String parameters = Stream
                    .of(findChildrenByType(JavaElementType.PARAMETER))
                    .map(element -> element.findChildByType(JavaElementType.NAME).getText())
                    .collect(Collectors.joining(","));


            return !parameters.isEmpty() ? "(" + parameters + ")->{...}" : "()->{...}";
        }
        return "<unnamed>";
    }
}
