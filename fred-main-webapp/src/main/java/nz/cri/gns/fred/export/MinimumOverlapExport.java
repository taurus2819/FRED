package nz.cri.gns.fred.export;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.abstractions.AgeRange;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.Stage;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.text.DecimalFormat;
import java.util.Vector;

public class MinimumOverlapExport extends OldFormatFredExport {

    public MinimumOverlapExport(Writer writer, DAOFactory factory) {
        super(writer);
        Export.setFactory(factory);
    }

    private static Set<String> groups;
    private static Set<String> ageGroups;
    private static Set<String> excludedIndentifiers;
    private static double baseAge = 65;

    static {
        groups = new HashSet<String>(3);
        groups.add("BIVALVIA");
        groups.add("GASTROPODA");
        groups.add("SCAPHOPODA");

        ageGroups = new HashSet<String>(5);
        ageGroups.add("FORAMINIFERA");
        ageGroups.add("DINOPHYCEAE");
        ageGroups.add("RADIOLARIA");
        ageGroups.add("SPORITES");
        ageGroups.add("POLLENITES");

        excludedIndentifiers = new HashSet<String>(17);
        excludedIndentifiers.add("Anderson, S.G.");
        excludedIndentifiers.add("Bell, J.M.");
        excludedIndentifiers.add("Fraser, C.");
        excludedIndentifiers.add("Hector, J.");
        excludedIndentifiers.add("Henderson, J.");
        excludedIndentifiers.add("Hopkins, J.C.");
        excludedIndentifiers.add("Hutton, F.W.");
        excludedIndentifiers.add("Lillie, A.R.");
        excludedIndentifiers.add("Macpherson, E.O.");
        excludedIndentifiers.add("McKay, A.");
        excludedIndentifiers.add("Morgan, P.G.");
        excludedIndentifiers.add("Neef, G.");
        excludedIndentifiers.add("Ongley, M.");
        excludedIndentifiers.add("Orman, H.R.");
        excludedIndentifiers.add("Park, J.");
        excludedIndentifiers.add("Suter, H.");
        excludedIndentifiers.add("Tarvydas, R.K.");
    }

    @Override
    public AgeRange getAgeRange(Sample sample, Paleontology list) throws StorageAccessException {
        Stage knownStage = sample.getKnownStage();
        AgeRange age = null;
        age = getMinimumOverlapIain(sample, knownStage);
        return age;
    }

    private AgeRange getMinimumOverlapIain(Sample sample, Stage knownStage) throws StorageAccessException {
        List<Paleontology> lists = Export.getFactory().getFredDAO().getPaleontologies(sample);

        if (knownStage == null) {
            if (lists.size() == 0) {
                return null;
            }
        } else {
            if (lists.size() == 0) {
                return new DefaultStageAgeRange(sample.getKnownStage(), "FOF - Known Age");
            }
        }

        Age lower = (knownStage != null && knownStage.getLowerAge() != null) ? knownStage.getLowerAge() : null;
        Age upper = (knownStage != null && knownStage.getUpperAge() != null) ? knownStage.getUpperAge() : null;
        boolean lowerCertain = true;
        boolean upperCertain = true;

        double lowerNumeric = (lower != null) ? lower.getBaseAge().doubleValue() : Double.NaN;
        double upperNumeric = (upper != null) ? upper.getTopAge().doubleValue() : Double.NaN;

        for (Paleontology pal : lists) {
            if (pal.getStage() != null) {
                Stage stage = pal.getStage();
                Age thisLower = stage.getLowerAge();
                Age thisUpper = stage.getUpperAge();
                //Lower bound
                if (thisLower != null && (Double.isNaN(lowerNumeric)
                        || (thisLower.getBaseAge() != null && thisLower.getBaseAge().doubleValue() < lowerNumeric))) {
                    lower = thisLower;
                    lowerCertain = !"?".equals(stage.getStageLowerMod());
                    lowerNumeric = thisLower.getBaseAge();
                }
                //Upper bound
                if (thisUpper != null && (Double.isNaN(upperNumeric)
                        || (thisUpper.getTopAge() != null && thisUpper.getTopAge().doubleValue() > lowerNumeric))) {
                    upper = thisUpper;
                    upperCertain = !"?".equals(stage.getStageUpperMod());
                    upperNumeric = thisUpper.getTopAge();
                }
            }
        }

        return new ListDerivedAge(lower, lowerCertain, upper, upperCertain, null);
    }

    @Override
    public Collection<Paleontology> getListsToExport(Sample sample) throws StorageAccessException {
        List<Paleontology> listSet = Export.getFactory().getFredDAO().getPaleontologies(sample);
        return listSet;

    }

    @Override
    public void handleList(Feature feature, Sample sample, AgeRange age, Paleontology list) throws IOException {
        //Reject anything that is unageable
        if (age == null) {
            return;
        }

        //write finky formatt here
        writer.write(feature.getFrNumber().getFrNumber());
        writer.write("|");
        if (age != null && (age.getLower() != null || age.getUpper() != null)) {
            writer.write("    Stage:       ");
            //Stages:
            boolean oneAge = age.getLower() == null || age.getUpper() == null || age.getLower().equals(age.getUpper());
            if (age.getLower() != null && age.getLower().equals(age.getUpper())) {
                oneAge = oneAge && !(age.isLowerCertain() ^ age.isUpperCertain());
            }
            DecimalFormat format = new DecimalFormat("0.0####");
            if (oneAge) {
                Age ageV = age.getLower() == null ? age.getUpper() : age.getLower();
                boolean ageU = age.getLower() == null ? age.isUpperCertain() : age.isLowerCertain();

                writer.write(ageV.getCode() + (ageU ? "" : "?") + "; ");
                writer.write(format.format(ageV.getBaseAge()) + "-" + format.format(ageV.getTopAge()));
            } else {
                writer.write(age.getLower().getCode() + (age.isLowerCertain() ? "" : "?")
                        + "-" + age.getUpper().getCode() + (age.isUpperCertain() ? "" : "?")
                        + "; " + format.format(age.getLower().getBaseAge()) + "-" + format.format(age.getUpper().getTopAge()));
            }
            writer.write(EOL);
            writer.flush();
        }

    }
}
