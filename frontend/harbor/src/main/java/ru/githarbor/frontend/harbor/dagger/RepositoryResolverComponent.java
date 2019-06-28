package ru.githarbor.frontend.harbor.dagger;

import dagger.BindsInstance;
import dagger.Component;
import ru.githarbor.frontend.github.client.GitHubGqlClient;
import ru.githarbor.frontend.harbor.core.InitParameters;
import ru.githarbor.frontend.harbor.core.github.request.BranchesRequest;
import ru.githarbor.frontend.harbor.core.github.request.FileContentRequest;
import ru.githarbor.frontend.harbor.core.github.request.RepositoryExistsRequest;
import ru.githarbor.frontend.harbor.core.rpc.UserManagerRpcClient;
import ru.githarbor.frontend.harbor.core.service.RepositoryPathsService;
import ru.githarbor.frontend.harbor.vue.component.resolve.ResolveRepositoryComponentFactory;
import ru.githarbor.shared.User;

import javax.inject.Singleton;

@Singleton
@Component
public interface RepositoryResolverComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder user(User user);

        @BindsInstance
        Builder initParameters(InitParameters initParameters);

        @BindsInstance
        Builder gitHubGqClient(GitHubGqlClient gitHubGqClient);

        RepositoryResolverComponent build();
    }


    RepositoryExistsRequest repositoryExistsRequest();
    RepositoryPathsService repositoryPathsService();
    BranchesRequest branchesRequest();
    FileContentRequest fileContentRequest();
    UserManagerRpcClient userManagerRpcClient();

    User user();

    GitHubGqlClient gitHubGqlClient();

    ResolveRepositoryComponentFactory resolveGitHubProjectComponentFactory();
}
