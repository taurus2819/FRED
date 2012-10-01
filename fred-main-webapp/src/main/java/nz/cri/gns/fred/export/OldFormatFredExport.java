package nz.cri.gns.fred.export;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.abstractions.AgeRange;
import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.PaleontologyListEntry;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.TaxonomicGroup;

public class OldFormatFredExport extends DefaultFredExport {

	protected static final String EOL = "\r\n";
	private int count;
	protected Writer writer;
	
	public OldFormatFredExport(Writer writer) {
		this.count = 1;
		this.writer = writer;
	}

	public void handleFeature(Feature feature) throws IOException, StorageAccessException {
//		Date date0 = new Date();
		Set<Sample> samples = feature.getSamples();
		samples.size();
//		System.out.println("Get samples: " + (new Date().getTime() - date0.getTime()));
//		date0 = new Date();
		System.out.println("Feature: " + feature.getFeatureId());
		for (Sample sample : samples) {
			for (Paleontology list : getListsToExport(sample)) {
//				Date date = new Date();
				handleList(feature, sample, getAgeRange(sample, list), list);
//				System.out.println("List: " + (new Date().getTime() - date.getTime()));
			}
		}
//		System.out.println("Handle Feature: " + (new Date().getTime() - date0.getTime()));
	}
	
    @Override
	public void handleList(Feature feature, Sample sample, AgeRange age, Paleontology list) throws IOException {
		//Don't export unapproved features
		if (feature.getFrNumber() == null)
			return;

		writeHeader(feature, sample, age, list);
		
		writeLists(list);
	}

	private void writeLists(Paleontology list) throws IOException {
		Set<TaxonomicGroup> groups = new HashSet<TaxonomicGroup>();
		for (PaleontologyListEntry entry : list.getListEntries()) {
			groups.add(entry.getTaxonomicGroup());
		}
		for (TaxonomicGroup group : groups) {
			if (!groupRequired(group))
				continue;
			writer.write(" Group: " + group.getDisplayName() + EOL);
			for (PaleontologyListEntry entry : list.getListEntries()) {
				if (entry.getTaxonomicGroup().equals(group)) {
					writer.write(" " + entry.getTaxonomicName() + " * " + DBUtils.nvl(entry.getComments()) + EOL);
				}
			}
		}
	}

	protected boolean groupRequired(TaxonomicGroup group) {
		return true;
	}

	protected void writeHeader(Feature feature, Sample sample, AgeRange age, Paleontology list) throws IOException {
		//File starts with a blank line and then the Item number
		writer.write(EOL);
		writer.write("Item " + count++ + EOL);		
		//Next line has a single space on it
		writer.write(" " + EOL);
		//Work out the fr number
		String frSuffix = (feature.getSamples().size() > 1) ? ("(" + DBUtils.nvl(sample.getTopDepth()) + "-" + DBUtils.nvl(sample.getBottomDepth()) + ")") : "";
		writer.write(" FOSSIL RECORD NUMBER -      " + feature.getFrNumber().getFrNumber() + frSuffix + EOL);
		//Identifier
		if (list.getIdentifiers().size() > 0) {
			writer.write("    Identifier:  ");
			for (Iterator<Person> identifiers = list.getIdentifiers().iterator(); identifiers.hasNext(); ) {
				writer.write(identifiers.next().getDisplayName());
				if (identifiers.hasNext())
					writer.write(",");
			}
			writer.write(EOL);
		}
		//Date
		if (list.getIdentificationDate() != null) {
			writer.write("    Date:        " + new SimpleDateFormat("dd/MM/yyyy").format(list.getIdentificationDate()) + EOL);
		}
		//Stage
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
			writer.write("    Comment on stage determination: [" + age.getAgeRangeType() + "]");
			if (age.getComment() != null) {
				writer.write("; " + age.getComment());
			}
			writer.write(EOL);
		}
		if (list.getLabNumber() != null) {
			writer.write("    Lab. number: " + list.getLabNumber() + EOL);
		}
		if (list.getCollectionComments() != null) {
			writer.write("    Comment on collection: " + list.getCollectionComments() + EOL);
		}
	}
}
