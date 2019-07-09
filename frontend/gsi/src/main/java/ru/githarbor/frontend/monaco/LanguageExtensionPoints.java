package ru.githarbor.frontend.monaco;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class LanguageExtensionPoints {

    private final Map<String, String> pointByExtension = new HashMap<>();

    @Inject
    public LanguageExtensionPoints() {

    }

    public String getLanguageFromFileName(String fileName) {
        if (pointByExtension.size() == 0) {
            for (ILanguageExtensionPoint iLanguageExtensionPoint : Monaco.getLanguages()) {
                for (String extension : iLanguageExtensionPoint.getExtensions()) {
                    extension = extension.substring(1);
                    pointByExtension.put(extension, iLanguageExtensionPoint.getId());
                }
            }
        }

        return pointByExtension.getOrDefault(fileName.substring(fileName.lastIndexOf('.') + 1), "text/plain");
    }

    public String[] getExtensionsByLanguage(String language) {

        for (ILanguageExtensionPoint iLanguageExtensionPoint : Monaco.getLanguages()) {
            if (iLanguageExtensionPoint.getId().equals(language.toLowerCase())) {
                return iLanguageExtensionPoint.getExtensions();
            }
        }

        return new String[0];
    }
}
