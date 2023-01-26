package be.civadis.poc.s3.dto;

import java.time.LocalDate;

public class DocumentStockageDTO {
    // doit commencer par /, pas de null et pas de double slashes
    private String cheminDestination;
    private String nomDestination;
    private String titreFr;
    private String titreNl;
    private String titreEn;
    private String titreDe;
    private LocalDate dateRetention;
    private Boolean suppressionAuto;
    private InfosArchivageDTO infosArchivageDTO;


    public String getCheminDestination() {
        return cheminDestination;
    }

    public void setCheminDestination(String cheminDestination) {
        this.cheminDestination = cheminDestination;
    }

    public String getNomDestination() {
        return nomDestination;
    }

    public void setNomDestination(String nomDestination) {
        this.nomDestination = nomDestination;
    }

    public String getTitreFr() {
        return titreFr;
    }

    public void setTitreFr(String titreFr) {
        this.titreFr = titreFr;
    }

    public String getTitreNl() {
        return titreNl;
    }

    public void setTitreNl(String titreNl) {
        this.titreNl = titreNl;
    }

    public String getTitreEn() {
        return titreEn;
    }

    public void setTitreEn(String titreEn) {
        this.titreEn = titreEn;
    }

    public String getTitreDe() {
        return titreDe;
    }

    public void setTitreDe(String titreDe) {
        this.titreDe = titreDe;
    }

    public LocalDate getDateRetention() {
        return dateRetention;
    }

    public void setDateRetention(LocalDate dateRetention) {
        this.dateRetention = dateRetention;
    }

    public Boolean getSuppressionAuto() {
        return suppressionAuto;
    }

    public void setSuppressionAuto(Boolean suppressionAuto) {
        this.suppressionAuto = suppressionAuto;
    }

    public InfosArchivageDTO getInfosArchivageDTO() {
        return infosArchivageDTO;
    }

    public void setInfosArchivageDTO(InfosArchivageDTO infosArchivageDTO) {
        this.infosArchivageDTO = infosArchivageDTO;
    }

    @Override
    public String toString() {
        return "DocumentStockageDTO{" +
                "cheminDestination='" + cheminDestination + '\'' +
                ", nomDestination='" + nomDestination + '\'' +
                ", titreFr='" + titreFr + '\'' +
                ", titreNl='" + titreNl + '\'' +
                ", titreEn='" + titreEn + '\'' +
                ", titreDe='" + titreDe + '\'' +
                '}';
    }
}

