package nz.cri.gns.fred.hibernate.dao;

/**
 *
 */
public interface CompositeKeyed extends AssignedKeyed {

	public boolean isUnsaved();

	public void setKey(CompositeKey arg1);
	
}
