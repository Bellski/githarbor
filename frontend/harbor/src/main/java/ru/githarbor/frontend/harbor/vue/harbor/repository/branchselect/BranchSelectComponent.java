package ru.githarbor.frontend.harbor.vue.harbor.repository.branchselect;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import jsinterop.annotations.JsMethod;
import ru.githarbor.frontend.harbor.core.github.core.Repository;

import javax.inject.Inject;
import java.util.Arrays;

@Component
public class BranchSelectComponent implements IsVueComponent, HasCreated {

    @Inject
    public Repository repository;

    @Data
    public boolean fetching = true;

    @Data
    public String branch;

    @Data
    public BranchOption[] branchOptions = new BranchOption[0];

    @Override
    public void created() {
        branch = repository.getCurrentBranch().name;

        repository.resolveBranches().subscribe(branches1 -> {
            branchOptions = Arrays.stream(branches1)
                    .map(branch -> new BranchOption(branch.name))
                    .toArray(BranchOption[]::new);

            fetching = false;
        });
    }

    @JsMethod
    public void onBranchChange(String branchName) {
        vue().$emit("change", branchName);
    }
}
