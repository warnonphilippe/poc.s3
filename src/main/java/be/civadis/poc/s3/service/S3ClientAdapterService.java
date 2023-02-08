package be.civadis.poc.s3.service;

import be.civadis.poc.s3.dto.DocumentDTO;
import be.civadis.poc.s3.federation.SystemStockageClient;
import be.civadis.poc.s3.federation.dto.SystemeStockageDocumentDTO;
import be.civadis.poc.s3.federation.exception.GpdocValidationException;
import be.civadis.poc.s3.federation.exception.NodeNotFoundException;
import be.civadis.poc.s3.federation.exception.SystemeStockageException;
import be.civadis.poc.s3.federation.s3.S3ClientService;
import be.civadis.poc.s3.repository.DocumentView;
import be.civadis.poc.s3.utils.FichierUtils;
import be.civadis.poc.s3.utils.UuidUtils;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class S3ClientAdapterService implements SystemStockageClient {

    private S3ClientService s3ClientService;
    private S3IdentifiantAdapterService s3IdentifiantAdapterService;
    private S3DocumentResolverAdapterService s3DocumentResolverAdapterService;

    public S3ClientAdapterService(S3ClientService s3ClientService, S3IdentifiantAdapterService s3IdentifiantAdapterService, S3DocumentResolverAdapterService s3DocumentResolverAdapterService) {
        this.s3ClientService = s3ClientService;
        this.s3IdentifiantAdapterService = s3IdentifiantAdapterService;
        this.s3DocumentResolverAdapterService = s3DocumentResolverAdapterService;
    }

    @Override
    public String checkOrCreateFolderPath(String completePath) {
        return completePath;
    }

    @Override
    public void deleteDocument(String uuid) throws SystemeStockageException, GpdocValidationException {
        DocumentView documentView = this.s3DocumentResolverAdapterService.getDocumentFromGpdoc(uuid);
        S3ObjectLocation loc = s3IdentifiantAdapterService.getObjectLocation(documentView);
        s3ClientService.deleteObject(loc.getBucketName(), loc.getObjectKey(), null);
    }

    @Override
    public List<SystemeStockageDocumentDTO> getDocumentAllVersions(String uuid) throws SystemeStockageException, GpdocValidationException {
        DocumentView documentView = this.s3DocumentResolverAdapterService.getDocumentFromGpdoc(uuid);
        S3ObjectLocation loc = s3IdentifiantAdapterService.getObjectLocation(documentView);
        return s3ClientService.getObjectVersions(loc.getBucketName(), loc.getObjectKey()).stream()
                .map(v -> createSystemeStockageDocumentDTO(documentView, v))
                .collect(Collectors.toList());
    }

    private SystemeStockageDocumentDTO createSystemeStockageDocumentDTO(DocumentView documentView, String version){
        SystemeStockageDocumentDTO dto = new SystemeStockageDocumentDTO();
        dto.setDirectory(false);
        dto.setMimeType(documentView.getMediaType());
        dto.setName(documentView.getNomDocument());
        dto.setPath(documentView.getCheminDocument());
        dto.setSize(Long.parseLong(documentView.getTaille()));
        dto.setVersionLabel(version);
        dto.setId(documentView.getUuidDocument());
        return dto;
    }

    @Override
    public SystemeStockageDocumentDTO uploadDocument(MultipartFile fileRef, String parentPath, String targetName, String destMimeType, String titre, String description) throws SystemeStockageException, IOException, NodeNotFoundException, NoSuchAlgorithmException {
        return uploadDocument(FichierUtils.getFileFromMultipart(fileRef), parentPath, targetName, destMimeType, titre, description);
    }

    @Override
    public SystemeStockageDocumentDTO uploadDocument(File dst, String parentPath, String targetName, String destMimeType, String titre, String description) throws SystemeStockageException, IOException, NodeNotFoundException, NoSuchAlgorithmException {

        SystemeStockageDocumentDTO dto = new SystemeStockageDocumentDTO();
        dto.setId(UuidUtils.generateTicketUUID());
        dto.setDirectory(false);
        dto.setMimeType(destMimeType);
        dto.setName(targetName);
        dto.setPath(parentPath);
        dto.setSize(dst.length());
        dto.setTitre(titre);
        dto.setDescription(description);

        S3ObjectLocation loc = s3IdentifiantAdapterService.getObjectLocation(parentPath, targetName);

        this.s3ClientService.createObject(loc.getBucketName(), loc.getObjectKey(), dst);
        completeVersion(loc, dto);

        return dto;
    }

    @Override
    public Resource downloadDocument(String uuid) throws SystemeStockageException, GpdocValidationException {
        return downloadDocument(uuid, null);
    }

    @Override
    public Resource downloadDocument(String uuid, String version) throws SystemeStockageException, GpdocValidationException {
        // TODO : voir si correspondance entre numérations de versions
        DocumentView documentView = this.s3DocumentResolverAdapterService.getDocumentFromGpdoc(uuid);
        var loc = s3IdentifiantAdapterService.getObjectLocation(documentView);
        return this.s3ClientService.getObjectContent(loc.getBucketName(), loc.getObjectKey(), version);
    }

    @Override
    public void updateProprietes(String uuid, String titre, String description, String name) throws SystemeStockageException {
        //dans l'implem S3,
        //  le nom ne peut pas être changé, mais on peut déplacé le fichier ? les autres champs ne sont pas présent
        //  l'uuid sera généré par gpdoc, il ne représente plus un uuid connu du système de stockage, mais on peut en déduire l'objectKey grâche aux infos de gpdoc

        try {

            DocumentView documentView = this.s3DocumentResolverAdapterService.getDocumentFromGpdoc(uuid);
            S3ObjectLocation srcLoc = s3IdentifiantAdapterService.getObjectLocation(documentView);
            S3ObjectLocation dstLoc = s3IdentifiantAdapterService.getObjectLocation(documentView.getCheminDocument(), name);

            s3ClientService.copyObject(srcLoc.getBucketName(), srcLoc.getObjectKey(), dstLoc.getBucketName(), dstLoc.getObjectKey());
            s3ClientService.deleteObject(srcLoc.getBucketName(), srcLoc.getObjectKey(), null);

        } catch (Exception ex){
            throw new SystemeStockageException(ex.getMessage(), ex);
        }

    }

    @Override
    public SystemeStockageDocumentDTO moveNode(String uuid, String cheminDestination) throws SystemeStockageException {

        try {

            DocumentView documentView = this.s3DocumentResolverAdapterService.getDocumentFromGpdoc(uuid);
            S3ObjectLocation srcLoc = s3IdentifiantAdapterService.getObjectLocation(documentView);
            S3ObjectLocation dstLoc = s3IdentifiantAdapterService.getObjectLocation(cheminDestination, documentView.getNomDocument());

            s3ClientService.copyObject(srcLoc.getBucketName(), srcLoc.getObjectKey(), dstLoc.getBucketName(), dstLoc.getObjectKey());
            s3ClientService.deleteObject(srcLoc.getBucketName(), srcLoc.getObjectKey(), null);

            SystemeStockageDocumentDTO dto = new SystemeStockageDocumentDTO();
            dto.setId(uuid);
            dto.setDirectory(false);
            dto.setMimeType(documentView.getMediaType());
            dto.setName(documentView.getNomDocument());
            dto.setPath(cheminDestination);
            dto.setSize(Long.parseLong(documentView.getTaille()));
            dto.setTitre(documentView.getTitreFr());

            completeVersion(dstLoc, dto);

            return dto;

        } catch (Exception ex){
            throw new SystemeStockageException(ex.getMessage(), ex);
        }

    }

    public void completeVersion(S3ObjectLocation loc, SystemeStockageDocumentDTO dto) throws SystemeStockageException {
        // TODO voir si on stocke la version issue de S3 ou si on garde une numérotation à associé aux numéros de s3
        // TODO comment s'assurer que la dernière version récupérée est bien celle associé à l'upload que l'on vient de faire,
        //  car on pourrait avoir fait 2 upload en même temps et le get versions pourrait retourner l'id de l'autre
        //  actuellement, on a bien la bonne valeur dans le dto, mais si 2 update en même temps, on pourrait risquer de save en db la mauvaise valeur
        List<String> versions = this.s3ClientService.getObjectVersions(loc.getBucketName(), loc.getObjectKey());
        dto.setVersionLabel(versions.get(0));
        Date now = new Date();
        if (versions.size() == 1){
            dto.setCreatedAt(now);
        }
        dto.setModifiedAt(now);
    }



}
