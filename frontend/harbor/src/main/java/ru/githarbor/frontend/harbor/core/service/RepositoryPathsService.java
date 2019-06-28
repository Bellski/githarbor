package ru.githarbor.frontend.harbor.core.service;

import elemental2.core.Global;
import elemental2.dom.Blob;
import elemental2.dom.FileReader;
import elemental2.dom.WebSocket;
import io.reactivex.Single;
import jsinterop.base.Js;
import ru.githarbor.frontend.harbor.jslib.Pako;
import ru.githarbor.shared.paths.Branch;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RepositoryPathsService {

    public interface ProcessHandler {
        void onMessage(String message);
    }

    @Inject
    public RepositoryPathsService() {

    }

    public Single<String[]> fetchPaths(Branch branch) {
        return fetchPaths(branch, null);
    }

    public Single<String[]> fetchPaths(Branch branch, ProcessHandler processHandler) {
        return Single.create(emitter -> {
            WebSocket pathsConnection = new WebSocket("ws://githarbor.com/websocket/paths");
            pathsConnection.onmessage = p0 -> {

                if (p0.data instanceof String) {
                    if (processHandler != null) {
                        processHandler.onMessage(Js.cast(p0.data));
                    }
                } else {
                    Blob blobPath = Js.cast(p0.data);
                    FileReader fileReader = new FileReader();
                    fileReader.onload = p01 -> {

                        pathsConnection.close();


                        final String paths = Pako.ungzipToString(fileReader.result.asArrayBuffer());

                        emitter.onSuccess(paths.split(","));

                        return null;
                    };

                    fileReader.readAsArrayBuffer(blobPath);
                }

                return null;
            };

            pathsConnection.onopen = p0 -> {
                pathsConnection.send(Global.JSON.stringify(branch));

                return null;
            };
        });
    }
}
