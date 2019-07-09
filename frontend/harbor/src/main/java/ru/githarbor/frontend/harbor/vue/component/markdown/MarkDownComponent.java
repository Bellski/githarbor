package ru.githarbor.frontend.harbor.vue.component.markdown;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.annotations.component.Ref;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import elemental2.core.JsArray;
import elemental2.dom.*;
import jsinterop.base.Js;
import ru.githarbor.frontend.harbor.core.github.core.Branch;
import ru.githarbor.frontend.harbor.core.github.core.File;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.jslib.Marked;
import ru.githarbor.frontend.harbor.jslib.PathToRegExp;
import ru.githarbor.frontend.harbor.vue.harbor.repository.RepositoryTreeComponentApi;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.SourceTabsSharedState;
import ru.githarbor.shared.User;

import javax.inject.Inject;

import static elemental2.dom.DomGlobal.window;

@Component
public class MarkDownComponent implements IsVueComponent, HasMounted {
    public static PathToRegExp repositoryPath = PathToRegExp.path("https://github.com/:owner/:name");
    public static PathToRegExp treePath = PathToRegExp.path("https://github.com/:owner/:name/tree/:branch/:path*");
    public static PathToRegExp blobPath = PathToRegExp.path("https://github.com/:owner/:name/blob/:branch/:path*");

    @Inject
    public User user;

    @Inject
    public Repository repository;

    @Inject
    public RepositoryTreeComponentApi repositoryTreeComponentApi;

    @Inject
    public SourceTabsSharedState sourceTabsSharedState;

    @Prop
    public String file;

    @Ref
    public HTMLDivElement renderContainer;

    @Computed
    public boolean getIsDark() {
        return user.darkTheme;
    }

    @Override
    public void mounted() {
        final File file = repository.getCurrentBranch().getFile(this.file).get();

        renderContainer.innerHTML = Marked.parse(file.getContent());


        final NodeList<Element> imgs = renderContainer.querySelectorAll("img");

        for (int i = 0; i < imgs.length; i++) {
            final HTMLImageElement img = Js.cast(imgs.getAt(i));
            final String src = img.getAttribute("src");

            if (!src.startsWith("http")) {
                img.src = "https://raw.githubusercontent.com/" + repository + "/" + repository.getCurrentBranch() + "/" + src;
            }
        }

        final NodeList<Element> hrefs = renderContainer.querySelectorAll("a");

        for (int i = 0; i < hrefs.length; i++) {
            final HTMLAnchorElement href = Js.cast(hrefs.getAt(i));

            if (href.getAttribute("href").startsWith("#")) {
                continue;
            }

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
    }
}
