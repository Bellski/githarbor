package ru.githarbor.frontend.fileviewer.vue;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.annotations.component.PropDefault;
import com.axellience.vuegwt.core.annotations.component.Ref;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import elemental2.core.JsRegExp;
import elemental2.dom.HTMLElement;
import ru.githarbor.frontend.monaco.IRange;
import ru.githarbor.frontend.monaco.ITextModel;
import ru.githarbor.frontend.monaco.Monaco;
import ru.githarbor.frontend.monaco.action.CopyUrlSelectionAction;
import ru.githarbor.frontend.monaco.editor.IDimension;
import ru.githarbor.frontend.monaco.editor.IEditor;
import ru.githarbor.frontend.fileviewer.core.InitParams;

import javax.inject.Inject;

import static elemental2.dom.DomGlobal.*;
import static elemental2.dom.DomGlobal.document;

@Component(hasTemplate = false)
public class MonacoContainerComponent implements IsVueComponent, HasMounted, HasCreated {

    @Inject
    public InitParams initParams;

    @Prop
    public String content;

    @Prop
    public String lang;

    @Prop
    public double offsetTop;

    @Ref
    public HTMLElement monacoContainer;

    protected IEditor monaco;

    @PropDefault("offsetTop")
    public double defaultOffsetTop() {
        return 1.3;
    }

    @Override
    public void created() {
        window.addEventListener("resize", evt -> {
            if (monaco != null) {
                setTimeout(p0 -> {
                    final IDimension iDimension = new IDimension();
                    iDimension.width = monacoContainer.offsetWidth;
                    iDimension.height = document.body.clientHeight - (monacoContainer.offsetTop * offsetTop);

                    monaco.layout(iDimension);
                }, 50);
            }
        });
    }

    @Override
    public void mounted() {

        final ITextModel model = Monaco.createModel(content, lang);

        vue().$nextTick(() -> {
            monaco = Monaco.createEditor(monacoContainer);
            monaco.addAction(new CopyUrlSelectionAction(
                    initParams.ownerWithName,
                    initParams.branch,
                    initParams.path
            ));
            monaco.setModel(model);

            revealSelection(monaco);

            onMonacoCreated();

            vue().$nextTick(() -> {
                final IDimension iDimension = new IDimension();
                iDimension.width = monacoContainer.offsetWidth;
                iDimension.height = document.body.clientHeight - (monacoContainer.offsetTop * offsetTop);

                monaco.layout(iDimension);

                monaco.focus();
            });
        });
    }

    private void revealSelection(IEditor monaco) {
        final String hash = location.getHash();

        String[] result = new JsRegExp("^#L([\\d]+)$").exec(hash);

        if (result == null) {
            result = new JsRegExp("^#L([\\d]+)-L([\\d]+)$").exec(hash);
        }

        if (result == null) {
            result = new JsRegExp("^#L([\\d]+)-L([\\d]+),C([\\d]+)-C([\\d]+)$").exec(hash);
        }

        if (result != null) {
            double startLine;
            double endLine;
            double startColumn = 1;
            double endColumn = 1;

            if (result.length == 2) {
                startLine = Double.valueOf(result[1]);
                endLine = Double.valueOf(result[1]);
            } else if (result.length == 3) {
                startLine = Double.valueOf(result[1]);
                endLine = Double.valueOf(result[2]);
                endColumn = monaco.getModel().getLineLastNonWhitespaceColumn(endLine);
            } else {
                startLine = Double.valueOf(result[1]);
                endLine = Double.valueOf(result[2]);
                startColumn = Double.valueOf(result[3]);
                endColumn = Double.valueOf(result[4]);
            }

            monaco.revealLineInCenter(startLine > 1 ? startLine - 1 : startLine);
            monaco.setSelection(IRange.create(startLine, endLine, startColumn, endColumn));
        }
    }

    protected void onMonacoCreated() {

    }
}
