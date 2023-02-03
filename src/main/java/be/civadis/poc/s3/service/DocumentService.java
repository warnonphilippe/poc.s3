package be.civadis.poc.s3.service;

import be.civadis.poc.s3.dto.DocumentDTO;
import be.civadis.poc.s3.dto.DocumentStockageDTO;
import be.civadis.poc.s3.federation.s3.S3ClientService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DocumentService {

    // REM, dans une vraie app, le tenant devrait être déduit du token d'auth
    // le tenant va être utilisé comme nom de bucket

    //ATTENTION, le document service sera modifié le moins possible car on devra pouvoir aller sur l'alfresco ou s3, mais l'impleme du client sera différente

    private String DEFAULT_TENANT = "jhipster";

    //TODO : Ne pas injecter s3 mais une implem de SystemeStockageService
    private S3ClientService s3;


    public DocumentService(S3ClientService s3) {
        this.s3 = s3;
    }

    public DocumentDTO stockerDocument(DocumentStockageDTO docDto, Resource resource) throws Exception {
        s3.createObject(DEFAULT_TENANT, createDocumentKey(docDto), resource);
        return createResultDocumentDTO(docDto);
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


}
