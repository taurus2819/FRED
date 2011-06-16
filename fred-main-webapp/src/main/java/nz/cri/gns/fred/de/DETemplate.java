package nz.cri.gns.fred.de;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import nz.cri.gns.fred.website.ContentProvider;
import nz.cri.gns.intranet.Template;

public class DETemplate {

    static class NoLineBreaksPrintWriter extends PrintWriter {

        public NoLineBreaksPrintWriter(Writer out) {
            super(out);
        }

        @Override
        public void println() {
        }

        @Override
        public void println(String x) {
            super.print(x);
        }
        
        
    }
    public void prepareTemplate(Template template, ContentProvider provider) {
        Template start = provider.getContent("start.de.table");
        StringWriter sw = new StringWriter();
        start.loadAll(new NoLineBreaksPrintWriter(sw));
        template.addSub("startDETable", sw.toString());
        sw = new StringWriter();
        Template end = provider.getContent("end.de.table");
        end.loadAll(new NoLineBreaksPrintWriter(sw));
        template.addSub("endDETable", sw.toString());
    }
}
