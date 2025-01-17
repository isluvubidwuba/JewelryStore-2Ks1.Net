package com.ks1dotnet.jewelrystore.service;

// jewelrystore-1c5a9
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
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

@Service
public class FirebaseStorageService {
    @Value("${firebase.img-url}")
    private String img_url;

    @Value("${fileUpload.userPath}")
    private String filePath;

    public ResponseData uploadImage(MultipartFile file, String folder) {
        if (file.getSize() <= 0)
            throw new ApplicationException("Upload image failed because image not found!",
                    HttpStatus.BAD_REQUEST);
        try {
            LocalDate myLocalDate = LocalDate.now();
            String fileName = UUID.randomUUID().toString() + "_" + myLocalDate.toString();

            Bucket bucket = StorageClient.getInstance().bucket();

            BlobInfo blobInfo = BlobInfo.newBuilder(bucket.getName(), folder + fileName).build();
            bucket.create(blobInfo.getName(), file.getBytes(), file.getContentType());

            return new ResponseData(HttpStatus.OK, "Upload image succesfully", fileName);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at uploadImage firabaseStorageService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());

        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at uploadImage firabaseStorageService: " + e.getMessage(),
                    "Error upload image!");
        }
    }

    public ResponseData deleteImage(String fileName, String folder) {
        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            Blob blob = bucket.get(folder + fileName);
            if (blob == null) {
                throw new ApplicationException("File not found: " + fileName, HttpStatus.NOT_FOUND);
            }
            boolean a = blob.delete();
            return new ResponseData(HttpStatus.OK,
                    a ? "Delete image succesfully" : "Delete image failed", a);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at deleteImage firabaseStorageService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());

        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at deleteImage firabaseStorageService: " + e.getMessage(),
                    "Delete image failed!!");
        }
    }

    public String getFileUrl(String fileName) {
        String fullPath = filePath + fileName;
        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            System.out.println("Bucket name: " + bucket.getName());
            Blob blob = bucket.get(fullPath);
            if (blob != null && blob.exists()) {
                String fileUrl = img_url + fullPath;
                System.out.println("File URL: " + fileUrl);
                return fileUrl;
            } else {
                throw new ApplicationException("File not found: " + fullPath, HttpStatus.NOT_FOUND);
            }
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at getFileUrl firabaseStorageService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());


        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getFileUrl firabaseStorageService: " + e.getMessage(),
                    "Get Url failed!!");
        }
    }
}
