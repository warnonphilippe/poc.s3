package be.civadis.poc.s3.dto;

public class MetaDonneeDTO {
    private String code;
    private String valeur;

    public MetaDonneeDTO() {
        this.code = "default";
        this.valeur = "default";
        // nécessaire pour EntityMapper qui travaille avec constructor () et setters
    }

    public MetaDonneeDTO(String code, String valeur) {
        this.code = code;
        this.valeur = valeur;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }

    @Override
    public String toString() {
        return "MetaDonneeDTO{" +
                "code='" + code + '\'' +
                ", valeur='" + valeur + '\'' +
                '}';
    }
}

