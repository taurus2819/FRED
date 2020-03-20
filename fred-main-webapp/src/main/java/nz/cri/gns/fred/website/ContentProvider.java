package nz.cri.gns.fred.website;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.PropertyResourceBundle;

import nz.cri.gns.intranet.Template;
/**
 *
 */
public class ContentProvider {

	private File contentPath;
	private PropertyResourceBundle contentMap;

	public ContentProvider(File contentPath) {
		this.contentPath = contentPath;
		try {
			contentMap = new PropertyResourceBundle(new FileInputStream(new File(contentPath, "content.map")));
		} catch (Exception e) {
		}
	}
	
	
	public Template getContent(String key) {
		String filename = contentMap.getString(key);
		try {
			return new Template(new FileReader(new File(contentPath, filename)), Template.BRACE_MODE);
		} catch (Exception e) {
                    System.out.println("Template:" + e);
			return null;
		}
	}
}
