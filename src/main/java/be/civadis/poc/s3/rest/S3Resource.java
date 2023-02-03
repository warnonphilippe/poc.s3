package be.civadis.poc.s3.rest;


import be.civadis.poc.s3.federation.s3.S3Service;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

@RestController
@RequestMapping("s3")
public class S3Resource {

    private S3Service s3;

    public S3Resource(S3Service s3) {
        this.s3 = s3;
    }

    @GetMapping("/{bucket}")
    public ResponseEntity<List<String>> getObjectsList(@PathVariable("bucket") String bucket,  @RequestParam(value = "recursive", required = false) Boolean recursive) throws Exception {
        return ResponseEntity.ok(s3.getObjectsList(bucket, recursive));
    }
    @PutMapping("/{bucket}/{keyBase}/**")
    public void createObject(@PathVariable("bucket") String bucket, @PathVariable("keyBase") String keyBase, @RequestBody String objectContent, HttpServletRequest request) throws Exception {
        s3.createObjectString(bucket, getObjectName(keyBase, request), objectContent);
    }

    @GetMapping("/{bucket}/{keyBase}/**")
    ResponseEntity<String> getObjectContent(@PathVariable("bucket") String bucket, @PathVariable("keyBase") String keyBase, @RequestParam(value = "versionId", required = false) String versionId, HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(s3.getObjectContentString(bucket, getObjectName(keyBase, request), versionId));
    }

    // TODO : create Object et get Object de type file


    @GetMapping("/{bucket}/infos/{keyBase}/**")
    ResponseEntity<List<String>> getObjectVersions(@PathVariable("bucket") String bucket, @PathVariable("keyBase") String keyBase, HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(s3.getObjectVersions(bucket, getObjectName(keyBase, request)));
    }

    private String getObjectName(String keyBase,HttpServletRequest request){
        final String path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
        final String bestMatchingPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();

        String arguments = new AntPathMatcher().extractPathWithinPattern(bestMatchingPattern, path);

        String objectName;
        if (null != arguments && !arguments.isEmpty()) {
            objectName = keyBase + '/' + arguments;
        } else {
            objectName = keyBase;
        }

        return objectName;
    }
}
