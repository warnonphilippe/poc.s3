package be.civadis.poc.s3.federation.s3;

import be.civadis.poc.s3.dto.DocumentDTO;
import be.civadis.poc.s3.dto.DocumentStockageDTO;
import be.civadis.poc.s3.federation.SystemStockageClient;
import be.civadis.poc.s3.federation.dto.SystemeStockageDocumentDTO;
import be.civadis.poc.s3.federation.exception.GpdocValidationException;
import be.civadis.poc.s3.federation.exception.NodeNotFoundException;
import be.civadis.poc.s3.federation.exception.SystemeStockageException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class S3ClientAdapterService implements SystemStockageClient {

    private S3ClientService s3ClientService;

    public S3ClientAdapterService(S3ClientService s3ClientService) {
        this.s3ClientService = s3ClientService;
    }

    @Override
    public String checkOrCreateFolderPath(String completePath) {
        return completePath;
    }

    @Override
    public void deleteDocument(String id) throws SystemeStockageException {

    }

    @Override
    public List<SystemeStockageDocumentDTO> getDocumentAllVersions(String id) throws SystemeStockageException {
        return null;
    }

    @Override
    public List<SystemeStockageDocumentDTO> getDocuments(String parent) throws SystemeStockageException, NodeNotFoundException {
        return null;
    }

    @Override
    public List<SystemeStockageDocumentDTO> getDocuments(String parent, Integer page, Integer pageSize) throws SystemeStockageException, NodeNotFoundException {
        return null;
    }

    @Override
    public SystemeStockageDocumentDTO uploadDocument(MultipartFile fileRef, String parentPath, String targetName, String destMimeType, String titre, String description) throws SystemeStockageException, IOException, NodeNotFoundException {
        return null;
    }

    @Override
    public SystemeStockageDocumentDTO uploadDocument(File dst, String parentPath, String targetName, String destMimeType, String titre, String description) throws SystemeStockageException, IOException, NodeNotFoundException {
        return null;
    }

    @Override
    public Resource downloadDocument(String id) throws SystemeStockageException {
        return null;
    }

    @Override
    public Resource downloadDocument(String id, String version) throws SystemeStockageException {
        return null;
    }

    @Override
    public SystemeStockageDocumentDTO getDocument(String id, String path) throws SystemeStockageException, GpdocValidationException {
        return null;
    }

    @Override
    public SystemeStockageDocumentDTO getDocument(String id, String path, String version) throws SystemeStockageException, GpdocValidationException {
        return null;
    }

    @Override
    public SystemeStockageDocumentDTO getDocumentAvecContenu(String id, String path) throws SystemeStockageException, GpdocValidationException {
        return null;
    }

    @Override
    public SystemeStockageDocumentDTO getDocumentAvecContenu(String id, String path, String version) throws SystemeStockageException, GpdocValidationException {
        return null;
    }

    @Override
    public void updateProprietes(String uuid, String titre, String description, String name) throws SystemeStockageException {

    }

    @Override
    public SystemeStockageDocumentDTO moveNode(String uuid, String cheminDestination) throws NodeNotFoundException, SystemeStockageException {
        return null;
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
