package ru.githarbor.shared.rpc;


import jsinterop.annotations.JsType;
import ru.githarbor.shared.UiState;

@JsType
public class CreateUiStateRequest extends UserManagerRequest {

    public final UiState uiState;

    public CreateUiStateRequest(UiState uiState) {
        super(CreateUiStateRequest.class.getName());

        this.uiState = uiState;
    }
}
