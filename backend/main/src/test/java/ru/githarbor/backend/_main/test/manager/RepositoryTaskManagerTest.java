package ru.githarbor.backend._main.test.manager;

import org.jooq.exception.DataAccessException;
import ru.githarbor.backend._main.dagger.DaggerMainComponent;
import ru.githarbor.backend._main.dagger.MainComponent;

public class RepositoryTaskManagerTest {

    public void createTaskTest() {
        final MainComponent component = DaggerMainComponent.create();

        try {
            final long result = component.repositoryTaskManager().createTask("javaparser/javaparser", "master", 1234);
            System.out.println(result);
        } catch (DataAccessException e) {
            if (e.sqlState().equals("23505")) {
                System.out.println("already exists");
            }
        }
    }


    public void addSubscriber() {
        final MainComponent component = DaggerMainComponent.create();
        component.repositoryTaskManager().addSubscriber("javaparser/javaparser", "master", "124215");
    }
}
