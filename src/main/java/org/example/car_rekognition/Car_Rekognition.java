package org.example.car_rekognition;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.rekognition.RekognitionClient;


import java.util.ArrayList;
import java.util.List;

public class Car_Rekognition {
    public static void main(String[] args) {

        //Step1: Initializing the s3 client
        S3Client s3 = S3Client.builder().region(Region.US_EAST_1).build();
        String bucketName = "my-image-recognition-demo-bucket";

        //Step2: We will read and list objects from s3
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder().bucket(bucketName).build();
        ListObjectsV2Response response = s3.listObjectsV2(listRequest);

        //Step3: Checking image extensions
        List<String> imageFiles = new ArrayList<>();

        //Step4: Collecting image files from s3
        for (S3Object object: response.contents()){
            String key = object.key();

            //filtering the files for extension
            if(key.endsWith(".jpg") || key.endsWith("jpeg") || key.endsWith("png")){
                System.out.println("Found Images : " + key);
                imageFiles.add(key);
            }
        }

        //Checking the images found and printing them
        System.out.println("Total Images found : " +imageFiles.size());
        System.out.println("Images files found : ");
        for (String imageFile : imageFiles){
            System.out.println(imageFile);
        }

            }
        }



