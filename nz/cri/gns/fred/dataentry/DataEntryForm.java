package nz.cri.gns.fred.dataentry;

public interface DataEntryForm {

	public abstract void setField(int field, String value) throws DataInputException;
		
	public abstract boolean saveData();
	
	public abstract boolean submitData();

}
