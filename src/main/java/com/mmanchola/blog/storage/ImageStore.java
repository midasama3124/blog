package com.mmanchola.blog.storage;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.mmanchola.blog.model.BucketName.IMAGE_STORAGE;

@Service
public class ImageStore {

    private final AmazonS3 s3;

    @Autowired
    public ImageStore(@Qualifier("amazon_s3_bean") AmazonS3 s3) {
        this.s3 = s3;
    }

    public void save(String path, String filename,
                     Optional<Map<String, String>> optionalMetadata,
                     InputStream inputStream) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        optionalMetadata.ifPresent(map -> {
                    if (!map.isEmpty())
                        map.forEach(objectMetadata::addUserMetadata);
                }
        );

        try {
            s3.putObject(path, filename, inputStream, objectMetadata);
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("failed to store file into S3", e);
        }
    }

    public byte[] download(String path, String key) {
        try {
            S3Object object = s3.getObject(path, key);
            return IOUtils.toByteArray(object.getObjectContent());
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("failed to download from s3", e);
        }
    }

    public List<S3ObjectSummary> listFilenames() {
        return s3.listObjects(IMAGE_STORAGE.toString())
                .getObjectSummaries();
    }

    public void delete(String key) {
        try {
            s3.deleteObject(IMAGE_STORAGE.toString(), key);
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("failed to delete image from s3", e);
        }
    }

}
