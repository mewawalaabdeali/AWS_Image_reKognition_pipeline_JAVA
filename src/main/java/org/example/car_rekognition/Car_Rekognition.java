package org.example.car_rekognition;

import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;


import java.util.ArrayList;
import java.util.List;

public class Car_Rekognition {
    public static void main(String[] args) {

        //Data Declaration
        String bucketName = "my-image-recognition-demo-bucket";
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/590183997993/image-recognition-demo";

        //Step1: Initializing the s3 client
        S3Client s3 = S3Client.builder().region(Region.US_EAST_1).build();


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

        //Now moving on to the part with Rekognition
        //Step1 : initializing Rekognition Client
        RekognitionClient rekognition = RekognitionClient.builder().region(Region.US_EAST_1).build();

        //Step2: We will also initialize SQS client
        SqsClient sqs = SqsClient.builder().region(Region.US_EAST_1).build();


        //Step : 3Now since we have processed the images with S3 above, we will ask rekognition to process only those images based on our filter
        for (String imageFile:imageFiles){
            DetectLabelsRequest detectRequest = DetectLabelsRequest.builder()
                    .image(Image.builder().s3Object(software.amazon.awssdk.services.rekognition.model.S3Object.builder()
                            .bucket(bucketName).name(imageFile).build()).build()).maxLabels(10).build();


            //Calling rekognition to detect labels
            DetectLabelsResponse detectResponse = rekognition.detectLabels(detectRequest);

            //Checking for Car labels
            boolean carDetected = false;
            for (Label label: detectResponse.labels()){
                if(label.name().equals("Car") && label.confidence()>=80.0f){
                    System.out.println("Car detected in image : " +imageFile);
                    carDetected = true;

                    //Here we will write method to send messages as well to SQS
                    SendMessageRequest sendMsgRequest = SendMessageRequest.builder().queueUrl(queueUrl)
                            .messageBody(imageFile).build();
                    sqs.sendMessage(sendMsgRequest);
                    System.out.println("Car detected in image: " + imageFile + ". Sent to SQS.");


                }
            }
            if(!carDetected){
                System.out.println("No car detected in image : " +imageFile);
            }
        }

        //We will declare a termination message
        SendMessageRequest terminationMsgRequest = SendMessageRequest.builder().queueUrl(queueUrl)
                .messageBody("-1").build();
        sqs.sendMessage(terminationMsgRequest);

        System.out.println("Sent termination message to SQS");

            }
        }



