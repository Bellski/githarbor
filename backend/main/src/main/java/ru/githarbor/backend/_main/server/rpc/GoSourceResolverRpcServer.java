package ru.githarbor.backend._main.server.rpc;

import com.google.gson.Gson;
import com.sun.jna.Library;
import com.sun.jna.Native;
import io.javalin.Context;
import io.javalin.Handler;
import org.jetbrains.annotations.NotNull;
import ru.githarbor.shared.rpc.go.GoResolveSourceRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static ru.githarbor.backend._main.server.rpc.JavaSourceResolverRpcServer.*;

@Singleton
public class GoSourceResolverRpcServer implements Handler {

    private final Gson gson;
    private final GoResolver goResolver;

    public interface GoResolver extends Library {
        String ResolveSource(String source);
    }

    @Inject
    public GoSourceResolverRpcServer(Gson gson) {
        this.gson = gson;
        goResolver = Native.load("C:\\Users\\hbell\\go\\src\\awesomeProject\\go-resolver.so", GoResolver.class);
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        final GoResolveSourceRequest goResolveSourceRequest = gson.fromJson(ctx.body(), GoResolveSourceRequest.class);

        if (GoResolveSourceRequest.class.getName().equals(goResolveSourceRequest.methodName)) {
            final HttpRequest request =  buildRequest(buildQuery(goResolveSourceRequest.ownerWithName, goResolveSourceRequest.path), ctx.sessionAttribute("accessToken"));

            final String blobContent = getBlobTextFromResponse(gson, JavaSourceResolverRpcServer.httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body());

            if (blobContent == null) {
                return;
            }


            ctx.result(goResolver.ResolveSource(blobContent));
        }
    }
}
