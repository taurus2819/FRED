package nz.cri.gns.fred.importer;

import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Transaction;

import nz.cri.gns.auth.domain.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.Match;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.de.DataInputException;
import nz.cri.gns.fred.hibernate.Age;
import nz.cri.gns.fred.hibernate.Paleontology;
import nz.cri.gns.fred.hibernate.Person;
import nz.cri.gns.fred.hibernate.Stage;
import nz.cri.gns.fred.hibernate.TaxonomicGroup;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.model.*;
import nz.cri.gns.fred.util.*;
import nz.cri.gns.munginator.MgException;
import nz.cri.gns.munginator.Read;
import nz.cri.gns.munginator.SchemaSingleton;
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
    Map<Integer, String> folderNameMatrix; // Map column num  -> Folder name
    Map<Integer, String> localityNameMatrix; // Map column num  -> Locality name.
    Map<Integer, BigDecimal> topDepthMatrix; // Map column num -> top depth
    Map<Integer, BigDecimal> bottomDepthMatrix; // Map column  num -> bottom depth.
    Map<Integer, String> sampleTypeMatrix; // Map column num -> sample type.
    Map<Integer, String> labNameMatrix; // Map column num ->  lab name

    // These are the rows in the spreadsheet.
    private static final int ROW_FOLDER = 1;
    private static final int ROW_LOCALITY = 2;
    private static final int ROW_TOP_DEPTH = 3;
    private static final int ROW_BOTTOM_DEPTH = 4;
    private static final int ROW_SAMPLE_TYPE = 5;

    private static final int ROW_ID_DATE = 6;
    private static final int ROW_DATE_ROUNDING = 7;
    private static final int ROW_IDENTIFIER = 8;
    private static final int ROW_START_STAGE = 9;
    private static final int ROW_START_MOD = 10;
    private static final int ROW_STOP_STAGE = 11;
    private static final int ROW_STOP_MOD = 12;
    private static final int ROW_STAGE_COMMENT = 13;
    private static final int ROW_LAB_NAME = 14;
    private static final int ROW_LAB_CODE = 15;
    private static final int ROW_LAB_NUMBER = 16;
    private static final int ROW_COLLECTION_COMMENTS = 17;
    private static final int ROW_MATRIX_START = 18;

    private static final int COL_TAXON_GROUP = 1;
    private static final int COL_TAXON = 2;
    private static final int COL_DATA_START = 3;

    private final FredDAO fredDAO;
    private final TaxonomicUtil taxonUtil;
    private final PersonUtil personUtil;
    private final RecordUtil recordUtil;
    private final StageUtil stageUtil;
    private final SampleUtil sampleUtil;
    private final FolderUtil folderUtil;
    private final AuditUtil auditUtil;
    private final User user;

    private boolean isRolledBack = false;
    private Transaction transaction = null;

    public PaleoRowProcessor(User user, DAOFactory factory, String code, Map<Integer, Record> paleoMatrix) {
        super(code);
        this.user = user;
        this.paleoMatrix = paleoMatrix;
        this.folderNameMatrix = new HashMap<>();
        this.localityNameMatrix = new HashMap<>();
        this.topDepthMatrix = new HashMap<>();
        this.bottomDepthMatrix = new HashMap<>();
        this.sampleTypeMatrix = new HashMap<>();
        this.labNameMatrix = new HashMap();
        this.fredDAO = factory.getFredDAO();
        this.taxonUtil = new TaxonomicUtil(factory);
        this.personUtil = new PersonUtil(factory);
        this.recordUtil = new RecordUtil(factory);
        this.stageUtil = new StageUtil(factory);
        this.sampleUtil = new SampleUtil(factory);
        this.folderUtil = new FolderUtil(factory);
        this.auditUtil = new AuditUtil(factory);
        this.canVerify = false;
    }

    @Override
    public void initialize(Connection c, Connection errorConn, Integer sheetId, Integer userId, Writer out) throws SQLException, RowImportException {
        try {
            this.transaction = FredHibernate.get().currentSession().beginTransaction();
        } catch (HibernateException x) {
            throw new SQLException("Could not begin transaction", x);
        }
        super.initialize(c, errorConn, sheetId, userId, out);
    }

    /* RowProcessor methods. */
    @Override
    protected void importRow(Row row) throws SQLException, RowImportException {
        int rowNum = row.getRowNum();
        Taxon taxon = null;

        if (rowNum == ROW_SAMPLE_TYPE + 1) {
            for (Integer c : localityNameMatrix.keySet()) {
                findSample(c, row);
            }
        }

        if (rowNum >= ROW_MATRIX_START) {
            taxon = addTaxon(row);
        }

        for (RowValue each : row) {
            // Each paleo is on it's own row.
            // Rows are paleos. Columns are paleo list entries.
            RowSingleValue v = (RowSingleValue) each;
            if (null == v || v.isEmpty()) {
                continue;
            }
            Paleontology paleo = null;
            if (rowNum > ROW_SAMPLE_TYPE && each.getColumnNum() >= COL_DATA_START) {
                paleo = getPaleo(v.getColumnNum());
                if (null == paleo) {
                    throw new RowImportException(row, v, "The locality wasn't defined back in row " + Integer.toString(ROW_LOCALITY + 1) + " of column " + XLSXSpreadsheet.columnNumToLetters(each.getColumnNum()));
                }
            }

            if (each.getColumnNum() >= COL_DATA_START) {
                if (rowNum < ROW_MATRIX_START) {
                    switch (rowNum) {
                        case ROW_FOLDER:
                            if (v.isEmpty()) {
                                throw new RowImportException(row, v, "You need to choose a folder.");
                            }
                            folderNameMatrix.put(v.getColumnNum(), v.getValueString());
                            break;
                        case ROW_LOCALITY:
                            if (v.isEmpty()) {
                                throw new RowImportException(row, v, "The locality is missing here.");
                            }
                            localityNameMatrix.put(v.getColumnNum(), v.getValueString());
                            break;
                        case ROW_TOP_DEPTH:
                            if (!v.isEmpty()) {
                                if (null == v.getValueNumber()) {
                                    throw new RowImportException("Top depth needs to be a decimal point number.");
                                }
                                topDepthMatrix.put(v.getColumnNum(), v.getValueNumber());
                            }
                            break;
                        case ROW_BOTTOM_DEPTH:
                            if (!v.isEmpty()) {
                                if (null == v.getValueNumber()) {
                                    throw new RowImportException("Bottom depth needs to be a decimal point number.");
                                }
                                bottomDepthMatrix.put(v.getColumnNum(), v.getValueNumber());
                            }
                            break;
                        case ROW_SAMPLE_TYPE:
                            if (!v.isEmpty()) {
                                sampleTypeMatrix.put(v.getColumnNum(), v.getValueString());
                            }
                            break;
                        case ROW_ID_DATE:
                            paleo.setIdentificationDate(v.getValueTimestamp());
                            log("Setting date: " + v.getValueString());
                            break;
                        case ROW_DATE_ROUNDING:
                            paleo.setDateRounding(v.getValueString());
                            log("Setting date rounding: " + v.getValueString());
                            break;
                        case ROW_IDENTIFIER:
                            setIdentifiers(paleo, row, v);
                            break;
                        case ROW_START_STAGE:
                            setStartStage(paleo, row, v);
                            break;
                        case ROW_START_MOD:
                            getStage(paleo).setStageLowerMod(v.getValueString());
                            break;
                        case ROW_STOP_STAGE:
                            setStopStage(paleo, row, v);
                            break;
                        case ROW_STOP_MOD:
                            getStage(paleo).setStageUpperMod(v.getValueString());
                            break;
                        case ROW_STAGE_COMMENT:
                            paleo.setStageComments(v.getValueString());
                            log("Setting stage comments: " + v.getValueString());
                            break;
                        case ROW_LAB_NAME:
                            labNameMatrix.put(v.getColumnNum(), v.getValueString());
                            break;
                        case ROW_LAB_CODE:
                            setLabSection(paleo, row, v);
                            break;
                        case ROW_LAB_NUMBER:
                            paleo.setLabNumber(v.getValueString());
                            log("Setting lab number: " + v.getValueString());
                            break;
                        case ROW_COLLECTION_COMMENTS:
                            paleo.setCollectionComments(v.getValueString());
                            log("Setting collection comments: " + v.getValueString());
                            break;
                        default:
                            throw new MgException();
                    }
                } else {
                    // Import the pal list entries.
                    String p = v.getValueString();
                    addPaleoListEntry(paleo, taxon, p);
                }
            }
        }
    }

    /**
     * Prevent NullPointerException.
     */
    private nz.cri.gns.fred.model.Stage getStage(Paleontology p) {
        if (null == p.getStage()) {
            p.setStage(new Stage());
        }
        return p.getStage();
    }

    @Override
    public void rollback() throws SQLException {
        this.isRolledBack = true;
        try {
            transaction.rollback();
        } catch (HibernateException x) {
            throw new SQLException("Could not commit transaction", x);
        }
    }

    @Override
    public void commit() throws SQLException {
        if (!isRolledBack) {
            for (Record each : paleoMatrix.values()) {
                try {
                    fredDAO.save(each);
                } catch (StorageAccessException ex) {
                    throw new MgException(ex);
                }
            }
            try {
                transaction.commit();
            } catch (HibernateException x) {
                throw new SQLException("Could not commit transaction", x);
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
            r = recordUtil.createRecord(sample, RecordUtil.PALEONTOLOGICAL, folderId, user);

            // PDB-259
            r.getAudit().setDataOrigin(this.auditUtil.getDataOrigin(new Integer(FREDConstants.DATA_ORIGIN_EXCEL)));
        } catch (StorageAccessException ex) {
            throw new MgException(ex);
        }
        paleoMatrix.put(index, r);

        // PDB-221
        r.getPaleontology().setStage(null);

        return r;
    }

    private Paleontology getPaleo(int index) {
        if (!paleoMatrix.containsKey(index)) {
            return null;
        }
        return (Paleontology) paleoMatrix.get(index).getPaleontology();
    }

    private Taxon addTaxon(Row row) throws RowImportException {
        TaxonomicGroup taxonGroup = null;

        if (!row.hasValue(COL_TAXON_GROUP)) {
            throw new RowImportException(row, null, "No taxon group on this row.");
        }
        String tgStr = row.getValue(COL_TAXON_GROUP).getValueString();
        if (null == tgStr) {
            throw new RowImportException(row, null, "No taxon group on this row.");
        }
        try {
            taxonGroup = (TaxonomicGroup) taxonUtil.getTaxonomicGroup(tgStr);
        } catch (StorageAccessException ex) {
            throw new RowImportException(row, "TAXON_GROUP", "Error occurred while looking for this taxon group", ex);
        }
        if (null == taxonGroup) {
            throw new RowImportException(row, "TAXON_GROUP", "Could not find the taxon group " + tgStr, null);
        }

        List<Taxon> txs;

        if (!row.hasValue(COL_TAXON)) {
            throw new RowImportException(row, null, "No taxon on this row.");
        }
        String txStr = row.getValue(COL_TAXON).getValueString();
        if (null == txStr) {
            throw new RowImportException(row, null, "No taxon on this row.");
        }
        try {
            txStr = TaxonomicUtil.normaliseTaxonomicName(txStr);
        } catch (DataInputException x) {
            throw new RowImportException(row, null, "Unexpected error when normalising taxonomic name.");
        }
        try {
            txs = taxonUtil.getMatchingTaxa(txStr, taxonGroup, Match.EXACT, 1);

        } catch (StorageAccessException ex) {
            throw new RowImportException(row, "TAXON", "Can not find this Taxonomy", ex);
        }
        Taxon tx;
        if (null == txs || txs.isEmpty()) {
            warn("Cannot find the taxon " + txStr + ". Assuming it is a new one.");
            tx = taxonUtil.createTaxon();
            tx.setTaxonomicGroup(taxonGroup);
            tx.setStatus(FREDConstants.PROVISIONAL);
            tx.setTaxonomicName(txStr);
            try {
                fredDAO.save(tx);
            } catch (StorageAccessException e) {
                throw new RowImportException(row, txStr, "Could not create this taxon because:", e);
            }
        } else {
            tx = txs.get(0);
        }
        return tx;
    }

    /**
     * Add a paleo list entry. The format of newEntry is:
     * <dl>
     * <dt>null or empty</dt><dd>no presence.</dd>
     * <dt>"*"</dt><dd>simple presence</dd>
     * <dt>A number</dt><dd>a count</dd>
     * <dt>"aff." or "cf."</dt><dd>all the specimens found in that sample are
     * qualified</dd>
     * <dt>any text</dt><dd>Simple occurrence, sames as "*". The text will be
     * used as a comment.</dd>
     * <dt>A number+"|"+Comment</dt><dd>A count, then a comment.</dd>
     * </dl>
     *
     * @param p
     * @param newEntry
     */
    private void addPaleoListEntry(Paleontology p, Taxon taxon, String newEntry) throws RowImportException {
        if (null == newEntry || newEntry.trim().isEmpty()) {
            return;
        }

        Integer count = null;
        String coords = null;
        String comments = "";

        if (!newEntry.contains("|")) {
            try {
                count = Integer.parseInt(newEntry);
            } catch (NumberFormatException e) {
                if (!"*".equals(newEntry)) {
                    comments = newEntry;
                }
            }
        } else {
            // We add a space to make sure we capture any last '|'.
            String[] parts = (newEntry + " ").split("\\|");
            try {
                if (null != parts[0] && !parts[0].isEmpty()) {
                    count = Integer.parseInt(parts[0]);
                }
            } catch (NumberFormatException e) {
                if (!"*".equals(parts[0])) {
                    comments = parts[0] + "|";
                }
            }
            if (parts.length == 2) {
                comments = comments + parts[1].trim();
            } else {
                if (parts.length == 3) {
                    coords = parts[1].trim();
                    if (coords.isEmpty()) {
                        coords = null;
                    }
                    comments = comments + parts[2].trim();
                } else {
                    comments = newEntry; // At the very least, don't lose data.
                }
            }
        }

        PaleontologyListEntry result = fredDAO.createNewPaleontologyListEntry();
        result.setSpecimenCount(count);
        result.setSpecimenCoords(coords);
        if (null == comments || comments.isEmpty()) {
            result.setComments(null);
        } else {
            result.setComments(comments);
        }
        result.setTaxonomicGroup(taxon.getTaxonomicGroup());
        result.setTaxon(taxon);
        result.setTaxonomicName(taxon.getTaxonomicName());
        result.setPaleontology(p);
        p.getListEntries().add(result);
        log("Made a new pal_list entry. Group: " + taxon.getTaxonomicGroup().getDisplayName() + " Taxon: " + taxon.getTaxonomicName() + " Count: " + count + " Coords: " + coords + " Comments: " + comments);
    }

    private void findSample(Integer columnNum, Row row) throws RowImportException {
        Sample sample;

        /**
         * This doesn't work. It expects a weird format for the sample that
         * includes depths, etc. try { sample = (Sample)
         * sampleUtil.findSample(localityName); } catch (StorageAccessException
         * ex) { throw new RowImportException(row, v, "Error occurred while
         * trying to look up this locality.", ex); } if (null == sample) { throw
         * new RowImportException(row, v, "Could not find a locality with this
         * name."); }
         */
        // Screw it. I'll do it manually.
        String localityName = localityNameMatrix.get(columnNum);
        Read r = null;
        try {
            SchemaSingleton schema = SchemaSingleton.getInstance(importConn);
            r = schema.select("SAMPLE");
            r.addColumn("SAMPLE_ID");
            r.addWhere("FEATURE_ID$FR_ID$FR_NUMBER", localityName);

            BigDecimal topDepth = null;
            if (topDepthMatrix.containsKey(columnNum)) {
                topDepth = topDepthMatrix.get(columnNum);
                r.addWhere("TOP_DEPTH", topDepth);
            }
            BigDecimal bottomDepth = null;
            if (bottomDepthMatrix.containsKey(columnNum)) {
                bottomDepth = bottomDepthMatrix.get(columnNum);
                r.addWhere("BOTTOM_DEPTH", bottomDepth);
            }
            String drillType = null;
            if (sampleTypeMatrix.containsKey(columnNum)) {
                drillType = sampleTypeMatrix.get(columnNum);
                r.addWhere("DRILL_TYPE_ID$NAME", drillType);
            }

            r.doIt(importConn);
            if (!r.next()) {
                log("If you're an expert, this is the SQL: " + r.toString());
                throw new RowImportException(row, (RowValue) null, "Could not find a sample with the FR number='" + localityName + "', topDepth=" + topDepth + ", bottomDepth=" + bottomDepth + ", drillType=" + drillType);
            }

            Integer sampleId = r.getInteger("SAMPLE_ID");
            log("Found a sample with the FR number='" + localityName + "', topDepth=" + topDepth + ", bottomDepth=" + bottomDepth + ", drillType=" + drillType + " for column " + XLSXSpreadsheet.columnNumToLetters(columnNum));

            if (r.next()) {
                throw new RowImportException(row, (RowValue) null, "Found multiple samples with this locality, depths and sample type.");
            }

            sample = sampleUtil.getSample(sampleId);
        } catch (SQLException | StorageAccessException e) {
            throw new RowImportException(row, (RowValue) null, null, e);
        } finally {
            if (null != r) {
                r.close();
            }
        }

        UserFolder folder;
        try {
            String folderName = folderNameMatrix.get(columnNum);
            folder = folderUtil
                    .getPersonalFolders(user)
                    .stream()
                    .filter(each -> each.getFolderName().equals(folderName))
                    .findFirst()
                    .orElseThrow(() -> new RowImportException(row, (RowValue) null, "Cannot find this folder."));
        } catch (StorageAccessException ex) {
            throw new RowImportException(row, (RowValue) null, "Can't get a folder because: " + ex.getMessage(), ex);
        }

        createRecord(columnNum, sample, folder.getFolder().getFolderId(), user);
    }

    private void setIdentifiers(Paleontology paleo, Row row, RowSingleValue v) throws RowImportException {
        Person p;
        String personName = v.getValueString();
        try {
            p = (Person) personUtil.findPerson(personName);
        } catch (StorageAccessException ex) {
            throw new RowImportException(row, v, "Could not find this person", ex);
        }
        if (null == p) {
            warn("Could not find this person: " + personName);
            return;
        }

        log("Found person: " + p.getDisplayName());
        paleo.getIdentifiers().add(p);
    }

    private void setStartStage(Paleontology paleo, Row row, RowSingleValue v) throws RowImportException {
        if (!v.isEmpty()) {
            String stageName = v.getValueString();
            Stage s = (Stage) getStage(paleo);
            try {
                Age a = (Age) stageUtil.getCurrentAgeByName(stageName);
                if (a == null) {
                    throw new RowImportException(row, v, "Misspelled, obsolete, or unknown age.");
                }
                s.setLowerAge(a);
                log("Setting lower stage: " + a.getDisplayName());
            } catch (StorageAccessException ex) {
                throw new RowImportException(row, v, "Something went wrong trying to verify this age.", ex);
            }
        }
    }

    private void setStopStage(Paleontology paleo, Row row, RowSingleValue v) throws RowImportException {
        if (!v.isEmpty()) {
            String stageName = v.getValueString();

            Stage s = (Stage) getStage(paleo);
            try {
                Age a = (Age) stageUtil.getCurrentAgeByName(stageName);
                if (a == null) {
                    throw new RowImportException(row, v, "Misspelled, obsolete, or unknown age.");
                }
                s.setUpperAge(a);
                log("Setting upper stage: " + a.getDisplayName());
            } catch (StorageAccessException ex) {
                throw new RowImportException(row, v, "Something went wrong trying to verify this age.", ex);
            }
        }
    }

    private void setLabSection(Paleontology paleo, Row row, RowSingleValue v) throws RowImportException {
        if (!v.isEmpty()) {
            LabSection ls;

            ls = fredDAO.getLabSection(labNameMatrix.get(v.getColumnNum()), v.getValueString());
            log("Setting lab section: " + ls);
            if (!v.isEmpty() && null == ls) {
                throw new RowImportException(row, v, "Failed to find this laboratory.");
            }
            paleo.setLabSection(ls);
        }
    }

    @Override
    public boolean rowIsMultipleValue(Row row) {
        return false;
    }
}
