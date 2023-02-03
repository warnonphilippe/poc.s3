package be.civadis.poc.s3.federation.s3;

import be.civadis.poc.s3.federation.exception.SystemeStockageException;
import be.civadis.poc.s3.utils.FichierUtils;
import io.minio.*;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

@Service
public class S3ClientService {

    private static final Logger logger = LoggerFactory.getLogger(S3ClientService.class);

    private final S3ClientConfigService clientConfigService;

    public S3ClientService(S3ClientConfigService clientConfigService) {
        this.clientConfigService = clientConfigService;
    }

    /**
     * Post un fichier dans un bucket
     * @param bucketName nom du bucket
     * @param objectKey key complète du fichier sous la forme [path]/nom.ext, ex : lot1/file1.txt (dans l'UI minIO; lot1 sera présenté comme un répertoire)
     * @param objectContent contenu du fichier
     */
    public void createObjectString(String bucketName, String objectKey, String objectContent) throws SystemeStockageException {

        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(objectContent.getBytes("UTF-8"));
            clientConfigService.getMinIOClient().putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .stream(bais, bais.available(), -1)
                    .build());

        } catch (Exception e) {
            throw new SystemeStockageException(e.getMessage(), e);
        } finally {
            if (bais != null){
                try {
                    bais.close();
                } catch (IOException e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }

    }

    public String getObjectContentString(String bucketName, String objectKey, String versionId) throws SystemeStockageException {
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
            return new String(content, StandardCharsets.UTF_8);

        } catch (Exception e){
            throw new SystemeStockageException(e.getMessage(), e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
    }

    public List<String> getObjectsList(String bucketName, Boolean recursive) throws Exception {
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

    public List<String> getObjectVersions(String bucketName, String objectKey) throws Exception {
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

    public void createObject(String bucketName, String objectKey, Resource fileRes) throws Exception{
        InputStream is = null;
        File file = null;
        try {
            file = FichierUtils.getFileFromResource(fileRes);
            is = new FileInputStream(file);
            clientConfigService.getMinIOClient().putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .stream(is, is.available(), -1)
                    .build());

        } catch (Exception e) {
            throw new SystemeStockageException(e.getMessage(), e);
        } finally {
            if (is != null){
                is.close();
            }
            if (file != null){
                Files.delete(file.toPath());
            }
        }
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
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
    }

    public void copyObject(String bucketName, String objectKeySrc, String objectKeyDest) throws Exception{
        try {
            var src = CopySource.builder()
                    .bucket(objectKeySrc)
                    .object(objectKeySrc);

            var dest = CopyObjectArgs.builder()
                    .bucket(bucketName)
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

}
