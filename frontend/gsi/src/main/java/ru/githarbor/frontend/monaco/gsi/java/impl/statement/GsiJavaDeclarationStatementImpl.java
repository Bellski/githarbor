package ru.githarbor.frontend.monaco.gsi.java.impl.statement;


import ru.githarbor.frontend.monaco.gsi.GsiElement;
import ru.githarbor.frontend.monaco.gsi.Range;
import ru.githarbor.frontend.monaco.gsi.java.impl.BaseGsiElement;
import ru.githarbor.frontend.monaco.gsi.java.statement.GsiJavaDeclarationStatement;
import ru.githarbor.frontend.monaco.gsi.java.variable.GsiJavaVariable;
import ru.githarbor.shared.JavaElementType;

import java.util.stream.Stream;

public class GsiJavaDeclarationStatementImpl extends BaseGsiElement implements GsiJavaDeclarationStatement {

    public GsiJavaDeclarationStatementImpl(Range range, GsiElement parent) {
        super(range, JavaElementType.DECLARATION_STATEMENT, parent);
    }

    @Override
    public String getText() {
        return "Declaration";
    }

    @Override
    public GsiElement findVariableByName(String name) {
        return Stream.of(findChildrenByType(JavaElementType.LOCAL_VARIABLE))
                .filter(variable -> ((GsiJavaVariable) variable).getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
