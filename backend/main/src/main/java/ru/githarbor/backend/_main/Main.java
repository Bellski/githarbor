package ru.githarbor.backend._main;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jsoniter.JsonIterator;
import io.javalin.Context;
import io.javalin.Handler;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import io.javalin.security.Role;
import io.javalin.staticfiles.Location;
import io.javalin.websocket.WsHandler;
import io.javalin.websocket.WsSession;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.NullSessionDataStore;
import org.eclipse.jetty.server.session.Session;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import ru.githarbor.backend._main.dagger.DaggerMainComponent;
import ru.githarbor.backend._main.dagger.MainComponent;
import ru.githarbor.backend._main.manager.UserManager;
import ru.githarbor.backend._main.rocker.file_viewer;
import ru.githarbor.backend._main.rocker.glide_hello;
import ru.githarbor.backend._main.rocker.glide_project;
import ru.githarbor.backend._main.server.ws.RepositoryPathsService;
import ru.githarbor.shared.UiState;
import ru.githarbor.shared.User;
import ru.githarbor.shared.paths.Branch;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Main {

    public static final String GH_ID = "ghId";

    public enum Roles implements Role {
        USER;

        public static Set<Role> USER() {
            return new HashSet<>(Collections.singletonList(USER));
        }
    }

    public static void main(String[] args) {
        final MainComponent mainComponent = DaggerMainComponent.create();

        final SessionHandler sessionHandler = new SessionHandler();
        final DefaultSessionCache defaultSessionCache = new DefaultSessionCache(sessionHandler);
        defaultSessionCache.setSessionDataStore(new NullSessionDataStore());
        sessionHandler.setSessionCache(defaultSessionCache);

        JavalinJackson.configure(
                new ObjectMapper()
                        .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        );


        Javalin.create()
                .maxBodySizeForRequestCache(5242880)
                .requestLogger((ctx, executionTimeMs) -> {

                })
                .enableStaticFiles("D:\\Projects\\githarbor\\frontend\\webpack-output", Location.EXTERNAL)
                .accessManager(Main::onAccess)
                .sessionHandler(() -> sessionHandler)
                .get("/", ctx -> onRoot(ctx, mainComponent.userManager()), Roles.USER())
                .get("/user/in", Main::onUserIn)
                .get("/github/oauth", ctx -> onGitHubOauth(ctx, mainComponent.userManager()))
                .get("/:owner/:name", ctx -> onRepository(ctx, mainComponent.userManager()), Roles.USER())
                .get("/:owner/:name/blob/:branch/*", ctx -> {


                    if (ctx.queryParam("repository") != null) {
                        onRepository(ctx, mainComponent.userManager());

                        return;
                    }

                    ctx.contentType("text/html; charset=utf-8");

                    final User user = mainComponent.userManager().getUser(ctx.sessionAttribute(GH_ID));

                    final String theme = user.darkTheme ? "dark.harbor.css"  : "default.harbor.css";

                    final JsonObject init = new JsonObject();
                    init.addProperty("accessToken", (String) ctx.sessionAttribute("accessToken"));
                    init.addProperty("dark", user.darkTheme);

                    ctx.result(
                            file_viewer.template(theme, init.toString())
                                    .render()
                                    .toString()
                    );
                }, Roles.USER())
                .ws("/websocket/paths", wsHandler -> onWebSocketPaths(wsHandler, defaultSessionCache, mainComponent.wsConnections(), mainComponent.repositoryPathsService()))
                .post("/user/user-manager", mainComponent.userManagerRpcServer(), Roles.USER())
                .post("/java-resolver", mainComponent.javaSourceResolverRpcServer(), Roles.USER())
                .post("/go-resolver", mainComponent.goSourceResolverRpcServer(), Roles.USER())
                .start(80);
    }

    private static void onAccess(Handler handler, Context ctx, Set<Role> permittedRoles) throws Exception {
        if (permittedRoles.contains(Roles.USER)) {
            if (ctx.sessionAttribute(GH_ID) == null) {
                ctx.header("Location", "http://about.githarbor.com");
                ctx.status(HttpServletResponse.SC_FOUND);

                return;
            }

            handler.handle(ctx);

            return;
        }

        if (ctx.path().equals("/github/oauth")) {
            final String code = ctx.queryParam("code");

            if (code == null) {
                ctx.header("Location", "http://about.githarbor.com");
                ctx.status(HttpServletResponse.SC_FOUND);

                return;
            }
        }

        if (ctx.path().equals("/user/in")) {

        }

        handler.handle(ctx);
    }

    private static void onRoot(Context ctx, UserManager userManager) {
        ctx.contentType("text/html; charset=utf-8");

        final User user = userManager.getUser(ctx.sessionAttribute(GH_ID));
        user.accessToken = ctx.sessionAttribute("accessToken");

        final String theme = user.darkTheme ? "dark.harbor.css"  : "default.harbor.css";

        ctx.result(glide_hello.template(new Gson().toJson(user), theme)
                .render()
                .toString());
    }

    private static void onGitHubOauth(Context ctx, UserManager usersManager) throws Exception {
        final String accessToken = GtiHubOauthHelper.requestAccessToken(ctx.queryParam("code"));

        if (accessToken == null) {
            ctx.header("Location", "/site");
            ctx.status(HttpServletResponse.SC_FOUND);

            return;
        }

        final long gitHubUserId = GtiHubOauthHelper.requestUserId(accessToken);

        if (!usersManager.isExists(gitHubUserId)) {
            usersManager.createUser(gitHubUserId);
        }

        ctx.sessionAttribute(GH_ID  , gitHubUserId);
        ctx.sessionAttribute("accessToken", accessToken);

        ctx.status(302);
        ctx.header("Location", "/");
    }


    private static void onUserIn(Context ctx) {
        if (ctx.sessionAttribute(GH_ID) == null) {
            ctx.header(
                    "Location",
                    "https://github.com/login/oauth/authorize?response_type=code&client_id=269e8e0c59cd02884187&redirect_uri=http://githarbor.com/github/oauth"
            );
        } else {
            ctx.header("Location", "/");
        }

        ctx.status(HttpServletResponse.SC_FOUND);
    }

    private static void onRepository(Context ctx, UserManager usersManager) {
        ctx.contentType("text/html; charset=utf-8");

        final User user = usersManager.getUser(ctx.sessionAttribute(GH_ID));
        user.accessToken = ctx.sessionAttribute("accessToken");

        if (user.tier1Backer) {
            user.uiState = usersManager.getUiState(ctx.sessionAttribute(GH_ID), ctx.pathParam(":owner") + "/" + ctx.pathParam(":name"));
        }

        final String theme = user.darkTheme ? "dark.harbor.css"  : "default.harbor.css";
        final String themeName = user.darkTheme ? "dark" : "default";

        ctx.result(glide_project.template(new Gson().toJson(user), null, theme, themeName)
                .render()
                .toString());
    }

    private static void onWebSocketPaths(WsHandler wsHandler, DefaultSessionCache defaultSessionCache, Map<String, WsSession> wsSessionMap, RepositoryPathsService repositoryPathsService) {
        wsHandler.onConnect(session -> {
            final String jSessionId = session.getUpgradeRequest().getCookies().get(0).getValue();

            final Session httpSession = defaultSessionCache.doGet(jSessionId.substring(0, jSessionId.lastIndexOf(".")));

            if (httpSession.getAttribute(GH_ID) == null) {
                session.disconnect();

                return;
            }

            ServletUpgradeRequest upgradeRequest = ((ServletUpgradeRequest) session.getUpgradeRequest());
            upgradeRequest.setServletAttribute("accessToken", httpSession.getAttribute("accessToken"));
            upgradeRequest.setServletAttribute(GH_ID, httpSession.getAttribute(GH_ID));

            wsSessionMap.put(session.getId(), session);
        });

        wsHandler.onClose((session, statusCode, reason) -> wsSessionMap.remove(session.getId()));

        wsHandler.onMessage((session, msg) -> {
            final ServletUpgradeRequest upgradeRequest = ((ServletUpgradeRequest) session.getUpgradeRequest());
            final String accessToken = (String) upgradeRequest.getServletAttribute("accessToken");
            final long userId = (Long) upgradeRequest.getServletAttribute(GH_ID);

            repositoryPathsService.repositoryPaths(JsonIterator.deserialize(msg, Branch.class), session, userId, accessToken);
        });
    }


    public static class GtiHubOauthHelper  {

        public static String requestAccessToken(String tempCode) throws Exception {

            final String result = httpRequest(
                    "https://github.com/login/oauth/access_token",
                    ("{\"client_id\":\"269e8e0c59cd02884187\", \"client_secret\": \"4549c3a8da24b427a3e83f18124573788323fa57\", \"code\":" + "\"" + tempCode + "\"" + "}").getBytes("UTF-8"),
                    null
            );

            return new JsonParser().parse(result).getAsJsonObject().get("access_token").getAsString();
        }

        public static int requestUserId(String accessToken) throws Exception {
            final JsonObject jsonResponse = (JsonObject) new JsonParser().parse(httpRequest(
                    "https://api.github.com/graphql",
                    ("{\"query\":\"query{viewer{databaseId login}}\"}").getBytes("UTF-8"),
                    accessToken
            ));

            return jsonResponse
                    .getAsJsonObject("data")
                    .getAsJsonObject("viewer")
                    .get("databaseId").getAsInt();
        }

        private static String httpRequest(String spec, byte[] body, String accessToken) throws Exception {
            final URL url = new URL(spec);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "application/json");

            if (accessToken != null) {
                con.setRequestProperty("Authorization", "token " + accessToken);
            }

            try (final OutputStream out = con.getOutputStream()) {
                out.write(body);
            }

            try (final InputStream in = con.getInputStream()) {
                return inputStreamToString(in);
            } finally {
                con.disconnect();
            }
        }

        private static String inputStreamToString(InputStream is) throws IOException {
            try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int length;

                while ((length = is.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }

                return result.toString("UTF-8");
            }
        }
    }

}
