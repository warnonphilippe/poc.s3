package be.civadis.poc.s3.federation.s3;

import be.civadis.poc.s3.utils.ApplicationInfosUtils;
import be.civadis.poc.s3.utils.TenantContext;
import io.minio.MinioClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class S3ClientConfigService {

    // TODO : Dans un vrai projet, A extraire dans config
    private final String URL = "http://localhost:9000";
    private static Map<String, S3ClientConfigService.ConnectionInfos> keyMap = new HashMap();
    static {
        //keyMap.put("testapp_00000", new S3ClientConfigService.ConnectionInfos("xchhmSRz98sphyUn", "83g5sYKZFDfxWQiNUYUCSlb3Z46ve9bM"));
        keyMap.put("testapp_00000", new S3ClientConfigService.ConnectionInfos("lYZElDKqeVgsqmVI", "NUQiqC61QF4FgtCOJaVopK6UuyGGqkUo"));
        keyMap.put("onyx_00000", new S3ClientConfigService.ConnectionInfos("tVzyYlpHY0eTfwYq", "MgTzHAu0vdZALd3cIFUOF4ftY3FLJ1GG"));
    }
    private final String REGION = "eu-west-3";

    // TODO, aussi possible de définir des users, se connecter via token oauth2,...

    //private S3FeignClient s3;
    //private final AmazonS3 s3;
    //private final MinioClient s3;

    public S3ClientConfigService() {
    }

    public MinioClient getMinIOClient(){

        // TODO : Dans un vrai projet, devra être issu de la sécurité (token,...)
        TenantContext.setCurrentTenant("00000");
        ApplicationInfosUtils.initDefaultCurrentApp("testapp");

        return MinioClient.builder()
                .endpoint(URL)
                .credentials(
                        getConnectionInfos(getCurrentStockageUser()).key,
                        getConnectionInfos(getCurrentStockageUser()).secret)
                .build();
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

    private String getCurrentStockageUser(){
        return ApplicationInfosUtils.getCurrentApp() + "_" + TenantContext.getCurrentTenant();
    }

    private S3ClientConfigService.ConnectionInfos getConnectionInfos(String user){
        return keyMap.get(user);
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

}
