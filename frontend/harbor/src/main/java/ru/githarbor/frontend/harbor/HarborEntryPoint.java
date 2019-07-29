package ru.githarbor.frontend.harbor;

import com.axellience.vuegwt.core.client.VueGWT;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import elemental2.core.*;
import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLMetaElement;
import io.reactivex.Single;
import io.reactivex.plugins.RxJavaPlugins;
import jsinterop.base.Js;
import ru.githarbor.frontend.github.client.GitHubGqlClient;
import ru.githarbor.frontend.monaco.IRange;
import ru.githarbor.frontend.monaco.Monaco;
import ru.githarbor.frontend.monaco.Theme;
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
import ru.githarbor.shared.BranchState;
import ru.githarbor.shared.RecentRepository;
import ru.githarbor.shared.UiState;
import ru.githarbor.shared.User;

import java.util.Arrays;

import static elemental2.core.Global.JSON;
import static elemental2.dom.DomGlobal.*;

public class HarborEntryPoint implements EntryPoint {

    @Override
    public void onModuleLoad() {

        RxJavaPlugins.setErrorHandler(throwable -> GWT.log("", throwable));
        VueGWT.initWithoutVueLib();

        final InitParameters initParameters = processInitParameters();
        window.history.replaceState(null, null, "/" + initParameters.ownerWithName);

        final User user = processUser();

        final GitHubGqlClient gitHubGqClient = new GitHubGqlClient(user);

        resolveRepository(gitHubGqClient, user, initParameters).subscribe(repository -> {
            if (user.tier1Backer && user.uiState == null) {
                final BranchState branchState = new BranchState();
                branchState.name = repository.getCurrentBranch().name;

                user.uiState = new UiState();
                user.uiState.name = repository.info.nameWithOwner.ownerWithName;
                user.uiState.currentBranch = repository.getCurrentBranch().name;
                user.uiState.sideBarTabContentWidth = 20;
                user.uiState.sideBarContentWidth = 80;
                user.uiState.sideBarTabIndex = 1;
                user.uiState.branches = new BranchState[] {branchState};
            } else if (user.tier1Backer && user.uiState.getBranchState() == null) {
                final BranchState branchState = new BranchState();
                branchState.name = repository.getCurrentBranch().name;

                user.uiState.addBranch(branchState);
            }

            final HarborDaggerComponent harborDaggerComponent = DaggerHarborDaggerComponent.builder()
                    .initParameters(initParameters)
                    .user(user)
                    .gitHubGqlClient(gitHubGqClient)
                    .repository(repository)
                    .build();

            if (user.tier1Backer || user.tier2Backer) {
                Monaco.defineTheme("light", Theme.LIGHT);
                Monaco.defineTheme("dark", Theme.DARK);

                Monaco.MonacoOptions.INSTANCE.theme = user.getTheme();

                window.addEventListener("blur", new EventListener() {
                    private UiState uiState = Js.cast(JSON.parse(JSON.stringify(user.uiState)));

                    @Override
                    public void handleEvent(Event evt) {
                        if (!JSON.stringify(uiState).equals(JSON.stringify(user.uiState))) {

                            harborDaggerComponent.userManagerRpcClient().updateUiState(user.uiState);

                            uiState = Js.cast(JSON.parse(JSON.stringify(user.uiState)));
                        }
                    }
                });

                window.addEventListener("beforeunload", new EventListener() {
                    private UiState uiState = Js.cast(JSON.parse(JSON.stringify(user.uiState)));

                    @Override
                    public void handleEvent(Event evt) {
                        if (!JSON.stringify(uiState).equals(JSON.stringify(user.uiState))) {
                            harborDaggerComponent.userManagerRpcClient().updateUiState(user.uiState);
                        }
                    }
                });
            } else {
                Monaco.defineTheme("light", Theme.LIGHT);
                Monaco.MonacoOptions.INSTANCE.theme = user.getTheme();
            }

            harborDaggerComponent
                    .harborComponentFactory()
                    .create()
                    .vue()
                    .$mount("#app");
        });
    }

    private User processUser() {
        final HTMLMetaElement meta = Js.cast(document.head.querySelector("[data-user]"));
        final User user = Js.cast(JSON.parse(meta.dataset.get("user")));

        meta.remove();

        return user;
    }

    private InitParameters processInitParameters() {
        InitParameters initParameters = null;
        final String pathName = DomGlobal.location.getPathname();

        final JsArray<String> ownerWithNamePath = PathToRegExp.path("/:owner/:name").exec(pathName);

        if (ownerWithNamePath != null) {
            initParameters = new InitParameters(ownerWithNamePath.getAt(1) + "/" + ownerWithNamePath.getAt(2));
        }

        if (initParameters == null) {
            final JsArray<String> blobPath = PathToRegExp.path("/:owner/:name/blob/:branch/:path*").exec(pathName);

            if (blobPath != null) {
                initParameters = new InitParameters(
                        blobPath.getAt(1) + "/" + blobPath.getAt(2),
                        blobPath.getAt(3),
                        blobPath.getAt(4),
                        true
                );

                IRange selection = null;

                final String hash = location.getHash();

                String[] result = new JsRegExp("^#L([\\d]+)$").exec(hash);

                if (result == null) {
                    result = new JsRegExp("^#L([\\d]+)-L([\\d]+)$").exec(hash);
                }

                if (result == null) {
                    result = new JsRegExp("^#L([\\d]+)-L([\\d]+),C([\\d]+)-C([\\d]+)$").exec(hash);
                }

                if (result != null) {
                    double startLine;
                    double endLine;
                    double startColumn = 1;
                    double endColumn = -1;

                    if (result.length == 2) {
                        startLine = Double.valueOf(result[1]);
                        endLine = Double.valueOf(result[1]);
                    } else if (result.length == 3) {
                        startLine = Double.valueOf(result[1]);
                        endLine = Double.valueOf(result[2]);
                    } else {
                        startLine = Double.valueOf(result[1]);
                        endLine = Double.valueOf(result[2]);
                        startColumn = Double.valueOf(result[3]);
                        endColumn = Double.valueOf(result[4]);
                    }

                    selection = IRange.create(
                            startLine > 1 ? startLine - 1 : startLine,
                            endLine,
                            startColumn,
                            endColumn
                    );
                }

                initParameters.selection = selection;
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

        String branchToResolve = initParameters.branch;

        if (branchToResolve == null && user.tier1Backer && user.uiState != null) {
            branchToResolve = user.uiState.currentBranch;
        }

        return resolverComponent.repositoryExistsRequest()
                .execute(initParameters.ownerWithName, branchToResolve)
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
                    document.title = repository.info.nameWithOwner.name;

                    resolveRepositoryComponent.vue().$el().remove();
                    resolveRepositoryComponent.vue().$destroy();
                })
                .doOnError(throwable -> resolveRepositoryComponent.setNotFound());
    }
}
