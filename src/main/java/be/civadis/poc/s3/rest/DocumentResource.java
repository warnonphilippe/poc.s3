package be.civadis.poc.s3.rest;

import be.civadis.poc.s3.dto.DefinitionMetaDonneeDTO;
import be.civadis.poc.s3.dto.DocumentDTO;
import be.civadis.poc.s3.dto.DocumentStockageDTO;
import be.civadis.poc.s3.service.DocumentService;
import be.civadis.poc.s3.utils.FichierUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("documents")
public class DocumentResource {


    private final DocumentService documentService;

    public DocumentResource(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Stocker un document
     *
     * @param docDto          propriétés du document
     * @param fichierDocument : Fichier du document
     */
    @PostMapping(value = "/documents")
    public ResponseEntity<DocumentDTO> stockerDocument(
            @RequestPart("docDto") DocumentStockageDTO docDto,
            @RequestPart("file") MultipartFile fichierDocument,
            @RequestPart(name = "defMetaDonneeDto", required = false)  DefinitionMetaDonneeDTO defMetaDonneeDto,
            @RequestHeader(value = "Content-Transfer-Encoding", required = false) String encoding)
            throws IOException {
        DocumentDTO documentDto = documentService.stockerDocument(docDto, FichierUtils.decodeBase64Multipart(fichierDocument, encoding).getResource());
        return ResponseEntity.ok(documentDto);
    }

}
