package nz.cri.gns.fred.reports;

import java.util.HashMap;
import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;

public class JNDI extends HashMap<String, Object> implements InitialContextFactoryBuilder, InitialContextFactory, Context {

	private static final long serialVersionUID = 1L;

	public static void setup() throws NamingException {
		NamingManager.setInitialContextFactoryBuilder(new JNDI());
	}

	public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> environment) throws NamingException {
		return this;
	}

	public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
		return this;
	}

	public Object lookup(Name name) throws NamingException {
		return get(name.toString());
	}

	public Object lookup(String name) throws NamingException {
		return get(name);
	}

	public void bind(Name name, Object obj) throws NamingException {
		if (lookup(name) != null)
			throw new NamingException(name + " already bound");
		put(name.toString(), obj);
	}

	public void bind(String name, Object obj) throws NamingException {
		if (lookup(name) != null)
			throw new NamingException(name + " already bound");
		put(name, obj);
	}

	public void rebind(Name name, Object obj) throws NamingException {
		put(name.toString(), obj);
	}

	public void rebind(String name, Object obj) throws NamingException {
		put(name, obj);
	}

	public void unbind(Name name) throws NamingException {
		remove(name.toString());
	}

	public void unbind(String name) throws NamingException {
		remove(name.toString());
	}

	public void rename(Name oldName, Name newName) throws NamingException {
		put(newName.toString(), remove(oldName.toString()));
	}

	public void rename(String oldName, String newName) throws NamingException {
		put(newName, remove(oldName));
	}

	public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	public void destroySubcontext(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	public void destroySubcontext(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	public Context createSubcontext(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	public Context createSubcontext(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	public Object lookupLink(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	public Object lookupLink(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	public NameParser getNameParser(Name name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	public NameParser getNameParser(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

	public Name composeName(Name name, Name prefix) throws NamingException {
		throw new UnsupportedOperationException();
	}

	public String composeName(String name, String prefix) throws NamingException {
		return prefix + "/" + name;
	}

	public Object addToEnvironment(String propName, Object propVal) throws NamingException {
		throw new UnsupportedOperationException();
	}

	public Object removeFromEnvironment(String propName) throws NamingException {
		throw new UnsupportedOperationException();
	}

	public Hashtable<?, ?> getEnvironment() throws NamingException {
		return null;
	}

	public void close() throws NamingException {
	}

	public String getNameInNamespace() throws NamingException {
		throw new UnsupportedOperationException();
	}

}
