package be.civadis.poc.s3.rest;


import be.civadis.poc.s3.federation.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity createBucket(String bucket){
        s3.createBucket(bucket);
        return ResponseEntity.ok(bucket + " created");
    }

    @GetMapping("/{bucket}")
    public ResponseEntity<List<String>> getObjectsList(String bucketName){
        return ResponseEntity.ok(s3.getObjectsList(bucketName));
    }
}
