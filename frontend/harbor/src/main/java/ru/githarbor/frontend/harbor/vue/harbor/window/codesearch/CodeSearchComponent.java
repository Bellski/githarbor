package ru.githarbor.frontend.harbor.vue.harbor.window.codesearch;

import com.axellience.vuegwt.core.annotations.component.*;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasBeforeDestroy;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import io.reactivex.disposables.Disposable;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.monaco.FindMatch;
import ru.githarbor.frontend.monaco.Monaco;
import ru.githarbor.frontend.harbor.core.github.core.File;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.core.github.request.CodeSearchRequest;
import ru.githarbor.frontend.harbor.core.state.HarborState;
import ru.githarbor.frontend.harbor.elementui.ElInput;
import ru.githarbor.frontend.harbor.jslib.HarborGlobal;
import ru.githarbor.frontend.harbor.jslib.Languages;
import ru.githarbor.frontend.harbor.jslib.monaco.ITextModel;
import ru.githarbor.frontend.harbor.jslib.monaco.MonacoFactory;
import ru.githarbor.frontend.harbor.vue.harbor.window.codesearch.item.CodeSearchItem;
import ru.githarbor.frontend.harbor.vue.harbor.window.codesearch.item.CodeSearchItemViewComponent;
import ru.githarbor.frontend.harbor.vue.harbor.window.codesearch.item.MatchLine;
import ru.githarbor.frontend.harbor.vue.harbor.window.codesearch.list.CodeSearchItemListComponent;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static elemental2.dom.DomGlobal.clearInterval;

@Component(components = {
        CodeSearchItemListComponent.class,
        CodeSearchItemViewComponent.class,
        LoaderComponent.class
})
public class CodeSearchComponent implements IsVueComponent, HasCreated, HasBeforeDestroy, HasMounted {

    @Inject
    public CodeSearchRequest codeSearchRequest;

    @Inject
    public MonacoFactory monacoFactory;

    @Inject
    public Languages languages;

    @Inject
    public Repository repository;

    @Inject
    public HarborState harborState;

    @Prop
    public String directory;

    @Data
    public String input;

    @Data
    public double found;

    @Data
    public boolean searching = false;

    @Data
    public String extension;

    @Data
    public double queryLimitRemaining;

    @Data
    public String queryLimitResetAt;

    @Data
    public JsArray<CodeSearchItem> allItems = new JsArray<>();

    @Data
    public double page = 1;

    @Data
    public boolean loadingMore = false;

    @Data
    public CodeSearchItem item;

    @Data
    public double leftPaneWidth = 25;

    @Data
    public double rightPaneWidth = 75;

    @Ref
    public CodeSearchItemViewComponent codeSearchItemViewComponent;

    @Ref
    public ElInput inputElement;

    private double inputInterval;

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
    public CodeSearchItem[] getItems() {
        return Js.uncheckedCast(allItems.slice(0));
    }

    @Computed
    public boolean getLoadMore() {
        return page != Math.ceil(found / 100);
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

        vue().$watch(() -> extension, (newExtension, oldExtension) -> {
            allItems = new JsArray<>();
            found = 0;
            item = null;
            searching = true;

            codeSearchRequest.execute(
                    directory,
                    input,
                    1,
                    newExtension.equals("All") ? null : newExtension
            ).subscribe(codeSearchResponse -> {

                found = codeSearchResponse.total_count;

                allItems.push(Arrays.stream(codeSearchResponse.items)
                        .map(codeSearchItem -> new CodeSearchItem(codeSearchItem.name, codeSearchItem.path))
                        .toArray(CodeSearchItem[]::new));

                searching = false;
            });
        });

        vue().$watch(() -> input, (newInput, oldInput) -> {
            clearInterval(inputInterval);

            if (newInput == null || newInput.isEmpty()) {
                found = 0;
                allItems = new JsArray<>();
                item = null;
                searching = false;

                return;
            }

            inputInterval = DomGlobal.setTimeout(p0 -> {
                double copyInputInterval = inputInterval;
                allItems = new JsArray<>();
                item = null;
                searching = true;

                codeSearchRequest.execute(
                        directory,
                        newInput,
                        1,
                        extension.equals("All") ? null : extension
                ).subscribe(codeSearchResponse -> {

                    if (copyInputInterval == inputInterval) {
                        found = codeSearchResponse.total_count;


                        allItems.push(Arrays.stream(codeSearchResponse.items)
                                .map(codeSearchItem -> new CodeSearchItem(codeSearchItem.name, codeSearchItem.path))
                                .toArray(CodeSearchItem[]::new));

                        searching = false;
                    }
                });
            }, 300);
        });
    }


    @Override
    public void mounted() {
        vue().$nextTick(() -> inputElement.focus());
    }

    @Computed
    public String getFormattedRateLimit() {
        return queryLimitRemaining +
                "/" +
                "30"
                + " (reset "
                + queryLimitResetAt + ")";
    }

    @JsMethod
    public void onClose() {
        harborState.window = null;
    }

    @JsMethod
    public void onSplitPanesResized(JsArray<JsPropertyMap> evt) {
        leftPaneWidth = Js.cast(evt.getAt(0).get("width"));
        rightPaneWidth = Js.cast(evt.getAt(1).get("width"));

        codeSearchItemViewComponent.layout();
    }

    @JsMethod
    public void onLoadMore() {
        loadingMore = true;

        codeSearchRequest.execute(
                directory,
                input,
                ++page,
                extension.equals("All") ? null : extension
        ).subscribe(codeSearchResponse -> {

            allItems.push(Arrays.stream(codeSearchResponse.items)
                    .map(codeSearchItem -> new CodeSearchItem(codeSearchItem.name, codeSearchItem.path))
                    .toArray(CodeSearchItem[]::new));

            loadingMore = false;
        });
    }

    @JsMethod
    public void onItemSelect(CodeSearchItem item) {

        if (!item.resolved) {
            item.resolving = true;
        }

        repository.getCurrentBranch().getFile(item.path).map(File::resolveContent).ifPresent(stringSingle -> stringSingle.subscribe(content -> {
            final ITextModel model = Js.cast(Monaco.createModel(content, "test"));
            final FindMatch[] matches = model.findMatches(input);

            item.matchLines = collectMatchLines(matches, model);
            item.occurrences = matches.length;
            item.resolved = true;
            item.resolving = false;

            vue().$nextTick(() -> {
                this.item = item;
            });

            model.dispose();
        }));
    }

    private MatchLine[] collectMatchLines(FindMatch[] findMatches, ITextModel model) {
        final Map<Double, MatchLine> tempMap = new HashMap<>();

        for (FindMatch findMatch : findMatches) {
            double startLine = findMatch.getRange().getStartLineNumber();

            if (tempMap.containsKey(startLine)) {
                tempMap.get(startLine).ranges.push(findMatch.getRange());
            } else {
                final MatchLine matchLine = new MatchLine("", "", "", model.getLineContent(startLine), "", startLine);
                matchLine.ranges.push(findMatch.getRange());

                tempMap.put(startLine, matchLine);
            }
        }

        return tempMap.values().toArray(new MatchLine[0]);
    }

    @Override
    public void beforeDestroy() {
        if (rateLimitObservation != null) {
            rateLimitObservation.dispose();
        }
    }

}
