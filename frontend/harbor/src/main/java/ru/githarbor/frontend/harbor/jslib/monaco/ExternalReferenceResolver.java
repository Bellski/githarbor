package ru.githarbor.frontend.harbor.jslib.monaco;

import ru.githarbor.frontend.monaco.gsi.GsiElement;
import ru.githarbor.frontend.monaco.java.DefinitionType;
import ru.githarbor.frontend.harbor.core.github.core.File;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.monaco.gsi.java.impl.JavaFile;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class ExternalReferenceResolver {

    public Repository repository;


    @Inject
    public ExternalReferenceResolver(Repository repository) {
        this.repository = repository;
    }

    public DefinitionResult resolve(JavaFile javaFile, File file, GsiElement element) {

        File[] files = resolveFromImport(javaFile, file, element);

        // Ищем внутри директории
        if (files.length == 0) {
            files = Arrays.stream(file.getParent().getFiles())
                    .filter(fileCandidate -> fileCandidate.canonicalName.equals(element.getText()))
                    .toArray(File[]::new);
        }

        if (files.length == 0) {
            return new DefinitionResult(DefinitionType.UNRESOLVED);
        }

        return new DefinitionResult(DefinitionType.EXTERNAL_REFERENCE, files);
    }

    private File[] resolveFromImport(JavaFile javaFile, File gitHubFile, GsiElement element) {
        final String import0 = javaFile.findImportByPostfix(element.getText());

        // Такого импорта нет
        if (import0 == null) {
            return new File[0];
        }

        final String javaFileToResolve = element.getText() + ".java";
        final String importPath = import0.replaceAll("\\.", "/").substring(0, import0.lastIndexOf("."));


        final String jPackage = javaFile.getPackage();
        final String pathPrefix = gitHubFile.getParentPath().substring(0, gitHubFile.getParentPath().length() - jPackage.length());

        // Поиск в пределах предполагаемого проекта
        final File[] files = Arrays.stream(repository.getCurrentBranch().findFile(javaFileToResolve, true, "java"))
                .filter(file -> file.getParentPath().equals(pathPrefix + importPath))
                .toArray(File[]::new);

        // Что-то нашли
        if (files.length > 0) {
            return files;
        }

        return Arrays.stream(repository.getCurrentBranch().findFile(javaFileToResolve, true, "java"))
                .filter(file -> file.getParentPath().contains(importPath))
                .toArray(File[]::new);
    }
}
