package be.civadis.poc.s3.federation.s3;

import be.civadis.poc.s3.federation.dto.SystemeStockageDocumentDTO;
import be.civadis.poc.s3.federation.exception.GpdocValidationException;
import be.civadis.poc.s3.federation.exception.NodeNotFoundException;
import be.civadis.poc.s3.federation.exception.SystemeStockageException;
import be.civadis.poc.s3.repository.DocumentView;
import be.civadis.poc.s3.service.S3ClientAdapterService;
import be.civadis.poc.s3.service.S3DocumentResolverAdapterService;
import be.civadis.poc.s3.service.S3IdentifiantAdapterService;
import be.civadis.poc.s3.service.S3ObjectLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class S3ClientAdapterServiceTest {

    @InjectMocks
    @Spy
    private S3ClientAdapterService s3ClientAdapterService;

    @Mock
    private S3IdentifiantAdapterService s3IdentifiantAdapterService;

    @Mock
    private S3DocumentResolverAdapterService s3DocumentResolverAdapterService;

    @Mock
    private S3ClientService s3ClientService;

    @Test
    void checkOrCreateFolderPath() {
        String path = "/path1/path2";
        var res = s3ClientAdapterService.checkOrCreateFolderPath(path);
        assertThat(res).isEqualTo(path);
    }

    @Test
    void deleteDocument() throws GpdocValidationException, SystemeStockageException {
        String uuid = "123456789";
        DocumentView doc = getDocumentView("testapp/lot", "name.txt", uuid);
        var loc = new S3ObjectLocation("testapp-00000", "lot/name.txt");

        Mockito.when(s3IdentifiantAdapterService.getObjectLocation(any(DocumentView.class))).thenReturn(loc);
        Mockito.when(s3DocumentResolverAdapterService.getDocumentFromGpdoc(uuid)).thenReturn(doc);
        Mockito.doNothing().when(s3ClientService).deleteObject(anyString(), anyString(), any());

        s3ClientAdapterService.deleteDocument(uuid);

        Mockito.verify(s3ClientService, Mockito.times(1)).deleteObject("testapp-00000", "lot/name.txt", null);
    }

    @Test
    void getDocumentAllVersions() throws GpdocValidationException, SystemeStockageException {
        String uuid = "123456789";
        var doc = getDocumentView("testapp/lot", "name.txt", uuid);
        var loc = new S3ObjectLocation("testapp-00000", "lot/name.txt");

        var versions = List.of(
                createSystemeStockageDocumentDTO(doc, "azer"),
                createSystemeStockageDocumentDTO(doc, "qsdf")
        );

        Mockito.when(s3IdentifiantAdapterService.getObjectLocation(doc)).thenReturn(loc);
        Mockito.when(s3DocumentResolverAdapterService.getDocumentFromGpdoc(uuid)).thenReturn(doc);
        Mockito.when(s3ClientService.getObjectVersions("testapp-00000", "lot/name.txt")).thenReturn(List.of("azer", "qsdf"));

        var res = s3ClientAdapterService.getDocumentAllVersions(uuid);

        Mockito.verify(s3ClientService, Mockito.times(1)).getObjectVersions("testapp-00000", "lot/name.txt");

        assertThat(res).isNotNull().hasSize(2);
        assertThat(res.get(0).getId()).isEqualTo(uuid);
        assertThat(res.get(0).getPath()).isEqualTo("testapp/lot");
        assertThat(res.get(0).getName()).isEqualTo("name.txt");
        assertThat(res.get(0).getVersionLabel()).isEqualTo("azer");
        assertThat(res.get(1).getId()).isEqualTo(uuid);
        assertThat(res.get(1).getPath()).isEqualTo("testapp/lot");
        assertThat(res.get(1).getName()).isEqualTo("name.txt");
        assertThat(res.get(1).getVersionLabel()).isEqualTo("qsdf");
    }

    @Test
    void uploadDocument() throws SystemeStockageException, IOException, NodeNotFoundException, NoSuchAlgorithmException {

        //var doc = getDocumentView("testapp/lot", "name.txt", uuid);
        var loc = new S3ObjectLocation("testapp-00000", "lot/name.txt");
        File tmp = File.createTempFile("tmp", ".txt");
        tmp.deleteOnExit();

        Mockito.when(s3IdentifiantAdapterService.getObjectLocation(anyString(), anyString())).thenReturn(loc);
        Mockito.doNothing().when(s3ClientService).createObject(any(), any(), any(File.class));
        Mockito.when(s3ClientService.getObjectVersions(anyString(), anyString())).thenReturn(List.of("azer"));

        var result = s3ClientAdapterService.uploadDocument(tmp, "testapp/lot", "name.txt", "application/text", "test", "");

        Mockito.verify(s3ClientService).createObject("testapp-00000", "lot/name.txt", tmp);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getPath()).isEqualTo("testapp/lot");
        assertThat(result.getName()).isEqualTo("name.txt");
        assertThat(result.getVersionLabel()).isEqualTo("azer");
        assertThat(result.getMimeType()).isEqualTo("application/text");
        assertThat(result.getSize()).isEqualTo((tmp.length()));
        assertThat(result.getTitre()).isEqualTo("test");
        assertThat(result.getDescription()).isEqualTo("");
        assertThat(result.isDirectory()).isFalse();
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getModifiedAt()).isNotNull();
    }

    @Test
    void downloadDocument() throws IOException, SystemeStockageException, GpdocValidationException {
        String uuid = "123456789";
        var doc = getDocumentView("testapp/lot", "name.txt", uuid);
        var loc = new S3ObjectLocation("testapp-00000", "lot/name.txt");

        Mockito.when(s3DocumentResolverAdapterService.getDocumentFromGpdoc(uuid)).thenReturn(doc);
        Mockito.when(s3IdentifiantAdapterService.getObjectLocation(doc)).thenReturn(loc);

        File tmp = tmp = File.createTempFile("tmp", "");
        tmp.deleteOnExit();
        InputStreamResource resource = null;
        try {
            resource = new InputStreamResource(new FileInputStream(tmp));
            Mockito.when(s3ClientService.getObjectContent(anyString(), anyString(), any())).thenReturn(resource);

            var result = s3ClientAdapterService.downloadDocument(uuid);

            Mockito.verify(s3ClientService, Mockito.times(1)).getObjectContent("testapp-00000", "lot/name.txt", null);
            assertThat(result).isEqualTo(resource);

        } finally {
            if (resource != null) resource.getInputStream().close();
        }
    }

    @Test
    void updateProprietes() throws GpdocValidationException, SystemeStockageException {
        String uuid = "123456789";
        var doc = getDocumentView("testapp/lot", "name.txt", uuid);
        var loc = new S3ObjectLocation("testapp-00000", "lot/name.txt");
        var locDest = new S3ObjectLocation("testapp-00000", "lot/name2.txt");

        Mockito.when(s3DocumentResolverAdapterService.getDocumentFromGpdoc(uuid)).thenReturn(doc);
        Mockito.when(s3IdentifiantAdapterService.getObjectLocation(doc)).thenReturn(loc);
        Mockito.when(s3IdentifiantAdapterService.getObjectLocation("testapp/lot", "name2.txt")).thenReturn(locDest);

        Mockito.doNothing().when(s3ClientService).copyObject(anyString(), anyString(), anyString(), anyString());
        Mockito.doNothing().when(s3ClientService).deleteObject(anyString(), anyString(), any());

        s3ClientAdapterService.updateProprietes(uuid, "titre", "description", "name2.txt");

        Mockito.verify(s3ClientService, Mockito.times(1)).copyObject(loc.getBucketName(), loc.getObjectKey(), locDest.getBucketName(), locDest.getObjectKey());
        Mockito.verify(s3ClientService, Mockito.times(1)).deleteObject(loc.getBucketName(), loc.getObjectKey(), null);
    }

    @Test
    void moveNode() throws GpdocValidationException, SystemeStockageException {
        String uuid = "123456789";
        var doc = getDocumentView("testapp/lot", "name.txt", uuid);
        var loc = new S3ObjectLocation("testapp-00000", "lot/name.txt");
        var locDest = new S3ObjectLocation("testapp-00000", "lot2/name.txt");

        Mockito.when(s3DocumentResolverAdapterService.getDocumentFromGpdoc(uuid)).thenReturn(doc);
        Mockito.when(s3IdentifiantAdapterService.getObjectLocation(doc)).thenReturn(loc);
        Mockito.when(s3IdentifiantAdapterService.getObjectLocation("testapp/lot2", "name.txt")).thenReturn(locDest);
        Mockito.doNothing().when(s3ClientService).copyObject(anyString(), anyString(), anyString(), anyString());
        Mockito.doNothing().when(s3ClientService).deleteObject(anyString(), anyString(), any());
        Mockito.when(s3ClientService.getObjectVersions(anyString(), anyString())).thenReturn(List.of("azer"));

        var result = s3ClientAdapterService.moveNode(uuid, "testapp/lot2");

        Mockito.verify(s3ClientService, Mockito.times(1)).copyObject(loc.getBucketName(), loc.getObjectKey(), locDest.getBucketName(), locDest.getObjectKey());
        Mockito.verify(s3ClientService, Mockito.times(1)).deleteObject(loc.getBucketName(), loc.getObjectKey(), null);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(uuid);
        assertThat(result.getPath()).isEqualTo("testapp/lot2");
        assertThat(result.getName()).isEqualTo("name.txt");
        assertThat(result.getVersionLabel()).isEqualTo("azer");
        assertThat(result.getMimeType()).isEqualTo(doc.getMediaType());
        assertThat(result.getSize()).isEqualTo(Long.parseLong(doc.getTaille()));
        assertThat(result.getTitre()).isEqualTo(doc.getTitreFr());
        assertThat(result.isDirectory()).isFalse();
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getModifiedAt()).isNotNull();
    }

    private DocumentView getDocumentView(String chemin, String nom, String uuid){
        return new DocumentView() {
            @Override
            public String getCheminDocument() {
                return chemin;
            }

            @Override
            public String getNomDocument() {
                return nom;
            }

            @Override
            public String getUuidDocument() {
                return uuid;
            }

            @Override
            public String getVersionDocument() {
                return "azerty-qsdfg-wxcvb";
            }

            @Override
            public String getMediaType() {
                return "application/txt";
            }

            @Override
            public String getTaille() {
                return "100";
            }

            @Override
            public String getTitreFr() {
                return "titreFr";
            }

            @Override
            public String getTitreNl() {
                return "titreNl";
            }

            @Override
            public String getTitreDe() {
                return "titreDe";
            }

            @Override
            public String getTitreEn() {
                return "titreEn";
            }

            @Override
            public Boolean getReadOnly() {
                return false;
            }

            @Override
            public Instant getDateRetention() {
                return null;
            }

            @Override
            public Boolean getSuppressionAuto() {
                return false;
            }

            @Override
            public Boolean getArchivageAuto() {
                return false;
            }

            @Override
            public Instant getDateDebutArchivage() {
                return null;
            }

            @Override
            public Instant getDateFinArchivage() {
                return null;
            }
        };
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
}