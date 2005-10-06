package nz.cri.gns.fred.de;

import java.util.Vector;

public class DataInputException extends Exception {
	
    private static final long serialVersionUID = 20050818L;
    private Vector<String[]> error;
    private Object auxData;
	
	public DataInputException() {
	}
	
   public DataInputException(String field, String msg) {
		super();
		this.error = new Vector<String[]>();
        error.add(new String[] {field, msg});
	}
	
	public DataInputException(Vector<String[]> error) {
        this.error = error;
    }

    public DataInputException(Vector<String[]> error, Object auxiliaryData) {
        this(error);
        this.auxData = auxiliaryData;
    }

    public Vector<String[]> getError() {
		return error;
	}
    
    public boolean hasAuxiliaryData() {
        return auxData != null;
    }
    
    public Object getAuxiliaryData() {
        return auxData;
    }
}
