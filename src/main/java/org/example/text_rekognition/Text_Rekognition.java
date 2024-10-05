package org.example.text_rekognition;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectTextRequest;
import software.amazon.awssdk.services.rekognition.model.DetectTextResponse;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.TextDetection;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;


public class Text_Rekognition {
    public static void main(String[] args) {
        String bucketName = "cs643-njit-project1";
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/590183997993/image-recognition-demo";
        //Initializing BuffedWriter and FileWriter
        FileWriter fileWriter = null;
        BufferedWriter writer = null;
        boolean terminate = false;


        try {
            fileWriter = new FileWriter("output.txt", false);
            writer = new BufferedWriter(fileWriter);


            //Step1: Initializing Clients
            SqsClient sqs = SqsClient.builder().region(Region.US_EAST_1).build();
            RekognitionClient rekognition = RekognitionClient.builder().region(Region.US_EAST_1).build();
            S3Client s3 = S3Client.builder().region(Region.US_EAST_1).build();


            //Step2 : Start processing SQS messages in a loop
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl).maxNumberOfMessages(10)
                    .waitTimeSeconds(20).build();

            while (true) {
                List<Message> messages = sqs.receiveMessage(receiveRequest).messages();

                for (Message message : messages) {
                    String body = message.body();

                    //Step3: Check for termination signal
                    if (body.equals("-1")) {
                        if (messages.size() == 1) {
                            System.out.println("Termination signal received and queue is empty. Exiting \n");
                            writer.write("Termination signal received and queue is empty. Exiting \n");
                            terminate = true;
                        } else {
                            System.out.println("Termination signal received but the queue is not empty. Continue processing \n");
                            writer.write("Termination signal received but the queue is not empty. Continue processing \n ");
                        }
                        continue;
                    }
                    try {
                        System.out.println("Processing Image : " + body);
                        DetectTextRequest textRequest = DetectTextRequest.builder()
                                .image(Image.builder().s3Object(software.amazon.awssdk.services.rekognition.model.S3Object.builder()
                                        .bucket(bucketName).name(body).build()).build()).build();
                        //Call Rekognition to detect text
                        DetectTextResponse textResponse = rekognition.detectText(textRequest);

                        //Now process the detected text
                        List<TextDetection> textDetections = textResponse.textDetections();
                        if (!textDetections.isEmpty()) {
                            System.out.println("Text Detected in image : " + body);
                            writer.write("Image: " + body + " - Detected Text: ");
                            for (TextDetection text : textDetections) {
                                if (text.confidence() >= 80) {
                                    System.out.println("Detected Text : " + text.detectedText());
                                    writer.write(text.detectedText() + " ");
                                }
                            }
                            writer.write("\n");
                            } else{
                                System.out.println("No Text detected in image : " + body);
                                writer.write("Image: " + body + " - No Text detected\n");
                            }
                            //Delete message from SQS after processing
                            String messageReceiptHandle = message.receiptHandle();
                            sqs.deleteMessage(DeleteMessageRequest.builder().queueUrl(queueUrl)
                                    .receiptHandle(messageReceiptHandle).build());
                        }
                     catch (Exception e) {
                        System.out.println("Error processing image : " + body);
                        e.printStackTrace();

                    }
                }
                if (terminate) {
                    System.out.println("Termination signal processed. Exiting");
                    writer.write("Termination signal processed. Exiting" + "\n");
                    break;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) writer.close();
                if (fileWriter != null) fileWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}


