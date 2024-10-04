package org.example.text_rekognition;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectTextRequest;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.util.ArrayList;
import java.util.List;


public class Text_Rekognition {
    public static void main(String[] args) {
        String bucketName = "my-image-recognition-demo-bucket";
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/590183997993/image-recognition-demo";


        //Step1: Initializing Clients
        SqsClient sqs = SqsClient.builder().region(Region.US_EAST_1).build();
        RekognitionClient rekognition = RekognitionClient.builder().region(Region.US_EAST_1).build();
        S3Client s3 = S3Client.builder().region(Region.US_EAST_1).build();

        //Step2: Start processing images from S3
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder().bucket(bucketName).build();
        ListObjectsV2Response response = s3.listObjectsV2(listRequest);

        List<String> imageFiles = new ArrayList<>();

        for (S3Object object : response.contents()){
            String key = object.key();
            if(key.endsWith("jpg") || key.endsWith("jpeg") || key.endsWith("png") ){
                System.out.println("Images found in : " +key);
                imageFiles.add(key);
            }

        }

        //Printing the image details
        System.out.println("Total Images found : " +imageFiles.size());
        System.out.println("Images found : ");
        for(String imageFile : imageFiles){
            System.out.println(imageFile);
        }

        //Now we will process these images with Rekognition for text
        for(String imageFile: imageFiles){
            DetectTextRequest textRequest = DetectTextRequest.builder()
                    .image(Image.builder().s3Object(software.amazon.awssdk.services.rekognition.model.S3Object.builder()
                            .bucket(bucketName).name(imageFile).build()).build()).build();


        }









        }
    }

