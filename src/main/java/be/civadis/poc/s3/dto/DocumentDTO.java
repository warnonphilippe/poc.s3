package be.civadis.poc.s3.dto;

import java.time.LocalDate;

public class DocumentDTO {
    //@Schema(example = "/mon/repertoire/", description= "Le chemin de destination ou est enregistré le document")
    private String cheminDocument;
    //@Schema(example = "NomDocumentStocke.pdf", description= "Nom du document sur l'espace de stockage (Nom + extension)")
    private String nomDocument;
    //@Schema(example = "", description= "UUID du document")
    private String uuidDocument;
    private String versionDocument;
    private String titreFr;
    private String titreNl;
    private String titreEn;
    private String titreDe;
    //@Schema(example = "application/pdf", description= "Format du document")
    private String mediaType;
    private String taille;
    private LocalDate dateRetention;
    private Boolean suppressionAuto;
    private String infoSuppression;
    private Boolean readOnly;
    private Boolean readOnlyForUser;
    private Boolean canSign;
    private Boolean estSigne;
    private Boolean eteSigne;
    private Boolean doitEtreSigne;
    private Boolean archivageAuto;
    private Boolean archive;
    private String applicationCreatrice;

    public String getCheminDocument() {
        return cheminDocument;
    }

    public void setCheminDocument(String cheminDocument) {
        this.cheminDocument = cheminDocument;
    }

    public String getNomDocument() {
        return nomDocument;
    }

    public void setNomDocument(String nomDocument) {
        this.nomDocument = nomDocument;
    }

    public String getUuidDocument() {
        return uuidDocument;
    }

    public void setUuidDocument(String uuidDocument) {
        this.uuidDocument = uuidDocument;
    }

    public String getVersionDocument() {
        return versionDocument;
    }

    public void setVersionDocument(String versionDocument) {
        this.versionDocument = versionDocument;
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

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getTaille() {
        return taille;
    }

    public void setTaille(String taille) {
        this.taille = taille;
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

    public Boolean isSuppressionAuto() {
        return getSuppressionAuto();
    }

    public void setSuppressionAuto(Boolean suppressionAuto) {
        this.suppressionAuto = suppressionAuto;
    }

    public String getInfoSuppression() {
        return infoSuppression;
    }

    public void setInfoSuppression(String infoSuppression) {
        this.infoSuppression = infoSuppression;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Boolean getReadOnlyForUser() {
        return readOnlyForUser;
    }

    public void setReadOnlyForUser(Boolean readOnlyForUser) {
        this.readOnlyForUser = readOnlyForUser;
    }

    public Boolean getEstSigne() {
        return estSigne;
    }

    public void setEstSigne(Boolean estSigne) {
        this.estSigne = estSigne;
    }

    public Boolean getEteSigne() {
        return eteSigne;
    }

    public void setEteSigne(Boolean eteSigne) {
        this.eteSigne = eteSigne;
    }

    public Boolean getDoitEtreSigne() {
        return doitEtreSigne;
    }

    public Boolean getCanSign() {
        return canSign;
    }

    public void setCanSign(Boolean canSign) {
        this.canSign = canSign;
    }

    public void setDoitEtreSigne(Boolean doitEtreSigne) {
        this.doitEtreSigne = doitEtreSigne;
    }

    public Boolean getArchivageAuto() {
        return archivageAuto;
    }

    public void setArchivageAuto(Boolean archivageAuto) {
        this.archivageAuto = archivageAuto;
    }

    public Boolean getArchive() {
        return archive;
    }

    public void setArchive(Boolean archive) {
        this.archive = archive;
    }

    public String getApplicationCreatrice() {
        return applicationCreatrice;
    }

    public void setApplicationCreatrice(String applicationCreatrice) {
        this.applicationCreatrice = applicationCreatrice;
    }
}
