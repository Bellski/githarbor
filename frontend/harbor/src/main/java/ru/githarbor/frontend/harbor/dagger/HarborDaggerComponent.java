package ru.githarbor.frontend.harbor.dagger;

import dagger.BindsInstance;
import dagger.Component;
import ru.githarbor.frontend.github.client.GitHubGqlClient;
import ru.githarbor.frontend.harbor.core.InitParameters;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.vue.harbor.HarborComponentFactory;
import ru.githarbor.shared.User;

import javax.inject.Singleton;

@Singleton
@Component
public interface HarborDaggerComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        HarborDaggerComponent.Builder user(User user);

        @BindsInstance
        HarborDaggerComponent.Builder initParameters(InitParameters initParameters);

        @BindsInstance
        HarborDaggerComponent.Builder repository(Repository repository);

        @BindsInstance
        HarborDaggerComponent.Builder gitHubGqlClient(GitHubGqlClient gitHubGqlClient);

        HarborDaggerComponent build();
    }

    HarborComponentFactory harborComponentFactory();
}
