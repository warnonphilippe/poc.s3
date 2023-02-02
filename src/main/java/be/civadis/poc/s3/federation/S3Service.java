package be.civadis.poc.s3.federation;

import be.civadis.poc.s3.federation.S3FeignClient;
import be.civadis.poc.s3.utils.FichierUtils;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.SignableRequest;
import com.amazonaws.auth.*;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.common.collect.Lists;
import feign.Feign;
import feign.FeignException;
import feign.Response;
import feign.auth.BasicAuthRequestInterceptor;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import io.minio.messages.Version;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class S3Service {

    // REM : Dans un vrai projet, A extraire dans config
    private final String URL = "http://localhost:9000";
    private final String ACCESS_KEY = "nmlfxEBWDyI6ZF1t";
    private final String SECRET_KEY = "NZoL9EovpXl8cLQqTl0PLPDI1e0THdJC";
    private final String REGION = "eu-west-3";

    // REM, aussi possible de définir des users, se connecter via token oauth2,...
    // TODO : Voir comment restreindre l'accès d'un bucket à un user donné et se connecter avec ce user


    //private S3FeignClient s3;
    //private final AmazonS3 s3;
    private final MinioClient s3;


    public S3Service(S3FeignClient s3FeignClient) {
        //this.s3 = s3FeignClient;
        s3 = getMinIOClient();
    }

    private MinioClient getMinIOClient(){
        return MinioClient.builder()
                        .endpoint(URL)
                        .credentials(ACCESS_KEY, SECRET_KEY)
                        .build();
    }

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
            s3.putObject(PutObjectArgs.builder()
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

            stream = s3.getObject(builder.build());

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
            var results = s3.listObjects(ListObjectsArgs.builder()
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

            var results = s3.listObjects(builder.build());

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

    public void createObject(String bucketName, String objectKey, byte[] objectContent){
        //s3.putObject(bucketName, objectKey,  objectContent); //par la lib
        //s3.putObject(bucketName, objectKey, "text/plain", objectContent);
        throw new RuntimeException("Not implemented yet");
    }

    public String getObjectContent(String bucketName, String objectKey) throws Exception {
        //Response response = s3.getObject(bucketName, objectKey);
        //byte[] object = response.body().asInputStream().readAllBytes();
        //return new String(object, StandardCharsets.UTF_8);
        throw new RuntimeException("Not implemented yet");
    }

    // TODO


    // upload d'autres fichiers que du text
    // essai en passant par DocumentService
    // secu bucket pour certains user
    // https://gist.github.com/krishnasrinivas/2f5a9affe6be6aff42fe723f02c86d6a
    // https://docs.aws.amazon.com/AmazonS3/latest/userguide/example-policies-s3.html
    // https://min.io/docs/minio/linux/administration/identity-access-management/policy-based-access-control.html

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
