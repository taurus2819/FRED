package nz.cri.gns.fred.hibernate.dao;

import java.io.Serializable;
import java.util.Iterator;

import net.sf.hibernate.CallbackException;
import net.sf.hibernate.Interceptor;
import net.sf.hibernate.type.Type;

/**
 * Checks for composite keyed entities saved state.  Everything else is left to defaults
 */
public class FREDInterceptor implements Interceptor, Serializable {

	private static final long serialVersionUID = 20050818L;

	public boolean onLoad(Object arg0, Serializable arg1, Object[] arg2, String[] arg3, Type[] arg4) throws CallbackException {
		return false;
	}

	public boolean onFlushDirty(Object arg0, Serializable arg1, Object[] arg2, Object[] arg3, String[] arg4, Type[] arg5) throws CallbackException {
		return false;
	}

	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
		System.out.println("In onSave : " + entity.getClass());
		try {
			throw new Exception();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (entity instanceof AssignedKeyed) {
			System.out.println("Modifying");
			//Check the key
			((AssignedKeyed)entity).updateKey();
		}
		return false;
	}

	public void onDelete(Object arg0, Serializable arg1, Object[] arg2, String[] arg3, Type[] arg4) throws CallbackException {
	}

	public void preFlush(Iterator arg0) throws CallbackException {
	}

	public void postFlush(Iterator arg0) throws CallbackException {
	}

	public Boolean isUnsaved(Object arg0) {
		if (arg0 instanceof CompositeKeyed) {
			return new Boolean(((CompositeKeyed)arg0).isUnsaved());
		}
		return null;
	}

	public int[] findDirty(Object arg0, Serializable arg1, Object[] arg2, Object[] arg3, String[] arg4, Type[] arg5) {
		return null;
	}

	public Object instantiate(Class clazz, Serializable arg1) throws CallbackException {
		//CompositeKeyed classes use the boolean constructor with true for saved
		if (CompositeKeyed.class.isAssignableFrom(clazz)) try {
			CompositeKeyed obj = (CompositeKeyed)clazz.getConstructor(new Class[] {boolean.class}).newInstance(new Object[] {new Boolean(true)});
			obj.setKey((CompositeKey)arg1);
			return obj;
		} catch (Exception e) {
			throw new CallbackException(e);
		}
		return null;
	}

}
