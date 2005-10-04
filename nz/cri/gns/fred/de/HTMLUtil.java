package nz.cri.gns.fred.de;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import nz.cri.gns.db.BoxCreator;
import nz.cri.gns.db.ComboDescriptor;
import nz.cri.gns.db.DefaultBoxCreator;

public class HTMLUtil {

	private HTMLUtil() {
	}

	/**
	 * Writes a select box from the given list. 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public static <T> void createSelect(PrintWriter out, ComboDescriptor cd, List<? extends T> list, Class<T> contentClass, String valueMethod, String textMethod) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		createSelect(out, cd, new DefaultBoxCreator(), list, contentClass, valueMethod, textMethod);
	}

	/**
	 * Writes a select box from the given list. 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public static <T> void createSelect(PrintWriter out, ComboDescriptor cd, BoxCreator boxCreator, List<? extends T> list, Class<T> contentClass, String valueMethod, String textMethod) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		boxCreator.writeBoxHeader(out, cd);
		//Work out these methods
		Method value = contentClass.getMethod(valueMethod, (Class[])null);
		Method text = contentClass.getMethod(textMethod, (Class[])null);
		for (T t : list) {
			boxCreator.addListEntry(out, cd, value.invoke(t, (Object[])null).toString(), text.invoke(t, (Object[])null).toString());
		}
		boxCreator.writeBoxFooter(out, cd);
	}
}
