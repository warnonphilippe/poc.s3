package be.civadis.poc.s3.federation;


import be.civadis.poc.s3.federation.dto.SystemeStockageDocumentDTO;
import be.civadis.poc.s3.federation.exception.SystemeStockageException;
import be.civadis.poc.s3.federation.exception.GpdocValidationException;
import be.civadis.poc.s3.federation.exception.NodeNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface SystemStockageClient {

    /**
     * Crée un répertoire (et les sous-répertoires)
     *
     * @param completePath path complet du répertoire (à partir de /Sites/...)
     * @return id du répertoire (créé ou déjà existant)
     */
    String checkOrCreateFolderPath(String completePath) throws SystemeStockageException, NodeNotFoundException;

    /**
     * Supprime un document
     *
     * @param id id du document
     */
    void deleteDocument(String id) throws SystemeStockageException, GpdocValidationException;

    /**
     * Recherche toutes les versions d'un document
     * REM :
     * Les documents alfresco contiennent un nr de version dans leur id
     * Un objet Document est donc associé à une version,
     * On peut à partir de celui-ci récupéré une liste de Document qui représente toutes les versions du document
     *
     * @param id id du document (l'id peut inclure un numéro de version du document)
     * @return
     */
    List<SystemeStockageDocumentDTO> getDocumentAllVersions(String id) throws SystemeStockageException, GpdocValidationException;

    /**
     * Upload d'un document
     *
     * @param fileRef      référence au fichier sous forme de MultipartFile, permet un upload html
     * @param parentPath   path du parent sous lequel placé le fichier
     * @param targetName   nom du fichier sous lequel enregistrer le fichier (si absent, utilise le filename transmis dans fileRef)
     * @param destMimeType mimeType du fichier
     * @param description  description du fichier
     * @return
     * @throws IOException
     */
    SystemeStockageDocumentDTO uploadDocument(MultipartFile fileRef, String parentPath, String targetName, String destMimeType, String titre, String description) throws SystemeStockageException, IOException, NodeNotFoundException;

    /**
     * Upload d'un document
     *
     * @param dst          fichier à uploader
     * @param parentPath   path du parent sous lequel placé le fichier
     * @param targetName   nom du fichier sous lequel enregistrer le fichier
     * @param destMimeType mimeType du fichier
     * @param description  description du fichier
     * @return
     * @throws IOException
     */
    SystemeStockageDocumentDTO uploadDocument(File dst, String parentPath, String targetName, String destMimeType, String titre, String description) throws SystemeStockageException, IOException, NodeNotFoundException;

    /**
     * Download d'un document
     *
     * @param id id du document
     * @return
     */
    Resource downloadDocument(String id) throws SystemeStockageException, GpdocValidationException;

    /**
     * Download d'un document
     *
     * @param id      id du document
     * @param version version du document
     * @return
     */
    Resource downloadDocument(String id, String version) throws SystemeStockageException, GpdocValidationException;

    /**
     * Update le titre et la description d'un document dans l'alfresco
     *
     * @param uuid
     * @param titre
     * @param description
     * @throws SystemeStockageException
     */
    void updateProprietes(String uuid, String titre, String description, String name) throws SystemeStockageException;

    /**
     * Déplacer un document
     * @param cheminDestination
     * @return
     * @throws NodeNotFoundException
     * @throws SystemeStockageException
     */
    SystemeStockageDocumentDTO moveNode(String uuid, String cheminDestination) throws NodeNotFoundException, SystemeStockageException;

    // méthodes supprimées (corriger leurs appels, toutes leurs valeurs doivent être retrouvées dans la db gpdoc)
    //getDocument
    //getDocuments

    // méthodes supprimées (à vérifier, mais pourraient être supprimées car existent déjà des alternatives)
    //getDocumentAvecContenu
    //searchDocument
    //searchDocuments
    //getFolderTree
    //createFolder (remplacer par createAndCheckFolder)
    //addTags (déjà plus utilisée)

}
