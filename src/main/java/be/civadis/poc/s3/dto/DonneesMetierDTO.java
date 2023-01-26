package be.civadis.poc.s3.dto;


import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Validated
public class DonneesMetierDTO {
    private LocalDate dateValeur;
    private String titre;
    private String typeMetier;
    private String numeroMetier;
    private String periode;
    private String identifiantDestinataire;
    private String identifiantNationalDestinataire;
    private String nomDestinataire;
    private String prenomDestinataire;
    private String emailDestinataire;
    private String zoneLibreTextuelle1;
    private String zoneLibreTextuelle2;
    private String zoneLibreTextuelle3;
    private LocalDate zoneLibreDate1;
    private Integer zoneLibreEntier1;

    public LocalDate getDateValeur() {
        return dateValeur;
    }

    public void setDateValeur(LocalDate dateValeur) {
        this.dateValeur = dateValeur;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getTypeMetier() {
        return typeMetier;
    }

    public void setTypeMetier(String typeMetier) {
        this.typeMetier = typeMetier;
    }

    public String getNumeroMetier() {
        return numeroMetier;
    }

    public void setNumeroMetier(String numeroMetier) {
        this.numeroMetier = numeroMetier;
    }

    public String getPeriode() {
        return periode;
    }

    public void setPeriode(String periode) {
        this.periode = periode;
    }

    public String getIdentifiantDestinataire() {
        return identifiantDestinataire;
    }

    public void setIdentifiantDestinataire(String identifiantDestinataire) {
        this.identifiantDestinataire = identifiantDestinataire;
    }

    public String getIdentifiantNationalDestinataire() {
        return identifiantNationalDestinataire;
    }

    public void setIdentifiantNationalDestinataire(String identifiantNationalDestinataire) {
        this.identifiantNationalDestinataire = identifiantNationalDestinataire;
    }

    public String getNomDestinataire() {
        return nomDestinataire;
    }

    public void setNomDestinataire(String nomDestinataire) {
        this.nomDestinataire = nomDestinataire;
    }

    public String getPrenomDestinataire() {
        return prenomDestinataire;
    }

    public void setPrenomDestinataire(String prenomDestinataire) {
        this.prenomDestinataire = prenomDestinataire;
    }

    public String getEmailDestinataire() {
        return emailDestinataire;
    }

    public void setEmailDestinataire(String emailDestinataire) {
        this.emailDestinataire = emailDestinataire;
    }

    public String getZoneLibreTextuelle1() {
        return zoneLibreTextuelle1;
    }

    public void setZoneLibreTextuelle1(String zoneLibreTextuelle1) {
        this.zoneLibreTextuelle1 = zoneLibreTextuelle1;
    }

    public String getZoneLibreTextuelle2() {
        return zoneLibreTextuelle2;
    }

    public void setZoneLibreTextuelle2(String zoneLibreTextuelle2) {
        this.zoneLibreTextuelle2 = zoneLibreTextuelle2;
    }

    public String getZoneLibreTextuelle3() {
        return zoneLibreTextuelle3;
    }

    public void setZoneLibreTextuelle3(String zoneLibreTextuelle3) {
        this.zoneLibreTextuelle3 = zoneLibreTextuelle3;
    }

    public LocalDate getZoneLibreDate1() {
        return zoneLibreDate1;
    }

    public void setZoneLibreDate1(LocalDate zoneLibreDate1) {
        this.zoneLibreDate1 = zoneLibreDate1;
    }

    public Integer getZoneLibreEntier1() {
        return zoneLibreEntier1;
    }

    public void setZoneLibreEntier1(Integer zoneLibreEntier1) {
        this.zoneLibreEntier1 = zoneLibreEntier1;
    }
}

