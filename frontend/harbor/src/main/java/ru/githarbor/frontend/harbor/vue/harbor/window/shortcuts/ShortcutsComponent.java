package ru.githarbor.frontend.harbor.vue.harbor.window.shortcuts;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasBeforeDestroy;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.options.functions.OnEvent;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import ru.githarbor.frontend.harbor.core.state.HarborState;
import ru.githarbor.frontend.harbor.event.Events;
import ru.githarbor.frontend.harbor.jslib.MyKeyboardEvent;

import javax.inject.Inject;

@Component
public class ShortcutsComponent implements IsVueComponent, HasCreated, HasBeforeDestroy {

    @Inject
    public HarborState harborState;

    private OnEvent globalKeyDownListener;

    @JsMethod
    public void onClose() {
        harborState.window = null;
    }

    @Override
    public void created() {
        vue().$root().vue().$on(Events.GLOBAL_KEYDOWN, globalKeyDownListener = parameter -> {
            final MyKeyboardEvent myKeyboardEvent = Js.cast(parameter);

            if (myKeyboardEvent.getKeyCode() == 27) {
                onClose();
            }
        });
    }

    @Override
    public void beforeDestroy() {
        vue().$root().vue().$off(Events.GLOBAL_KEYDOWN, globalKeyDownListener);
    }
}
