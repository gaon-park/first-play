package service;

import com.google.inject.ImplementedBy;

import java.io.File;

@ImplementedBy(AWSServiceImpl.class)
public interface AWSService {
    String uploadFile(File file);
    void removeFile(String filePath);
}
