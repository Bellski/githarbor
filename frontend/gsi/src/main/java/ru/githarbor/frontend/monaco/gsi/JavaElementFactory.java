package ru.githarbor.frontend.monaco.gsi;

import ru.githarbor.frontend.monaco.gsi.impl.GsiIdentifierImpl;
import ru.githarbor.frontend.monaco.gsi.java.impl.GsiJavaClassImpl;
import ru.githarbor.frontend.monaco.gsi.java.impl.GsiJavaMethodImpl;
import ru.githarbor.frontend.monaco.gsi.java.impl.GsiJavaTypeImpl;
import ru.githarbor.frontend.monaco.gsi.java.impl.JavaFile;
import ru.githarbor.frontend.monaco.gsi.java.impl.expression.GsiJavaAssignExpressionImpl;
import ru.githarbor.frontend.monaco.gsi.java.impl.expression.GsiJavaReferenceExpressionImpl;
import ru.githarbor.frontend.monaco.gsi.java.impl.statement.GsiJavaDeclarationStatementImpl;
import ru.githarbor.frontend.monaco.gsi.java.impl.statement.GsiJavaStatementImpl;
import ru.githarbor.frontend.monaco.gsi.java.impl.variable.GsiJavaVariableImpl;
import ru.githarbor.frontend.monaco.IRange;
import ru.githarbor.frontend.monaco.ITextModel;
import ru.githarbor.shared.JavaSourceMetadataDTO;
import ru.githarbor.shared.SourceElementDTO;

import java.util.Arrays;
import java.util.stream.Stream;

import static ru.githarbor.shared.JavaElementType.*;

public class JavaElementFactory {

    public static JavaFile createJavaFile(JavaSourceMetadataDTO metadataDTO, ITextModel model) {
        final String packag3 = model.getValueInRange(IRange.create(
                metadataDTO.packag3[0],
                metadataDTO.packag3[1],
                metadataDTO.packag3[2],
                metadataDTO.packag3[3]
        ));

        final String[] imports = Arrays
                .stream(metadataDTO.imports)
                .map(importRange -> model.getValueInRange(IRange.create(
                        importRange[0],
                        importRange[1],
                        importRange[2],
                        importRange[3]
                )))
                .toArray(String[]::new);

        return new JavaFile(
                packag3,
                Arrays.
                        stream(metadataDTO.elements)
                        .filter(sourceElementDTO -> sourceElementDTO.getParentIndex() == -1 && sourceElementDTO.getType() == CLASS)
                        .map(classElementDTO -> createElement(classElementDTO, metadataDTO, null))
                        .toArray(GsiElement[]::new),
                imports
        );
    }

    public static GsiElement createElement(SourceElementDTO elementDTO, JavaSourceMetadataDTO metadataDTO, GsiElement parent) {
        switch (elementDTO.getType()) {
            case CLASS:
            case ENUM:
                return createClass(elementDTO, metadataDTO, parent);
            case FIELD:
            case PARAMETER:
            case LOCAL_VARIABLE:
                return createVariable(elementDTO, metadataDTO, parent);
            case METHOD:
                return createMethod(elementDTO, metadataDTO, parent, false);
            case CONSTRUCTOR:
                return createMethod(elementDTO, metadataDTO, parent, true);
            case METHOD_CALL_EXPRESSION:
            case THIS_EXPRESSION:
            case FIELD_ACCESS_EXPRESSION:
            case NAME_EXPRESSION:
            case OBJECT_CREATION_EXPRESSION:
            case TYPE_EXPRESSION:
                return createReferenceExpression(elementDTO, metadataDTO, parent);
            case NAME:
                return createIdentifier(elementDTO, metadataDTO, parent);
            case ASSIGN_EXPRESSION:
                return createAssignExpression(elementDTO, metadataDTO, parent);
            case FOR_STATEMENT:
            case FOR_EACH_STATEMENT:
            case TRY_STATEMENT:
            case TRY_BLOCK_STATEMENT:
            case CATCH_CLAUSE_STATEMENT:
            case FINALLY_BLOCK_STATEMENT:
            case SWITCH_STATEMENT:
            case SWITCH_ENTRY_STATEMENT:
            case INITIALIZER_STATEMENT:
            case LAMBDA_STATEMENT:
            case ANONYMOUS_CLASS_BODY_STATEMENT:
                return createStatement(elementDTO, metadataDTO, parent);
            case DECLARATION_STATEMENT:
                return createDeclarationStatement(elementDTO, metadataDTO, parent);
        }

        throw new IllegalStateException();
    }

    public static GsiElement createClass(SourceElementDTO elementDTO, JavaSourceMetadataDTO metadataDTO, GsiElement parent) {

        final GsiJavaClassImpl javaClass = new GsiJavaClassImpl(
                Range.of(elementDTO),
                parent,
                false,
                elementDTO.getType() == ENUM
        );

        javaClass.setChildren(collectChildren(elementDTO, metadataDTO, javaClass));

        return javaClass;
    }

    public static GsiElement createVariable(SourceElementDTO elementDTO, JavaSourceMetadataDTO metadataDTO, GsiElement parent) {
        final GsiJavaVariableImpl javaVariable = new GsiJavaVariableImpl(Range.of(elementDTO), elementDTO.getType(), parent);
        javaVariable.setChildren(collectChildren(elementDTO, metadataDTO, javaVariable));

        return javaVariable;
    }

    public static GsiElement createMethod(SourceElementDTO elementDTO, JavaSourceMetadataDTO metadataDTO, GsiElement parent, boolean constructor) {
        final GsiJavaMethodImpl javaMethod = new GsiJavaMethodImpl(Range.of(elementDTO), parent, constructor);
        javaMethod.setChildren(collectChildren(elementDTO, metadataDTO, javaMethod));

        return javaMethod;
    }

    public static GsiElement createType(SourceElementDTO elementDTO, JavaSourceMetadataDTO metadataDTO, GsiElement parent) {
        final GsiJavaTypeImpl javaType = new GsiJavaTypeImpl(Range.of(elementDTO), parent);
        javaType.setChildren(new GsiElement[]{createIdentifier(elementDTO, metadataDTO, javaType)});

        return javaType;
    }

    public static GsiElement createReferenceExpression(SourceElementDTO elementDTO, JavaSourceMetadataDTO metadataDTO, GsiElement parent) {
        final GsiJavaReferenceExpressionImpl javaReferenceExpression = new GsiJavaReferenceExpressionImpl(Range.of(elementDTO), elementDTO.getType(), parent);

        if (elementDTO.getType() == NAME_EXPRESSION || elementDTO.getType() == TYPE_EXPRESSION) {
            javaReferenceExpression.setChildren(new GsiElement[]{createIdentifier(elementDTO, metadataDTO, javaReferenceExpression)});
        } else {
            javaReferenceExpression.setChildren(collectChildren(elementDTO, metadataDTO, javaReferenceExpression));
        }

        return javaReferenceExpression;
    }

    public static GsiElement createAssignExpression(SourceElementDTO elementDTO, JavaSourceMetadataDTO metadataDTO, GsiElement parent) {
        final GsiJavaAssignExpressionImpl javaAssignExpression = new GsiJavaAssignExpressionImpl(Range.of(elementDTO), parent);
        javaAssignExpression.setChildren(collectChildren(elementDTO, metadataDTO, javaAssignExpression));

        return javaAssignExpression;
    }

    public static GsiElement createDeclarationStatement(SourceElementDTO elementDTO, JavaSourceMetadataDTO metadataDTO, GsiElement parent) {
        final GsiJavaDeclarationStatementImpl javaDeclarationStatement = new GsiJavaDeclarationStatementImpl(Range.of(elementDTO), parent);
        javaDeclarationStatement.setChildren(collectChildren(elementDTO, metadataDTO, javaDeclarationStatement));

        return javaDeclarationStatement;
    }

    public static GsiElement createStatement(SourceElementDTO elementDTO, JavaSourceMetadataDTO metadataDTO, GsiElement parent) {
        final GsiJavaStatementImpl javaStatement = new GsiJavaStatementImpl(Range.of(elementDTO), elementDTO.getType(), parent);
        javaStatement.setChildren(collectChildren(elementDTO, metadataDTO, javaStatement));

        return javaStatement;
    }

    public static GsiElement createIdentifier(SourceElementDTO elementDTO, JavaSourceMetadataDTO metadataDTO, GsiElement parent) {
        return new GsiIdentifierImpl(Range.of(elementDTO), JavaSourceMetadataHelper.getElementName(elementDTO, metadataDTO), parent);
    }

    public static GsiElement[] collectChildren(SourceElementDTO elementDTO, JavaSourceMetadataDTO metadataDTO, GsiElement parent) {
        return Stream.of(JavaSourceMetadataHelper.getElementChildren(elementDTO, metadataDTO))
                .map(childElementDTO -> createElement(childElementDTO, metadataDTO, parent))
                .toArray(GsiElement[]::new);
    }
}
