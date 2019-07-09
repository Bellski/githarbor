package ru.githarbor.frontend.fileviewer;

import com.google.gwt.core.client.EntryPoint;
import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.dom.HTMLMetaElement;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.monaco.Monaco;
import ru.githarbor.frontend.monaco.Theme;
import ru.githarbor.frontend.fileviewer.core.GitHubGqlClient;
import ru.githarbor.frontend.fileviewer.core.InitParams;
import ru.githarbor.frontend.fileviewer.dagger.DaggerFileViewerDaggerComponent;

import static elemental2.dom.DomGlobal.document;
import static elemental2.dom.DomGlobal.location;

public class FileViewerEntryPoint implements EntryPoint {

    @Override
    public void onModuleLoad() {
        final JsPropertyMap initMap = processInit();
        boolean dark = Js.cast(initMap.get("dark"));

        Monaco.defineTheme("light", Theme.LIGHT);
        Monaco.defineTheme("dark", Theme.DARK);

        Monaco.MonacoOptions.INSTANCE.theme = dark ? "dark" : "light";


        DaggerFileViewerDaggerComponent.builder()
                .gitHubGqlClient(new GitHubGqlClient(Js.cast(initMap.get("accessToken"))))
                .initParams(processInitParams())
                .build()
                .fileViewerComponentFactory()
                .create()
                .vue()
                .$mount("#app");
    }

    private InitParams processInitParams() {
        final JsArray<String> route = PathToRegExp.path("/:owner/:name/blob/:branch/:path*").exec(location.getPathname());

        return new InitParams(
                route.getAt(1) + "/" + route.getAt(2),
                route.getAt(3),
                route.getAt(4)
        );
    }

    private JsPropertyMap processInit() {
        final HTMLMetaElement meta = Js.cast(document.head.querySelector("[data-init]"));

        JsPropertyMap<Object> initMap = Js.cast(Global.JSON.parse(meta.dataset.get("init")));

        meta.remove();

        return initMap;
    }
}
