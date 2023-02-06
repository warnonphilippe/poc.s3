package be.civadis.poc.s3.federation.s3;

import be.civadis.poc.s3.dto.DocumentDTO;
import be.civadis.poc.s3.dto.DocumentStockageDTO;
import be.civadis.poc.s3.federation.SystemStockageClient;
import be.civadis.poc.s3.federation.dto.SystemeStockageDocumentDTO;
import be.civadis.poc.s3.federation.exception.GpdocValidationException;
import be.civadis.poc.s3.federation.exception.NodeNotFoundException;
import be.civadis.poc.s3.federation.exception.SystemeStockageException;
import be.civadis.poc.s3.utils.FichierUtils;
import be.civadis.poc.s3.utils.TenantContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public List<SystemeStockageDocumentDTO> getDocumentAllVersions(String uuid) throws SystemeStockageException, GpdocValidationException {
        DocumentDTO documentDTO = getDocumentFromGpdoc(uuid);
        String documentKey = getDocumentKey(documentDTO);
        return s3ClientService.getObjectVersions(TenantContext.getCurrentTenant(), documentKey).stream()
                .map(v -> createSystemeStockageDocumentDTO(documentDTO, v))
                .collect(Collectors.toList());
    }

    private SystemeStockageDocumentDTO createSystemeStockageDocumentDTO(DocumentDTO documentDTO, String version){
        SystemeStockageDocumentDTO dto = new SystemeStockageDocumentDTO();
        dto.setDirectory(false);
        dto.setMimeType(documentDTO.getMediaType());
        dto.setName(documentDTO.getNomDocument());
        dto.setPath(documentDTO.getCheminDocument());
        dto.setSize(Long.parseLong(documentDTO.getTaille()));
        dto.setVersionLabel(version);
        dto.setId(documentDTO.getUuidDocument());
        return dto;
    }

    @Override
    public SystemeStockageDocumentDTO uploadDocument(MultipartFile fileRef, String parentPath, String targetName, String destMimeType, String titre, String description) throws SystemeStockageException, IOException, NodeNotFoundException {
        return uploadDocument(FichierUtils.getFileFromMultipart(fileRef), parentPath, targetName, destMimeType, titre, description);
    }

    @Override
    public SystemeStockageDocumentDTO uploadDocument(File dst, String parentPath, String targetName, String destMimeType, String titre, String description) throws SystemeStockageException, IOException, NodeNotFoundException {

        SystemeStockageDocumentDTO dto = new SystemeStockageDocumentDTO();
        dto.setDirectory(false);
        dto.setMimeType(destMimeType);
        dto.setName(dst.getName());
        dto.setPath(parentPath);
        dto.setSize(dst.length());

        String key = getDocumentKey(parentPath, dst.getName());
        this.s3ClientService.createObject(TenantContext.getCurrentTenant(), key, dst);
        completeVersion(key, dto);

        return dto;
    }

    @Override
    public Resource downloadDocument(String uuid) throws SystemeStockageException, GpdocValidationException {
        return downloadDocument(uuid, null);
    }

    @Override
    public Resource downloadDocument(String uuid, String version) throws SystemeStockageException, GpdocValidationException {
        // TODO : voir si correspondance entre numérations de versions
        DocumentDTO documentDTO = getDocumentFromGpdoc(uuid);
        return this.s3ClientService.getObjectContent(
                TenantContext.getCurrentTenant(),
                getDocumentKey(documentDTO.getCheminDocument(), documentDTO.getNomDocument()), version);
    }

    @Override
    public void updateProprietes(String uuid, String titre, String description, String name) throws SystemeStockageException {
        //dans l'implem S3,
        //  le nom ne peut pas être changé, mais on peut déplacé le fichier ? les autres champs ne sont pas présent
        //  l'uuid sera généré par gpdoc, il ne représente plus un uuid connu du système de stockage, mais on peut en déduire l'objectKey grâche aux infos de gpdoc

        try {

            DocumentDTO documentDTO = getDocumentFromGpdoc(uuid);

            String srcKey = getDocumentKey(documentDTO);
            String dstKey = getDocumentKey(documentDTO.getCheminDocument(), name);

            s3ClientService.copyObject(TenantContext.getCurrentTenant(), srcKey, dstKey);
            s3ClientService.deleteObject(TenantContext.getCurrentTenant(), srcKey, null);

        } catch (Exception ex){
            throw new SystemeStockageException(ex.getMessage(), ex);
        }

    }

    @Override
    public SystemeStockageDocumentDTO moveNode(String uuid, String cheminDestination) throws SystemeStockageException {

        try {

            DocumentDTO documentDTO = getDocumentFromGpdoc(uuid);

            String srcKey = getDocumentKey(documentDTO);
            String dstKey = getDocumentKey(cheminDestination, documentDTO.getNomDocument());

            s3ClientService.copyObject(TenantContext.getCurrentTenant(), srcKey, dstKey);
            s3ClientService.deleteObject(TenantContext.getCurrentTenant(), srcKey, null);

            SystemeStockageDocumentDTO dto = new SystemeStockageDocumentDTO();
            dto.setDirectory(false);
            dto.setMimeType(documentDTO.getMediaType());
            dto.setName(documentDTO.getNomDocument());
            dto.setPath(cheminDestination);
            dto.setSize(Long.parseLong(documentDTO.getTaille()));

            completeVersion(dstKey, dto);

            return dto;

        } catch (Exception ex){
            throw new SystemeStockageException(ex.getMessage(), ex);
        }

    }

    private DocumentDTO getDocumentFromGpdoc(String uuid) throws GpdocValidationException {
        //TODO : Rechercher les infos à propos du document dans la db gpdoc
        //  Attention, L'adapteur devra donc être dans la couche service, sinon pas d'accès au repo
        //  L'interface générale du client pourra être dans la federation
        //  Les les implémentations des client S3 et Alfresco devront être dans federation
        // ATTENTION, GpdocValidationException si pas trouvé !
        throw new RuntimeException("Not implemented yet");
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

    private String getDocumentKey(DocumentDTO docDto){
        return getDocumentKey(docDto.getCheminDocument(), docDto.getNomDocument());
    }

    private DocumentDTO createResultDocumentDTO(DocumentStockageDTO docDto) {
        DocumentDTO dto = new DocumentDTO();
        dto.setUuidDocument(UUID.randomUUID().toString());
        dto.setNomDocument(docDto.getNomDestination());
        dto.setCheminDocument(docDto.getCheminDestination());
        return dto;
    }

    private void completeVersion(String key, SystemeStockageDocumentDTO dto) throws SystemeStockageException {
        // TODO voir si on stocke la version issue de S3 ou si on garde une numérotation à associé aux numéros de s3
        // TODO comment s'assurer que la dernière version récupérée est bien celle associé à l'upload que l'on vient de faire,
        //  car on pourrait avoir fait 2 upload en même temps et le get versions pourrait retourner l'id de l'autre
        //  actuellement, on a bien la bonne valeur dans le dto, mais si 2 update en même temps, on pourrait risquer de save en db la mauvaise valeur
        List<String> versions = this.s3ClientService.getObjectVersions(TenantContext.getCurrentTenant(), key);
        dto.setVersionLabel(versions.get(0));
        Date now = new Date();
        if (versions.size() == 1){
            dto.setCreatedAt(now);
        }
        dto.setModifiedAt(now);
    }

}
