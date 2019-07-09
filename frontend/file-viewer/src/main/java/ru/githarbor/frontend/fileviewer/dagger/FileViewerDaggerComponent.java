package ru.githarbor.frontend.fileviewer.dagger;

import dagger.BindsInstance;
import dagger.Component;
import ru.githarbor.frontend.fileviewer.core.GitHubGqlClient;
import ru.githarbor.frontend.fileviewer.core.InitParams;
import ru.githarbor.frontend.fileviewer.vue.FileViewerComponentFactory;

import javax.inject.Singleton;

@Component
@Singleton
public interface FileViewerDaggerComponent {
    FileViewerComponentFactory fileViewerComponentFactory();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder initParams(InitParams initParams);

        @BindsInstance
        Builder gitHubGqlClient(GitHubGqlClient client);

        FileViewerDaggerComponent build();
    }
}
