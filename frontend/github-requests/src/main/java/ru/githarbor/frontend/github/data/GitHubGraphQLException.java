package ru.githarbor.frontend.github.data;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GitHubGraphQLException extends RuntimeException {
    public static final int RESOURCE_NOT_FOUND = 1;

    private int code;

    public GitHubGraphQLException(Error[] errors) {
        super(Stream.of(errors)
                .map(error -> error.message)
                .collect(Collectors.joining(",")));
    }

    public GitHubGraphQLException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
