package ru.githarbor.frontend.hello.vue;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import ru.githarbor.frontend.hello.vue.repository.RepositoriesViewComponent;
import ru.githarbor.frontend.hello.vue.searchrepository.SearchRepositoryComponent;

@Component(components = {
        RepositoriesViewComponent.class,
        SearchRepositoryComponent.class
})
public class HelloComponent implements IsVueComponent {

}
