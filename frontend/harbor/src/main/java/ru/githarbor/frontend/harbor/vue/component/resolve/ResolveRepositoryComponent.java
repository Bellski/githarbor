package ru.githarbor.frontend.harbor.vue.component.resolve;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import ru.githarbor.frontend.harbor.core.service.RepositoryResolverService;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;

import javax.inject.Inject;

@Component(components = {
        LoaderComponent.class
})
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
                this.info = "Cache new repository";
                break;
            case  "1":
                this.info = "Cache new branch";
                break;
            case  "2":
                this.info = "Update branch";
                break;
            case  "3":
                this.info = "Cache huge repository, it may take some time, but only once";
                break;
        }
    }

    public void setNotFound() {
        notFound = true;
    }
}
