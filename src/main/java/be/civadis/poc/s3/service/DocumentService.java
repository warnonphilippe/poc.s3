package be.civadis.poc.s3.service;

import be.civadis.poc.s3.dto.DocumentDTO;
import be.civadis.poc.s3.dto.DocumentStockageDTO;
import be.civadis.poc.s3.federation.S3Service;
import be.civadis.poc.s3.utils.FichierUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class DocumentService {

    // REM, dans une vraie app, le tenant devrait être déduit du token d'auth
    // le tenant va être utilisé comme nom de bucket
    private String DEFAULT_TENANT = "jhipster";

    private S3Service s3;


    public DocumentService(S3Service s3) {
        this.s3 = s3;
    }

    public DocumentDTO stockerDocument(DocumentStockageDTO docDto, Resource resource) throws IOException {

        try (InputStream inputStream = resource.getInputStream()){

            //File docFile = FichierUtils.getFileFromResource(resource);
            byte[] bytes = FichierUtils.readBytes(inputStream);

            s3.createObject(DEFAULT_TENANT, createDocumentKey(docDto), bytes);

            return createResultDocumentDTO(docDto);
        }
    }

    private String createDocumentKey(DocumentStockageDTO docDto){
        StringBuilder str = new StringBuilder();
        str.append(docDto.getCheminDestination());
        if (!docDto.getCheminDestination().endsWith("/")){
            str.append("/");
        }
        str.append(docDto.getNomDestination());
        return str.toString();
    }

    private DocumentDTO createResultDocumentDTO(DocumentStockageDTO docDto) {
        DocumentDTO dto = new DocumentDTO();
        dto.setUuidDocument(UUID.randomUUID().toString());
        dto.setNomDocument(docDto.getNomDestination());
        dto.setCheminDocument(docDto.getCheminDestination());
        return dto;
    }


    // utiliser des buckets différents pour isoler les tenants
    // comment gérer des paths de fichiers ? hiérarchie dans les buckets ?
    //  -> Simuler en spécifiant des paths dans les objets keys
    // rest
    // docker MimIO

}
