package nz.cri.gns.fred.importer;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import nz.cri.gns.auth.domain.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.Match;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.hibernate.Age;
import nz.cri.gns.fred.hibernate.LabSection;
import nz.cri.gns.fred.hibernate.Paleontology;
import nz.cri.gns.fred.hibernate.Person;
import nz.cri.gns.fred.hibernate.Record;
import nz.cri.gns.fred.hibernate.Sample;
import nz.cri.gns.fred.hibernate.Stage;
import nz.cri.gns.fred.hibernate.TaxonomicGroup;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.model.UserFolder;
import nz.cri.gns.fred.util.FolderUtil;
import nz.cri.gns.fred.util.PersonUtil;
import nz.cri.gns.fred.util.RecordUtil;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.fred.util.StageUtil;
import nz.cri.gns.fred.util.TaxonomicUtil;
import nz.cri.gns.munginator.MgException;
import nz.cri.gns.munginator.export.XLSXSpreadsheet;
import nz.cri.gns.munginator.upload.RowImportException;
import nz.cri.gns.munginator.upload.RowProcessor;
import nz.cri.gns.munginator.upload.stagingarea.Row;
import nz.cri.gns.munginator.upload.stagingarea.RowSingleValue;
import nz.cri.gns.munginator.upload.stagingarea.RowValue;

/**
 * I am both a RowProcessor and CustomExportSpreadsheetHandler for Paleo sheets.
 * This is only because the first 60 lines of both were identical and the code
 * kind of belongs together.
 *
 * @author mikevdg
 */
public class PaleoRowProcessor extends RowProcessor {

    Map<Integer, Record> paleoMatrix; // Map column num -> Record.

    // These are the rows in the spreadsheet.
    private static final int ROW_LOCALITY = 1;
    private static final int ROW_ID_DATE = 2;
    private static final int ROW_DATE_ROUNDING = 3;
    private static final int ROW_IDENTIFIER = 4;
    private static final int ROW_START_STAGE = 5;
    private static final int ROW_START_MOD = 6;
    private static final int ROW_STOP_STAGE = 7;
    private static final int ROW_STOP_MOD = 8;
    private static final int ROW_STAGE_COMMENT = 9;
    private static final int ROW_LABORATORY = 10;
    private static final int ROW_LAB_NUMBER = 11;
    private static final int ROW_COLLECTION_COMMENTS = 12;
    private static final int ROW_MATRIX_START = 13;
    private final FredDAO fredDAO;
    private final TaxonomicUtil taxonUtil;
    private final PersonUtil personUtil;
    private final RecordUtil recordUtil;
    private final StageUtil stageUtil;
    private final SampleUtil sampleUtil;
    private final FolderUtil folderUtil;
    private final User user;

    public PaleoRowProcessor(User user, DAOFactory factory, String code, Map<Integer, Record> paleoMatrix) {
        super(code);
        this.user = user;
        this.paleoMatrix = paleoMatrix;
        this.fredDAO = factory.getFredDAO();
        this.taxonUtil = new TaxonomicUtil(factory);
        this.personUtil = new PersonUtil(factory);
        this.recordUtil = new RecordUtil(factory);
        this.stageUtil = new StageUtil(factory);
        this.sampleUtil = new SampleUtil(factory);
        this.folderUtil = new FolderUtil(factory);
    }

    /* RowProcessor methods. */
    @Override
    protected void importRow(Row row) throws SQLException, RowImportException {
        int rowNum = row.getRowNum();
        for (RowValue each : row) {
            // Each paleo is on it's own row.
            // Rows are paleos. Columns are paleo list entries.
            RowSingleValue v = (RowSingleValue) each;
            if (null==v || v.isEmpty()) {
                continue;
            }
            Paleontology paleo=null;
            if (rowNum > ROW_LOCALITY && each.getColumnNum()>=2) {
                paleo = getPaleo(v.getColumnNum());
                if (null==paleo) {
                    throw new RowImportException(row, v, "The locality wasn't defined back in row "+Integer.toString(ROW_LOCALITY)+" of column "+XLSXSpreadsheet.columnNumToLetters(each.getColumnNum()));
                }
            } 
            
            if (each.getColumnNum() >= 2) {
                if (rowNum < ROW_MATRIX_START) {
                    switch (rowNum) {
                        case ROW_LOCALITY:
                            setRowLocality(row, each);
                            break;
                        case ROW_ID_DATE:
                            paleo.setIdentificationDate(v.getValueTimestamp());
                            log("Setting date: "+v.getValueString());
                            break;
                        case ROW_DATE_ROUNDING:
                            paleo.setDateRounding(v.getValueString());
                            log("Setting date rounding: "+v.getValueString());
                            break;
                        case ROW_IDENTIFIER:
                            setIdentifiers(paleo, row, v);
                            break;
                        case ROW_START_STAGE:
                            setStartStage(paleo, row, v);
                            break;
                        case ROW_START_MOD:
                            paleo.getStage().setStageLowerMod(v.getValueString());
                            break;
                        case ROW_STOP_STAGE:
                            setStopStage(paleo, row, v);
                            break;
                        case ROW_STOP_MOD:
                            paleo.getStage().setStageUpperMod(v.getValueString());
                            break;
                        case ROW_STAGE_COMMENT:
                            paleo.setStageComments(v.getValueString());
                            log("Setting stage comments: "+v.getValueString());
                            break;
                        case ROW_LABORATORY:
                            setLabSection(paleo, row, v);
                            break;
                        case ROW_LAB_NUMBER:
                            paleo.setLabNumber(v.getValueString());
                            log("Setting lab number:" +v.getValueString());
                            break;
                        case ROW_COLLECTION_COMMENTS:
                            paleo.setCollectionComments(v.getValueString());
                            log("Setting collection comments:"+v.getValueString());
                            break;
                        default:
                            throw new MgException();
                    }
                } else {
                    // Import the pal list entries.
                    String p = v.getValueString();
                    addPaleoListEntry(paleo, row, p);
                }
            } 
        }
    }

    @Override
    public void close() {
        for (Record each : paleoMatrix.values()) {
            try {
                fredDAO.save(each);
            } catch (StorageAccessException ex) {
                throw new MgException(ex);
            }
        }
    }

    /**
     * Create the record, add it to the matrix.
     */
    private Record createRecord(int index, Sample sample, Integer folderId, User user) {
        if (paleoMatrix.containsKey(index)) {
            throw new MgException("The programmer made a mistake. This row already has a record.");
        }
        Record r;
        try {
            r = (Record) recordUtil.createRecord(sample, RecordUtil.PALEONTOLOGICAL, folderId, user);
        } catch (StorageAccessException ex) {
            throw new MgException(ex);
        }
        paleoMatrix.put(index, r);
        return r;
    }

    private Paleontology getPaleo(int index) {
        if (!paleoMatrix.containsKey(index)) {
            return null;
        }
        return (Paleontology) paleoMatrix.get(index).getPaleontology();
    }

    /**
     * Add a paleo list entry. The format of newEntry is:
     * <dl>
     * <dt>null or empty</dt><dd>no presence.</dd>
     * <dt>"*"</dt><dd>simple presence</dd>
     * <dt>A number</dt><dd>a count</dd>
     * <dt>"aff." or "cf."</dt><dd>all the specimens found in that sample are
     * qualified</dd>
     * <dt>any text</dt><dd>Simple occurence, sames as "*". The text will be
     * used as a comment.</dd>
     * <dt>A number+"|"+Comment</dt><dd>A count, then a comment.</dd>
     * </dl>
     *
     * @param p
     * @param newEntry
     */
    private void addPaleoListEntry(Paleontology p, Row row, String newEntry) throws RowImportException {
        if (null == newEntry || newEntry.trim().isEmpty()) {
            return;
        }

        Integer count = null;
        String coords = null;
        String comments = null;
        TaxonomicGroup taxonGroup = null;

        if (!newEntry.contains("|")) {
            try {
                count = Integer.parseInt(newEntry);
            } catch (NumberFormatException e) {
                comments = newEntry;
            }
        } else {
            String[] parts = newEntry.split("|");
            try {
                count = Integer.parseInt(newEntry);
            } catch (NumberFormatException e) {
            }
            if (parts.length == 2) {
                comments = parts[1].trim();
                if (comments.isEmpty()) {
                    comments = null;
                }
            } else {
                if (parts.length == 3) {
                    coords = parts[1].trim();
                    if (coords.isEmpty()) {
                        coords = null;
                    }
                    comments = parts[2].trim();
                    if (comments.isEmpty()) {
                        comments = null;
                    }
                }
            }
        }

        if (!row.hasValue(0)) {
            throw new RowImportException(row, null, "No taxon group in column A on this row.");
        }
        String tgStr = row.getValue(0).getValueString();
        if (null == tgStr) {
            throw new RowImportException(row, null, "No taxon group in column A on this row.");
        }
        try {
            taxonGroup = (TaxonomicGroup) taxonUtil.getTaxonomicGroup(tgStr);
        } catch (StorageAccessException ex) {
            throw new RowImportException(row, "TAXON_GROUP", "Error occurred while looking for this taxon group in column A.", ex);
        }
        if (null==taxonGroup) {
            throw new RowImportException(row, "TAXON_GROUP", "Could not find the taxon group "+tgStr+" in column A", null);
        }
        
        List<Taxon> txs;

        String txStr = row.getValue(1).getValueString();
        if (null == txStr) {
            throw new RowImportException(row, null, "No taxon on this row.");
        }
        try {
            txs = taxonUtil.getMatchingTaxa(txStr, taxonGroup, Match.EXACT, 1);

        } catch (StorageAccessException ex) {
            throw new RowImportException(row, "TAXON", "Can not find this Taxonomy", ex);
        }
        Taxon tx;
        if (null == txs || txs.isEmpty()) {
            warn("Cannot find this taxon. Assuming it is a new one.");
            tx = taxonUtil.createTaxon();
            tx.setTaxonomicGroup(taxonGroup);
            tx.setTaxonomicName(txStr);
        } else {
            tx = txs.get(0);
        }

        PaleontologyListEntry result = fredDAO.createNewPaleontologyListEntry();
        result.setSpecimenCount(count);
        result.setSpecimenCoords(coords);
        result.setComments(comments);
        result.setTaxonomicGroup(taxonGroup);
        result.setTaxon(tx);
        result.setTaxonomicName(txStr);
        result.setPaleontology(p);
        
        
        log("Made a new pal_list entry. Group:"+taxonGroup.getDisplayName()+" Taxon:"+txStr+" Count:"+count+" Coords:"+coords+" Comments:"+comments);
    }

    private void setRowLocality(Row row, RowValue v) throws RowImportException {
        // This is the first row, so we initialize stuff.
        if (null == v || v.isEmpty()) {
            throw new RowImportException(row, v, "Locality is empty in row "+row.getRowNum()+"column "+XLSXSpreadsheet.columnNumToLetters(v.getColumnNum()));
        }
        String localityName = ((RowSingleValue) v).getValueString();
        Sample sample;
        try {
            sample = (Sample) sampleUtil.findSample(localityName);
        } catch (StorageAccessException ex) {
            throw new RowImportException(row, v, "Error occurred while trying to look up this locality.", ex);
        }
        if (null == sample) {
            throw new RowImportException(row, v, "Could not find a locality with this name.");
        }
        log("Found locality: "+localityName+" for column "+XLSXSpreadsheet.columnNumToLetters(v.getColumnNum()));
        
        UserFolder folder;
        try {
            folder = folderUtil.getPersonalFolders(user).get(0);
        } catch (StorageAccessException ex) {
            throw new RowImportException(row, v, "Can't get a folder.", ex);
        }
        warn("TODO: I don't know which folder to use. Putting everything in " + folder.getFolder().getName());

        createRecord(v.getColumnNum(), sample, folder.getFolder().getFolderId(), user);
    }

    private void setIdentifiers(Paleontology paleo, Row row, RowSingleValue v) throws RowImportException {
        Person p;
        String personName = v.getValueString();
        try {
            p = (Person) personUtil.findPerson(personName);
        } catch (StorageAccessException ex) {
            throw new RowImportException(row, v, "Could not find this person", ex);
        }
        if (null==p) {
            warn("Could not find this person:"+personName);
            return;
        }
        
        log("Found person: "+p.getDisplayName());
        paleo.getIdentifiers().add(p);
    }

    private void setStartStage(Paleontology paleo, Row row, RowSingleValue v) throws RowImportException {
        if (!v.isEmpty()) {
            String stageName = v.getValueString();
            Stage s = (Stage) paleo.getStage();
            try {
                Age a = (Age) stageUtil.getAgeByName(stageName);
                s.setLowerAge(a);
                log("Setting lower stage: "+a.getDisplayName());
            } catch (StorageAccessException ex) {
                throw new RowImportException(row, v, "Could not find this age.", ex);
            }
        }
    }

    private void setStopStage(Paleontology paleo, Row row, RowSingleValue v) throws RowImportException {
        if (!v.isEmpty()) {
            String stageName = v.getValueString();

            Stage s = (Stage) paleo.getStage();
            try {
                Age a = (Age) stageUtil.getAgeByName(stageName);
                s.setUpperAge(a);
                log("Setting upper stage: "+a.getDisplayName());
            } catch (StorageAccessException ex) {
                throw new RowImportException(row, v, "Could not find this age.", ex);
            }

        }
    }

    private void setLabSection(Paleontology paleo, Row row, RowSingleValue v) throws RowImportException {
        if (!v.isEmpty()) {
            LabSection labSection;
            try {
                labSection = (LabSection) recordUtil.getLabSectionByName(v.getValueString());
                log("Setting lab section: "+labSection);
            } catch (StorageAccessException ex) {
                throw new RowImportException(row, v, "Can't find that lab section.", ex);
            }
            paleo.setLabSection(labSection);
        }
    }

    @Override
    public boolean rowIsMultipleValue(Row row) {
        return false;
    }
}
