package com.ks1dotnet.jewelrystore.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.ks1dotnet.jewelrystore.exception.RunTimeExceptionV1;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

@Service
public class FirebaseStorageService {
    @Value("${firebase.img-url}")
    private String img_url;

    public ResponseData uploadImage(MultipartFile file, String folder) {
        try {
            LocalDate myLocalDate = LocalDate.now();
            String fileName = UUID.randomUUID().toString() + "_" + myLocalDate.toString();

            Bucket bucket = StorageClient.getInstance().bucket();

            BlobInfo blobInfo = BlobInfo.newBuilder(bucket.getName(), folder + fileName).build();
            bucket.create(blobInfo.getName(), file.getBytes(), file.getContentType());

            return new ResponseData(HttpStatus.OK, "Upload image succesfully", fileName);
        } catch (Exception e) {
            throw new RunTimeExceptionV1("Upload imgae fail ", e.getMessage());
        }
    }

    public ResponseData deleteImage(String fileName, String folder) {
        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            Blob blob = bucket.get(folder + fileName);
            if (blob == null) {
                throw new RunTimeExceptionV1("File not found: " + fileName);
            }
            boolean a = blob.delete();
            return new ResponseData(HttpStatus.OK, a ? "Delete image succesfully" : "Delete image failed", a);
        } catch (Exception e) {
            throw new RunTimeExceptionV1("Delete imgae fail ", e.getMessage());
        }
    }
}