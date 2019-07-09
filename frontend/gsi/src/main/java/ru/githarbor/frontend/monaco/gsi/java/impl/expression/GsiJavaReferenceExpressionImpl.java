package ru.githarbor.frontend.monaco.gsi.java.impl.expression;

import ru.githarbor.frontend.monaco.gsi.GsiElement;
import ru.githarbor.frontend.monaco.gsi.Range;
import ru.githarbor.frontend.monaco.gsi.java.GsiJavaClass;
import ru.githarbor.frontend.monaco.gsi.java.expression.GsiJavaReferenceExpression;
import ru.githarbor.frontend.monaco.gsi.java.impl.BaseGsiElement;
import ru.githarbor.frontend.monaco.gsi.java.statement.GsiJavaDeclarationStatement;
import ru.githarbor.frontend.monaco.gsi.java.variable.GsiJavaVariable;
import ru.githarbor.shared.JavaElementType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class GsiJavaReferenceExpressionImpl extends BaseGsiElement implements GsiJavaReferenceExpression {

    public GsiJavaReferenceExpressionImpl(Range range, JavaElementType type, GsiElement parent) {
        super(range, type, parent);
    }

    @Override
    public String getText() {
        if (getType() == JavaElementType.OBJECT_CREATION_EXPRESSION) {
            return "new " + findChildByType(JavaElementType.TYPE_EXPRESSION).getText() + "()";
        } else if (getType() == JavaElementType.TYPE_EXPRESSION) {
            return findChildByType(JavaElementType.NAME).getText();
        } else if (getType() == JavaElementType.NAME_EXPRESSION) {
            return findChildByType(JavaElementType.NAME).getText();
        }
        return getType().name();
    }

    private String getName() {
        return findChildByType(JavaElementType.NAME).getText();
    }

    @Override
    public GsiElement[] resolve() {
        switch (getType()) {
            case FIELD_ACCESS_EXPRESSION:
                final GsiElement resolvedFieldAccess = resolveFieldAccess();
                return resolvedFieldAccess != null ? new GsiElement[]{resolvedFieldAccess} : new GsiElement[0];
            case NAME_EXPRESSION:
                final GsiElement resolvedNameExpression = resolveNameExpression();

                return resolvedNameExpression != null ? new GsiElement[]{resolvedNameExpression} : new GsiElement[0];
            case METHOD_CALL_EXPRESSION:
                return resolveMethodCallExpression();
            case TYPE_EXPRESSION:
                return resolveTypeExpression();
        }

        return new GsiElement[0];
    }

    private GsiElement resolveNameExpression() {
        GsiElement resolveResult;
        GsiElement parent = getParent();

        while (parent != null) {

            resolveResult = Stream.of(parent.findChildrenByType(JavaElementType.DECLARATION_STATEMENT))
                    .map(statement -> ((GsiJavaDeclarationStatement) statement).findVariableByName(getName()))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);

            if (resolveResult == null) {
                resolveResult = Stream.of(parent.findChildrenByType(JavaElementType.PARAMETER))
                        .filter(variable -> ((GsiJavaVariable) variable).getName().equals(getName()))
                        .findFirst()
                        .orElse(null);
            }

            if (resolveResult == null) {
                resolveResult = Stream.of(parent.findChildrenByType(JavaElementType.FIELD))
                        .filter(variable -> ((GsiJavaVariable) variable).getName().equals(getName()))
                        .findFirst()
                        .orElse(null);
            }

            if (resolveResult != null) {
                return resolveResult;
            }

            parent = parent.getParent();
        }

        GsiElement[] resolvedType = resolveTypeExpression();

        if (resolvedType.length == 1) {
            return resolvedType[0];
        }

        return null;
    }

    private GsiElement resolveFieldAccess() {
        final GsiJavaClass containingClass = findContainingClass(this);

        return containingClass.findFieldByName(getName());
    }

    private GsiElement[] resolveMethodCallExpression() {
        GsiJavaClass containingClass = findContainingClass(this);

        final List<GsiElement> resolvedMethods = new ArrayList<>();

        while (containingClass != null) {
            resolvedMethods
                    .addAll(Arrays.asList(containingClass.findMethodsByName(getName())));

            containingClass = findContainingClass(containingClass);
        }

        return resolvedMethods.toArray(new GsiElement[0]);
    }

    private GsiElement[] resolveTypeExpression() {
        GsiJavaClass containingClass = findContainingClass(this);

        while (containingClass != null) {
            if (containingClass.getName().equals(getName())) {
                return new GsiElement[] {containingClass};
            }

            final GsiElement innerClass = containingClass.findInnerClassByName(getName());

            if (innerClass != null) {
                return new GsiElement[] {innerClass};
            }

            containingClass = findContainingClass(containingClass);
        }

        return new GsiElement[0];
    }

    private static GsiJavaClass findContainingClass(GsiElement findFrom) {
        GsiElement parent = findFrom.getParent();

        while (parent != null) {
            if (parent.getType() == JavaElementType.CLASS) {
                return (GsiJavaClass) parent;
            }

            parent = parent.getParent();
        }

        return null;
    }
}
