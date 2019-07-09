package ru.githarbor.frontend.harbor.jslib.monaco;

import elemental2.dom.HTMLElement;
import io.reactivex.Single;
import jsinterop.base.Js;
import ru.githarbor.frontend.monaco.LanguageExtensionPoints;
import ru.githarbor.frontend.monaco.Monaco;
import ru.githarbor.frontend.monaco.editor.IEditor;
import ru.githarbor.frontend.monaco.editor.StandaloneDiffEditor;
import ru.githarbor.frontend.harbor.core.github.core.File;
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

    public IEditor create(HTMLElement container) {
        final IEditor editor = Monaco.createEditor(container);

        Monaco.setTheme(user.getTheme());

        return editor;
    }

    public StandaloneDiffEditor createDiff(HTMLElement container, String modified, String original, String file) {

        final StandaloneDiffEditor editor = Monaco.createDiffEditor(container, initModel(file, modified), initModel(file, original));

        Monaco.setTheme(user.getTheme());

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
        return Js.cast(Monaco.createModel(content, languageExtensionPoints.getLanguageFromFileName(name)));
    }

//    public Single<String> colorize(String name, String content) {
//        return RxElemental2.fromPromise(Monaco.colorize(content, languageExtensionPoints.getLanguageFromFileName(name)));
//    }

    public StandaloneDiffEditor.DiffModel initDiffModel(String modified, String original, String file) {
        final StandaloneDiffEditor.DiffModel diffModel = new StandaloneDiffEditor.DiffModel();
        diffModel.setOriginal(initModel(file, modified));
        diffModel.setModified(initModel(file, original));

        return diffModel;
    }
}
