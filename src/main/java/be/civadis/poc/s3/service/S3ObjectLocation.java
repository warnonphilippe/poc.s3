package be.civadis.poc.s3.service;

public class S3ObjectLocation {
    private String bucketName;
    private String objectKey;

    public S3ObjectLocation(String bucketName, String objectKey) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getObjectKey() {
        return objectKey;
    }
}
