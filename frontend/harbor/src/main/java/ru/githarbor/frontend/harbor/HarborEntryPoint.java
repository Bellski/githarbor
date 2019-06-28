package ru.githarbor.frontend.harbor;

import com.axellience.vuegwt.core.client.VueGWT;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.core.JsDate;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLMetaElement;
import io.reactivex.Single;
import io.reactivex.plugins.RxJavaPlugins;
import jsinterop.base.Js;
import ru.githarbor.frontend.github.client.GitHubGqlClient;
import ru.githarbor.frontend.harbor.core.InitParameters;
import ru.githarbor.frontend.harbor.core.github.core.Branch;
import ru.githarbor.frontend.harbor.core.github.core.OwnerWithName;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.core.github.core.RepositoryInfo;
import ru.githarbor.frontend.harbor.dagger.DaggerHarborDaggerComponent;
import ru.githarbor.frontend.harbor.dagger.DaggerRepositoryResolverComponent;
import ru.githarbor.frontend.harbor.dagger.HarborDaggerComponent;
import ru.githarbor.frontend.harbor.dagger.RepositoryResolverComponent;
import ru.githarbor.frontend.harbor.jslib.PathToRegExp;
import ru.githarbor.frontend.harbor.vue.component.resolve.ResolveRepositoryComponent;
import ru.githarbor.shared.RecentRepository;
import ru.githarbor.shared.User;

import java.util.Arrays;

import static elemental2.dom.DomGlobal.document;

public class HarborEntryPoint implements EntryPoint {

    @Override
    public void onModuleLoad() {
        RxJavaPlugins.setErrorHandler(throwable -> GWT.log("", throwable));
        VueGWT.initWithoutVueLib();

        final InitParameters initParameters = processInitParameters();
        final User user = processUser();
        final GitHubGqlClient gitHubGqClient = new GitHubGqlClient(user);

        resolveRepository(gitHubGqClient, user, initParameters).subscribe(repository -> {
            final HarborDaggerComponent.Builder harborDaggerComponent = DaggerHarborDaggerComponent.builder()
                    .initParameters(initParameters)
                    .user(user)
                    .gitHubGqlClient(gitHubGqClient)
                    .repository(repository);

            harborDaggerComponent.build()
                    .harborComponentFactory()
                    .create()
                    .vue()
                    .$mount("#app");
        });
    }

    private User processUser() {
        final HTMLMetaElement meta = Js.cast(document.head.querySelector("[data-user]"));
        final User user = Js.cast(Global.JSON.parse(meta.dataset.get("user")));

        meta.remove();

        return user;
    }

    private InitParameters processInitParameters() {
        InitParameters initParameters = null;
        final String pathName = DomGlobal.location.getPathname();

        final JsArray<String> ownerWithNamePath = PathToRegExp.path("/github/:owner/:name").exec(pathName);

        if (ownerWithNamePath != null) {
            initParameters = new InitParameters(ownerWithNamePath.getAt(1) + "/" + ownerWithNamePath.getAt(2));
        }

        if (initParameters == null) {
            final JsArray<String> blobPath = PathToRegExp.path("/github/:owner/:name/blob/:branch/:path*").exec(pathName);

            if (blobPath != null) {
                initParameters = new InitParameters(
                        blobPath.getAt(1) + "/" + blobPath.getAt(2),
                        blobPath.getAt(3),
                        blobPath.getAt(4),
                        true
                );
            }
        }

        return initParameters != null ? initParameters : new InitParameters();
    }

    public Single<Repository> resolveRepository(GitHubGqlClient gitHubGqClient, User user, InitParameters initParameters) {
        final RepositoryResolverComponent resolverComponent = DaggerRepositoryResolverComponent.builder()
                .user(user)
                .initParameters(initParameters)
                .gitHubGqClient(gitHubGqClient)
                .build();

        final ResolveRepositoryComponent resolveRepositoryComponent = resolverComponent
                .resolveGitHubProjectComponentFactory()
                .create();

        resolveRepositoryComponent.vue()
                .$mount("#resolve");

        return resolverComponent.repositoryExistsRequest()
                .execute(initParameters.ownerWithName, initParameters.branch)
                .flatMap(repository -> {

                    final RecentRepository recentRepository = new RecentRepository();
                    recentRepository.name = repository.nameWithOwner;
                    recentRepository.date = (long) JsDate.now();

                    if (user.recentRepositories == null) {
                        user.recentRepositories = new RecentRepository[] {recentRepository};
                    } else {

                        boolean notExists = Arrays.stream(user.recentRepositories)
                                .noneMatch(recentRepository1 -> recentRepository1.name.equals(repository.nameWithOwner));

                        if (notExists) {
                            final JsArray<RecentRepository> recentRepositories = new JsArray<>();
                            recentRepositories.push(recentRepository);
                            recentRepositories.push(user.recentRepositories);

                            user.recentRepositories = Js.uncheckedCast(recentRepositories.slice(0));
                        }
                    }

                    resolverComponent.userManagerRpcClient().addRecentRepository(recentRepository.name, recentRepository.date);

                    final boolean inFavorites = user.favoriteRepositories != null && Arrays.asList(user.favoriteRepositories).contains(repository.nameWithOwner);
                    final OwnerWithName ownerWithName = new OwnerWithName(repository.nameWithOwner);

                    final Branch defaultBranch = new Branch(
                            ownerWithName,
                            repository.defaultBranch.name,
                            repository.defaultBranch.oid,
                            repository.defaultBranch.committedDate,
                            resolverComponent.repositoryPathsService(),
                            resolverComponent.fileContentRequest()
                    );

                    final Branch currentBranch = repository.currentBranch != null
                            ? new Branch(
                            ownerWithName,
                            repository.currentBranch.name,
                            repository.currentBranch.oid,
                            repository.currentBranch.committedDate,
                            resolverComponent.repositoryPathsService(),
                            resolverComponent.fileContentRequest()
                    )
                            : defaultBranch;

                    final Branch toResolveBranch = defaultBranch.equals(currentBranch) ? defaultBranch : currentBranch;

                    return toResolveBranch.resolve(resolveRepositoryComponent::setInfo)
                            .toSingle(() -> new Repository(
                                            new RepositoryInfo(repository, inFavorites),
                                            defaultBranch,
                                            toResolveBranch,
                                            resolverComponent.branchesRequest(),
                                            resolverComponent.userManagerRpcClient()
                                    )
                            );
                })
                .doOnSuccess(repository -> {
                    resolveRepositoryComponent.vue().$el().remove();
                    resolveRepositoryComponent.vue().$destroy();
                })
                .doOnError(throwable -> resolveRepositoryComponent.setNotFound());
    }
}
