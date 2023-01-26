package be.civadis.poc.s3.service;

import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    private S3Service s3;

    public DocumentService(S3Service s3) {
        this.s3 = s3;
    }






    // utiliser des buckets différents pour isoler les tenants
    // comment gérer des paths de fichiers ? hiérarchie dans les buckets ?
    //  -> Simuler en spécifiant des paths dans les objets keys
    // rest
    // docker MimIO

}
