package ru.githarbor.frontend.harbor.vue.harbor;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import jsinterop.annotations.JsMethod;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.jslib.SimpleBarResizeEvent;
import ru.githarbor.frontend.harbor.vue.component.sidebar.SideBarComponent;
import ru.githarbor.frontend.harbor.vue.component.sidebar.SideBarPaneComponent;
import ru.githarbor.frontend.harbor.vue.harbor.favorite.FavoriteRepositoriesComponent;
import ru.githarbor.frontend.harbor.vue.harbor.owner.OwnerRepositoriesComponent;
import ru.githarbor.frontend.harbor.vue.harbor.recent.RecentRepositoriesComponent;
import ru.githarbor.frontend.harbor.vue.harbor.repository.RepositoryTreeComponent;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.SourceTabsComponent;

import javax.inject.Inject;

@Component(components = {
        SideBarComponent.class,
        SideBarPaneComponent.class,
        RepositoryTreeComponent.class,
        SourceTabsComponent.class,
        FavoriteRepositoriesComponent.class,
        OwnerRepositoriesComponent.class,
        RecentRepositoriesComponent.class
})
public class HarborSideBar implements IsVueComponent {

    @Inject
    public Repository repository;

    @Computed
    public String getName() {
        return repository.info.nameWithOwner.name;
    }

    @Computed
    public String getOwner() {
        return repository.info.nameWithOwner.owner;
    }

    @JsMethod
    protected void handleMoved(SimpleBarResizeEvent event) {

    }
}
