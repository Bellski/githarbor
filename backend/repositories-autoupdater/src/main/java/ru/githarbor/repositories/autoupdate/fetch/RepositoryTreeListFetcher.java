package ru.githarbor.repositories.autoupdate.fetch;

import com.jsoniter.JsonIterator;
import com.jsoniter.spi.JsonException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


public class RepositoryTreeListFetcher {
    private class Node {
        private String path;
        private boolean isTree;

        public Node(String path, boolean isTree) {
            this.path = path;
            this.isTree = isTree;
        }

        public String getPath() {
            return path;
        }

        public boolean isTree() {
            return isTree;
        }
    }

    private final OkHttpClient httpClient;

    public RepositoryTreeListFetcher(OkHttpClient httpClient) {
        this.httpClient = httpClient;

        JsonIterator.enableStreamingSupport();
    }

    public List<String> fetch(String ownerWithName, String branch, String accessToken) throws IOException {
        return fetch(ownerWithName, branch, accessToken, null);
    }

    public List<String> fetch(String ownerWithName, String branch, String accessToken, Runnable truncatedHandler) throws IOException {
        System.out.println("---- start " + ownerWithName);

        final List<String> paths = new ArrayList<>();

        boolean truncated;
        try (Response response = requestRepositoryTreeRecursive(ownerWithName, branch, accessToken)) {
            truncated = collectBlobsFrom(response.body().byteStream(), paths, null);
        }

        if (truncated) {

            if (truncatedHandler != null) {
                truncatedHandler.run();
            }

            String lastPath = paths.get(paths.size() - 1);
            String truncatedPath = lastPath.substring(0, lastPath.indexOf("/"));

            try(Response response = requestRepositoryTree(ownerWithName, branch, null, false, accessToken)) {
                processTruncated(ownerWithName, branch, collectTreeNodesFrom(response.body().byteStream(), null), paths, truncatedPath, accessToken);
            }
        }

        System.out.println("------- end " + ownerWithName + "/" + paths.size());

        return paths;
    }

    private void processTruncated(String ownerWithName, String branch, List<Node> nodesToProcess, List<String> paths, String truncated, String accessToken) throws IOException {
        removePathsStartsFrom(truncated, paths);

        int startFrom = -1;
        for (int i = 0; i < nodesToProcess.size(); i++) {
            if (nodesToProcess.get(i).path.equals(truncated)) {
                startFrom = i;
            }
        }

        for (int i = startFrom; i < nodesToProcess.size(); i++) {
            final Node nodeToProcess = nodesToProcess.get(i);

            if (nodeToProcess.isTree) {
                try (Response responseTreeRecursive = requestRepositoryTreeRecursive(ownerWithName, branch, nodeToProcess.path, accessToken)) {

                    //truncated
                    if (collectBlobsFrom(responseTreeRecursive.body().byteStream(), paths, nodeToProcess.path)) {
                        String lastPath = paths.get(paths.size() - 1);
                        String truncatedPath = lastPath.substring(0, lastPath.indexOf("/", nodeToProcess.path.length() + 1));

                        try(Response response = requestRepositoryTree(ownerWithName, branch, nodeToProcess.path, false, accessToken)) {
                            processTruncated(ownerWithName, branch, collectTreeNodesFrom(response.body().byteStream(), nodeToProcess.path), paths, truncatedPath, accessToken);
                        }
                    }
                }
            } else {
                paths.add(nodeToProcess.path);
            }
        }
    }

    private void removePathsStartsFrom(String startsFrom, List<String> paths) {
        final ListIterator<String> reverseIterator = paths.listIterator(paths.size());

        while (reverseIterator.hasPrevious()) {
            if (reverseIterator.previous().startsWith(startsFrom)) {
                reverseIterator.remove();
            } else {
                break;
            }
        }
    }

    private boolean collectBlobsFrom(InputStream body, List<String> paths, String root) throws IOException {
        boolean truncated = false;

        JsonIterator iter = JsonIterator.parse(body, 1024);

        String path = null;

        try {
            for (String field = iter.readObject(); field != null; field = iter.readObject()) {
                switch (field) {
                    case "tree":
                        while (iter.readArray()) {
                            for (String field2 = iter.readObject(); field2 != null; field2 = iter.readObject()) {
                                switch (field2) {
                                    case "path":
                                        path = iter.readString();
                                        break;
                                    case "type":
                                        if ("blob".equals(iter.readString())) {
                                            if (root != null) {
                                                paths.add(root + "/" + path);
                                            } else {
                                                paths.add(path);
                                            }
                                        }
                                        break;
                                    default:
                                        iter.skip();
                                }
                            }
                        }
                        break;
                    case "truncated":
                        truncated = iter.readBoolean();
                        break;
                    default:
                        iter.skip();
                }
            }
        } catch (JsonException e) {
            String s = "";
        }

        iter.close();

        return truncated;
    }

    private List<Node> collectTreeNodesFrom(InputStream body, String parentPath) throws IOException {
        final List<Node> nodes = new ArrayList<>();

        JsonIterator iter = JsonIterator.parse(body, 1024);

        String path = null;
        for (String field = iter.readObject(); field != null; field = iter.readObject()) {
            switch (field) {
                case "tree":
                    while (iter.readArray()) {
                        for (String field2 = iter.readObject(); field2 != null; field2 = iter.readObject()) {
                            switch (field2) {
                                case "path":
                                    path = iter.readString();
                                    break;
                                case "type":
                                    boolean isDirectory = iter.readString().equals("tree");
                                    if (parentPath != null) {
                                        nodes.add(new Node(parentPath + "/" + path, isDirectory));
                                    } else {
                                        nodes.add(new Node(path, isDirectory));
                                    }
                                    break;
                                default:
                                    iter.skip();
                            }
                        }
                    }
                    break;
                default:
                    iter.skip();
            }
        }

        iter.close();

        return nodes;
    }

    private Response requestRepositoryTreeRecursive(String ownerWithName, String branch, String path, String accessToken) throws IOException {
        return requestRepositoryTree(ownerWithName, branch, path, true, accessToken);
    }

    private Response requestRepositoryTreeRecursive(String ownerWithName, String branch, String accessToken) throws IOException {
        return requestRepositoryTree(ownerWithName, branch, null, true, accessToken);
    }

    private Response requestRepositoryTree(String ownerWithName, String branch, String path, boolean recursive, String accessToken) throws IOException {
        final StringBuilder uriBuilder = new StringBuilder()
                .append("https://api.github.com/repos/")
                .append(ownerWithName)
                .append("/git/trees/")
                .append(branch);

        if (path != null) {
            uriBuilder.append(":");
            uriBuilder.append(path);
        }

        if (recursive) {
            uriBuilder.append("?recursive=1");
        }

        final Request request = new Request.Builder()
                .url(uriBuilder.toString())
                .header("Accept", "application/json")
                .header("Authorization", "token " + accessToken)
                .build();

        return httpClient.newCall(request).execute();
    }
}
