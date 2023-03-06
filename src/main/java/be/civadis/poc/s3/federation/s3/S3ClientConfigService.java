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
    //private final String URL = "http://localhost:9000";
    private final String URL = "https://172.18.10.211:443";
    private static Map<String, S3ClientConfigService.ConnectionInfos> keyMap = new HashMap();
    static {

        // Netap
        keyMap.put("testapp_00000", new S3ClientConfigService.ConnectionInfos("84X2_7_AUIa1GN8xAOu_4_1UFXQ0_lfl510J09wca0HI9sMBqBKdEU_BPJcJ19u3yTAD_rPa1B7uBjs1pC8j_01ZJZApq9u7hsup6sTkA7n9t_qBcuQSh6g5a_bacVDC",
                                                                              "6XA_OEg6NuhB2KBATCxzwlNnA45S_HjZ58BV83_05nz_TR_1y4dL4zPvM9b3n_xBk8j_OqAr8Etp5g2PXX0gZ1331h8cJ10cE_nlX9SYp9vA8D55mFCz1f989Za3dwG8"));

        //keyMap.put("testapp_00000", new S3ClientConfigService.ConnectionInfos("xchhmSRz98sphyUn", "83g5sYKZFDfxWQiNUYUCSlb3Z46ve9bM"));
        //keyMap.put("testapp_00000", new S3ClientConfigService.ConnectionInfos("lYZElDKqeVgsqmVI", "NUQiqC61QF4FgtCOJaVopK6UuyGGqkUo"));
        //keyMap.put("onyx_00000", new S3ClientConfigService.ConnectionInfos("tVzyYlpHY0eTfwYq", "MgTzHAu0vdZALd3cIFUOF4ftY3FLJ1GG"));
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
