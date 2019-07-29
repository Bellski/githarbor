package ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.goview;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Ref;
import com.axellience.vuegwt.core.client.component.hooks.HasBeforeDestroy;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import elemental2.core.JsArray;
import elemental2.core.JsNumber;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import ru.githarbor.frontend.harbor.core.github.core.File;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.core.rpc.go.GoSourceMetadata;
import ru.githarbor.frontend.harbor.core.rpc.go.GoSourceResolverRpcClient;
import ru.githarbor.frontend.harbor.jslib.monaco.ITextModel;
import ru.githarbor.frontend.harbor.jslib.monaco.MonacoFactory;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.MonacoSourceTabComponent;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.javaview.usages.UsagesPopupComponent;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.javaview.usages.UsagesPosition;
import ru.githarbor.frontend.monaco.*;
import ru.githarbor.frontend.monaco.editor.events.IEditorMouseEvent;
import ru.githarbor.frontend.monaco.editor.events.IKeyboardEvent;
import ru.githarbor.frontend.monaco.java.UsageRange;
import ru.githarbor.shared.User;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component(components = UsagesPopupComponent.class)
public class GoSourceViewComponent extends MonacoSourceTabComponent implements HasCreated, HasMounted, HasBeforeDestroy {

    @Inject
    public GoSourceResolverRpcClient goSourceResolverRpcClient;

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
    protected Usages usagesContainer = new Usages();

    @Data
    public UsagesPosition usagesPosition;

    private String[] decorations = new String[0];

    private Disposable onKeyUpDisposable;
    private Disposable onMouseUpDisposable;
    private Disposable onMouseMoveDisposable;

    private GoSourceMetadata goSourceMetadata;

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

            goSourceResolverRpcClient.resolveSource(repository.toString(), repository.getCurrentBranch() + ":" + file.getPath()).subscribe(meta -> {
                this.goSourceMetadata = meta;
            });

            loading = false;

            vue().$nextTick(() -> {
                monaco = monacoFactory.create(monacoContainer);
                monaco.setModel(model);

                onKeyUpDisposable = monaco.onKeyUp(event -> onMonacoKeyUp(event));
                onMouseUpDisposable = monaco.onMouseUp(event -> onMonacoMouseUp(event));
                onMouseMoveDisposable = monaco.onMouseMove(event -> onMonacoMouseMove(event));

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


    private void onMonacoKeyUp(IKeyboardEvent event) {
        if (event.ctrlKey) {
            clearDecorations();
        }
    }

    private void onMonacoMouseUp(IEditorMouseEvent event) {
        final ITextModel model = Js.cast(monaco.getModel());
        final WordPosition wordAtPosition = model.getWordAtPosition(event.position);

        if (wordAtPosition == null) {
            return;
        }

        final Optional<JsArray<JsNumber>> resolvedOptional = findInResolved(event.position.lineNumber, wordAtPosition.getStartColumn());

        if (resolvedOptional.isPresent()) {
            final JsArray<JsNumber> resolved = resolvedOptional.get();

            final JsArray<JsNumber>[] usages = Arrays
                    .stream(goSourceMetadata.ids)
                    .filter(idPosition -> idPosition.getAt(2).valueOf() == resolved.getAt(0).valueOf() && idPosition.getAt(3).valueOf() == resolved.getAt(1).valueOf())
                    .toArray(JsArray[]::new);

            if (usages.length == 1) {
                return;
            }

            usagesContainer.usages.length = 0;

            for (JsArray<JsNumber> usageRange : usages) {

                final IRange range = IRange.create(
                        usageRange.getAt(0).valueOf(),
                        usageRange.getAt(0).valueOf(),
                        usageRange.getAt(1).valueOf(),
                        usageRange.getAt(1).valueOf()
                );

                usagesContainer
                        .usages
                        .push(
                                new Usage(
                                        source.name,
                                        usageRange.getAt(0).valueOf(),
                                        model.getLineContent(usageRange.getAt(0).valueOf()),
                                        range
                                )
                        );
            }

            usagesPosition = new UsagesPosition(
                    (event.browserEvent.clientX - event.editorPosX),
                    (event.browserEvent.clientY - event.editorPosY)
            );

            clearDecorations();

            return;
        }

        final Optional<JsArray<JsNumber>> idOptional = finInIds(event.position.lineNumber, wordAtPosition.getStartColumn());

        if (idOptional.isPresent()) {
            final JsArray<JsNumber> idPositions = idOptional.get();

            monaco.setPosition(Position.create(idPositions.getAt(2).valueOf(), idPositions.getAt(3).valueOf()));
            monaco.revealPositionInCenter(Position.create(idPositions.getAt(2).valueOf(), idPositions.getAt(3).valueOf()));

            clearDecorations();

        }
    }

    private void onMonacoMouseMove(IEditorMouseEvent event) {
        if (!event.getCtrlKey || event.position == null) {
            return;
        }

        clearDecorations();

        final WordPosition wordAtPosition = monaco.getModel().getWordAtPosition(event.position);

        if (wordAtPosition != null) {
            final IRange range = IRange.create(
                    event.position.lineNumber,
                    event.position.lineNumber,
                    wordAtPosition.getStartColumn(),
                    wordAtPosition.getEndColumn()
            );

            final Optional<JsArray<JsNumber>> resolvedOptional = findInResolved(event.position.lineNumber, wordAtPosition.getStartColumn());

            if (resolvedOptional.isPresent()) {
                decorations = monaco.deltaDecorations(
                        decorations,
                        new IModelDeltaDecoration[]{
                                new IModelDeltaDecoration(
                                        range,
                                        "goto-definition-link",
                                        "Show usages of '" + wordAtPosition.getWord() + "'"
                                )
                        });

                return;
            }

            final Optional<JsArray<JsNumber>> idOptional = finInIds(event.position.lineNumber, wordAtPosition.getStartColumn());

            if (idOptional.isPresent()) {
                decorations = monaco.deltaDecorations(
                        decorations,
                        new IModelDeltaDecoration[]{
                                new IModelDeltaDecoration(
                                        range,
                                        "goto-definition-link"
                                )
                        });
            }
        }
    }

    private Optional<JsArray<JsNumber>> findInResolved(double line, double column) {
        return Arrays
                .stream(goSourceMetadata.resolved)
                .filter(position -> position.getAt(0).valueOf() == line && position.getAt(1).valueOf() == column)
                .findFirst();
    }

    private Optional<JsArray<JsNumber>> finInIds(double line, double column) {
        return Arrays
                .stream(goSourceMetadata.ids)
                .filter(position -> position.getAt(0).valueOf() == line && position.getAt(1).valueOf() == column)
                .findFirst();
    }

    public void clearDecorations() {
        if (decorations.length > 0) {
            decorations = monaco.deltaDecorations(decorations, new IModelDeltaDecoration[0]);
        }
    }

    @JsMethod
    public void onUsageSelect(Usage usage) {
        clearDecorations();

        monaco.revealPositionInCenter(Position.create(usage.line, usage.range.getStartColumn()));
        monaco.setPosition(Position.create(usage.line, usage.range.getStartColumn()));
        monaco.focus();
    }
}
