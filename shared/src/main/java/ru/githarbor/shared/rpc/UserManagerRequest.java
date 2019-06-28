package ru.githarbor.shared.rpc;

public class UserManagerRequest {
    public String methodName;

    public UserManagerRequest(String methodName) {
        this.methodName = methodName;
    }
}
