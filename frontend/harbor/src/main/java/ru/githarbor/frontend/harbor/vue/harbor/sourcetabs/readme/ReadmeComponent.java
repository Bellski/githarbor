package ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.readme;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Ref;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import elemental2.core.JsArray;
import elemental2.dom.*;
import jsinterop.base.Js;
import ru.githarbor.frontend.harbor.core.github.core.Branch;
import ru.githarbor.frontend.harbor.core.github.core.File;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.core.state.HarborState;
import ru.githarbor.frontend.harbor.jslib.Marked;
import ru.githarbor.frontend.harbor.jslib.PathToRegExp;
import ru.githarbor.frontend.harbor.vue.harbor.repository.RepositoryTreeComponentApi;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.SourceTabsSharedState;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;
import ru.githarbor.shared.User;

import javax.inject.Inject;

import java.util.Optional;

import static elemental2.dom.DomGlobal.window;

@Component(components = LoaderComponent.class)
public class ReadmeComponent implements IsVueComponent, HasCreated {

    public static PathToRegExp repositoryPath = PathToRegExp.path("https://github.com/:owner/:name");
    public static PathToRegExp treePath = PathToRegExp.path("https://github.com/:owner/:name/tree/:branch/:path*");
    public static PathToRegExp blobPath = PathToRegExp.path("https://github.com/:owner/:name/blob/:branch/:path*");


    @Inject
    public User user;

    @Inject
    public HarborState harborState;

    @Inject
    public RepositoryTreeComponentApi repositoryTreeComponentApi;

    @Inject
    public SourceTabsSharedState sourceTabsSharedState;

    @Inject
    public Repository repository;

    @Data
    public boolean resolved;

    @Data
    public String loadInfo = "Loading readme...";

    @Data
    public boolean notFound = false;

    @Ref
    public HTMLDivElement renderContainer;

    @Computed
    public boolean getIsDark() {
        return user.darkTheme;
    }

    @Override
    public void created() {
        resolveContent(repository.getCurrentBranch());
    }

    private void resolveContent(Branch branch) {
        final Optional<File> readmeFileOpt = branch.findFileByNameAtRoot("readme.md");

        if (!readmeFileOpt.isPresent()) {
            loadInfo = "Readme not found";
            notFound = true;

            return;
        }

        branch.findFileByNameAtRoot("readme.md").ifPresent(file -> file.resolveContent().subscribe(content -> {
            resolved = true;

            vue().$nextTick(() -> {
                renderContainer.innerHTML = Marked.parse(content);


                final NodeList<Element> imgs = renderContainer.querySelectorAll("img");

                for (int i = 0; i < imgs.length; i++) {
                    final HTMLImageElement img = Js.cast(imgs.getAt(i));
                    final String src = img.getAttribute("src");

                    if (!src.startsWith("http")) {
                        img.src = "https://raw.githubusercontent.com/" + repository.info.nameWithOwner.ownerWithName + "/" + branch.name + "/" + src;
                    }
                }

                final NodeList<Element> hrefs = renderContainer.querySelectorAll("a");

                for (int i = 0; i < hrefs.length; i++) {
                    final HTMLAnchorElement href = Js.cast(hrefs.getAt(i));
                    href.onclick = p0 -> {

                        final String url = ((HTMLAnchorElement) p0.target).href;

                        if (repositoryPath.exec(url) != null) {
                            p0.preventDefault();

                            window.open(url.replace("https://github.com", "http://githarbor.com"), "_blank");

                        } else if (blobPath.exec(url) != null) {
                            p0.preventDefault();

                            final JsArray<String> treePathResult = blobPath.exec(url);
                            final String owner = treePathResult.getAt(1);
                            final String name = treePathResult.getAt(2);
                            final String branchName = treePathResult.getAt(3);


                            if (!repository.info.nameWithOwner.ownerWithName.equals(owner + "/" + name)) {
                                window.open(
                                        url.replace(
                                                "https://github.com",
                                                "http://githarbor.com"),
                                        "_blank"
                                );

                                return null;
                            }

                            DomGlobal.console.warn("blob");

                            repository.getCurrentBranch()
                                    .getFile(treePathResult.getAt(4)).ifPresent(file1 -> sourceTabsSharedState.addSourceTab(file1));

                        } else if (treePath.exec(url) != null) {
                            p0.preventDefault();

                            final JsArray<String> treePathResult = treePath.exec(url);

                            repositoryTreeComponentApi.revealKey(treePathResult.getAt(4));
                        } else if(!url.startsWith("http")) {
                            p0.preventDefault();

                            window.open(
                                    url.replace(
                                            "https://github.com",
                                            "http://githarbor.com"),
                                    "_blank"
                            );
                        } else {
                            p0.preventDefault();

                            window.open(url, "_blank");
                        }

                        return null;
                    };
                }
            });
        }));
    }
}
