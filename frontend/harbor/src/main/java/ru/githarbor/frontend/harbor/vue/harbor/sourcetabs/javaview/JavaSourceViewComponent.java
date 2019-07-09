package ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.javaview;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Ref;
import com.axellience.vuegwt.core.client.component.hooks.HasBeforeDestroy;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import ru.githarbor.frontend.monaco.*;
import ru.githarbor.frontend.monaco.gsi.GsiElement;
import ru.githarbor.frontend.monaco.gsi.JavaElementFactory;
import ru.githarbor.frontend.monaco.java.DefinitionType;
import ru.githarbor.frontend.monaco.java.JavaFileProvider;
import ru.githarbor.frontend.monaco.java.UsageRange;
import ru.githarbor.frontend.harbor.core.github.core.File;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.monaco.gsi.java.impl.JavaFile;
import ru.githarbor.frontend.harbor.core.rpc.JavaSourceResolverRpcClient;
import ru.githarbor.frontend.monaco.action.CopyUrlSelectionAction;
import ru.githarbor.frontend.monaco.editor.events.ICursorPositionChangedEvent;
import ru.githarbor.frontend.monaco.editor.events.IEditorMouseEvent;
import ru.githarbor.frontend.monaco.editor.events.IKeyboardEvent;
import ru.githarbor.frontend.harbor.jslib.monaco.DefinitionResult;
import ru.githarbor.frontend.harbor.jslib.monaco.ITextModel;
import ru.githarbor.frontend.harbor.jslib.monaco.JavaDefinitionProvider;
import ru.githarbor.frontend.harbor.jslib.monaco.MonacoFactory;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.MonacoSourceTabComponent;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.javaview.breadcrumbs.CodeBreadCrumb;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.javaview.breadcrumbs.CodeBreadCrumbsComponent;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.javaview.usages.UsagesPopupComponent;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.javaview.usages.UsagesPosition;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;
import ru.githarbor.shared.User;

import javax.inject.Inject;
import java.util.Arrays;

@Component(components = {
        LoaderComponent.class,
        UsagesPopupComponent.class,
        CodeBreadCrumbsComponent.class
})
public class JavaSourceViewComponent extends MonacoSourceTabComponent implements HasCreated, HasMounted, HasBeforeDestroy {

    @Inject
    public JavaSourceResolverRpcClient javaSourceResolverRpcClient;

    @Inject
    protected JavaFileProvider javaMetadataProvider;

    @Inject
    protected JavaDefinitionProvider javaDefinitionProvider;

    @Inject
    public User user;

    @Inject
    public Repository repository;

    @Inject
    public MonacoFactory monacoFactory;

    @Ref
    public HTMLElement monacoContainer;

    @Data
    public boolean loading = true;

    @Data
    public boolean analyzing;

    @Data
    protected Usages usagesContainer = new Usages();

    @Data
    public UsagesPosition usagesPosition;

    @Data
    public CodeBreadCrumb[] breadCrumbs = new CodeBreadCrumb[0];

    private boolean cursorPositionProgrammatically = false;

    private Disposable copyDisposableAction;
    private Disposable onMouseMoveDisposable;
    private Disposable onKeyUpDisposable;
    private Disposable onMouseUpDisposable;
    private Disposable onDidChangeCursorPositionDisposable;

    @Computed
    public Usage[] getUsages() {
        return Js.uncheckedCast(usagesContainer.usages.slice(0));
    }

    @Override
    public void created() {
        vue().$watch(() -> sourceTabsSharedState.getCurrentState().activeCodeTab, (newTab, oldTab) -> {
            if (loading) {
                mounted();
            }
        });
    }

    @Override
    public void mounted() {
        if (source.key.equals(sourceTabsSharedState.getCurrentState().activeCodeTab)) {
            init();
        }
    }

    private void init() {
        final File file = repository.getCurrentBranch().getFile(source.key).get();

        file.resolveContent().subscribe(content -> {
            final ITextModel model = monacoFactory.initModel(file.name, content);
            model.setGitHubFile(file);

            resolveJavaSource(file, model);

            loading = false;

            vue().$nextTick(() -> {
                monaco = monacoFactory.create(monacoContainer);
                copyDisposableAction = monaco.addAction(new CopyUrlSelectionAction(repository.toString(), repository.getCurrentBranch().name, source.key));
                monaco.setModel(model);
                onMouseMoveDisposable = monaco.onMouseMove(event -> onMonacoMouseMove(event));
                onKeyUpDisposable = monaco.onKeyUp(event -> onMonacoKeyUp(event));
                onMouseUpDisposable = monaco.onMouseUp(event -> onMonacoMouseUp(event));
                onDidChangeCursorPositionDisposable = monaco.onDidChangeCursorPosition(event -> onChangeMonacoCursorPosition(event));

                onMonacoCreated();

                vue().$nextTick(() -> {
                    if (source.range != null) {
                        revealRange(source.range);
                    }

                    monaco.layout();
                    monaco.focus();
                });
            });
        });
    }


    private void resolveJavaSource(File file, ITextModel model) {
        final JavaFile javaFile = javaMetadataProvider.getJavaFile(file.getPath());

        if (javaFile == null) {
            analyzing = true;

            javaSourceResolverRpcClient
                    .resolveSource(repository.toString(), repository.getCurrentBranch() + ":" + file.getPath())
                    .subscribe(metadata -> {
                        javaMetadataProvider.putIfAbsent(source.key, JavaElementFactory.createJavaFile(metadata, model));

                        analyzing = false;
                    });
        }
    }

    private void onMonacoMouseMove(IEditorMouseEvent event) {
        if (analyzing || !event.getCtrlKey || event.position == null) {
            return;
        }

        javaDefinitionProvider.clearDecorations(monaco);

        final WordPosition wordAtPosition = monaco.getModel().getWordAtPosition(event.position);

        if (wordAtPosition != null) {
            javaDefinitionProvider.highLight(
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

        if (definitionResult.getType() == DefinitionType.EXTERNAL_REFERENCE) {
            final File[] references = definitionResult.getExternalReferences();

            if (references.length == 1) {
                sourceTabsSharedState.addSourceTab(references[0]);
            }

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
                        (event.browserEvent.clientX - event.editorPosX),
                        (event.browserEvent.clientY - event.editorPosY)
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

        final GsiElement[] depth = javaMetadataProvider.getJavaFile(source.key).resolveDepth(event.position.lineNumber, event.position.column);

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

    @Override
    public void beforeDestroy() {
        onMouseMoveDisposable.dispose();
        onKeyUpDisposable.dispose();
        onMouseUpDisposable.dispose();
        onDidChangeCursorPositionDisposable.dispose();
        copyDisposableAction.dispose();
    }
}
