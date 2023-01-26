package be.civadis.poc.s3.federation;

import be.civadis.poc.s3.federation.S3FeignClient;
import feign.Feign;
import feign.FeignException;
import feign.Response;
import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class S3Service {

    // REM : Dans un vrai projet, A extraire dans config
    private final String url = "http://localhost:9000";
    private final String ACCESS_KEY = "Fe5Qg1gNzUhIM3KC";
    private final String SECRET_KEY = "OkE7NjQgJsIIy6dHxgy5GNk0VkdzMPOM";
    private final String REGION = "eu-west-3";

    // REM, aussi possible de définir des users, se connecter via token oauth2,...
    // TODO : Voir comment restreindre l'accès d'un bucket à un user donné et se connecter avec ce user


    private final S3FeignClient s3;

    //private String bucketName = "my-test-bucket";

    public S3Service() {
        // REM : dans un vrai projet : a créer via injection et bean de config feign
        s3 = Feign.builder().requestInterceptor(new BasicAuthRequestInterceptor(ACCESS_KEY, SECRET_KEY))
                .target(S3FeignClient.class, url);
    }

    public void createBucket(String bucketName){
        s3.createBucket(bucketName);
    }

    public void createObject(String bucketName, String objectKey, String objectContent){
        s3.putObject(bucketName, objectKey, "text/plain", objectContent.getBytes());
    }

    /**
     * Post un fichier dans un bucket
     * @param bucketName nom du bucket
     * @param objectKey key complète du fichier sous la forme [path]/nom.ext, ex : lot1/file1.txt (dans l'UI minIO; lot1 sera présenté comme un répertoire)
     * @param objectContent contenu du fichier
     */
    public void createObject(String bucketName, String objectKey, byte[] objectContent){
        s3.putObject(bucketName, objectKey, "text/plain", objectContent);
    }

    public String getObjectContent(String bucketName, String objectKey) throws IOException {
        Response response = s3.getObject(bucketName, objectKey);
        byte[] object = response.body().asInputStream().readAllBytes();
        return new String(object, StandardCharsets.UTF_8);
    }

    public List<String> getObjectsList(String bucketName){
        return s3.listObjects(bucketName);
    }

    private boolean existsBucket(String bucketName){
        try { Response response = s3.existsBucket(bucketName);
            return response.status() == 200;
        } catch (FeignException e) {
            if (e.status() == 404) {
                return false;
            }
            throw e;
        }
    }

}
