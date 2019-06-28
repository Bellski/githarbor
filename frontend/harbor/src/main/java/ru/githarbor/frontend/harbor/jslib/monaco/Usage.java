package ru.githarbor.frontend.harbor.jslib.monaco;

public class Usage {
    public final String className;
    public final double line;
    public String usageText;
    public final IRange range;

    public Usage(String className, double line, String usageOf, String usageText, IRange range) {
        this.className = className;
        this.line = line;
        this.range = range;

//        VueUtil.makePropertiesNonReactive(this);

        this.usageText = "";

        usageText = usageText.substring(0, (int) (range.getStartColumn() - 1))
                + "****"
                + usageText.substring((int) range.getStartColumn() - 1, (int) range.getEndColumn() - 1)
                + "/***"
                + usageText.substring((int) (range.getEndColumn() -1));


//        Monaco
//                .colorize(usageText.trim(), "java")
//                .then(data -> {
//                    data = data
//                            .replaceFirst("\\*\\*\\*\\*", "<span class=\"target\">")
//                            .replaceFirst("\\/\\*\\*\\*", "</span>");
//
//                    HTMLTemplateElement templateElement = (HTMLTemplateElement) DomGlobal.document.createElement("template");
//
//                    templateElement.innerHTML = data;
//
//                    this.usageText = ((HTMLElement) templateElement.content.firstChild).innerHTML;
//                });
    }

}
