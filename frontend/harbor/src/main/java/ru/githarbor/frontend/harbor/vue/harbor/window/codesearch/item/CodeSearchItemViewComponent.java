package ru.githarbor.frontend.harbor.vue.harbor.window.codesearch.item;

import com.axellience.vuegwt.core.annotations.component.*;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasBeforeDestroy;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsMethod;
import ru.githarbor.frontend.monaco.FindDecorations;
import ru.githarbor.frontend.monaco.IRange;
import ru.githarbor.frontend.monaco.Position;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.monaco.editor.IEditor;
import ru.githarbor.frontend.harbor.jslib.monaco.MonacoFactory;

import javax.inject.Inject;

@Component
public class CodeSearchItemViewComponent implements IsVueComponent, HasCreated, HasMounted, HasBeforeDestroy {

    @Inject
    public Repository repository;

    @Inject
    public MonacoFactory monacoFactory;

    @Prop
    public CodeSearchItem item;

    @Prop
    public String query;

    @Data
    public boolean monacoContainerShow = false;

    @Ref
    public JsArray<HTMLElement> itemElements;

    @Ref
    public HTMLElement monacoContainer;

    private IEditor editor;

    private FindDecorations findDecorations;

    @Computed
    public MatchLine[] getMatchLines() {
        return item.matchLines;
    }

    @Computed
    public double getMatchLineIndex() {
        return item.currentMatchLine;
    }

    @Override
    public void created() {
//        vue().$watch(() -> item, (newItem, oldItem) -> {
//            Observable.fromArray(item.matchLines)
//                    .flatMap(matchLine -> {
//                        return RxElemental2.fromPromise(Monaco.colorize(matchLine.content, "java"))
//                                .doOnSuccess(s -> {
//                                    matchLine.content = s;
//                                })
//                                .toObservable();
//                    })
//                    .lastOrError()
//                    .subscribe();
//
//            editor(newItem);
//
//            vue().$nextTick(() -> {
//                itemElements.getAt((int) newItem.currentMatchLine)
//                        .scrollIntoView(Element.ScrollIntoViewTopUnionType.of(
//                                JsPropertyMap.of("block", "center")
//                        ));
//            });
//        });
    }

    private void editor(CodeSearchItem item) {
        repository.getCurrentBranch().getFile(item.path).ifPresent(file -> {
            file.resolveContent().subscribe(content -> {
                if (editor == null) {
                    editor = monacoFactory.create(monacoContainer);
                    editor.setModel(monacoFactory.initModel(file.name, content));
                    editor.layout();

                    findDecorations = new FindDecorations(editor);
                    findDecorations.set(editor.getModel().findMatches(query));

                    revealMathLine(item.matchLines[(int) item.currentMatchLine]);

                    DomGlobal.setTimeout(p0 -> {
                        monacoContainerShow = true;
                    }, 100);

                    return;
                }

                editor.getModel().dispose();
                editor.setModel(monacoFactory.initModel(file.name, content));
                findDecorations.set(editor.getModel().findMatches(query));
                revealMathLine(item.matchLines[(int) item.currentMatchLine]);
                editor.layout();
            });
        });
    }

    @JsMethod
    public void onMatchLineClick(double index, MatchLine matchLine) {
        item.currentMatchLine = index;
        revealMathLine(matchLine);
    }

    private void revealMathLine(MatchLine matchLine) {
        if (editor != null) {
            editor.revealLineInCenter(matchLine.line);
            editor.setSelection(IRange.create(matchLine.line, 1, 3));
            editor.setPosition(Position.create(matchLine.line, 1));
        }
    }

    @Override
    public void mounted() {

    }

    @Override
    public void beforeDestroy() {
        if (editor != null) {
            editor.getModel().dispose();
            editor.dispose();

            editor = null;

            findDecorations.dispose();
            findDecorations = null;
        }
    }

    public void layout() {
        if (editor != null) {
            editor.layout();
        }
    }
}
