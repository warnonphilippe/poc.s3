package be.civadis.poc.s3.dto;

import java.util.List;

public class DefinitionMetaDonneeDTO {
    List<MetaDonneeDTO> metaDonnees;
    List<String> cleASupprimer;
    Boolean supprimerTout;

    public List<MetaDonneeDTO> getMetaDonnees() {
        return metaDonnees;
    }

    public void setMetaDonnees(List<MetaDonneeDTO> metaDonnees) {
        this.metaDonnees = metaDonnees;
    }

    public List<String> getCleASupprimer() {
        return cleASupprimer;
    }

    public void setCleASupprimer(List<String> cleASupprimer) {
        this.cleASupprimer = cleASupprimer;
    }

    public Boolean getSupprimerTout() {
        return supprimerTout;
    }

    public void setSupprimerTout(Boolean supprimerTout) {
        this.supprimerTout = supprimerTout;
    }

    @Override
    public String toString() {
        return "DefinitionMetaDonneeDTO{" +
                "metaDonnees=" + metaDonnees +
                ", cleASupprimer=" + cleASupprimer +
                ", supprimerTout=" + supprimerTout +
                '}';
    }
}

