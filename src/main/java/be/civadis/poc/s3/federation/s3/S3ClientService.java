package be.civadis.poc.s3.federation.s3;

import be.civadis.poc.s3.federation.exception.SystemeStockageException;
import be.civadis.poc.s3.utils.FichierUtils;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class S3ClientService {

    private static final Logger logger = LoggerFactory.getLogger(S3ClientService.class);

    private final S3ClientConfigService clientConfigService;

    public S3ClientService(S3ClientConfigService clientConfigService) {
        this.clientConfigService = clientConfigService;
    }

    public List<String> getObjectsList(String bucketName, Boolean recursive) throws SystemeStockageException {
        try {
            List<String> codeList = new ArrayList<>();
            var results = clientConfigService.getMinIOClient().listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .recursive(Boolean.TRUE.equals(recursive))
                    .build());
            for (Result<Item> result : results) {
                Item item = result.get();
                codeList.add(item.objectName());
            }
            return codeList;
        } catch (Exception e){
            throw new SystemeStockageException(e.getMessage(), e);
        }

    }

    public List<String> getObjectVersions(String bucketName, String objectKey) throws SystemeStockageException {
        try {

            var builder = ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(objectKey)
                    .includeVersions(true);

            var results = clientConfigService.getMinIOClient().listObjects(builder.build());

            List<Item> itemList = new ArrayList<>();
            for (Result<Item> result : results) {
                itemList.add(result.get());
            }

            return itemList.stream()
                    .sorted(Comparator.comparing(Item::lastModified).reversed())
                    .map(Item::versionId)
                    .toList();

        } catch (Exception e){
            throw new SystemeStockageException(e.getMessage(), e);
        }

    }

    public void createObject(String bucketName, String objectKey, Resource fileRes) throws SystemeStockageException {
        InputStream is = null;
        File file = null;
        try {
            file = FichierUtils.getFileFromResource(fileRes);
            is = new FileInputStream(file);
            callPutObject(bucketName, objectKey, is);

        } catch (Exception e) {
            throw new SystemeStockageException(e.getMessage(), e);

        } finally {
            cleanStream(is);
            cleanFile(file);
        }
    }

    public void createObject(String bucketName, String objectKey, File file) throws SystemeStockageException {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            callPutObject(bucketName, objectKey, is);

        } catch (Exception e) {
            throw new SystemeStockageException(e.getMessage(), e);

        } finally {
            cleanStream(is);
        }
    }

    private void callPutObject(String bucketName, String objectKey, InputStream is) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        clientConfigService.getMinIOClient().putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectKey)
                .stream(is, is.available(), -1)
                .build());
    }

    public Resource getObjectContent(String bucketName, String objectKey, String versionId) throws SystemeStockageException {
        InputStream stream = null;
        try {

            var builder = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey);

            if (versionId != null && !versionId.isBlank() && !versionId.isEmpty()){
                builder.versionId(versionId);
            }

            stream = clientConfigService.getMinIOClient().getObject(builder.build());

            byte[] content = FichierUtils.readBytes(stream);
            return new ByteArrayResource(content);

        } catch (Exception e){
            throw new SystemeStockageException(e.getMessage(), e);
        } finally {
            cleanStream(stream);
        }
    }

    public void copyObject(String srcBucketName, String objectKeySrc, String dstBucketName, String objectKeyDest) throws SystemeStockageException{
        try {
            var src = CopySource.builder()
                    .bucket(srcBucketName)
                    .object(objectKeySrc);

            var dest = CopyObjectArgs.builder()
                    .bucket(dstBucketName)
                    .object(objectKeyDest)
                    .source(src.build());

            clientConfigService.getMinIOClient().copyObject(dest.build());

        } catch (Exception e) {
            throw new SystemeStockageException(e.getMessage(), e);
        }
    }

    public void deleteObject(String bucketName, String objectKey, String versionId) throws SystemeStockageException{
        try {

            var builder = RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey);

            if (versionId != null && !versionId.isBlank() && !versionId.isEmpty()){
                builder.versionId(versionId);
            }

            clientConfigService.getMinIOClient().removeObject(builder.build());

        } catch (Exception e){
            throw new SystemeStockageException(e.getMessage(), e);
        }
    }

    private void cleanFile(File file){
        if (file != null){
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    private void cleanStream(InputStream is){
        if (is != null){
            try {
                is.close();
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

}
