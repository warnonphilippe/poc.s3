package be.civadis.poc.s3.repository;

import java.util.Optional;

public interface DocumentBizRepository {

    Optional<DocumentView> findDocumentViewByUuidDocument(String docUuid);

}
