package ru.githarbor.repositories.autoupdate.test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseLanguages {
    @Test
    public void name() throws IOException {
        final InputStream languagesStream = ParseLanguages.class.getClassLoader().getResourceAsStream("languages.json");

        final JsonObject json = new Gson().fromJson(new InputStreamReader(languagesStream), JsonObject.class);

        final Map<String, List<String>> languageMap = new HashMap<>();

        for (Map.Entry<String, JsonElement> stringJsonElementEntry : json.entrySet()) {
            final List<String> extenstions = languageMap.computeIfAbsent(stringJsonElementEntry.getKey(), key -> new ArrayList<>());

            final JsonElement extenstionsElement = stringJsonElementEntry.getValue().getAsJsonObject().get("extensions");

            if (extenstionsElement != null) {
                for (JsonElement jsonElement : extenstionsElement.getAsJsonArray()) {
                    extenstions.add(jsonElement.getAsString());
                }
            }
        }

        System.out.println(new Gson().toJson(languageMap));
    }
}
