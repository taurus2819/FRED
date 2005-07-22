package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;

/** @author Hibernate CodeGenerator */
public class FrNumber implements Serializable, nz.cri.gns.fred.model.FrNumber {

    /** identifier field */
    private Integer frId;

    /** persistent field */
    private String mapSheet;

    /** persistent field */
    private Integer serialNumber;

    /** nullable persistent field */
    private String recollectionNumber;

    /** nullable persistent field */
    private String frnumComments;

    /** nullable persistent field */
    private String frNumber;

    /** persistent field */
    private Set samples;

    /** full constructor */
    public FrNumber(String mapSheet, Integer serialNumber, String recollectionNumber, String frnumComments, String frNumber, Set samples) {
        this.mapSheet = mapSheet;
        this.serialNumber = serialNumber;
        this.recollectionNumber = recollectionNumber;
        this.frnumComments = frnumComments;
        this.frNumber = frNumber;
        this.samples = samples;
    }

    /** default constructor */
    public FrNumber() {
    }

    /** minimal constructor */
    public FrNumber(String mapSheet, Integer serialNumber, Set samples) {
        this.mapSheet = mapSheet;
        this.serialNumber = serialNumber;
        this.samples = samples;
    }

    public Integer getFrId() {
        return this.frId;
    }

    public void setFrId(Integer frId) {
        this.frId = frId;
    }

    public String getMapSheet() {
        return this.mapSheet;
    }

    public void setMapSheet(String mapSheet) {
        this.mapSheet = mapSheet;
    }

    public Integer getSerialNumber() {
        return this.serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getRecollectionNumber() {
        return this.recollectionNumber;
    }

    public void setRecollectionNumber(String recollectionNumber) {
        this.recollectionNumber = recollectionNumber;
    }

    public String getFrnumComments() {
        return this.frnumComments;
    }

    public void setFrnumComments(String frnumComments) {
        this.frnumComments = frnumComments;
    }

    public String getFrNumber() {
        return this.frNumber;
    }

    public void setFrNumber(String frNumber) {
        this.frNumber = frNumber;
    }

    public Set getSamples() {
        return this.samples;
    }

    public void setSamples(Set samples) {
        this.samples = samples;
    }
}
