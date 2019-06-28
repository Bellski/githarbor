package ru.githarbor.frontend.harbor.vue.component.resolve;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import ru.githarbor.frontend.harbor.core.service.RepositoryResolverService;

import javax.inject.Inject;

@Component
public class ResolveRepositoryComponent implements IsVueComponent{

    @Inject
    public RepositoryResolverService repositoryResolverService;

    @Data
    protected String info = "Resolving repository";

    @Data
    protected boolean notFound = false;

    public void setInfo(String info) {
        switch (info) {
            case "0":
                this.info = "Caching the new repository";
                break;
            case  "1":
                this.info = "Caching the new branch";
                break;
            case  "2":
                this.info = "Updating the branch";
                break;
            case  "3":
                this.info = "Caching the huge repository, please wait";
                break;
        }
    }

    public void setNotFound() {
        notFound = true;
    }
}
