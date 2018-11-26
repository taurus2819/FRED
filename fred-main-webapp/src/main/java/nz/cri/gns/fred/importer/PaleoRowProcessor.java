package nz.cri.gns.fred.importer;

import java.sql.SQLException;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.model.LabSection;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Stage;
import nz.cri.gns.fred.model.Taxon;
import nz.cri.gns.fred.model.TaxonomicGroup;
import nz.cri.gns.fred.util.PersonUtil;
import nz.cri.gns.fred.util.RecordUtil;
import nz.cri.gns.fred.util.StageUtil;
import nz.cri.gns.fred.util.TaxonomicUtil;
import nz.cri.gns.munginator.MgException;
import nz.cri.gns.munginator.upload.RowImportException;
import nz.cri.gns.munginator.upload.RowProcessor;
import nz.cri.gns.munginator.upload.stagingarea.Row;
import nz.cri.gns.munginator.upload.stagingarea.RowSingleValue;
import nz.cri.gns.munginator.upload.stagingarea.RowValue;

public class PaleoRowProcessor extends RowProcessor {

    PaleoRowProcessorMatrix paleoMatrix;

    // These are the rows in the spreadsheet.
    private static final int ROW_LOCALITY = 0;
    private static final int ROW_ID_DATE = 1;
    private static final int ROW_DATE_ROUNDING = 2;
    private static final int ROW_IDENTIFIER = 3;
    private static final int ROW_START_STAGE = 4;
    private static final int ROW_START_MOD = 5;
    private static final int ROW_STOP_STAGE = 6;
    private static final int ROW_STOP_MOD = 7;
    private static final int ROW_STAGE_COMMENT = 8;
    private static final int ROW_LABORATORY = 9;
    private static final int ROW_LAB_NUMBER = 10;
    private static final int ROW_COLLECTION_COMMENTS = 11;
    private static final int ROW_MATRIX_START = 12;
    private final FredDAO fredDAO;
    private final TaxonomicUtil taxonUtil;
    private final PersonUtil personUtil;
    private final RecordUtil recordUtil;
    private final StageUtil stageUtil;

    PaleoRowProcessor(User user, DAOFactory factory, String code, PaleoRowProcessorMatrix paleoMatrix) {
        super(code);
        this.paleoMatrix = paleoMatrix;
        this.fredDAO = factory.getFredDAO();
        this.taxonUtil = new TaxonomicUtil(factory);
        this.personUtil = new PersonUtil(factory);
        this.recordUtil = new RecordUtil(factory);
        this.stageUtil = new StageUtil(factory);
    }

    @Override
    protected void importRow(Row row) throws SQLException, RowImportException {
        int rowNum = row.getRowNum();
        for (RowValue each : row) {
            // Each paleo is on it's own row.
            // Rows are paleos. Columns are paleo list entries.
            RowSingleValue v = (RowSingleValue) each;
            Paleontology paleo = getPaleo(v.getColumnNum());

            if (rowNum < ROW_MATRIX_START) {
                switch (rowNum) {
                    case ROW_LOCALITY:
                        throw new RowImportException(row, each, "Not implemented");
                        
                    case ROW_ID_DATE:
                        paleo.setIdentificationDate(v.getValueTimestamp());
                        break;
                    case ROW_DATE_ROUNDING:
                        paleo.setDateRounding(v.getValueString());
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
                        break;
                    case ROW_LABORATORY:
                        setLabSection(paleo, row, v);
                        break;
                    case ROW_LAB_NUMBER:
                        paleo.setLabNumber(v.getValueString());
                        break;
                    case ROW_COLLECTION_COMMENTS:
                        paleo.setCollectionComments(v.getValueString());
                        break;
                    default:
                        throw new MgException("The programmer did something wrong.");
                }
            } else {
                // Import the pal list entries.
                String p = v.getValueString();
                addPaleoListEntry(paleo, row, p);
            }
        }
    }

    @Override
    public void close() {
        paleoMatrix.close();
    }

    private Paleontology getPaleo(int index) {
        if (null == paleoMatrix.get(index)) {
            Paleontology r = fredDAO.createNewPaleontology();
            paleoMatrix.put(index, r);
            return r;
        } else {
            return paleoMatrix.get(index);
        }
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

        Integer tgId = idFromName(row, "TAXON_GROUP");
        try {
            taxonGroup = taxonUtil.getTaxonomicGroup(tgId);
        } catch (StorageAccessException ex) {
            throw new RowImportException(row, "TAXON_GROUP", "Can not find this Taxonomic Group by ID", ex);
        }

        Taxon tx;
        String txName;
        String txStr = getRowValueNotNull(row, "TAXON").getValueString();
        if (txStr.indexOf("/") > 0) {
            Integer txId = idFromName(row, "TAXON");
            txName = nameFromName(row, "TAXON");
            try {
                tx = taxonUtil.getTaxon(txId);
            } catch (StorageAccessException ex) {
                throw new RowImportException(row, "TAXON", "Can not find this Taxonomy by ID", ex);
            }
        } else {
            tx = null;
            txName = txStr;
        }

        PaleontologyListEntry result = fredDAO.createNewPaleontologyListEntry();
        result.setSpecimenCount(count);
        result.setSpecimenCoords(coords);
        result.setComments(comments);
        result.setTaxonomicGroup(taxonGroup);
        result.setTaxon(tx);
        result.setTaxonomicName(txName);
        result.setPaleontology(p);
    }


    private void setIdentifiers(Paleontology paleo, Row row, RowSingleValue v) throws RowImportException {
        Person p;
        try {
            p = personUtil.findPerson(nameFromName(row, v));
        } catch (StorageAccessException ex) {
            throw new RowImportException(row, v, "Could not find this person", ex);
        }
        paleo.getIdentifiers().add(p);

    }

    private void setStartStage(Paleontology paleo, Row row, RowSingleValue v) throws RowImportException {
        Integer ageId = idFromName(row, v);
        if (null != ageId) {
            Stage s = paleo.getStage();
            try {
                Age a = stageUtil.getAge(ageId);
                s.setLowerAge(a);
            } catch (StorageAccessException ex) {
                throw new RowImportException(row, v, "Could not find this age.", ex);
            }            
        }
    }

    private void setStopStage(Paleontology paleo, Row row, RowSingleValue v) throws RowImportException {
        Integer ageId = idFromName(row, v);
        if (null != ageId) {
            Stage s = paleo.getStage();
            try {
                Age a = stageUtil.getAge(ageId);
                s.setUpperAge(a);
            } catch (StorageAccessException ex) {
                throw new RowImportException(row, v, "Could not find this age.", ex);
            }            
        }    }

    private void setLabSection(Paleontology paleo, Row row, RowSingleValue v) throws RowImportException {
        Integer id = idFromName(row, v);
        if (null != id) {
            LabSection labSection;
            try {
                labSection = recordUtil.getLabSection(id);
            } catch (StorageAccessException ex) {
                throw new RowImportException(row, v, "Can't find that lab section.", ex);
            }
            paleo.setLabSection(labSection);
        }
    }

}
