package nz.cri.gns.fred.reports;

import java.lang.reflect.Method;
import java.sql.Connection;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class UnclosableConnection implements MethodInterceptor {

	private Connection conn;

	public UnclosableConnection(Connection conn) {
		this.conn = conn;
	}

	public static Connection create(Connection conn) {
		UnclosableConnection connection = new UnclosableConnection(conn);
		Enhancer e = new Enhancer();
		e.setInterfaces(new Class[] {Connection.class});
		e.setCallback(connection);
		return (Connection)e.create();
	}

	public Object intercept(Object arg0, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (method.getName().equals("close"))
			return null;
		return proxy.invoke(conn, args);
			
	}


}
