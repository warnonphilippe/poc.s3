package be.civadis.poc.s3.rest;


import be.civadis.poc.s3.federation.S3Service;
import feign.Param;
import feign.RequestLine;
import feign.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("s3")
public class S3Resource {

    private S3Service s3;

    public S3Resource(S3Service s3) {
        this.s3 = s3;
    }

    @PostMapping("/{bucket}")
    @PutMapping("/bucket")
    public ResponseEntity createBucket(@Param("bucket") String bucket){
        s3.createBucket(bucket);
        return ResponseEntity.ok(bucket + " created");
    }

    @GetMapping("/{bucket}")
    public ResponseEntity<List<String>> getObjectsList(@Param("bucket") String bucket){
        return ResponseEntity.ok(s3.getObjectsList(bucket));
    }

    @PutMapping("/{bucket}/{key}")
    public void createObject(@Param("bucket") String bucket, @Param("key") String key, @RequestBody String objectContent){
        s3.createObject(bucket, key, objectContent.getBytes());
    }

    @GetMapping("/{bucket}/{key}")
    ResponseEntity<String> getObject(@Param("bucket") String bucket, @Param("key") String key) throws IOException {
        return ResponseEntity.ok(s3.getObjectContent(bucket, key));
    }
}
