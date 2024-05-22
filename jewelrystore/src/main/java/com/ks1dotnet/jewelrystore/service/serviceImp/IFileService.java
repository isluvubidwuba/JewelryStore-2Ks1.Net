package com.ks1dotnet.jewelrystore.service.serviceImp;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {
    boolean savefile(MultipartFile file);

    Resource loadFile(String fileName);
}
