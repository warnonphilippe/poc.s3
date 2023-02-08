package be.civadis.poc.s3.repository;

import java.time.Instant;

public interface DocumentView {

    public String getCheminDocument();

    public String getNomDocument();
    public String getUuidDocument();

    public String getVersionDocument();

    public String getMediaType();
    public String getTaille();

    String getTitreFr();

    String getTitreNl();

    String getTitreDe();

    String getTitreEn();

    Boolean getReadOnly();

    Instant getDateRetention();

    Boolean getSuppressionAuto();

    Boolean getArchivageAuto();

    Instant getDateDebutArchivage();

    Instant getDateFinArchivage();
}
