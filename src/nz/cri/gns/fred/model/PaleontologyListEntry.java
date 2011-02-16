package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface PaleontologyListEntry extends Comparable<PaleontologyListEntry>, NameableAndIdentifiable {
    public Integer getPalListId();
    public void setPalListId(Integer palListId);
    public String getComments();
    public void setComments(String comments);
    public Integer getSpecimenCount();
    public void setSpecimenCount(Integer specimenCount);
    public String getSpecimenCoords();
    public void setSpecimenCoords(String specimenCoords);
    public String getTaxonomicName();
    public void setTaxonomicName(String taxonomicName);
    public TaxonomicGroup getTaxonomicGroup();
    public void setTaxonomicGroup(TaxonomicGroup taxonomicGroup);
    public Paleontology getPaleontology();
    public void setPaleontology(Paleontology paleontology);
    public Taxon getTaxon();
    public void setTaxon(Taxon taxon);
    public Set<MetaCat> getMetaCats();
    public void setMetaCats(Set<MetaCat> metaCats);
}