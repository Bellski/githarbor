package ru.githarbor.frontend.harbor.vue.harbor;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.jslib.HarborGlobal;
import ru.githarbor.frontend.harbor.vue.component.iconbage.IconBadgeComponent;

import javax.inject.Inject;

@Component(components = IconBadgeComponent.class)
public class RepositoryInfoComponent implements IsVueComponent {

    @Inject
    public Repository repository;

    @Computed
    public String getOwner() {
        return repository.info.nameWithOwner.owner;
    }

    @Computed
    public String getName() {
        return repository.info.nameWithOwner.name;
    }

    @Computed
    public String getStars() {
        return HarborGlobal.kFormat(Double.valueOf(repository.info.stars));
    }

    @Computed
    public String getUpdatedAt() {
        return HarborGlobal.timeAgo(repository.getCurrentBranch().committedDate);
    }
}
