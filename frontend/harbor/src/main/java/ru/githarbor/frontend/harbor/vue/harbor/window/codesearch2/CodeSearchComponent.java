package ru.githarbor.frontend.harbor.vue.harbor.window.codesearch2;

import com.axellience.vuegwt.core.annotations.component.*;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasBeforeDestroy;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import io.reactivex.disposables.Disposable;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import ru.githarbor.frontend.harbor.core.github.core.File;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.core.github.request.CodeSearchRequest;
import ru.githarbor.frontend.harbor.core.github.request.FileContentRequest;
import ru.githarbor.frontend.harbor.core.state.HarborState;
import ru.githarbor.frontend.harbor.elementui.ElInput;
import ru.githarbor.frontend.harbor.jslib.HarborGlobal;
import ru.githarbor.frontend.harbor.jslib.Languages;
import ru.githarbor.frontend.harbor.jslib.monaco.*;
import ru.githarbor.frontend.harbor.jslib.monaco.editor.IEditor;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.SourceTabsSharedState;
import ru.githarbor.frontend.harbor.vue.harbor.window.codesearch.Extension;
import ru.githarbor.frontend.harbor.vue.harbor.window.codesearch.item.MatchLine;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static elemental2.dom.DomGlobal.clearInterval;
import static elemental2.dom.DomGlobal.setTimeout;

@Component(components = {
        LoaderComponent.class
})
public class CodeSearchComponent implements IsVueComponent, HasCreated, HasMounted, HasBeforeDestroy {

    @Inject
    public SourceTabsSharedState sourceTabsSharedState;

    @Inject
    public Repository repository;

    @Inject
    public CodeSearchRequest codeSearchRequest;

    @Inject
    public FileContentRequest fileContentRequest;

    @Inject
    public Languages languages;

    @Inject
    public HarborState harborState;

    @Inject
    public MonacoFactory monacoFactory;

    @Prop
    public String directory;

    @Data
    public String input;

    @Data
    public double found;

    @Data
    public double page = 0;

    @Data
    public double pages = 0;

    @Data
    public String extension;

    @Data
    public double queryLimitRemaining;

    @Data
    public String queryLimitResetAt;

    @Data
    public JsArray<MatchLine> matchLines = new JsArray<>();

    @Data
    public double matchLineIndex = 0;

    @Data
    public boolean searching = false;

    @Data
    public boolean loadingMore;

    @Data
    public boolean visibleResult;

    @Data
    public boolean nothingToShow = true;

    @Data
    public boolean loadMore;

    @Data
    public boolean monacoContainerVisible = false;

    @Ref
    public ElInput inputElement;

    @Ref
    public HTMLElement monacoContainer;

    private IEditor monaco;

    private FindDecorations findDecorations;

    private double inputInterval;

    @JsMethod
    public void onClose() {
        harborState.window = null;
    }

    private Disposable rateLimitObservation;

    @Computed
    public Extension[] getExtensions() {
        final JsArray<Extension> extensions = new JsArray<>();
        extensions.push(new Extension("All", "All"));
        extensions.push(Arrays.stream(repository.getCurrentBranch().getExtensions())
                .map(Extension::new)
                .toArray(Extension[]::new));

        return Js.uncheckedCast(extensions.slice(0));
    }

    @Computed
    public MatchLine getCurrentMatchLine() {
        return matchLines.getAt((int) matchLineIndex);
    }

    @Computed
    public String getFormattedRateLimit() {
        return queryLimitRemaining +
                "/" +
                "30"
                + " (reset "
                + queryLimitResetAt + ")";
    }

    @Override
    public void created() {
        if (codeSearchRequest.getRateLimit() != null) {
            queryLimitRemaining = codeSearchRequest.getRateLimit().remaining;
            queryLimitResetAt = HarborGlobal.timeAgo(Long.valueOf(codeSearchRequest.getRateLimit().resetAt) * 1000);
        }

        rateLimitObservation = codeSearchRequest.onRateLimitUpdate().subscribe(rateLimit -> {
            queryLimitRemaining = rateLimit.remaining;
            queryLimitResetAt = HarborGlobal.timeAgo(Long.valueOf(rateLimit.resetAt) * 1000);
        });

        final String[] currentExtensions = repository.getCurrentBranch().getExtensions();
        final String[] primaryExtensions = languages.getExtensions(repository.info.primaryLanguage);

        extension = currentExtensions[Arrays.asList(currentExtensions).indexOf(primaryExtensions[0])];

        vue().$watch(() -> input, (newInput, oldInput) -> search(newInput, 600));

        vue().$watch(() -> extension, (newValue, oldValue) -> search(input, 0));
    }

    private void search(String input, double delay) {
        clearInterval(inputInterval);


        if (input == null || input.isEmpty()) {
            reset();

            nothingToShow = true;

            return;
        }

        if (input.length() > 120) {
            reset();

            nothingToShow = true;

            return;
        }

        inputInterval = DomGlobal.setTimeout(p0 -> {
            disposeMonaco();
            reset();

            searching = true;

            double copyInputInterval = inputInterval;

            codeSearchRequest.execute(directory, input, ++page, extension.equals("All") ? null : extension).subscribe(codeSearchResponse -> {
                if (copyInputInterval == inputInterval) {
                    found = codeSearchResponse.total_count;


                    if (found == 0) {
                        searching = false;
                        nothingToShow = true;

                        return;
                    }

                    pages = Math.ceil(found / 20);

                    if (pages > 1) {
                        loadMore = true;
                    }

                    final String[] paths = Arrays.stream(codeSearchResponse.items)
                            .map(codeSearchItem -> codeSearchItem.path)
                            .toArray(String[]::new);

                    repository.getCurrentBranch().resolvePaths(paths).subscribe(files -> {
                        monacoFactory.onReady().subscribe(() -> {
                            for (File file : files) {
                                final ITextModel model = Monaco.createModel(file.getContent(), "text");
                                final FindMatch[] matches = model.findMatches(input);

                                matchLines.push(collectMatchLines(file, matches, model));

                                model.dispose();
                            }

                            searching = false;
                            visibleResult = true;

                            vue().$nextTick(() -> {
                                if (matchLines.length > 0) {
                                    onMatchLineSelect(matchLineIndex, getCurrentMatchLine());
                                }
                            });
                        });
                    });
                }
            });
        }, delay);
    }

    private MatchLine[] collectMatchLines(File file, FindMatch[] findMatches, ITextModel model) {
        final Map<Double, MatchLine> tempMap = new HashMap<>();

        for (FindMatch findMatch : findMatches) {
            double startLine = findMatch.getRange().getStartLineNumber();

            if (tempMap.containsKey(startLine)) {
                tempMap.get(startLine).ranges.push(findMatch.getRange());
            } else {
                final MatchLine matchLine = new MatchLine(file.name, file.getPath(), model.getLineContent(startLine), startLine);
                matchLine.ranges.push(findMatch.getRange());

                tempMap.put(startLine, matchLine);
            }
        }

        return tempMap.values().toArray(new MatchLine[0]);
    }

    @Override
    public void mounted() {
        vue().$nextTick(() -> inputElement.focus());
    }

    @JsMethod
    public void onMatchLineSelect(double index, MatchLine matchLine) {
        if (monaco == null) {
            repository.getCurrentBranch().getFile(matchLine.filePath).map(File::getContent)
                    .ifPresent(content -> {
                        monaco = monacoFactory.create(monacoContainer);
                        monaco.setModel(monacoFactory.initModel(matchLine.fileName, content));

                        revealMathLine(matchLine);

                        monaco.layout();

                        setTimeout(p0 -> monacoContainerVisible = true, 100);
                    });

            this.matchLineIndex = index;

            return;
        }

        if (matchLine.filePath.equals(getCurrentMatchLine().filePath)) {
            revealMathLine(matchLine);

            this.matchLineIndex = index;

            return;
        }

        repository.getCurrentBranch().getFile(matchLine.filePath).map(File::getContent).ifPresent(content -> {
            monaco.getModel().dispose();
            monaco.setModel(monacoFactory.initModel(matchLine.fileName, content));

            revealMathLine(matchLine);

            monaco.layout();

            this.matchLineIndex = index;
        });
    }

    @JsMethod
    public void onMatchLineDoubleClick(double index, MatchLine matchLine) {
        repository.getCurrentBranch().getFile(matchLine.filePath).ifPresent(file -> {
            sourceTabsSharedState.addSourceTab(file, matchLine.ranges.getAt(0));

            harborState.window = null;
        });
    }

    @JsMethod
    public void onLoadMore() {
        if (loadingMore) {
            return;
        }

        loadingMore = true;

        codeSearchRequest.execute(directory, input, ++page, extension.equals("All") ? null : extension).subscribe(codeSearchResponse -> {

            final String[] paths = Arrays.stream(codeSearchResponse.items)
                    .map(codeSearchItem -> codeSearchItem.path)
                    .toArray(String[]::new);

            repository.getCurrentBranch().resolvePaths(paths).subscribe(files -> {
                for (File file : files) {
                    final ITextModel model = Monaco.createModel(file.getContent(), "text");
                    final FindMatch[] matches = model.findMatches(input);

                    matchLines.push(collectMatchLines(file, matches, model));

                    model.dispose();

                    if (matchLineIndex == 0 && matchLines.length > 0) {
                        onMatchLineSelect(matchLineIndex, matchLines.getAt((int) matchLineIndex));
                    }

                    loadingMore = false;
                }

                if (pages == page) {
                    loadMore = false;
                }
            });
        });
    }

    private void revealMathLine(MatchLine matchLine) {
        if (monaco != null) {
            monaco.setPosition(Position.create(matchLine.line, 1));
            monaco.revealLineInCenter(matchLine.line);

            if (findDecorations == null) {
                findDecorations = new FindDecorations(monaco);
            }

            final FindMatch[] findMatches = new FindMatch[matchLine.ranges.length];

            for (int i = 0; i < findMatches.length; i++) {
                final FindMatch findMatch = new FindMatch();
                findMatch.setRange(matchLine.ranges.getAt(i));

                findMatches[i] = findMatch;
            }

            findDecorations.set(findMatches);
        }
    }

    private void disposeMonaco() {
        if (monaco != null) {
            monaco.getModel().dispose();
            monaco = null;

            findDecorations.dispose();
            findDecorations = null;
        }
    }

    private void reset() {
        found = 0;
        page = 0;
        pages = 0;
        matchLines = new JsArray<>();
        searching = false;
        loadingMore = false;
        visibleResult = false;
        nothingToShow = false;
        matchLineIndex = 0;
        loadMore = false;
    }

    @Override
    public void beforeDestroy() {
        if (rateLimitObservation != null) {
            rateLimitObservation.dispose();
        }

        disposeMonaco();
    }
}
