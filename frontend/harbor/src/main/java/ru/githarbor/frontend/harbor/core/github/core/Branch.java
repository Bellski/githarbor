package ru.githarbor.frontend.harbor.core.github.core;

import elemental2.core.JsObject;
import elemental2.dom.DomGlobal;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.subjects.CompletableSubject;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.harbor.core.github.request.FileContentRequest;
import ru.githarbor.frontend.harbor.core.service.RepositoryPathsService;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public class Branch {
    protected final OwnerWithName ownerWithName;

    public final String name;
    public final String oid;
    public final String committedDate;

    final RepositoryPathsService repositoryPathsService;
    final FileContentRequest fileContentRequest;

    private boolean resolved;

    private Directory[] directories;
    private File[] files;
    private String[] extensions;

    private final CompletableSubject resolveSubject = CompletableSubject.create();

    public Branch(OwnerWithName ownerWithName,
                  String name,
                  String oid,
                  String committedDate,
                  RepositoryPathsService repositoryPathsService,
                  FileContentRequest fileContentRequest) {

        this.ownerWithName = ownerWithName;
        this.name = name;
        this.oid = oid;
        this.committedDate = committedDate;

        this.repositoryPathsService = repositoryPathsService;
        this.fileContentRequest = fileContentRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Branch branch = (Branch) o;
        return name.equals(branch.name) &&
                oid.equals(branch.oid) &&
                committedDate.equals(branch.committedDate);
    }

    public Completable resolve(RepositoryPathsService.ProcessHandler processHandler) {
        if (resolved) {
            return Completable.complete();
        }

        final ru.githarbor.shared.paths.Branch branchParams = ru.githarbor.shared.paths.Branch.create(
                ownerWithName.ownerWithName,
                name,
                committedDate
        );

        return repositoryPathsService.fetchPaths(branchParams, processHandler).doOnSuccess(fileStrings -> {


            files = new File[fileStrings.length];

            int directoryCount = 0;
            final JsPropertyMap<Directory> directoryJsPropertyMap = Js.cast(JsPropertyMap.of());
            final JsPropertyMap<String> extensionsMap = Js.cast(JsPropertyMap.of());

            for (int i = 0; i < fileStrings.length; i++) {
                final String file = fileStrings[i];
                final int lastSeparatorIndex = file.lastIndexOf("/");
                final String fileName = lastSeparatorIndex > 0 ? file.substring(lastSeparatorIndex + 1) : file;
                final String directoryPath = lastSeparatorIndex > 0 ? file.substring(0, lastSeparatorIndex) : null;

                if (directoryPath == null) {
                    files[i] = new File(fileName, -1, this);

                    continue;
                }

                Directory directory = directoryJsPropertyMap.get(directoryPath);

                if (directory == null) {
                    directory = new Directory(directoryCount++, directoryPath, this);
                    directoryJsPropertyMap.set(directoryPath, directory);
                }

                directory.fileIndexes.push((double) i);

                files[i] = new File(fileName, directory.index, this);

                if (!extensionsMap.has(files[i].extension)) {
                    extensionsMap.set(files[i].extension, "");
                }
            }

            directories = Js.uncheckedCast(JsObject.values(directoryJsPropertyMap));
            extensions = Js.uncheckedCast(JsObject.keys(extensionsMap));

            resolved = true;

            resolveSubject.onComplete();

        }).toCompletable();
    }

    public boolean isResolved() {
        return resolved;
    }

    public Completable waitResolve() {
        return resolveSubject;
    }

    public File[] getFiles() {
        return files;
    }

    public Directory[] getDirectories() {
        return directories;
    }

    public Directory getDirectory(String path) {
        return Arrays.stream(directories)
                .filter(directory -> directory.path.equals(path))
                .findFirst()
                .orElse(null);
    }

    public Optional<File> findFileByNameAtRoot(String fileName) {
        return Arrays.stream(files)
                .filter(file -> file.parent == -1 && file.name.toLowerCase().equals(fileName.toLowerCase()))
                .findFirst();
    }


    public Optional<File> getFile(String path) {
        final int lastSeparatorIndex = path.lastIndexOf("/");
        final String fileName = lastSeparatorIndex > 0 ? path.substring(lastSeparatorIndex + 1) : path;
        final String directoryPath = lastSeparatorIndex > 0 ? path.substring(0, lastSeparatorIndex) : null;

        if (directoryPath == null) {
            return findFileByNameAtRoot(fileName);
        }

        return getDirectory(directoryPath).getFile(fileName);
    }

    public File[] findFile(String query, String... extensions) {
        return findFile(query,false, extensions);
    }

    public File[] findFile(String query, boolean exact, String... extensions) {
        final String queryLowerCase = query.toLowerCase();

        return Arrays.stream(files).filter(file -> {
            final String fileNameLowerCase = file.name.toLowerCase();
            final boolean containsFile = exact ? file.name.equals(query) : fileNameLowerCase.contains(queryLowerCase);

            if (extensions.length > 0) {
                final boolean containsExtension = Arrays.asList(extensions)
                        .contains(file.extension);

                return containsExtension && containsFile;
            }

            return containsFile;
        })
                .map(file -> new FileScore(file, queryLowerCase))
                .sorted(Comparator.<FileScore>comparingInt(fileScore -> (int) fileScore.score).reversed())
                .map(fileScore -> fileScore.file)
                .toArray(File[]::new);
    }

    public Single<File[]> resolvePaths(String[] paths) {
        return fileContentRequest.executeBatch(name, ownerWithName.ownerWithName, paths).map(blobs -> {

            final File[] resolvedFiles = Arrays.stream(paths)
                    .map(this::getFile)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toArray(File[]::new);

            for (int i = 0; i < blobs.length; i++) {
                resolvedFiles[i].setContent(blobs[i].text);
            }

            return resolvedFiles;
        });
    }

    public String[] getExtensions() {
        return extensions;
    }

    @Override
    public String toString() {
        return name;
    }
}
