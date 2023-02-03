package be.civadis.poc.s3.federation;


import be.civadis.poc.s3.federation.dto.SystemeStockageDocumentDTO;
import be.civadis.poc.s3.federation.exception.SystemeStockageException;
import be.civadis.poc.s3.federation.exception.GpdocValidationException;
import be.civadis.poc.s3.federation.exception.NodeNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface SystemStockageClient {

    /**
     * Crée un folder
     *
     * @param parent parent du document (path si commence par /, sinon id
     * @param name   nom du document
     * @return
     */
    SystemeStockageDocumentDTO createFolder(String parent, String name) throws SystemeStockageException, NodeNotFoundException;

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
    void deleteDocument(String id) throws SystemeStockageException;

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
    List<SystemeStockageDocumentDTO> getDocumentAllVersions(String id) throws SystemeStockageException;

    /**
     * Obtenir une liste de documents
     *
     * @param parent path du parent des documents
     * @return
     */
    List<SystemeStockageDocumentDTO> getDocuments(String parent) throws SystemeStockageException, NodeNotFoundException;

    /**
     * Obtenir une liste de documents
     *
     * @param parent   path du parent des documents
     * @param page     page
     * @param pageSize nombre d'éléments par page
     * @return
     */
    List<SystemeStockageDocumentDTO> getDocuments(String parent, Integer page, Integer pageSize) throws SystemeStockageException, NodeNotFoundException;

    /**
     * Recherche des documents (leur dernière version) :
     * sous un répertoire parent
     * par valeurs du contenu et le type de recherche, si type =
     * phrase : la value est recherchée dans le document
     * and : tous les mots de value doivent être présent dans le document
     * or : au moins un des mots de value dans être présent dans le document
     * par valeurs des tags, selon le type de recherche dans les tags
     * and : les documents doivent avoir tous le tags
     * or : les documents doivent avoir au moins un des tags
     * Si différents types de critères sont fournis (ex : tags et contenu), les critères devront tous être vérifiés
     *
     * @param parent            path du répertoire parent sous lequel effectué la recherche (doit commencer par /, sinon considéré comme id)
     * @param value             : ensemble de mots ou phrase à rechercher, dépend de search
     * @param searchTypeContent : type de recherche, phrase, and, or (par defaut)
     * @param tags              liste des tags
     * @param searchTypeTags    type de recherche appliquée ax tags : and, or (par défaut)
     * @param page
     * @param pageSize
     * @return
     */
    List<SystemeStockageDocumentDTO> searchDocument(String parent, String value, String searchTypeContent, List<String> tags, String searchTypeTags, Integer page, Integer pageSize) throws SystemeStockageException;

    List<SystemeStockageDocumentDTO> searchDocument(String parent, String value, String searchTypeContent, List<String> tags, String searchTypeTags) throws SystemeStockageException, NodeNotFoundException;


    enum SearchTypeContent {
        OR,
        AND,
        PHRASE,
    }

    enum SearchTypeTags {
        OR,
        AND,
    }

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
    ResponseEntity<Resource> downloadDocument(String id) throws SystemeStockageException;

    /**
     * Download d'un document
     *
     * @param id      id du document
     * @param version version du document
     * @return
     */
    ResponseEntity<Resource> downloadDocument(String id, String version) throws SystemeStockageException;

    /**
     * Upload d'un document et checkIn dans Cmis
     *
     * @param fileRef     référence au fichier sous forme de MultipartFile, permet un upload html
     * @param id          id de la copie privée du document dans cmis
     * @param tmpPrefix   prefix à utiliser pour les fichiers temporaires, doit dépendre de l'application appelante
     * @param description description du document
     * @return
     * @throws IOException
     */
    SystemeStockageDocumentDTO uploadDocumentForCheckin(MultipartFile fileRef, String id, String tmpPrefix, String description) throws SystemeStockageException, IOException;

    /**
     * Retourne un document
     *
     * @param id   id de l'objet
     * @param path path de l'objet
     * @return
     */
    SystemeStockageDocumentDTO getDocument(String id, String path) throws SystemeStockageException, GpdocValidationException;

    /**
     * Retourne un document
     *
     * @param id      id de l'objet
     * @param path    path de l'objet
     * @param version version du document
     * @return
     */
    SystemeStockageDocumentDTO getDocument(String id, String path, String version) throws SystemeStockageException, GpdocValidationException;

    /**
     * Retourne un document et son contenu (ne fonctionne que pour un fichier, pas un répertoire)
     *
     * @param id   id de l'objet
     * @param path path de l'objet
     * @return
     */
    SystemeStockageDocumentDTO getDocumentAvecContenu(String id, String path) throws SystemeStockageException, GpdocValidationException;

    /**
     * Retourne un document et son contenu (ne fonctionne que pour un fichier, pas un répertoire)
     *
     * @param id      id de l'objet
     * @param path    path de l'objet
     * @param version version du document
     * @return
     */
    SystemeStockageDocumentDTO getDocumentAvecContenu(String id, String path, String version) throws SystemeStockageException, GpdocValidationException;

    /**
     * Check-out d'un document selon son id ou son path.
     * Une private working copy de la dernière version du document sera créée et retournée
     *
     * @param id   id du document
     * @param path path du document
     * @return private working copy
     * @throws SystemeStockageException si le document dispose déjà d'une private working copy
     */
    SystemeStockageDocumentDTO checkOutLatestDocument(String id, String path) throws SystemeStockageException;

    /**
     * Check in d'un document
     *
     * @param dst         file contenant le nouveau contenu
     * @param pwcId       id de la private working copy
     * @param description nouvelle description du document
     * @return
     * @throws IOException
     */
    SystemeStockageDocumentDTO checkInDocumentToCmis(File dst, String pwcId, String description) throws SystemeStockageException, IOException;

    /**
     * Indique si la dernièer version d'un document est lockée
     *
     * @param docId
     * @return
     */
    boolean isDocumentCheckedOut(String docId) throws SystemeStockageException;

    /**
     * Annule le lock de la dernière version d'un document
     *
     * @param docId
     */
    void cancelDocumentCheckout(String docId) throws SystemeStockageException;

    /**
     * Update le titre et la description d'un document dans l'alfresco
     *
     * @param uuid
     * @param titre
     * @param description
     * @throws SystemeStockageException
     */
    public void updateProprietes(String uuid, String titre, String description, String name) throws SystemeStockageException;

    /**
     * Recherche des folders sont un parent
     *
     * @param parentId id du parent
     * @return
     * @throws SystemeStockageException
     */
    public List<SystemeStockageDocumentDTO> searchFolders(String parentId, boolean allTree) throws SystemeStockageException;

    /**
     * Déplacer un document
     * @param uuid uuid du document à déplacer
     * @param cheminDestination destination à laquelle déplacer le document
     * @return
     */
    public SystemeStockageDocumentDTO moveNode(String uuid, String cheminDestination) throws NodeNotFoundException, SystemeStockageException;
}
