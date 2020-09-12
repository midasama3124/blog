package com.mmanchola.blog.service;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.mmanchola.blog.storage.ImageStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.mmanchola.blog.model.BucketName.IMAGE_STORAGE;
import static org.apache.http.entity.ContentType.*;

@Service
public class ImageService {

    private final ImageStore imageStore;

    @Autowired
    public ImageService(ImageStore imageStore) {
        this.imageStore = imageStore;
    }

    public List<S3ObjectSummary> getObjectSummaries() {
        return imageStore.listFilenames();
    }

    public void uploadImage(MultipartFile file) {
        // Check if file is not empty
        isFileEmpty(file);
        // Check if file is image
        isImage(file);
        // Grab metadata from file if any
        Map<String, String> metadata = extractMetadata(file);
        // Store image into s3
        try {
            imageStore.save(
                    IMAGE_STORAGE.toString(),
                    file.getOriginalFilename(),
                    Optional.of(metadata),
                    file.getInputStream()
            );
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Map<String, String> extractMetadata(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Size", String.valueOf(file.getSize()));
        return metadata;
    }

    private void isImage(MultipartFile file) {
        if (!Arrays.asList(
                IMAGE_JPEG.getMimeType(),
                IMAGE_GIF.getMimeType(),
                IMAGE_SVG.getMimeType(),
                IMAGE_PNG.getMimeType()).contains(file.getContentType())) {
            throw new IllegalStateException("File must be an image");
        }
    }

    private void isFileEmpty(MultipartFile file) {
        if (file.isEmpty())
            throw new IllegalStateException("Cannot upload empty file [" + file.getSize() + "]");
    }

    public byte[] downloadImage(String key) {
        return imageStore.download(IMAGE_STORAGE.toString(), key);
    }

    public void delete(String key) throws IllegalStateException {
        imageStore.delete(key);
    }

}
