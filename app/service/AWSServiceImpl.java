package service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;

/**
 * This class allows you to upload/delete images on the aws s3.
 */
public class AWSServiceImpl implements AWSService {
    private static final String BUCKET_NAME = "first-play-bucket";
    private static final String ACCESS_KEY = "";
    private static final String SECRET_KEY = "";
    private AmazonS3 amazonS3;

    public AWSServiceImpl(){
        AWSCredentials awsCredentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        amazonS3 = new AmazonS3Client(awsCredentials);
    }

    /**
     * Upload to Aws s3.
     * @param file
     * @return String(file path)
     */
    @Override
    public String uploadFile(File file){
        if(amazonS3 != null){
            try{
                PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME + "/images", file.getName(), file);
                putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
                amazonS3.putObject(putObjectRequest);
            } catch (AmazonServiceException ase){
                ase.printStackTrace();
            } finally {
                amazonS3 = null;
            }
        }
        return BUCKET_NAME + "/images/" + file.getName();
    }

    /**
     * Delete the corresponding file in the aws s3.
     * @param filePath
     */
    @Override
    public void removeFile(String filePath){
        if(amazonS3 != null){
            amazonS3.deleteObject(BUCKET_NAME, filePath);
        }
    }
}
