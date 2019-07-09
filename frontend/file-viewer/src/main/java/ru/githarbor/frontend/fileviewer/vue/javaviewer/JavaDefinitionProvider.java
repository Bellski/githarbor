package ru.githarbor.frontend.fileviewer.vue.javaviewer;


import jsinterop.base.Js;
import ru.githarbor.frontend.monaco.IModelDeltaDecoration;
import ru.githarbor.frontend.monaco.IRange;
import ru.githarbor.frontend.monaco.ITextModel;
import ru.githarbor.frontend.monaco.Position;
import ru.githarbor.frontend.monaco.editor.IEditor;
import ru.githarbor.frontend.monaco.gsi.GsiElement;
import ru.githarbor.frontend.monaco.gsi.java.GsiJavaClass;
import ru.githarbor.frontend.monaco.gsi.java.expression.GsiJavaExpression;
import ru.githarbor.frontend.monaco.gsi.java.expression.GsiJavaReferenceExpression;
import ru.githarbor.frontend.monaco.gsi.java.impl.JavaFile;
import ru.githarbor.frontend.monaco.java.DefinitionResult;
import ru.githarbor.frontend.monaco.java.DefinitionType;
import ru.githarbor.frontend.monaco.java.UsageRange;
import ru.githarbor.frontend.monaco.java.UsageRanges;
import ru.githarbor.shared.JavaElementType;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;

@Singleton
public class JavaDefinitionProvider {

    private String[] decorations = new String[0];

    @Inject
    public JavaDefinitionProvider() {

    }

    public DefinitionResult provideDefinition(JavaFile javaFile, IEditor editor, IRange range) {
        return javaFile
                .findElementStartFrom(range.getStartLineNumber(), range.getStartColumn())
                .map(element -> {

                    if (element instanceof GsiJavaExpression) {
                        final GsiJavaReferenceExpression referenceExpression = (GsiJavaReferenceExpression) element;

                        final IRange[] result = Stream
                                .of(referenceExpression.resolve())
                                .map(resolved -> {
                                    final GsiElement identifier = resolved.findChildByType(JavaElementType.NAME);

                                    return IRange.create(
                                            identifier.getRange().getStartLine(),
                                            identifier.getRange().getEndLine(),
                                            identifier.getRange().getStartColumn(),
                                            identifier.getRange().getEndColumn()
                                    );
                                })
                                .toArray(IRange[]::new);


                        return new DefinitionResult(DefinitionType.REFERENCE, result);
                    } else {
                        final UsageRange[] usageRanges = Stream
                                .of(javaFile.findUsages(element))
                                .map(resolved -> {
                                    final GsiElement identifier = resolved.findChildByType(JavaElementType.NAME);

                                    return new UsageRange(collectClasses(resolved), IRange.create(
                                            identifier.getRange().getStartLine(),
                                            identifier.getRange().getEndLine(),
                                            identifier.getRange().getStartColumn(),
                                            identifier.getRange().getEndColumn()
                                    ));
                                })
                                .toArray(UsageRange[]::new);

                        return new DefinitionResult(
                                DefinitionType.FIND_USAGES,
                                new UsageRanges(
                                        element.getText(),
                                        usageRanges
                                )
                        );
                    }
                })
                .orElseGet(() -> {

                    link(editor, range, "Nothing found");
                    return DefinitionResult.UNRESOLVED;
                });
    }

    public void highLight(JavaFile javaFile, IEditor editor, IRange range) {
        final ITextModel model = Js.cast(editor.getModel());

        clearDecorations(editor);


        final GsiElement element = javaFile
                .findElementStartFrom(range.getStartLineNumber(), range.getStartColumn())
                .orElse(null);

        if (element != null) {

            link(editor, range);


            if (element instanceof GsiJavaExpression) {
                final GsiJavaReferenceExpression referenceExpression = (GsiJavaReferenceExpression) element;

                GsiElement[] resolvedElements = referenceExpression.resolve();

                if (element.getType() == JavaElementType.TYPE_EXPRESSION || element.getType() == JavaElementType.NAME_EXPRESSION) {

                    final DefinitionResult definitionResult = provideDefinition(javaFile, editor, range);

                    if (definitionResult.getType() == DefinitionType.UNRESOLVED) {
                        link(editor, range, "Nothing found");

                        return;
                    }

                    return;
                }

                if (resolvedElements.length == 1) {
                    final GsiElement resolvedElement = resolvedElements[0];
                    final GsiElement resolvedNameElement = resolvedElement.findChildByType(JavaElementType.NAME);

                    String[] message;

                    if (resolvedElement.getRange().getStartLine() == resolvedElement.getRange().getEndLine()) {
                        IRange hoveMessageRange = IRange.create(
                                resolvedElement.getRange().getStartLine(),
                                resolvedElement.getRange().getStartColumn(),
                                resolvedElement.getRange().getEndColumn()
                        );

                        message = new String[]{model.getValueInRange(hoveMessageRange)};
                    } else {
                        IRange hoveMessageRange = IRange.create(
                                resolvedElement.getRange().getStartLine(),
                                resolvedElement.getRange().getStartColumn(),
                                model.getLineLastNonWhitespaceColumn(resolvedElement.getRange().getStartLine())
                        );

                        message = new String[]{model.getValueInRange(hoveMessageRange)};
                    }


                    if (resolvedElement.getType() == JavaElementType.CLASS || resolvedElement.getType() == JavaElementType.ENUM) {
                        StringJoiner className = new StringJoiner(".");

                        GsiElement classElement = resolvedElement.getParent();

                        while (classElement != null) {
                            className.add(((GsiJavaClass) classElement).getName());

                            classElement = classElement.getParent();
                        }

                        className.add(resolvedNameElement.getText());

                        String beforeName = model.getValueInRange(
                                IRange.create(
                                        resolvedElement.getRange().getStartLine(),
                                        resolvedElement.getRange().getStartColumn(),
                                        resolvedNameElement.getRange().getStartColumn()
                                )
                        );

                        String afterName = model.getValueInRange(
                                IRange.create(
                                        resolvedElement.getRange().getStartLine(),
                                        resolvedNameElement.getRange().getEndColumn(),
                                        model.getLineLastNonWhitespaceColumn(resolvedElement.getRange().getStartLine())
                                )
                        );

                        message = new String[]{
                                javaFile.getPackage(),
                                beforeName + " " + className + " " + afterName
                        };
                    }


                    link(editor, range, message);
                }
            } else {
                link(
                        editor,
                        range,
                        "Show usages of " + element.getType() + " '" + element.findChildByType(JavaElementType.NAME).getText() + "'"
                );
            }
        }
    }

    private void link(IEditor editor, IRange range) {
        decorations = editor.deltaDecorations(decorations, new IModelDeltaDecoration[]{new IModelDeltaDecoration(range, "goto-definition-link")});
    }

    private void link(IEditor editor, IRange range, String hoveMessage) {
        decorations = editor.deltaDecorations(decorations, new IModelDeltaDecoration[]{new IModelDeltaDecoration(range, "goto-definition-link", hoveMessage)});
    }

    private void link(IEditor editor, IRange range, String[] hoveMessage) {
        decorations = editor.deltaDecorations(
                decorations,
                new IModelDeltaDecoration[]{
                        new IModelDeltaDecoration(
                                range,
                                "goto-definition-link",
                                hoveMessage
                        )
                }
        );
    }

    private static String collectClasses(GsiElement findFrom) {
        List<String> classNames = new ArrayList<>();
        GsiElement parent = findFrom.getParent();

        while (parent != null) {
            if (parent.getType() == JavaElementType.CLASS || parent.getType() == JavaElementType.ENUM) {
                classNames.add(0, ((GsiJavaClass) parent).getName());
            }

            parent = parent.getParent();
        }

        return String.join(".", classNames);
    }

    public void clearDecorations(IEditor editor) {
        if (decorations.length > 0) {
            decorations = editor.deltaDecorations(decorations, new IModelDeltaDecoration[0]);
        }
    }

    public GsiElement[] resolveDepth(JavaFile javaFile, Position position) {
        return javaFile.resolveDepth(position.lineNumber, position.column);
    }

}
