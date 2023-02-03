package be.civadis.poc.s3.federation.s3;

import be.civadis.poc.s3.utils.ApplicationInfosUtils;
import be.civadis.poc.s3.utils.FichierUtils;
import be.civadis.poc.s3.utils.TenantContext;
import io.minio.*;
import io.minio.messages.Item;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

@Service
public class S3Service {

    // REM : Dans un vrai projet, A extraire dans config
    private final String URL = "http://localhost:9000";
    private static Map<String, ConnectionInfos> keyMap = new HashMap();
    static {
        keyMap.put("testapp_00000", new ConnectionInfos("t1Cdwo2WqInZhdsb", "4ZGYCwmjhs4ZRlUsZ1kIyZOnxmongs7D"));
        keyMap.put("onyx_00000", new ConnectionInfos("tVzyYlpHY0eTfwYq", "MgTzHAu0vdZALd3cIFUOF4ftY3FLJ1GG"));
    }
    private final String REGION = "eu-west-3";

    // TODO, aussi possible de définir des users, se connecter via token oauth2,...

    //private S3FeignClient s3;
    //private final AmazonS3 s3;
    //private final MinioClient s3;

    public S3Service() {
        // REM : Dans un vrai porjet, devra être issu de la sécurité (token,...)
        TenantContext.setCurrentTenant("00000");
        ApplicationInfosUtils.initDefaultCurrentApp("testapp");
    }

    private MinioClient getMinIOClient(){
        return MinioClient.builder()
                        .endpoint(URL)
                        .credentials(
                                getConnectionInfos(getCurrentStockageUser()).key,
                                getConnectionInfos(getCurrentStockageUser()).secret)
                        .build();
    }

    private String getCurrentStockageUser(){
        return ApplicationInfosUtils.getCurrentApp() + "_" + TenantContext.getCurrentTenant();
    }

    private ConnectionInfos getConnectionInfos(String user){
        return keyMap.get(user);
    }
/*
    private AmazonS3 getAmazonCLient(){
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);

        ClientConfiguration config = new ClientConfiguration();
        config.setProtocol(Protocol.HTTP);
        config.setProxyHost("localhost");
        config.setProxyPort(8080);

        return AmazonS3ClientBuilder.standard()
                .withClientConfiguration(config)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(URL, REGION))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                //.withRegion(Regions.US_EAST_1)
                .build();
    }
*/

    /**
     * Post un fichier dans un bucket
     * @param bucketName nom du bucket
     * @param objectKey key complète du fichier sous la forme [path]/nom.ext, ex : lot1/file1.txt (dans l'UI minIO; lot1 sera présenté comme un répertoire)
     * @param objectContent contenu du fichier
     */
    public void createObjectString(String bucketName, String objectKey, String objectContent) throws Exception {

        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(objectContent.getBytes("UTF-8"));
            getMinIOClient().putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .stream(bais, bais.available(), -1)
                    .build());

        } catch (Exception e) {
            // TODO log...
            throw e;
        } finally {
            if (bais != null){
                bais.close();
            }
        }

    }

    public String getObjectContentString(String bucketName, String objectKey, String versionId) throws Exception {
        InputStream stream = null;
        try {

            var builder = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey);

            if (versionId != null && !versionId.isBlank() && !versionId.isEmpty()){
                builder.versionId(versionId);
            }

            stream = getMinIOClient().getObject(builder.build());

            byte[] content = FichierUtils.readBytes(stream);
            return new String(content, StandardCharsets.UTF_8);

        } catch (Exception e){
            //TODO log
            throw e;
        } finally {
            if (stream != null) stream.close();
        }
    }

    public List<String> getObjectsList(String bucketName, Boolean recursive) throws Exception {
        try {
            List<String> codeList = new ArrayList<>();
            var results = getMinIOClient().listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .recursive(Boolean.TRUE.equals(recursive))
                    .build());
            for (Result<Item> result : results) {
                Item item = result.get();
                codeList.add(item.objectName());
            }
            return codeList;
        } catch (Exception e){
            //TODO log...
            throw e;
        }

    }

    public List<String> getObjectVersions(String bucketName, String objectKey) throws Exception {
        try {

            var builder = ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(objectKey)
                    .includeVersions(true);

            var results = getMinIOClient().listObjects(builder.build());

            List<Item> itemList = new ArrayList<>();
            for (Result<Item> result : results) {
                itemList.add(result.get());
            }

            return itemList.stream()
                    .sorted(Comparator.comparing(Item::lastModified).reversed())
                    .map(Item::versionId)
                    .toList();

        } catch (Exception e){
            //TODO log...
            throw e;
        }

    }

    public void createObject(String bucketName, String objectKey, Resource fileRes) throws Exception{
        InputStream is = null;
        File file = null;
        try {
            file = FichierUtils.getFileFromResource(fileRes);
            is = new FileInputStream(file);
            getMinIOClient().putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .stream(is, is.available(), -1)
                    .build());

        } catch (Exception e) {
            // TODO log...
            throw e;
        } finally {
            if (is != null){
                is.close();
            }
            if (file != null){
                Files.delete(file.toPath());
            }
        }
    }

    public Resource getObjectContent(String bucketName, String objectKey, String versionId) throws Exception {
        InputStream stream = null;
        try {

            var builder = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey);

            if (versionId != null && !versionId.isBlank() && !versionId.isEmpty()){
                builder.versionId(versionId);
            }

            stream = getMinIOClient().getObject(builder.build());

            byte[] content = FichierUtils.readBytes(stream);
            return new ByteArrayResource(content);

        } catch (Exception e){
            //TODO log
            throw e;
        } finally {
            if (stream != null) stream.close();
        }
    }

    private static class ConnectionInfos{

        private String key;
        private String secret;

        public ConnectionInfos(String key, String secret) {
            this.key = key;
            this.secret = secret;
        }

        public String getKey() {
            return key;
        }

        public String getSecret() {
            return secret;
        }
    }

    // TODO

    // essai en passant par DocumentService


    // essai avec lib amazon
    // nettoyer code

    // LINKS

    //Amazon
    // https://nirajsonawane.github.io/2021/05/16/Spring-Boot-with-AWS-S3/
    // https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-s3-objects.html

    //MinIO
    // https://min.io/docs/minio/linux/developers/java/API.html#
    // https://github.com/minio/minio-java/tree/release/examples

}
