package ru.githarbor.frontend.fileviewer.vue;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import jsinterop.annotations.JsMethod;
import ru.githarbor.frontend.fileviewer.vue.javaviewer.JavaViewerComponent;
import ru.githarbor.frontend.fileviewer.vue.sourceviewer.SourceViewerComponent;
import ru.githarbor.frontend.monaco.LanguageExtensionPoints;
import ru.githarbor.frontend.fileviewer.core.FileContentRequest;
import ru.githarbor.frontend.fileviewer.core.InitParams;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;

import javax.inject.Inject;

import static elemental2.dom.DomGlobal.*;

@Component(components = {
        JavaViewerComponent.class,
        SourceViewerComponent.class,
        LoaderComponent.class
})
public class FileViewerComponent implements IsVueComponent, HasCreated {

    @Inject
    public LanguageExtensionPoints languageExtensionPoints;

    @Inject
    public InitParams initParams;

    @Inject
    public FileContentRequest fileContentRequest;

    @Data
    public boolean resolving = true;

    @Data
    public String resolvingInfo = "Loading file...";

    @Data
    public boolean noText = false;

    @Data
    public String lang;

    @Data
    public String content;

    @Computed
    public String getPath() {
        return initParams.ownerWithName + "/" + initParams.branch + "/" + initParams.path;
    }

    @Computed
    public boolean getIsJava() {
        return "java".equals(lang);
    }

    @Override
    public void created() {

        lang = languageExtensionPoints.getLanguageFromFileName(initParams.path);

        fileContentRequest.execute(initParams.ownerWithName, initParams.branch, initParams.path)
                .subscribe(blob -> {
                    content = blob.text;

                    resolving = false;
                }, throwable -> {
                    noText = true;
                    resolvingInfo = "No text to show";
                });
    }


    @JsMethod
    public void onOpenRepository() {
        window.location.setHref(
                "http://githarbor.com" +
                        window.location.getPathname() +
                        "?repository=true" +
                        window.location.getHash()
        );
    }
}
