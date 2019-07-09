package ru.githarbor.frontend.monaco.gsi;

import ru.githarbor.shared.JavaSourceMetadataDTO;
import ru.githarbor.shared.SourceElementDTO;

import java.util.stream.Stream;

public class JavaSourceMetadataHelper {

    public static String getElementName(SourceElementDTO elementDTO, JavaSourceMetadataDTO javaSourceMetadataDTO) {
        return elementDTO.getNameIndex() == -1 ? "<unnamed>" : javaSourceMetadataDTO.symbols[elementDTO.getNameIndex()];
    }

    public static SourceElementDTO[] getElementChildren(SourceElementDTO parentDTO, JavaSourceMetadataDTO javaSourceMetadataDTO) {
        return Stream
                .of(javaSourceMetadataDTO.elements)
                .filter(elementDTO -> elementDTO.getParentIndex() == parentDTO.getIndex())
                .toArray(SourceElementDTO[]::new);
    }
}
