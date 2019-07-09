package ru.githarbor.backend._main.server.resolver;


import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import ru.githarbor.shared.JavaElementType;
import ru.githarbor.shared.JavaSourceMetadataDTO;
import ru.githarbor.shared.SourceElementDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JavaSourceResolver {

    public static Optional<JavaSourceMetadataDTO> resolve(String javaSourceText) {
        final JavaParser javaparser = new JavaParser(new ParserConfiguration()
                .setLanguageLevel(ParserConfiguration.LanguageLevel.RAW)
                .setIgnoreAnnotationsWhenAttributingComments(true)
                .setAttributeComments(false));

        final ParseResult<CompilationUnit> parseResult = javaparser.parse(javaSourceText);

        if (parseResult.isSuccessful()) {
            return parseResult.getResult()
                    .map(JavaSourceResolver::translate);
        }

        return Optional.empty();
    }

    private static JavaSourceMetadataDTO translate(CompilationUnit compilationUnit) {
        int[] packag3;
        final List<int[]> imports = new ArrayList<>();
        final List<String> symbols = new ArrayList<>();
        final List<SourceElementDTO> elements = new ArrayList<>();

        for (ImportDeclaration importDeclaration : compilationUnit.getImports()) {
            final Range importRange = importDeclaration.getName().getRange().get();

            int[] range = new int[4];
            range[0] = importRange.begin.line;
            range[1] = importRange.end.line;
            range[2] = importRange.begin.column;
            range[3] = importRange.end.column + 1;

            imports.add(range);
        }

        packag3 = compilationUnit
                .getPackageDeclaration()
                .map(packageDeclaration -> {
                    final Range packageRange = packageDeclaration.getName().getRange().get();

                    final int[] range = new int[4];
                    range[0] = packageRange.begin.line;
                    range[1] = packageRange.end.line;
                    range[2] = packageRange.begin.column;
                    range[3] = packageRange.end.column + 1;

                    return range;
                })
                .orElse(new int[0]);

        compilationUnit.accept(new VoidVisitorAdapter<SourceElementDTO>() {
            @Override
            public void visit(ClassOrInterfaceType classOrInterfaceType, SourceElementDTO parent) {
                final SourceElementDTO classOrInterfaceTypeElement = createSourceElement(
                        elements.size(),
                        classOrInterfaceType.getNameAsString(),
                        symbols,
                        classOrInterfaceType.getName().getRange().get(),
                        JavaElementType.TYPE_EXPRESSION,
                        parent.getIndex(),
                        parent.getClazzIndex()
                );

                elements.add(classOrInterfaceTypeElement);

                classOrInterfaceType.getTypeArguments().ifPresent(l -> l.forEach(v -> v.accept(this, parent)));
            }

            @Override
            public void visit(FieldDeclaration fieldDeclaration, SourceElementDTO parent) {

                for (int i = 0; i < fieldDeclaration.getVariables().size(); i++) {
                    final VariableDeclarator variableDeclarator = fieldDeclaration.getVariable(i);

                    Range fieldRange;

                    if (i == 0) {
                        final Position beginPosition = fieldDeclaration.getBegin().get();

                        fieldRange = new Range(beginPosition, variableDeclarator.getEnd().get());
                    } else {
                        fieldRange = variableDeclarator.getRange().get();
                    }

                    final SourceElementDTO fieldVariableDeclaratorElement = createSourceElement(
                            elements.size(),
                            variableDeclarator.getNameAsString(),
                            symbols,
                            fieldRange,
                            JavaElementType.FIELD,
                            parent.getIndex(),
                            parent.getClazzIndex()
                    );

                    elements.add(fieldVariableDeclaratorElement);

                    variableDeclarator.getName().accept(this, fieldVariableDeclaratorElement);
                    variableDeclarator.getInitializer().ifPresent(l -> l.accept(this, fieldVariableDeclaratorElement));
                    variableDeclarator.getType().accept(this, fieldVariableDeclaratorElement);
                }
            }


            @Override
            public void visit(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, SourceElementDTO parent) {
                final SourceElementDTO classOrInterfaceSourceElement = createSourceElement(
                        elements.size(),
                        classOrInterfaceDeclaration.getName().getId(),
                        symbols,
                        classOrInterfaceDeclaration.getRange().get(),
                        JavaElementType.CLASS,
                        parent != null ? parent.getIndex() : -1,
                        parent != null ? parent.getIndex() : -1
                );


                elements.add(classOrInterfaceSourceElement);

                classOrInterfaceDeclaration.getName().accept(this, classOrInterfaceSourceElement);
                classOrInterfaceDeclaration.getMembers().forEach(p -> p.accept(this, classOrInterfaceSourceElement));
                classOrInterfaceDeclaration.getExtendedTypes().forEach(p -> p.accept(this, classOrInterfaceSourceElement));
                classOrInterfaceDeclaration.getImplementedTypes().forEach(p -> p.accept(this, classOrInterfaceSourceElement));
            }

            @Override
            public void visit(EnumDeclaration enumDeclaration, SourceElementDTO parent) {
                final SourceElementDTO enumElement = createSourceElement(
                        elements.size(),
                        enumDeclaration.getNameAsString(),
                        symbols,
                        enumDeclaration.getRange().get(),
                        JavaElementType.ENUM,
                        parent != null ? parent.getIndex() : -1,
                        parent != null ? parent.getIndex() : -1
                );

                elements.add(enumElement);

                enumDeclaration.getName().accept(this, enumElement);
            }

            @Override
            public void visit(InitializerDeclaration initializerDeclaration, SourceElementDTO parent) {
                final SourceElementDTO initializerDeclarationSourceElement = createSourceElement(
                        elements.size(),
                        "",
                        symbols,
                        initializerDeclaration.getRange().get(),
                        JavaElementType.INITIALIZER_STATEMENT,
                        parent.getIndex(),
                        parent.getClazzIndex()

                );

                elements.add(initializerDeclarationSourceElement);

                initializerDeclaration.getBody().accept(this, initializerDeclarationSourceElement);
            }

            @Override
            public void visit(ConstructorDeclaration constructorDeclaration, SourceElementDTO parent) {
                final SourceElementDTO constructorDeclarationSourceElement = createSourceElement(
                        elements.size(),
                        constructorDeclaration.getName().getId(),
                        symbols,
                        constructorDeclaration.getRange().get(),
                        JavaElementType.CONSTRUCTOR,
                        parent.getIndex(),
                        parent.getIndex()
                );


                elements.add(constructorDeclarationSourceElement);

                constructorDeclaration.getName().accept(this, constructorDeclarationSourceElement);
                constructorDeclaration.getBody().accept(this, constructorDeclarationSourceElement);
                constructorDeclaration.getParameters().forEach(p -> p.accept(this, constructorDeclarationSourceElement));
            }

            @Override
            public void visit(MethodDeclaration methodDeclaration, SourceElementDTO parent) {
                final SourceElementDTO methodDeclarationSourceElement = createSourceElement(
                        elements.size(),
                        methodDeclaration.getNameAsString(),
                        symbols,
                        methodDeclaration.getRange().get(),
                        JavaElementType.METHOD,
                        parent.getIndex(),
                        parent.getIndex()
                );


                elements.add(methodDeclarationSourceElement);

                methodDeclaration.getType().accept(this, methodDeclarationSourceElement);
                methodDeclaration.getName().accept(this, methodDeclarationSourceElement);
                methodDeclaration.getBody().ifPresent(l -> l.accept(this, methodDeclarationSourceElement));
                methodDeclaration.getParameters().forEach(p -> p.accept(this, methodDeclarationSourceElement));
            }

            @Override
            public void visit(ObjectCreationExpr objectCreationExpr, SourceElementDTO parent) {
                final SourceElementDTO objectCreationSourceElement = createSourceElement(
                        elements.size(),
                        "",
                        symbols,
                        objectCreationExpr.getRange().get(),
                        JavaElementType.OBJECT_CREATION_EXPRESSION,
                        parent.getIndex(),
                        parent.getClazzIndex()
                );

                elements.add(objectCreationSourceElement);

                objectCreationExpr.getAnonymousClassBody().ifPresent(bodyDeclarations -> {
                    final SourceElementDTO classBodySourceElement = createSourceElement(
                            elements.size(),
                            "",
                            symbols,
                            objectCreationExpr.getRange().get(),
                            JavaElementType.ANONYMOUS_CLASS_BODY_STATEMENT,
                            objectCreationSourceElement.getIndex(),
                            objectCreationSourceElement.getClazzIndex()
                    );

                    elements.add(classBodySourceElement);

                    bodyDeclarations.forEach(v -> v.accept(this, classBodySourceElement));
                });

                objectCreationExpr.getArguments().forEach(p -> p.accept(this, objectCreationSourceElement));
                objectCreationExpr.getType().accept(this, objectCreationSourceElement);
                objectCreationExpr.getTypeArguments().ifPresent(l -> l.forEach(v -> v.accept(this, objectCreationSourceElement)));

            }

            @Override
            public void visit(LambdaExpr lambdaExpr, SourceElementDTO parent) {
                final SourceElementDTO lambdaExprSourceElement = createSourceElement(
                        elements.size(),
                        "",
                        symbols,
                        lambdaExpr.getRange().get(),
                        JavaElementType.LAMBDA_STATEMENT,
                        parent.getIndex(),
                        parent.getClazzIndex()
                );
                elements.add(lambdaExprSourceElement);

                lambdaExpr.getBody().accept(this, lambdaExprSourceElement);
                lambdaExpr.getParameters().forEach(p -> p.accept(this, lambdaExprSourceElement));
            }

            @Override
            public void visit(Parameter parameter, SourceElementDTO parent) {

                final SourceElementDTO parameterSourceElement = createSourceElement(
                        elements.size(),
                        parameter.getNameAsString(),
                        symbols,
                        parameter.getRange().get(),
                        JavaElementType.PARAMETER,
                        parent.getIndex(),
                        parent.getClazzIndex()
                );


                elements.add(parameterSourceElement);

                parameter.getName().accept(this, parameterSourceElement);
                parameter.getType().accept(this, parameterSourceElement);
            }

            @Override
            public void visit(VariableDeclarationExpr variableDeclarationExpr, SourceElementDTO parent) {
                final SourceElementDTO variableDeclarationExprElement = createSourceElement(
                        elements.size(),
                        "",
                        symbols,
                        variableDeclarationExpr.getRange().get(),
                        JavaElementType.DECLARATION_STATEMENT,
                        parent.getIndex(),
                        parent.getClazzIndex()
                );

                elements.add(variableDeclarationExprElement);

                variableDeclarationExpr.getVariables().forEach(p -> p.accept(this, variableDeclarationExprElement));
            }

            @Override
            public void visit(AssignExpr assignExpr, SourceElementDTO parent) {
                final SourceElementDTO assignExprSourceElement = createSourceElement(
                        elements.size(),
                        "",
                        symbols,
                        assignExpr.getRange().get(),
                        JavaElementType.ASSIGN_EXPRESSION,
                        parent.getIndex(),
                        parent.getClazzIndex()
                );

                elements.add(assignExprSourceElement);

                assignExpr.getTarget().accept(this, assignExprSourceElement);
                assignExpr.getValue().accept(this, assignExprSourceElement);
            }

            @Override
            public void visit(NameExpr nameExpr, SourceElementDTO parent) {
                final SourceElementDTO referenceSourceElement = createSourceElement(
                        elements.size(),
                        nameExpr.getNameAsString(),
                        symbols,
                        nameExpr.getRange().get(),
                        JavaElementType.NAME_EXPRESSION,
                        parent.getIndex(),
                        parent.getClazzIndex()
                );

                elements.add(referenceSourceElement);
            }

            @Override
            public void visit(FieldAccessExpr fieldAccessExpr, SourceElementDTO parent) {
                if (fieldAccessExpr.getScope().isThisExpr()) {
                    final SourceElementDTO fieldAccessSourceElement = createSourceElement(
                            elements.size(),
                            "",
                            symbols,
                            fieldAccessExpr.getRange().get(),
                            JavaElementType.FIELD_ACCESS_EXPRESSION,
                            parent.getIndex(),
                            parent.getClazzIndex()
                    );

                    elements.add(fieldAccessSourceElement);

                    final SourceElementDTO thisExpressionSourceElement = createSourceElement(
                            elements.size(),
                            "",
                            symbols,
                            fieldAccessExpr.getScope().getRange().get(),
                            JavaElementType.THIS_EXPRESSION,
                            fieldAccessSourceElement.getIndex(),
                            fieldAccessSourceElement.getClazzIndex()
                    );

                    elements.add(thisExpressionSourceElement);

                    final SourceElementDTO nameSourceElement = createSourceElement(
                            elements.size(),
                            fieldAccessExpr.getNameAsString(),
                            symbols,
                            fieldAccessExpr.getName().getRange().get(),
                            JavaElementType.NAME,
                            fieldAccessSourceElement.getIndex(),
                            fieldAccessSourceElement.getClazzIndex()
                    );

                    elements.add(nameSourceElement);
                } else {
                    final Optional<NameExpr> nameExprOptional = fieldAccessExpr.findFirst(NameExpr.class);
                    nameExprOptional.ifPresent(nameExpr -> {
                        final SourceElementDTO nameSourceElement = createSourceElement(
                                elements.size(),
                                nameExpr.getNameAsString(),
                                symbols,
                                nameExpr.getName().getRange().get(),
                                JavaElementType.NAME_EXPRESSION,
                                parent.getIndex(),
                                parent.getClazzIndex()
                        );

                        elements.add(nameSourceElement);
                    });
                }
            }

            @Override
            public void visit(VariableDeclarator variableDeclarator, SourceElementDTO parent) {
                final SourceElementDTO localVariableElement = createSourceElement(
                        elements.size(),
                        "",
                        symbols,
                        variableDeclarator.getRange().get(),
                        JavaElementType.LOCAL_VARIABLE,
                        parent.getIndex(),
                        parent.getClazzIndex()
                );

                elements.add(localVariableElement);

                variableDeclarator.getName().accept(this, localVariableElement);
                variableDeclarator.getType().accept(this, localVariableElement);
                variableDeclarator.getInitializer().ifPresent(l -> l.accept(this, localVariableElement));
            }

            @Override
            public void visit(MethodCallExpr methodCallExpr, SourceElementDTO parent) {
                if (!methodCallExpr.getScope().isPresent() || methodCallExpr.getScope().get().isThisExpr()) {
                    final SourceElementDTO methodCallExprSourceElement = createSourceElement(
                            elements.size(),
                            "",
                            symbols,
                            methodCallExpr.getRange().get(),
                            JavaElementType.METHOD_CALL_EXPRESSION,
                            parent.getIndex(),
                            parent.getClazzIndex()
                    );

                    elements.add(methodCallExprSourceElement);


                    methodCallExpr
                            .getScope()
                            .ifPresent(expression -> {
                                if (expression.isThisExpr()) {
                                    final SourceElementDTO thisExpressionSourceElement = createSourceElement(
                                            elements.size(),
                                            "",
                                            symbols,
                                            expression.getRange().get(),
                                            JavaElementType.THIS_EXPRESSION,
                                            methodCallExprSourceElement.getIndex(),
                                            methodCallExprSourceElement.getClazzIndex()
                                    );

                                    elements.add(thisExpressionSourceElement);

                                    final Optional<NameExpr> nameExprOptional = expression.findFirst(NameExpr.class);
                                    nameExprOptional.ifPresent(nameExpr -> {
                                        final SourceElementDTO nameSourceElement = createSourceElement(
                                                elements.size(),
                                                nameExpr.getNameAsString(),
                                                symbols,
                                                nameExpr.getName().getRange().get(),
                                                JavaElementType.NAME_EXPRESSION,
                                                thisExpressionSourceElement.getIndex(),
                                                thisExpressionSourceElement.getClazzIndex()
                                        );

                                        elements.add(nameSourceElement);
                                    });
                                }
                            });

                    methodCallExpr.getName().accept(this, methodCallExprSourceElement);
                    methodCallExpr.getArguments().forEach(p -> p.accept(this, methodCallExprSourceElement));
                } else if (methodCallExpr.getScope().isPresent()) {

                    final Optional<NameExpr> nameExprOptional = methodCallExpr.findFirst(NameExpr.class);
                    nameExprOptional.ifPresent(nameExpr -> {
                        final SourceElementDTO nameSourceElement = createSourceElement(
                                elements.size(),
                                nameExpr.getNameAsString(),
                                symbols,
                                nameExpr.getName().getRange().get(),
                                JavaElementType.NAME_EXPRESSION,
                                parent.getIndex(),
                                parent.getClazzIndex()
                        );

                        elements.add(nameSourceElement);
                    });

                    if (!nameExprOptional.isPresent()) {
                        final Optional<MethodCallExpr> methodCallExprFirstOptional = methodCallExpr.getScope().get().findFirst(MethodCallExpr.class);
                        methodCallExprFirstOptional.ifPresent(methodCallExpr1 -> {
                            final SourceElementDTO methodCallExprFirstElement = createSourceElement(
                                    elements.size(),
                                    methodCallExpr1.getNameAsString(),
                                    symbols,
                                    methodCallExpr1.getName().getRange().get(),
                                    JavaElementType.METHOD_CALL_EXPRESSION,
                                    parent.getIndex(),
                                    parent.getClazzIndex()
                            );

                            elements.add(methodCallExprFirstElement);

                            methodCallExpr1.getName().accept(this, methodCallExprFirstElement);
                            methodCallExpr1.getArguments().forEach(p -> p.accept(this, methodCallExprFirstElement));
                        });
                    }

                    methodCallExpr.getArguments().forEach(p -> p.accept(this, parent));
                }

                methodCallExpr.getScope().ifPresent(expression -> {
                    if (expression.isMethodCallExpr()) {
                        ((MethodCallExpr) expression).getArguments().forEach(p -> p.accept(this, parent));
                    }
                });
            }


            @Override
            public void visit(ExpressionStmt n, SourceElementDTO arg) {
                if (n.getExpression().isFieldAccessExpr()) {
                    if (n.getExpression().getChildNodes().size() == 2) {
                        super.visit(n, arg);
                    }
                } else {
                    super.visit(n, arg);
                }
            }

            @Override
            public void visit(ForEachStmt foreachStmt, SourceElementDTO parent) {
                final SourceElementDTO foreachStmtSourceElement = createSourceElement(
                        elements.size(),
                        "",
                        symbols,
                        foreachStmt.getRange().get(),
                        JavaElementType.FOR_EACH_STATEMENT,
                        parent.getIndex(),
                        parent.getClazzIndex()
                );

                elements.add(foreachStmtSourceElement);

                foreachStmt.getBody().accept(this, foreachStmtSourceElement);
                foreachStmt.getIterable().accept(this, foreachStmtSourceElement);
                foreachStmt.getVariable().accept(this, foreachStmtSourceElement);
            }

            @Override
            public void visit(ForStmt forStmt, SourceElementDTO parent) {

                final SourceElementDTO forStmtSourceElement = createSourceElement(
                        elements.size(),
                        "",
                        symbols,
                        forStmt.getRange().get(),
                        JavaElementType.FOR_STATEMENT,
                        parent.getIndex(),
                        parent.getClazzIndex()
                );

                elements.add(forStmtSourceElement);

                forStmt.getBody().accept(this, forStmtSourceElement);
                forStmt.getCompare().ifPresent(l -> l.accept(this, forStmtSourceElement));
                forStmt.getInitialization().forEach(p -> p.accept(this, forStmtSourceElement));
                forStmt.getUpdate().forEach(p -> p.accept(this, forStmtSourceElement));
            }

            @Override
            public void visit(TryStmt tryStmt, SourceElementDTO parent) {
                final SourceElementDTO tryStmtSourceElement = createSourceElement(
                        elements.size(),
                        "",
                        symbols,
                        tryStmt.getRange().get(),
                        JavaElementType.TRY_STATEMENT,
                        parent.getIndex(),
                        parent.getClazzIndex()
                );

                elements.add(tryStmtSourceElement);

                final SourceElementDTO tryBlockStmtSourceElement = createSourceElement(
                        elements.size(),
                        "",
                        symbols,
                        tryStmt.getTryBlock().getRange().get(),
                        JavaElementType.TRY_BLOCK_STATEMENT,
                        tryStmtSourceElement.getIndex(),
                        tryStmtSourceElement.getClazzIndex()
                );

                elements.add(tryBlockStmtSourceElement);

                tryStmt.getTryBlock().accept(this, tryBlockStmtSourceElement);

                tryStmt.getResources().forEach(p -> p.accept(this, tryBlockStmtSourceElement));

                tryStmt.getCatchClauses().forEach(catchClause -> {
                    final SourceElementDTO catchClauseSourceElement = createSourceElement(
                            elements.size(),
                            "",
                            symbols,
                            catchClause.getRange().get(),
                            JavaElementType.CATCH_CLAUSE_STATEMENT,
                            tryStmtSourceElement.getIndex(),
                            tryStmtSourceElement.getClazzIndex()
                    );

                    elements.add(catchClauseSourceElement);

                    catchClause.accept(this, catchClauseSourceElement);
                });


                tryStmt.getFinallyBlock()
                        .ifPresent(blockStmt -> {
                            final SourceElementDTO finallyStmtSourceElement = createSourceElement(
                                    elements.size(),
                                    "",
                                    symbols,
                                    blockStmt.getRange().get(),
                                    JavaElementType.FINALLY_BLOCK_STATEMENT,
                                    tryStmtSourceElement.getIndex(),
                                    tryStmtSourceElement.getClazzIndex()
                            );

                            elements.add(finallyStmtSourceElement);

                            blockStmt.accept(this, finallyStmtSourceElement);
                        });
            }

            @Override
            public void visit(SwitchStmt switchStmt, SourceElementDTO parent) {
                final SourceElementDTO switchStmtElement = createSourceElement(
                        elements.size(),
                        "",
                        symbols,
                        switchStmt.getRange().get(),
                        JavaElementType.SWITCH_STATEMENT,
                        parent.getIndex(),
                        parent.getClazzIndex()
                );

                elements.add(switchStmtElement);

                switchStmt.getEntries().forEach(switchEntryStmt -> {
                    final SourceElementDTO switchEntryStmtElement = createSourceElement(
                            elements.size(),
                            "",
                            symbols,
                            switchEntryStmt.getRange().get(),
                            JavaElementType.SWITCH_STATEMENT,
                            switchStmtElement.getIndex(),
                            switchStmtElement.getClazzIndex()
                    );

                    elements.add(switchEntryStmtElement);

                    switchEntryStmt.accept(this, switchEntryStmtElement);
                });

                switchStmt.getSelector().accept(this, switchStmtElement);
            }

            @Override
            public void visit(SimpleName simpleName, SourceElementDTO parent) {
                final SourceElementDTO simpleNameSourceElement = createSourceElement(
                        elements.size(),
                        simpleName.asString(),
                        symbols,
                        simpleName.getRange().get(),
                        JavaElementType.NAME,
                        parent.getIndex(),
                        parent.getClazzIndex()
                );

                elements.add(simpleNameSourceElement);

            }

        }, null);

        return JavaSourceMetadataDTO.create(packag3, symbols, elements, imports);
    }

    private static SourceElementDTO createSourceElement(int index, String name, List<String> symbols, Range range, JavaElementType type, int parentIndex, int clazz) {
        int n = symbols.indexOf(name);

        if (n == -1) {
            symbols.add(name);
            n = symbols.size() - 1;
        }

        return SourceElementDTO.create(
                index,
                !name.isEmpty() ? n : -1,
                range.begin.line,
                range.end.line,
                range.begin.column,
                range.end.column + 1,
                type.type,
                parentIndex,
                clazz
        );
    }
}
