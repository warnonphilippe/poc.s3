package be.civadis.poc.s3.federation.s3;

import be.civadis.poc.s3.dto.DocumentDTO;
import be.civadis.poc.s3.federation.exception.GpdocValidationException;
import be.civadis.poc.s3.federation.exception.SystemeStockageException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class S3ClientAdapterServiceTest {

    @InjectMocks
    @Spy
    private S3ClientAdapterService s3ClientAdapterService;

    @Mock
    private S3ClientService s3ClientService;

    @Test
    void removeFirstSlash() {
        String res = s3ClientAdapterService.removeLeadingSlash("/testapp/lot/");
        assertThat(res).isEqualTo("testapp/lot/");
    }

    @Test
    void removeFirstSlash_no_slash() {
        String res = s3ClientAdapterService.removeLeadingSlash("testapp/lot");
        assertThat(res).isEqualTo("testapp/lot");
    }

    @Test
    void removeFirstSlash_empty() {
        String res = s3ClientAdapterService.removeLeadingSlash("");
        assertThat(res).isEqualTo("");
    }

    @Test
    void removeFirstSlash_null() {
        String res = s3ClientAdapterService.removeLeadingSlash("");
        assertThat(res).isEqualTo("");
    }

    @Test
    void isBucketOfUser_leading_slash() {
        Mockito.when(s3ClientAdapterService.getCurrentApp()).thenReturn("testapp");
        boolean res = s3ClientAdapterService.isBucketOfUser("/testapp/file");
        assertThat(res).isTrue();
    }

    @Test
    void isBucketOfUser() {
        Mockito.when(s3ClientAdapterService.getCurrentApp()).thenReturn("testapp");
        boolean res = s3ClientAdapterService.isBucketOfUser("testapp/file");
        assertThat(res).isTrue();
    }

    @Test
    void isBucketOfUser_other_app_leading_slash() {
        Mockito.when(s3ClientAdapterService.getCurrentApp()).thenReturn("testapp");
        boolean res = s3ClientAdapterService.isBucketOfUser("/onyx/file");
        assertThat(res).isFalse();
    }

    @Test
    void isBucketOfUser_other_app() {
        Mockito.when(s3ClientAdapterService.getCurrentApp()).thenReturn("testapp");
        boolean res = s3ClientAdapterService.isBucketOfUser("onyx/file");
        assertThat(res).isFalse();
    }

    @Test
    void isBucketOfUser_empty() {
        Mockito.when(s3ClientAdapterService.getCurrentApp()).thenReturn("testapp");
        boolean res = s3ClientAdapterService.isBucketOfUser("");
        assertThat(res).isFalse();
    }

    @Test
    void isBucketOfUser_null() {
        boolean res = s3ClientAdapterService.isBucketOfUser(null);
        assertThat(res).isFalse();
    }

    @Test
    void getApp_leading_slash() {
        String app = s3ClientAdapterService.getApp("/testapp/file");
        assertThat(app).isEqualTo("testapp");
    }

    @Test
    void getApp() {
        String app = s3ClientAdapterService.getApp("testapp/file");
        assertThat(app).isEqualTo("testapp");
    }

    @Test
    void getApp_leading_slash_no_file() {
        String app = s3ClientAdapterService.getApp("/testapp/");
        assertThat(app).isEqualTo("testapp");
    }

    @Test
    void getApp_leading_file_no_slash() {
        String app = s3ClientAdapterService.getApp("/testapp");
        assertThat(app).isEqualTo("testapp");
    }

    @Test
    void getApp_slash_ony() {
        String app = s3ClientAdapterService.getApp("/");
        assertThat(app).isEqualTo("");
    }

    @Test
    void getApp_empty() {
        String app = s3ClientAdapterService.getApp("");
        assertThat(app).isEqualTo("");
    }

    @Test
    void getApp_null() {
        String app = s3ClientAdapterService.getApp(null);
        assertThat(app).isEqualTo("");
    }

    @Test
    void removeApp_leading_slash() {
        String res = s3ClientAdapterService.removeApp("/testapp/file", "testapp");
        assertThat(res).isEqualTo("/file");
    }

    @Test
    void removeApp() {
        String res = s3ClientAdapterService.removeApp("testapp/file", "testapp");
        assertThat(res).isEqualTo("/file");
    }

    @Test
    void removeApp_no_file() {
        String res = s3ClientAdapterService.removeApp("testapp", "testapp");
        assertThat(res).isEqualTo("");
    }

    @Test
    void removeApp_other_app() {
        String res = s3ClientAdapterService.removeApp("onyx/file", "testapp");
        assertThat(res).isEqualTo("onyx/file");
    }

    @Test
    void removeApp_other_empty() {
        String res = s3ClientAdapterService.removeApp("", "testapp");
        assertThat(res).isEqualTo("");
    }

    @Test
    void removeApp_other_null() {
        String res = s3ClientAdapterService.removeApp(null, "testapp");
        assertThat(res).isEqualTo("");
    }

    @Test
    void getMyBucketName() {
        Mockito.when(s3ClientAdapterService.getCurrentTenant()).thenReturn("00000");
        Mockito.when(s3ClientAdapterService.getCurrentApp()).thenReturn("testapp");
        String res = s3ClientAdapterService.getMyBucketName();
        assertThat(res).isEqualTo("testapp-00000");
    }

    @Test
    void getOtherBucketName() {
        Mockito.when(s3ClientAdapterService.getCurrentTenant()).thenReturn("00000");
        String res = s3ClientAdapterService.getOtherBucketName("onyx");
        assertThat(res).isEqualTo("onyx-00000");
    }

    @Test
    void getDocumentKey_lt(){
        String res = s3ClientAdapterService.getDocumentKey("/path1/path2/", "file");
        assertThat(res).isEqualTo("path1/path2/file");
    }

    @Test
    void getDocumentKey_l(){
        String res = s3ClientAdapterService.getDocumentKey("/path1/path2", "file");
        assertThat(res).isEqualTo("path1/path2/file");
    }

    @Test
    void getDocumentKey_t(){
        String res = s3ClientAdapterService.getDocumentKey("path1/path2/", "file");
        assertThat(res).isEqualTo("path1/path2/file");
    }

    @Test
    void getDocumentKey_0(){
        String res = s3ClientAdapterService.getDocumentKey("path1/path2", "file");
        assertThat(res).isEqualTo("path1/path2/file");
    }

    @Test
    void getObjectLocation_dto() {
        Mockito.when(s3ClientAdapterService.getCurrentTenant()).thenReturn("00000");
        Mockito.when(s3ClientAdapterService.getCurrentApp()).thenReturn("testapp");
        DocumentDTO dto = new DocumentDTO();
        dto.setCheminDocument("/path1/path2");
        dto.setNomDocument("name.txt");
        s3ClientAdapterService.getObjectLocation(dto);
        Mockito.verify(s3ClientAdapterService, Mockito.times(1)).getObjectLocation("/path1/path2", "name.txt");
    }

    @Test
    void getObjectLocation_lt() {
        Mockito.when(s3ClientAdapterService.getCurrentTenant()).thenReturn("00000");
        Mockito.when(s3ClientAdapterService.getCurrentApp()).thenReturn("testapp");
        var res = s3ClientAdapterService.getObjectLocation("/testapp/lot/", "name.txt");
        assertThat(res).isNotNull();
        assertThat(res.getBucketName()).isEqualTo("testapp-00000");
        assertThat(res.getObjectKey()).isEqualTo("lot/name.txt");
    }

    @Test
    void getObjectLocation_l() {
        Mockito.when(s3ClientAdapterService.getCurrentTenant()).thenReturn("00000");
        Mockito.when(s3ClientAdapterService.getCurrentApp()).thenReturn("testapp");
        var res = s3ClientAdapterService.getObjectLocation("/testapp/lot", "name.txt");
        assertThat(res).isNotNull();
        assertThat(res.getBucketName()).isEqualTo("testapp-00000");
        assertThat(res.getObjectKey()).isEqualTo("lot/name.txt");
    }

    @Test
    void getObjectLocation_t() {
        Mockito.when(s3ClientAdapterService.getCurrentTenant()).thenReturn("00000");
        Mockito.when(s3ClientAdapterService.getCurrentApp()).thenReturn("testapp");
        var res = s3ClientAdapterService.getObjectLocation("/testapp/lot", "name.txt");
        assertThat(res).isNotNull();
        assertThat(res.getBucketName()).isEqualTo("testapp-00000");
        assertThat(res.getObjectKey()).isEqualTo("lot/name.txt");
    }

    @Test
    void getObjectLocation_0() {
        Mockito.when(s3ClientAdapterService.getCurrentTenant()).thenReturn("00000");
        Mockito.when(s3ClientAdapterService.getCurrentApp()).thenReturn("testapp");
        var res = s3ClientAdapterService.getObjectLocation("testapp/lot", "name.txt");
        assertThat(res).isNotNull();
        assertThat(res.getBucketName()).isEqualTo("testapp-00000");
        assertThat(res.getObjectKey()).isEqualTo("lot/name.txt");
    }

    @Test
    void getObjectLocation_other_app_lt() {
        Mockito.when(s3ClientAdapterService.getCurrentTenant()).thenReturn("00000");
        Mockito.when(s3ClientAdapterService.getCurrentApp()).thenReturn("testapp");
        var res = s3ClientAdapterService.getObjectLocation("/onyx/lot/", "name.txt");
        assertThat(res).isNotNull();
        assertThat(res.getBucketName()).isEqualTo("onyx-00000");
        assertThat(res.getObjectKey()).isEqualTo("lot/name.txt");
    }

    @Test
    void getObjectLocation_other_app_l() {
        Mockito.when(s3ClientAdapterService.getCurrentTenant()).thenReturn("00000");
        Mockito.when(s3ClientAdapterService.getCurrentApp()).thenReturn("testapp");
        var res = s3ClientAdapterService.getObjectLocation("/onyx/lot", "name.txt");
        assertThat(res).isNotNull();
        assertThat(res.getBucketName()).isEqualTo("onyx-00000");
        assertThat(res.getObjectKey()).isEqualTo("lot/name.txt");
    }

    @Test
    void getObjectLocation_other_app_t() {
        Mockito.when(s3ClientAdapterService.getCurrentTenant()).thenReturn("00000");
        Mockito.when(s3ClientAdapterService.getCurrentApp()).thenReturn("testapp");
        var res = s3ClientAdapterService.getObjectLocation("onyx/lot/", "name.txt");
        assertThat(res).isNotNull();
        assertThat(res.getBucketName()).isEqualTo("onyx-00000");
        assertThat(res.getObjectKey()).isEqualTo("lot/name.txt");
    }

    @Test
    void getObjectLocation_other_app_0() {
        Mockito.when(s3ClientAdapterService.getCurrentTenant()).thenReturn("00000");
        Mockito.when(s3ClientAdapterService.getCurrentApp()).thenReturn("testapp");
        var res = s3ClientAdapterService.getObjectLocation("onyx/lot", "name.txt");
        assertThat(res).isNotNull();
        assertThat(res.getBucketName()).isEqualTo("onyx-00000");
        assertThat(res.getObjectKey()).isEqualTo("lot/name.txt");
    }

    @Test
    void checkOrCreateFolderPath() {
        String path = "/path1/path2";
        var res = s3ClientAdapterService.checkOrCreateFolderPath(path);
        assertThat(res).isEqualTo(path);
    }

    @Test
    void deleteDocument() throws GpdocValidationException, SystemeStockageException {
        String uuid = "123456789";
        DocumentDTO dto = new DocumentDTO();
        dto.setUuidDocument(uuid);
        dto.setNomDocument("name.txt");
        dto.setCheminDocument("testapp/lot");

        Mockito.when(s3ClientAdapterService.getCurrentTenant()).thenReturn("00000");
        Mockito.when(s3ClientAdapterService.getCurrentApp()).thenReturn("testapp");
        Mockito.when(s3ClientAdapterService.getDocumentFromGpdoc(uuid)).thenReturn(dto);

        Mockito.doNothing().when(s3ClientService).deleteObject(anyString(), anyString(), anyString());

        Mockito.verify(s3ClientService, Mockito.times(1)).deleteObject("testapp-00000", "lot/name.txt", null);
    }

    @Test
    void getDocumentAllVersions() {
    }

    @Test
    void uploadDocument() {
    }

    @Test
    void testUploadDocument() {
    }

    @Test
    void downloadDocument() {
    }

    @Test
    void testDownloadDocument() {
    }

    @Test
    void updateProprietes() {
    }

    @Test
    void moveNode() {
    }
}