package be.civadis.poc.s3.service;

import be.civadis.poc.s3.dto.DocumentDTO;
import be.civadis.poc.s3.federation.exception.GpdocValidationException;
import be.civadis.poc.s3.repository.DocumentView;
import org.springframework.stereotype.Service;

@Service
public class S3DocumentResolverAdapterService {

    public DocumentView getDocumentFromGpdoc(String uuid) throws GpdocValidationException {
        //TODO : Rechercher les infos à propos du document dans la db gpdoc
        //  Attention, L'adapteur devra donc être dans la couche service, sinon pas d'accès au repo
        //  L'interface générale du client pourra être dans la federation
        //  Les les implémentations des client S3 et Alfresco devront être dans federation
        // ATTENTION, GpdocValidationException si pas trouvé !
        throw new RuntimeException("Not implemented yet");
    }

}
