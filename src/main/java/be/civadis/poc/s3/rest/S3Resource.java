package be.civadis.poc.s3.rest;


import be.civadis.poc.s3.federation.exception.SystemeStockageException;
import be.civadis.poc.s3.federation.s3.S3ClientService;
import be.civadis.poc.s3.utils.FichierUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

@RestController
@RequestMapping("s3")
public class S3Resource {

    private S3ClientService s3;

    public S3Resource(S3ClientService s3) {
        this.s3 = s3;
    }

    @GetMapping("/{bucket}")
    public ResponseEntity<List<String>> getObjectsList(@PathVariable("bucket") String bucket,  @RequestParam(value = "recursive", required = false) Boolean recursive) throws Exception {
        return ResponseEntity.ok(s3.getObjectsList(bucket, recursive));
    }

    @PutMapping("/{bucket}/{keyBase}/**")
    public void createObject(@PathVariable("bucket") String bucket, @PathVariable("keyBase") String keyBase, @RequestPart("file") MultipartFile objectContent,
                             @RequestHeader(value = "Content-Transfer-Encoding", required = false) String encoding, HttpServletRequest request) throws Exception {
        Resource resource = FichierUtils.decodeBase64Multipart(objectContent, encoding).getResource();
        s3.createObject(bucket, getObjectKey(keyBase, request), resource);
    }

    @GetMapping("/{bucket}/{keyBase}/**")
    ResponseEntity<Resource> getObjectContent(@PathVariable("bucket") String bucket, @PathVariable("keyBase") String keyBase, @RequestParam(value = "versionId", required = false) String versionId, HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(s3.getObjectContent(bucket, getObjectKey(keyBase, request), versionId));
    }

    @DeleteMapping("/{bucket}/{keyBase}/**")
    ResponseEntity deleteObject(@PathVariable("bucket") String bucket, @PathVariable("keyBase") String keyBase, @RequestParam(value = "versionId", required = false) String versionId, HttpServletRequest request) throws Exception {
        s3.deleteObject(bucket, getObjectKey(keyBase, request), versionId);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/{bucket}/versions/{keyBase}/**")
    ResponseEntity<List<String>> getObjectVersions(@PathVariable("bucket") String bucket, @PathVariable("keyBase") String keyBase, HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(s3.getObjectVersions(bucket, getObjectKey(keyBase, request)));
    }

    @PostMapping("/{bucket}/rename/{keyBase}/**")
    public ResponseEntity renameObject(@PathVariable("bucket") String bucketName, @PathVariable("keyBase") String keyBase, HttpServletRequest request,
                                     @RequestPart("dstkey") String dstKey) throws SystemeStockageException {
        String objectKey = getObjectKey(keyBase, request);
        s3.copyObject(bucketName, objectKey, bucketName, dstKey);
        s3.deleteObject(bucketName, objectKey,null);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/{bucket}/move/{keyBase}/**")
    public ResponseEntity moveObject(@PathVariable("bucket") String bucketName, @PathVariable("keyBase") String keyBase, HttpServletRequest request,
                                     @RequestPart("dstpath") String dstObjectPath) throws SystemeStockageException {
        String objectKey = getObjectKey(keyBase, request);
        String objectName = getObjectName(keyBase, request);
        s3.copyObject(bucketName, objectKey, bucketName, dstObjectPath + "/" + objectName);
        s3.deleteObject(bucketName, objectKey,null);
        return ResponseEntity.ok(null);
    }

    private String getObjectKey(String keyBase,HttpServletRequest request){
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

    private String getObjectName(String keyBase,HttpServletRequest request){
        final String path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
        final String bestMatchingPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();

        String arguments = new AntPathMatcher().extractPathWithinPattern(bestMatchingPattern, path);
        String objectName;
        if (null != arguments && !arguments.isEmpty()) {
            objectName = arguments.substring(arguments.lastIndexOf("/") + 1);
        } else {
            objectName = keyBase;
        }

        return objectName;
    }


}
