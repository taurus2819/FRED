package nz.cri.gns.fred.dataentry;

public interface DataEntryForm {

	public abstract void setField(int field, String value);
	
	public abstract void parseField(int field) throws DataInputException;
	
	public abstract boolean saveData();
	
	public abstract boolean submitData();

}
