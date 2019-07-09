package ru.githarbor.frontend.monaco.gsi.java.impl.expression;


import ru.githarbor.frontend.monaco.gsi.Range;
import ru.githarbor.frontend.monaco.gsi.java.expression.GsiJavaMethodCallExpression;
import ru.githarbor.frontend.monaco.gsi.java.impl.BaseGsiElement;
import ru.githarbor.shared.JavaElementType;

public class GsiJavaMethodCallExpressionImpl extends BaseGsiElement implements GsiJavaMethodCallExpression {

    public GsiJavaMethodCallExpressionImpl(Range range, JavaElementType type) {
        super(range, type);
       }

    @Override
    public String getText() {
        return null;
    }
}
