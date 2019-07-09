package ru.githarbor.frontend.harbor.vue.harbor.reposearch;

import com.axellience.vuegwt.core.annotations.component.*;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import elemental2.dom.DomGlobal;
import elemental2.dom.MouseEvent;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import ru.githarbor.frontend.github.request.RepositorySearchRequest;
import ru.githarbor.frontend.harbor.core.state.HarborState;
import ru.githarbor.frontend.harbor.elementui.ElInput;
import ru.githarbor.frontend.harbor.jslib.HarborGlobal;
import ru.githarbor.frontend.harbor.jslib.MyKeyboardEvent;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;
import ru.githarbor.frontend.vue.component.searchrepository.Repository;
import ru.githarbor.frontend.vue.component.searchrepository.RepositoryListComponent;


import javax.inject.Inject;
import java.util.Arrays;

import static elemental2.dom.DomGlobal.*;

@Component(components = {LoaderComponent.class, RepositoryListComponent.class})
public class RepositorySearchComponent implements IsVueComponent, HasMounted {

    @Inject
    public HarborState harborState;

    @Inject
    public ru.githarbor.frontend.harbor.core.github.core.Repository coreRepository;

    @Inject
    public RepositorySearchRequest repositorySearchRequest;

    @Data
    public String input;

    @Data
    public boolean searching;

    @Data
    public boolean visible;

    @Data
    public Repository[] repositories  = new Repository[0];

    @Data
    public String toOpenRepository;

    @Data
    public String searchIn = "organization";

    @Data
    public double currentRepositoryIndex;

    @Ref
    public ElInput inputElement;

    private double inputInterval;

    private String popoverId;

    @Watch("input")
    public void watchInput(String newInput) {

        final String query = searchIn.equals("organization") ? getOwner() + "/" + newInput : newInput;

        clearInterval(inputInterval);

        if (input == null || input.isEmpty()) {
            repositories = new Repository[0];
            currentRepositoryIndex = 0;
            visible = false;

            return;
        }

        inputInterval = setTimeout(p0 -> {
            final Double innerInterval = Double.valueOf(inputInterval);

            visible = true;
            searching = true;

            repositorySearchRequest.execute(query)
                    .subscribe(search -> {

                        if (innerInterval.equals(inputInterval)) {
                            repositories = Arrays.stream(search.repositories)
                                    .filter(repository -> repository.nameWithOwner.contains(query))
                                    .map(repository -> {
                                        final RepositorySearchRequest.PrimaryLanguage primaryLanguage = repository.primaryLanguage;

                                        String languageName = primaryLanguage != null ? primaryLanguage.name : null;
                                        String languageColor = primaryLanguage != null ? primaryLanguage.color : null;

                                        return new ru.githarbor.frontend.vue.component.searchrepository.Repository(
                                                repository.nameWithOwner,
                                                languageColor,
                                                languageName,
                                                HarborGlobal.kFormat(repository.stars),
                                                HarborGlobal.timeAgo(repository.updatedAt, "en")
                                        );
                                    })
                                    .toArray(ru.githarbor.frontend.vue.component.searchrepository.Repository[]::new);

                            currentRepositoryIndex = 0;
                            searching = false;
                        }
                    });
        }, 300);
    }

    @Computed
    public String getOwner() {
        return coreRepository.info.nameWithOwner.owner;
    }

    @JsMethod
    public void onRepositoryOpen(RepositorySearchRequest.Repository repository) {
        this.toOpenRepository = repository.nameWithOwner;
    }

    @Override
    public void mounted() {
        vue().$root().vue().$on("global-keydown", parameter -> {
            final MyKeyboardEvent evt = Js.cast(parameter);
            final double keyCode = evt.getKeyCode();

            if (evt.altKey && keyCode == 83) { // alt + s
                harborState.window = null;

                evt.preventDefault();

                if (!inputElement.getFocused()) {
                    inputElement.focus();

                    return;
                }

                searchIn = searchIn.equals("organization") ? "GitHub" : "organization";

                watchInput(input);

                return;
            }

            if (repositories.length == 0) {
                return;
            }

            if (evt.getKeyCode() == 38 && currentRepositoryIndex > 0) {
                evt.preventDefault();
                evt.stopPropagation();

                --currentRepositoryIndex;
            } else if (evt.getKeyCode() == 40 && currentRepositoryIndex < repositories.length -1) {
                evt.preventDefault();
                evt.stopPropagation();

                ++currentRepositoryIndex;
            } else if (evt.getKeyCode() == 40 && currentRepositoryIndex == repositories.length -1) {
                evt.preventDefault();
                evt.stopPropagation();

                currentRepositoryIndex = 0;
            } else if (evt.getKeyCode() == 38 && currentRepositoryIndex == 0) {
                evt.preventDefault();
                evt.stopPropagation();

                currentRepositoryIndex = repositories.length -1;
            } else if (evt.getKeyCode() == 13) {
                evt.preventDefault();
                evt.stopPropagation();

                window.open("/" + repositories[(int) currentRepositoryIndex].nameWithOwner, "_blank");

                closeResultList();
            } else if (evt.getKeyCode() == 33) {
                evt.preventDefault();
                evt.stopPropagation();

                currentRepositoryIndex = 0;
            } else if (evt.getKeyCode() == 34) {
                evt.preventDefault();
                evt.stopPropagation();

                currentRepositoryIndex = repositories.length -1;
            } else if (evt.getKeyCode() == 27) {
               closeResultList();
            }
        });

        DomGlobal.window.addEventListener("click", evt -> {
            final MouseEvent mouseEvent = Js.cast(evt);

            if (!Arrays.asList(mouseEvent.path).contains(inputElement.vue().$el())) {
                visible = false;
                searching = false;
                repositories = new Repository[0];
                currentRepositoryIndex = 0;
            }
        });

        inputElement.vue().$el().addEventListener("focusin", evt -> {
            if (!visible) {
                watchInput(input);
            }
        });
    }

    private void closeResultList() {
        visible = false;
        searching = false;
        repositories = new Repository[0];
        currentRepositoryIndex = 0;
    }
}
