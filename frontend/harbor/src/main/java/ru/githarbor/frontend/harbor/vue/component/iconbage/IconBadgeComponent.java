package ru.githarbor.frontend.harbor.vue.component.iconbage;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.annotations.component.PropDefault;
import com.axellience.vuegwt.core.client.component.IsVueComponent;

@Component
public class IconBadgeComponent implements IsVueComponent {

    @Prop
    protected String text;

    @Prop
    protected String prefix;

    @Prop
    protected String icon;

    @Prop
    protected boolean isAnchor;

    @Prop
    protected String href;

    @Prop
    protected boolean isHoverable;

    @PropDefault("text")
    public String defaultText() {
        return null;
    }

    @PropDefault("prefix")
    public String defaultPrefix() {
        return null;
    }

    @PropDefault("isAnchor")
    public boolean defaultIsAnchor() {
        return false;
    }

    @PropDefault("href")
    public String defaultHref() {
        return null;
    }

    @PropDefault("isHoverable")
    public boolean defaultIsHoverable() {
        return false;
    }

    @Computed
    public boolean getHasPrefix() {
        return prefix != null;
    }
}
