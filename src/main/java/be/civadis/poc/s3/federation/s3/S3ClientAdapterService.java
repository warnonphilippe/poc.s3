package be.civadis.poc.s3.federation.s3;

import be.civadis.poc.s3.dto.DocumentDTO;
import be.civadis.poc.s3.federation.SystemStockageClient;
import be.civadis.poc.s3.federation.dto.SystemeStockageDocumentDTO;
import be.civadis.poc.s3.federation.exception.GpdocValidationException;
import be.civadis.poc.s3.federation.exception.NodeNotFoundException;
import be.civadis.poc.s3.federation.exception.SystemeStockageException;
import be.civadis.poc.s3.utils.ApplicationInfosUtils;
import be.civadis.poc.s3.utils.FichierUtils;
import be.civadis.poc.s3.utils.TenantContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
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
    public void deleteDocument(String uuid) throws SystemeStockageException, GpdocValidationException {
        DocumentDTO documentDTO = getDocumentFromGpdoc(uuid);
        ObjectLocation loc = getObjectLocation(documentDTO);
        s3ClientService.deleteObject(loc.bucketName, loc.objectKey, null);
    }

    @Override
    public List<SystemeStockageDocumentDTO> getDocumentAllVersions(String uuid) throws SystemeStockageException, GpdocValidationException {
        DocumentDTO documentDTO = getDocumentFromGpdoc(uuid);
        ObjectLocation loc = getObjectLocation(documentDTO);
        return s3ClientService.getObjectVersions(loc.bucketName, loc.objectKey).stream()
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

        ObjectLocation loc = getObjectLocation(parentPath, dst.getName());

        this.s3ClientService.createObject(loc.bucketName, loc.objectKey, dst);
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
        DocumentDTO documentDTO = getDocumentFromGpdoc(uuid);
        var loc = getObjectLocation(documentDTO);
        return this.s3ClientService.getObjectContent(loc.bucketName, loc.objectKey, version);
    }

    @Override
    public void updateProprietes(String uuid, String titre, String description, String name) throws SystemeStockageException {
        //dans l'implem S3,
        //  le nom ne peut pas être changé, mais on peut déplacé le fichier ? les autres champs ne sont pas présent
        //  l'uuid sera généré par gpdoc, il ne représente plus un uuid connu du système de stockage, mais on peut en déduire l'objectKey grâche aux infos de gpdoc

        try {

            DocumentDTO documentDTO = getDocumentFromGpdoc(uuid);
            ObjectLocation srcLoc = getObjectLocation(documentDTO);
            ObjectLocation dstLoc = getObjectLocation(documentDTO.getCheminDocument(), name);

            s3ClientService.copyObject(srcLoc.bucketName, srcLoc.objectKey, dstLoc.bucketName, dstLoc.objectKey);
            s3ClientService.deleteObject(srcLoc.bucketName, srcLoc.objectKey, null);

        } catch (Exception ex){
            throw new SystemeStockageException(ex.getMessage(), ex);
        }

    }

    @Override
    public SystemeStockageDocumentDTO moveNode(String uuid, String cheminDestination) throws SystemeStockageException {

        try {

            DocumentDTO documentDTO = getDocumentFromGpdoc(uuid);
            ObjectLocation srcLoc = getObjectLocation(documentDTO);
            ObjectLocation dstLoc = getObjectLocation(cheminDestination, documentDTO.getNomDocument());

            s3ClientService.copyObject(srcLoc.bucketName, srcLoc.objectKey, dstLoc.bucketName, dstLoc.objectKey);
            s3ClientService.deleteObject(srcLoc.bucketName, srcLoc.objectKey, null);

            SystemeStockageDocumentDTO dto = new SystemeStockageDocumentDTO();
            dto.setDirectory(false);
            dto.setMimeType(documentDTO.getMediaType());
            dto.setName(documentDTO.getNomDocument());
            dto.setPath(cheminDestination);
            dto.setSize(Long.parseLong(documentDTO.getTaille()));

            completeVersion(dstLoc, dto);

            return dto;

        } catch (Exception ex){
            throw new SystemeStockageException(ex.getMessage(), ex);
        }

    }

    protected DocumentDTO getDocumentFromGpdoc(String uuid) throws GpdocValidationException {
        //TODO : Rechercher les infos à propos du document dans la db gpdoc
        //  Attention, L'adapteur devra donc être dans la couche service, sinon pas d'accès au repo
        //  L'interface générale du client pourra être dans la federation
        //  Les les implémentations des client S3 et Alfresco devront être dans federation
        // ATTENTION, GpdocValidationException si pas trouvé !
        throw new RuntimeException("Not implemented yet");
    }

    protected void completeVersion(ObjectLocation loc, SystemeStockageDocumentDTO dto) throws SystemeStockageException {
        // TODO voir si on stocke la version issue de S3 ou si on garde une numérotation à associé aux numéros de s3
        // TODO comment s'assurer que la dernière version récupérée est bien celle associé à l'upload que l'on vient de faire,
        //  car on pourrait avoir fait 2 upload en même temps et le get versions pourrait retourner l'id de l'autre
        //  actuellement, on a bien la bonne valeur dans le dto, mais si 2 update en même temps, on pourrait risquer de save en db la mauvaise valeur
        List<String> versions = this.s3ClientService.getObjectVersions(loc.bucketName, loc.objectKey);
        dto.setVersionLabel(versions.get(0));
        Date now = new Date();
        if (versions.size() == 1){
            dto.setCreatedAt(now);
        }
        dto.setModifiedAt(now);
    }

    protected ObjectLocation getObjectLocation(DocumentDTO documentDTO){
        return getObjectLocation(documentDTO.getCheminDocument(), documentDTO.getNomDocument());
    }

    protected ObjectLocation getObjectLocation(String paramPath, String name){
        String path = removeLeadingSlash(paramPath);
        if (isBucketOfUser(path)){
            //ex : testapp_00000 veut accéder à testapp/file.txt, on va donc accéder à file.txt dans le testapp-00000
            return new ObjectLocation(getMyBucketName(), getDocumentKey(removeApp(path, getCurrentApp()), name));
        } else {
            //ex : testapp-00000 veut accéder à onyx/facture.pdf, on va donc accéder à facture.pdf dans le bucket onyx-000
            // testapp_00000 devra avoir alors droit d'accès à un autre bucket que le sien
            String app = getApp(path);
            return new ObjectLocation(getOtherBucketName(app), getDocumentKey(removeApp(path, app), name));
        }
    }

    protected String removeLeadingSlash(String path){
        if (path != null){
            if (path.startsWith("/")){
                if (path.length() > 1){
                    return path.substring(1);
                } else {
                    return "";
                }
            } else {
                return path;
            }
        } else {
            return "";
        }
    }

    protected boolean isBucketOfUser(String path){
        if (path == null) return false;
        String app = getCurrentApp();
        return (path.startsWith("/" + app) || path.startsWith(app));
    }

    protected String getApp(String paramPath){
        String path = removeLeadingSlash(paramPath);
        if (path.contains("/")){
            return path.substring(0, path.indexOf("/"));
        } else {
            return path;
        }
    }

    protected String removeApp(String paramPath, String app){
        String path = removeLeadingSlash(paramPath);
        if (path.startsWith(app)){
            return path.substring(app.length());
        }
        return path;
    }

    protected String getMyBucketName(){
        return  getCurrentApp() + "-" + getCurrentTenant();
    }

    protected String getOtherBucketName(String app){
        return app + "-" + getCurrentTenant();
    }

    protected String getCurrentApp(){
        return ApplicationInfosUtils.getAppUser();
    }

    protected String getCurrentTenant(){
        return TenantContext.getCurrentTenant();
    }

    protected String getDocumentKey(String path, String name){
        StringBuilder str = new StringBuilder();
        str.append(path);
        if (!path.endsWith("/")){
            str.append("/");
        }
        str.append(name);
        return removeLeadingSlash(str.toString());
    }

    public class ObjectLocation {
        private String bucketName;
        private String objectKey;

        public ObjectLocation(String bucketName, String objectKey) {
            this.bucketName = bucketName;
            this.objectKey = objectKey;
        }

        public String getBucketName() {
            return bucketName;
        }

        public String getObjectKey() {
            return objectKey;
        }
    }

}
