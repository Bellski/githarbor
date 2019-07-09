package ru.githarbor.frontend.fileviewer.vue.javaviewer;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Data;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import ru.githarbor.frontend.fileviewer.core.InitParams;
import ru.githarbor.frontend.fileviewer.core.JavaSourceResolverRpcClient;
import ru.githarbor.frontend.fileviewer.vue.MonacoContainerComponent;
import ru.githarbor.frontend.fileviewer.vue.javaviewer.breadcrumbs.CodeBreadCrumb;
import ru.githarbor.frontend.fileviewer.vue.javaviewer.breadcrumbs.CodeBreadCrumbsComponent;
import ru.githarbor.frontend.fileviewer.vue.javaviewer.usages.UsagesPopupComponent;
import ru.githarbor.frontend.fileviewer.vue.javaviewer.usages.UsagesPosition;
import ru.githarbor.frontend.monaco.*;
import ru.githarbor.frontend.monaco.editor.events.ICursorPositionChangedEvent;
import ru.githarbor.frontend.monaco.editor.events.IEditorMouseEvent;
import ru.githarbor.frontend.monaco.editor.events.IKeyboardEvent;
import ru.githarbor.frontend.monaco.gsi.GsiElement;
import ru.githarbor.frontend.monaco.gsi.JavaElementFactory;
import ru.githarbor.frontend.monaco.gsi.java.impl.JavaFile;
import ru.githarbor.frontend.monaco.java.DefinitionResult;
import ru.githarbor.frontend.monaco.java.DefinitionType;
import ru.githarbor.frontend.monaco.java.UsageRange;

import javax.inject.Inject;
import java.util.Arrays;

@Component(components = {
        CodeBreadCrumbsComponent.class,
        UsagesPopupComponent.class
})
public class JavaViewerComponent extends MonacoContainerComponent  {

    @Inject
    public InitParams initParams;

    @Inject
    public JavaDefinitionProvider javaDefinitionProvider;

    @Inject
    public JavaSourceResolverRpcClient javaSourceResolverRpcClient;

    @Data
    protected Usages usagesContainer = new Usages();

    @Data
    public UsagesPosition usagesPosition;

    @Data
    public CodeBreadCrumb[] breadCrumbs = new CodeBreadCrumb[0];

    private boolean cursorPositionProgrammatically = false;

    private JavaFile javaFile;

    private boolean analyzing = true;

    @Computed
    public Usage[] getUsages() {
        return Js.uncheckedCast(usagesContainer.usages.slice(0));
    }

    @Override
    protected void onMonacoCreated() {
        javaSourceResolverRpcClient.resolveSource(initParams.ownerWithName, initParams.branch + ":" + initParams.path)
                .subscribe(javaSourceMetadataDTO -> {
                    javaFile = JavaElementFactory.createJavaFile(javaSourceMetadataDTO, monaco.getModel());

                    analyzing = false;
                });

        monaco.onMouseMove(event -> onMonacoMouseMove(event));
        monaco.onKeyUp(event -> onMonacoKeyUp(event));
        monaco.onMouseUp(event -> onMonacoMouseUp(event));
        monaco.onDidChangeCursorPosition(event -> onChangeMonacoCursorPosition(event));
    }

    private void onMonacoMouseMove(IEditorMouseEvent event) {
        if (analyzing || !event.getCtrlKey || event.position == null) {
            return;
        }

        javaDefinitionProvider.clearDecorations(monaco);

        final WordPosition wordAtPosition = monaco.getModel().getWordAtPosition(event.position);

        if (wordAtPosition != null) {
            javaDefinitionProvider.highLight(
                    javaFile,
                    monaco,
                    IRange.create(
                            event.position.lineNumber,
                            event.position.lineNumber,
                            wordAtPosition.getStartColumn(),
                            wordAtPosition.getEndColumn()
                    )
            );
        }
    }

    private void onMonacoKeyUp(IKeyboardEvent event) {
        if (analyzing || !event.ctrlKey) {
            return;
        }

        javaDefinitionProvider.clearDecorations(monaco);
    }

    private void onMonacoMouseUp(IEditorMouseEvent event) {
        if (analyzing || !event.getCtrlKey || event.position == null) {
            return;
        }

        final ITextModel model = Js.cast(monaco.getModel());
        final WordPosition wordAtPosition = model.getWordAtPosition(event.position);

        if (wordAtPosition == null) {
            return;
        }

        final DefinitionResult definitionResult =
                javaDefinitionProvider.provideDefinition(
                        javaFile,
                        monaco,
                        IRange.create(event.position.lineNumber, event.position.lineNumber, wordAtPosition.getStartColumn(), wordAtPosition.getEndColumn()));

        if (definitionResult.getType() == DefinitionType.REFERENCE) {

            if (definitionResult.isEmpty()) {
                return;
            }

            final IRange definitionRange = definitionResult.getRanges()[0];

            monaco.setPosition(Position.create(definitionRange.getStartLineNumber(), definitionRange.getStartColumn()));
            monaco.revealPositionInCenter(Position.create(definitionRange.getStartLineNumber(), definitionRange.getStartColumn()));

            return;
        }

        if (definitionResult.getType() == DefinitionType.FIND_USAGES) {
            if (definitionResult.getUsageRanges().usageRanges.length == 1) {

                final UsageRange definitionRange = definitionResult.getUsageRanges().usageRanges[0];

                monaco.setPosition(Position.create(definitionRange.range.getStartLineNumber(), definitionRange.range.getStartColumn()));
                monaco.revealPositionInCenter(Position.create(definitionRange.range.getStartLineNumber(), definitionRange.range.getStartColumn()));

                return;
            }

            if (definitionResult.getUsageRanges().usageRanges.length > 1) {
                usagesContainer.usages.length = 0;
                usagesContainer.of = definitionResult.getUsageRanges().of;

                for (UsageRange usageRange : definitionResult.getUsageRanges().usageRanges) {
                    usagesContainer
                            .usages
                            .push(
                                    new Usage(
                                            usageRange.inClass,
                                            usageRange.range.getStartLineNumber(),
                                            model.getLineContent(usageRange.range.getStartLineNumber()),
                                            usageRange.range
                                    )
                            );
                }

                usagesPosition = new UsagesPosition(
                        (event.browserEvent.clientX),
                        (event.browserEvent.clientY)
                );
            }
        }
    }

    private void onChangeMonacoCursorPosition(ICursorPositionChangedEvent event) {
        if (cursorPositionProgrammatically) {
            cursorPositionProgrammatically = false;

            return;
        }

        javaDefinitionProvider.clearDecorations(monaco);

        final GsiElement[] depth = javaFile.resolveDepth(event.position.lineNumber, event.position.column);

        breadCrumbs = Arrays.stream(depth)
                .map(gsiElement -> new CodeBreadCrumb(gsiElement.getText(), gsiElement.getRange().getStartLine(), gsiElement.getRange().getStartColumn()))
                .toArray(CodeBreadCrumb[]::new);
    }

    @JsMethod
    public void onUsageSelect(Usage usage) {
        javaDefinitionProvider.clearDecorations(monaco);

        monaco.revealPositionInCenter(Position.create(usage.line, usage.range.getStartColumn()));
        monaco.setPosition(Position.create(usage.line, usage.range.getStartColumn()));
        monaco.focus();
    }

    @JsMethod
    public void onBreadCrumbSelect(CodeBreadCrumb codeBreadCrumb) {
        cursorPositionProgrammatically = true;

        monaco.revealPositionInCenter(Position.create(codeBreadCrumb.line, codeBreadCrumb.column));
        monaco.setPosition(Position.create(codeBreadCrumb.line, codeBreadCrumb.column));
        monaco.focus();
    }
}
