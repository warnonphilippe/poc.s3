package be.civadis.poc.s3.dto;


import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Validated
public class InfosArchivageDTO {
    private Boolean archivageAuto;
    private LocalDate dateDebutArchivage;
    private LocalDate dateFinArchivage;
    private DonneesMetierDTO donneesMetier;


    public Boolean getArchivageAuto() {
        return archivageAuto;
    }

    public void setArchivageAuto(Boolean archivageAuto) {
        this.archivageAuto = archivageAuto;
    }

    public LocalDate getDateDebutArchivage() {
        return dateDebutArchivage;
    }

    public void setDateDebutArchivage(LocalDate dateDebutArchivage) {
        this.dateDebutArchivage = dateDebutArchivage;
    }

    public LocalDate getDateFinArchivage() {
        return dateFinArchivage;
    }

    public void setDateFinArchivage(LocalDate dateFinArchivage) {
        this.dateFinArchivage = dateFinArchivage;
    }

    public DonneesMetierDTO getDonneesMetier() {
        return donneesMetier;
    }

    public void setDonneesMetier(DonneesMetierDTO donneesMetier) {
        this.donneesMetier = donneesMetier;
    }
}
