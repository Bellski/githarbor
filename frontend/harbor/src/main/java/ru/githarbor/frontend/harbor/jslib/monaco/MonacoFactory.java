package ru.githarbor.frontend.harbor.jslib.monaco;

import com.intendia.rxgwt2.elemental2.RxElemental2;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLScriptElement;
import io.reactivex.Completable;
import io.reactivex.Single;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.harbor.core.github.core.File;
import ru.githarbor.frontend.harbor.jslib.monaco.editor.IEditor;
import ru.githarbor.frontend.harbor.jslib.monaco.editor.StandaloneDiffEditor;
import ru.githarbor.shared.User;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MonacoFactory {

    private User user;
    private LanguageExtensionPoints languageExtensionPoints;

    @Inject
    public MonacoFactory(User user, LanguageExtensionPoints languageExtensionPoints) {
        this.user = user;
        this.languageExtensionPoints = languageExtensionPoints;
    }

    public boolean isReady() {
        return DomGlobal.document.querySelector("[monaco]") != null;
    }

    public Completable onReady() {
        if (!isReady()) {
            return Completable.create(e -> {
                final HTMLScriptElement monacoScriptElement = Js.cast(DomGlobal.document.createElement("script"));
                monacoScriptElement.setAttribute("monaco", "");
                monacoScriptElement.type = "text/javascript";
                monacoScriptElement.src = "/harbor/assets/webpack/monaco.js";
                monacoScriptElement.onload = p0 -> {
                    Monaco.defineTheme("light", Theme.LIGHT);
                    Monaco.defineTheme("dark", Theme.DARK);

                    Monaco.MonacoOptions.INSTANCE.theme = user.theme;

                    Monaco.setJavaScriptEagerModelSync(true);
                    Monaco.setJavaScriptDiagnosticsOptions(
                            JsPropertyMap.of(
                                    "noSemanticValidation", true,
                                    "noSyntaxValidation", true
                            )
                    );

                    e.onComplete();

                    return null;
                };

                DomGlobal.document.head.appendChild(monacoScriptElement);
            });
        }

        return Completable.complete();
    }

    public IEditor create(HTMLElement container) {
        final IEditor editor = Monaco.createEditor(container);

        Monaco.setTheme(user.theme);

        return editor;
    }

    public StandaloneDiffEditor createDiff(HTMLElement container, String modified, String original, String file) {

        final StandaloneDiffEditor editor = Monaco.createDiffEditor(container, initModel(file, modified), initModel(file, original));

        Monaco.setTheme(user.theme);

        return editor;
    }

    public Single<ITextModel> initModel(File file) {

        if (file.isContentResolved()) {
            return Single.just(initModel(file.name, file.getContent()));
        }

        return file.resolveContent()
                .map(content -> initModel(file.name, content));
    }

    public ITextModel initModel(String name, String content) {
        return Monaco.createModel(content, languageExtensionPoints.getLanguageFromFileName(name));
    }

    public Single<String> colorize(String name, String content) {
        return RxElemental2.fromPromise(Monaco.colorize(content, languageExtensionPoints.getLanguageFromFileName(name)));
    }

    public StandaloneDiffEditor.DiffModel initDiffModel(String modified, String original, String file) {
        final StandaloneDiffEditor.DiffModel diffModel = new StandaloneDiffEditor.DiffModel();
        diffModel.setOriginal(initModel(file, modified));
        diffModel.setModified(initModel(file, original));

        return diffModel;
    }
}
