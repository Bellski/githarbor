package ru.githarbor.frontend.monaco.gsi.java.impl.expression;


import ru.githarbor.frontend.monaco.gsi.GsiElement;
import ru.githarbor.frontend.monaco.gsi.Range;
import ru.githarbor.frontend.monaco.gsi.java.expression.GsiJavaAssignExpression;
import ru.githarbor.frontend.monaco.gsi.java.impl.BaseGsiElement;
import ru.githarbor.shared.JavaElementType;

public class GsiJavaAssignExpressionImpl extends BaseGsiElement implements GsiJavaAssignExpression {

    public GsiJavaAssignExpressionImpl(Range range, GsiElement parent) {
        super(range, JavaElementType.ASSIGN_EXPRESSION, parent);
    }

    @Override
    public String getText() {
        return getType() + " " + getRange();
    }
}
