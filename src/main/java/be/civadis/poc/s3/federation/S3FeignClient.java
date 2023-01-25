package be.civadis.poc.s3.federation;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;
import java.util.List;

public interface S3FeignClient {

    @RequestLine("PUT /{bucket}")
    @Headers("Content-Type: {contentType}")
    void createBucket(@Param("bucket") String bucket);

    @RequestLine("HEAD /{bucket}")
    @Headers("Content-Type: {contentType}")
    Response existsBucket(@Param("bucket") String bucket);

    @RequestLine("PUT /{bucket}/{key}")
    @Headers("Content-Type: {contentType}")
    void putObject(@Param("bucket") String bucket, @Param("key") String key, @Param("contentType") String contentType, byte[] object);

    @RequestLine("GET /{bucket}/{key}")
    Response getObject(@Param("bucket") String bucket, @Param("key") String key);

    @RequestLine("GET /{bucket}")
    List<String> listObjects(@Param("bucket") String bucket);
}
