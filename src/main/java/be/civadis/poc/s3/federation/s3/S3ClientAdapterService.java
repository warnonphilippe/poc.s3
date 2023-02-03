package be.civadis.poc.s3.federation.s3;

import be.civadis.poc.s3.dto.DocumentDTO;
import be.civadis.poc.s3.dto.DocumentStockageDTO;
import be.civadis.poc.s3.federation.SystemStockageClient;
import be.civadis.poc.s3.federation.dto.SystemeStockageDocumentDTO;
import be.civadis.poc.s3.federation.exception.GpdocValidationException;
import be.civadis.poc.s3.federation.exception.NodeNotFoundException;
import be.civadis.poc.s3.federation.exception.SystemeStockageException;
import be.civadis.poc.s3.utils.TenantContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
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
        s3ClientService.deleteObject(TenantContext.getCurrentTenant(), id, null);
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
        // le nom ne peut pas être changé, mais on peut déplacé le fichier ? les autres champs ne sont pas présent

        try {
            //l'uuid sera généré par gpdoc, il ne représente plus un uuid connu du système de stockage, mais on peut en déduire l'objectKey grâche aux infos de gpdoc
            DocumentDTO documentDTO = getDocumentFromGpdoc(uuid);

            String srcKey = getDocumentKey(documentDTO.getCheminDocument(), documentDTO.getNomDocument());

            s3ClientService.copyObject(TenantContext.getCurrentTenant(),
                    srcKey,
                    getDocumentKey(documentDTO.getCheminDocument(), name));

            s3ClientService.deleteObject(TenantContext.getCurrentTenant(), srcKey, null);

        } catch (Exception ex){
            throw new SystemeStockageException(ex.getMessage(), ex);
        }

    }

    @Override
    public SystemeStockageDocumentDTO moveNode(String uuid, String cheminDestination) throws NodeNotFoundException, SystemeStockageException {

        try {
            //l'uuid sera généré par gpdoc, il ne représente plus un uuid connu du système de stockage, mais on peut en déduire l'objectKey grâche aux infos de gpdoc
            DocumentDTO documentDTO = getDocumentFromGpdoc(uuid);

            String srcKey = getDocumentKey(documentDTO.getCheminDocument(), documentDTO.getNomDocument());

            s3ClientService.copyObject(TenantContext.getCurrentTenant(),
                    srcKey,
                    getDocumentKey(cheminDestination, documentDTO.getNomDocument()));

            s3ClientService.deleteObject(TenantContext.getCurrentTenant(), srcKey, null);

            // TODO
            return null;

        } catch (Exception ex){
            throw new SystemeStockageException(ex.getMessage(), ex);
        }

    }

    private DocumentDTO getDocumentFromGpdoc(String uuid){
        // TODO
        throw new RuntimeException("Not yet iplemented !!!");
    }

    private String getDocumentKey(String path, String name){
        StringBuilder str = new StringBuilder();
        str.append(path);
        if (!path.endsWith("/")){
            str.append("/");
        }
        str.append(name);
        return str.toString();
    }

    private String getDocumentKey(DocumentStockageDTO docDto){
        return getDocumentKey(docDto.getCheminDestination(), docDto.getNomDestination());
    }

    private DocumentDTO createResultDocumentDTO(DocumentStockageDTO docDto) {
        DocumentDTO dto = new DocumentDTO();
        dto.setUuidDocument(UUID.randomUUID().toString());
        dto.setNomDocument(docDto.getNomDestination());
        dto.setCheminDocument(docDto.getCheminDestination());
        return dto;
    }

}
