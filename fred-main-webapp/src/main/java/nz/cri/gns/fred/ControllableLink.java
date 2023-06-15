package nz.cri.gns.fred;

import java.util.function.Supplier;
import nz.cri.gns.jsp.Link;

/**
 * A {@link Link} that can be disabled or hidden.
 */
public class ControllableLink implements Link {
    
    public static Link disableable(Link child, boolean disabled) {
        return new ControllableLink(child, disabled, false);
    }
    
    public static Link hideable(Link child, boolean hidden) {
        return new ControllableLink(child, false, hidden);
    }

    private final Link child;
    private final boolean disabled;
    private final boolean hidden;

    private ControllableLink(Link child, boolean disabled, boolean hidden) {
        this.child = child;
        this.disabled = disabled;
        this.hidden = hidden;
    }

    @Override
    public String getHTML(boolean bln) {
        return doLink(() -> child.getHTML(bln));
    }

    @Override
    public String getNewHTML(boolean bln) {
        return doLink(() -> child.getNewHTML(bln));
    }

    private String doLink(Supplier<String> linkContent) {
        if (hidden) {
            return "";  // it's hiding. nothing to show here
        }
        if (disabled) {
            return String.format(
                    "<span class=\"link-disabled\" title=\"Login required\">%s</span>",
                    linkContent.get()
            );
        }
        return linkContent.get();
    }


}
