package ru.githarbor.frontend.harbor.vue.harbor.window.filesearch;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Ref;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.hooks.HasDestroyed;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsMethod;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.harbor.core.github.core.File;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.core.state.HarborState;
import ru.githarbor.frontend.harbor.elementui.ElInput;
import ru.githarbor.frontend.harbor.jslib.Languages;
import ru.githarbor.frontend.harbor.jslib.MyKeyboardEvent;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.SourceTabsSharedState;

import javax.inject.Inject;
import java.util.Arrays;

import static elemental2.dom.DomGlobal.clearInterval;

@Component
public class FileSearchComponent implements IsVueComponent, HasCreated, HasMounted, HasDestroyed {
    public static final String NAME = "file-search";

    @Inject
    public HarborState harborState;

    @Inject
    public Languages languages;

    @Inject
    public Repository repository;

    @Inject
    public SourceTabsSharedState sourceTabsSharedState;

    @Data
    public String filter;

    @Data
    public String input;

    @Data
    public Item[] items = new Item[0];


    public File[] files = new File[0];

    @Data
    public int page = 40;

    @Data
    public double itemIndex = 0;

    @Data
    public double found = 0;

    @Data
    public boolean loadingMore = false;

    @Ref
    public ElInput inputComponent;

    @Ref
    public JsArray<HTMLElement> itemElements;

    @Ref
    public HTMLElement loadMoreElement;

    private double inputInterval;

    private EventListener keydownListener;

    @Computed
    public String getPrimaryLanguage() {
        return repository.info.primaryLanguage;
    }

    @Computed
    public boolean getHasMoreItems() {
        return found > page;
    }

    @Override
    public void created() {
        filter = getPrimaryLanguage() != null ? getPrimaryLanguage() : "All";

        vue().$watch(() -> filter, (newValue, oldValue) -> {
            inputComponent.focus();

            if (input != null && !input.isEmpty()) {
                search(input);
            }
        });

        vue().$watch(() -> input, (newValue, oldValue) -> {
            clearInterval(inputInterval);

            if (input == null || input.isEmpty()) {
                items = new Item[0];
                found = 0;
                page = 40;
                itemIndex = 0;

                return;
            }

            inputInterval = DomGlobal.setTimeout(p0 -> {
                final Double innerInterval = Double.valueOf(inputInterval);

                if (innerInterval.equals(inputInterval)) {
                    search(newValue);
                }
            }, 50);
        });
    }

    private void search(String query) {
        page = 40;
        itemIndex = 0;

        final String[] extensions = filter.equals(getPrimaryLanguage()) ? languages.getExtensions(getPrimaryLanguage()) : new String[0];

        files = repository.getCurrentBranch()
                .findFile(query, extensions);


        found = files.length;

        if (files.length > page) {
            items = Arrays.stream(files)
                    .limit(page)
                    .map(file -> new Item(file.name, file.getParentPath(), file.extension))
                    .toArray(Item[]::new);

            return;
        }

        items = Arrays.stream(files)
                .map(file -> new Item(file.name, file.getParentPath(), file.extension))
                .toArray(Item[]::new);
    }

    @JsMethod
    public void onLoadMore() {
        if ((files.length - page) <= 40) {
            items = Arrays.stream(files)
                    .map(file -> new Item(file.name, file.getParentPath(), file.extension))
                    .toArray(Item[]::new);

            return;
        }

        items = Arrays.stream(files)
                .limit(page + 40)
                .map(file -> new Item(file.name, file.getParentPath(), file.extension))
                .toArray(Item[]::new);
    }

    @Override
    public void mounted() {
        vue().$nextTick(() -> inputComponent.focus());

        DomGlobal.document.addEventListener("keydown", keydownListener = evt -> {
            double endOfList = getHasMoreItems() ? items.length : items.length - 1;

            final MyKeyboardEvent keyboardEvent = (MyKeyboardEvent) evt;

            if (keyboardEvent.altKey && keyboardEvent.getKeyCode() == 78) {
                evt.preventDefault();
                evt.stopPropagation();

                filter = filter.equals("All") ? getPrimaryLanguage() : "All";

                return;
            }

            if (keyboardEvent.getKeyCode() == 38 && itemIndex > 0) {
                evt.preventDefault();
                evt.stopPropagation();

                scrollTo(--itemIndex);
            } else if (keyboardEvent.getKeyCode() == 40 && itemIndex < endOfList) {
                evt.preventDefault();
                evt.stopPropagation();

                scrollTo(++itemIndex);
            } else if (keyboardEvent.getKeyCode() == 40 && itemIndex == endOfList) {
                evt.preventDefault();
                evt.stopPropagation();

                scrollTo(itemIndex = 0);
            } else if (keyboardEvent.getKeyCode() == 38 && itemIndex == 0) {
                evt.preventDefault();
                evt.stopPropagation();

                scrollTo(itemIndex = endOfList);
            } else if (keyboardEvent.getKeyCode() == 13) {
                evt.preventDefault();
                evt.stopPropagation();

                if (getHasMoreItems() && itemIndex == endOfList) {
                    onLoadMore();
                } else if (itemIndex >= 0) {
                    onFileSelection(itemIndex);
                }
            } else if (keyboardEvent.getKeyCode() == 33) {
                evt.preventDefault();
                evt.stopPropagation();

                scrollTo(itemIndex = 0);
            } else if (keyboardEvent.getKeyCode() == 34) {
                evt.preventDefault();
                evt.stopPropagation();

                scrollTo(itemIndex = endOfList);
            } else if (keyboardEvent.getKeyCode() == 27) {
                if (Arrays.asList(evt.path).contains(vue().$el())) {
                    onClose();
                }
            }
        });
    }

    @JsMethod
    public void onFileSelection(double itemIndex) {
        sourceTabsSharedState.addSourceTab(files[(int) itemIndex]);

        harborState.window = null;
    }

    private void scrollTo(double itemIndex) {
        final HTMLElement scrollTo;

        if (items.length == itemIndex) {
            scrollTo = loadMoreElement;
        } else {
            scrollTo = itemElements.getAt((int) itemIndex);
        }

        scrollTo.scrollIntoView(Element.ScrollIntoViewTopUnionType.of(
                JsPropertyMap.of("block", "nearest")
        ));
    }

    @JsMethod
    public void onClose() {
        harborState.window = null;
    }

    @Override
    public void destroyed() {
        DomGlobal.document.removeEventListener("keydown", keydownListener);
    }
}
