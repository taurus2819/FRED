package nz.cri.gns.fred.de;

import java.util.ArrayList;
import java.util.Vector;

public class DataInputException extends Exception {

    private static final long serialVersionUID = 20050818L;
    private ArrayList<String[]> error;
    private Object auxData;

    public DataInputException() {
    }

    public DataInputException(String field, String msg) {
        super();
        this.error = new ArrayList<>();
        error.add(new String[]{field, msg});
    }

    public DataInputException(ArrayList<String[]> error) {
        this.error = error;
    }

    public DataInputException(ArrayList<String[]> error, Object auxiliaryData) {
        this(error);
        this.auxData = auxiliaryData;
    }

    public ArrayList<String[]> getError() {
        return error;
    }

    public boolean hasAuxiliaryData() {
        return auxData != null;
    }

    public Object getAuxiliaryData() {
        return auxData;
    }

    @Override
    public String getMessage() {
        if (error.size() > 0) {
            return (error.get(0))[1];
        }
        return super.getMessage();
    }
}
