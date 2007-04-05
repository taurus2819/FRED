package nz.cri.gns.fred.export;

import nz.cri.gns.fred.dao.DAOFactory;

public class Export {

	private static DAOFactory factory;

	private Export() {}
	
	public static void setFactory(DAOFactory factory) {
		Export.factory = factory;
	}
	
	public static DAOFactory getFactory() {
		return factory;
	}
}
