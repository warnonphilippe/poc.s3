package be.civadis.poc.s3.federation.s3;

import be.civadis.poc.s3.dto.DocumentDTO;
import be.civadis.poc.s3.service.S3IdentifiantAdapterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class S3IdentifiantAdapterServiceTest {

    @InjectMocks
    @Spy
    private S3IdentifiantAdapterService s3IdentifiantAdapterService;

    @Test
    void removeFirstSlash() {
        String res = s3IdentifiantAdapterService.removeLeadingSlash("/testapp/lot/");
        assertThat(res).isEqualTo("testapp/lot/");
    }

    @Test
    void removeFirstSlash_no_slash() {
        String res = s3IdentifiantAdapterService.removeLeadingSlash("testapp/lot");
        assertThat(res).isEqualTo("testapp/lot");
    }

    @Test
    void removeFirstSlash_empty() {
        String res = s3IdentifiantAdapterService.removeLeadingSlash("");
        assertThat(res).isEqualTo("");
    }

    @Test
    void removeFirstSlash_null() {
        String res = s3IdentifiantAdapterService.removeLeadingSlash("");
        assertThat(res).isEqualTo("");
    }

    @Test
    void isBucketOfUser_leading_slash() {
        Mockito.when(s3IdentifiantAdapterService.getCurrentApp()).thenReturn("testapp");
        boolean res = s3IdentifiantAdapterService.isBucketOfUser("/testapp/file");
        assertThat(res).isTrue();
    }

    @Test
    void isBucketOfUser() {
        Mockito.when(s3IdentifiantAdapterService.getCurrentApp()).thenReturn("testapp");
        boolean res = s3IdentifiantAdapterService.isBucketOfUser("testapp/file");
        assertThat(res).isTrue();
    }

    @Test
    void isBucketOfUser_other_app_leading_slash() {
        Mockito.when(s3IdentifiantAdapterService.getCurrentApp()).thenReturn("testapp");
        boolean res = s3IdentifiantAdapterService.isBucketOfUser("/onyx/file");
        assertThat(res).isFalse();
    }

    @Test
    void isBucketOfUser_other_app() {
        Mockito.when(s3IdentifiantAdapterService.getCurrentApp()).thenReturn("testapp");
        boolean res = s3IdentifiantAdapterService.isBucketOfUser("onyx/file");
        assertThat(res).isFalse();
    }

    @Test
    void isBucketOfUser_empty() {
        Mockito.when(s3IdentifiantAdapterService.getCurrentApp()).thenReturn("testapp");
        boolean res = s3IdentifiantAdapterService.isBucketOfUser("");
        assertThat(res).isFalse();
    }

    @Test
    void isBucketOfUser_null() {
        boolean res = s3IdentifiantAdapterService.isBucketOfUser(null);
        assertThat(res).isFalse();
    }

    @Test
    void getApp_leading_slash() {
        String app = s3IdentifiantAdapterService.getApp("/testapp/file");
        assertThat(app).isEqualTo("testapp");
    }

    @Test
    void getApp() {
        String app = s3IdentifiantAdapterService.getApp("testapp/file");
        assertThat(app).isEqualTo("testapp");
    }

    @Test
    void getApp_leading_slash_no_file() {
        String app = s3IdentifiantAdapterService.getApp("/testapp/");
        assertThat(app).isEqualTo("testapp");
    }

    @Test
    void getApp_leading_file_no_slash() {
        String app = s3IdentifiantAdapterService.getApp("/testapp");
        assertThat(app).isEqualTo("testapp");
    }

    @Test
    void getApp_slash_ony() {
        String app = s3IdentifiantAdapterService.getApp("/");
        assertThat(app).isEqualTo("");
    }

    @Test
    void getApp_empty() {
        String app = s3IdentifiantAdapterService.getApp("");
        assertThat(app).isEqualTo("");
    }

    @Test
    void getApp_null() {
        String app = s3IdentifiantAdapterService.getApp(null);
        assertThat(app).isEqualTo("");
    }

    @Test
    void removeApp_leading_slash() {
        String res = s3IdentifiantAdapterService.removeApp("/testapp/file", "testapp");
        assertThat(res).isEqualTo("/file");
    }

    @Test
    void removeApp() {
        String res = s3IdentifiantAdapterService.removeApp("testapp/file", "testapp");
        assertThat(res).isEqualTo("/file");
    }

    @Test
    void removeApp_no_file() {
        String res = s3IdentifiantAdapterService.removeApp("testapp", "testapp");
        assertThat(res).isEqualTo("");
    }

    @Test
    void removeApp_other_app() {
        String res = s3IdentifiantAdapterService.removeApp("onyx/file", "testapp");
        assertThat(res).isEqualTo("onyx/file");
    }

    @Test
    void removeApp_other_empty() {
        String res = s3IdentifiantAdapterService.removeApp("", "testapp");
        assertThat(res).isEqualTo("");
    }

    @Test
    void removeApp_other_null() {
        String res = s3IdentifiantAdapterService.removeApp(null, "testapp");
        assertThat(res).isEqualTo("");
    }

    @Test
    void getMyBucketName() {
        Mockito.when(s3IdentifiantAdapterService.getCurrentTenant()).thenReturn("00000");
        Mockito.when(s3IdentifiantAdapterService.getCurrentApp()).thenReturn("testapp");
        String res = s3IdentifiantAdapterService.getMyBucketName();
        assertThat(res).isEqualTo("testapp-00000");
    }

    @Test
    void getOtherBucketName() {
        Mockito.when(s3IdentifiantAdapterService.getCurrentTenant()).thenReturn("00000");
        String res = s3IdentifiantAdapterService.getOtherBucketName("onyx");
        assertThat(res).isEqualTo("onyx-00000");
    }

    @Test
    void getDocumentKey_lt(){
        String res = s3IdentifiantAdapterService.getDocumentKey("/path1/path2/", "file");
        assertThat(res).isEqualTo("path1/path2/file");
    }

    @Test
    void getDocumentKey_l(){
        String res = s3IdentifiantAdapterService.getDocumentKey("/path1/path2", "file");
        assertThat(res).isEqualTo("path1/path2/file");
    }

    @Test
    void getDocumentKey_t(){
        String res = s3IdentifiantAdapterService.getDocumentKey("path1/path2/", "file");
        assertThat(res).isEqualTo("path1/path2/file");
    }

    @Test
    void getDocumentKey_0(){
        String res = s3IdentifiantAdapterService.getDocumentKey("path1/path2", "file");
        assertThat(res).isEqualTo("path1/path2/file");
    }

    @Test
    void getObjectLocation_dto() {
        Mockito.when(s3IdentifiantAdapterService.getCurrentTenant()).thenReturn("00000");
        Mockito.when(s3IdentifiantAdapterService.getCurrentApp()).thenReturn("testapp");
        DocumentDTO dto = new DocumentDTO();
        dto.setCheminDocument("/path1/path2");
        dto.setNomDocument("name.txt");
        s3IdentifiantAdapterService.getObjectLocation(dto);
        Mockito.verify(s3IdentifiantAdapterService, Mockito.times(1)).getObjectLocation("/path1/path2", "name.txt");
    }

    @Test
    void getObjectLocation_lt() {
        Mockito.when(s3IdentifiantAdapterService.getCurrentTenant()).thenReturn("00000");
        Mockito.when(s3IdentifiantAdapterService.getCurrentApp()).thenReturn("testapp");
        var res = s3IdentifiantAdapterService.getObjectLocation("/testapp/lot/", "name.txt");
        assertThat(res).isNotNull();
        assertThat(res.getBucketName()).isEqualTo("testapp-00000");
        assertThat(res.getObjectKey()).isEqualTo("lot/name.txt");
    }

    @Test
    void getObjectLocation_l() {
        Mockito.when(s3IdentifiantAdapterService.getCurrentTenant()).thenReturn("00000");
        Mockito.when(s3IdentifiantAdapterService.getCurrentApp()).thenReturn("testapp");
        var res = s3IdentifiantAdapterService.getObjectLocation("/testapp/lot", "name.txt");
        assertThat(res).isNotNull();
        assertThat(res.getBucketName()).isEqualTo("testapp-00000");
        assertThat(res.getObjectKey()).isEqualTo("lot/name.txt");
    }

    @Test
    void getObjectLocation_t() {
        Mockito.when(s3IdentifiantAdapterService.getCurrentTenant()).thenReturn("00000");
        Mockito.when(s3IdentifiantAdapterService.getCurrentApp()).thenReturn("testapp");
        var res = s3IdentifiantAdapterService.getObjectLocation("/testapp/lot", "name.txt");
        assertThat(res).isNotNull();
        assertThat(res.getBucketName()).isEqualTo("testapp-00000");
        assertThat(res.getObjectKey()).isEqualTo("lot/name.txt");
    }

    @Test
    void getObjectLocation_0() {
        Mockito.when(s3IdentifiantAdapterService.getCurrentTenant()).thenReturn("00000");
        Mockito.when(s3IdentifiantAdapterService.getCurrentApp()).thenReturn("testapp");
        var res = s3IdentifiantAdapterService.getObjectLocation("testapp/lot", "name.txt");
        assertThat(res).isNotNull();
        assertThat(res.getBucketName()).isEqualTo("testapp-00000");
        assertThat(res.getObjectKey()).isEqualTo("lot/name.txt");
    }

    @Test
    void getObjectLocation_other_app_lt() {
        Mockito.when(s3IdentifiantAdapterService.getCurrentTenant()).thenReturn("00000");
        Mockito.when(s3IdentifiantAdapterService.getCurrentApp()).thenReturn("testapp");
        var res = s3IdentifiantAdapterService.getObjectLocation("/onyx/lot/", "name.txt");
        assertThat(res).isNotNull();
        assertThat(res.getBucketName()).isEqualTo("onyx-00000");
        assertThat(res.getObjectKey()).isEqualTo("lot/name.txt");
    }

    @Test
    void getObjectLocation_other_app_l() {
        Mockito.when(s3IdentifiantAdapterService.getCurrentTenant()).thenReturn("00000");
        Mockito.when(s3IdentifiantAdapterService.getCurrentApp()).thenReturn("testapp");
        var res = s3IdentifiantAdapterService.getObjectLocation("/onyx/lot", "name.txt");
        assertThat(res).isNotNull();
        assertThat(res.getBucketName()).isEqualTo("onyx-00000");
        assertThat(res.getObjectKey()).isEqualTo("lot/name.txt");
    }

    @Test
    void getObjectLocation_other_app_t() {
        Mockito.when(s3IdentifiantAdapterService.getCurrentTenant()).thenReturn("00000");
        Mockito.when(s3IdentifiantAdapterService.getCurrentApp()).thenReturn("testapp");
        var res = s3IdentifiantAdapterService.getObjectLocation("onyx/lot/", "name.txt");
        assertThat(res).isNotNull();
        assertThat(res.getBucketName()).isEqualTo("onyx-00000");
        assertThat(res.getObjectKey()).isEqualTo("lot/name.txt");
    }

    @Test
    void getObjectLocation_other_app_0() {
        Mockito.when(s3IdentifiantAdapterService.getCurrentTenant()).thenReturn("00000");
        Mockito.when(s3IdentifiantAdapterService.getCurrentApp()).thenReturn("testapp");
        var res = s3IdentifiantAdapterService.getObjectLocation("onyx/lot", "name.txt");
        assertThat(res).isNotNull();
        assertThat(res.getBucketName()).isEqualTo("onyx-00000");
        assertThat(res.getObjectKey()).isEqualTo("lot/name.txt");
    }
    
}
