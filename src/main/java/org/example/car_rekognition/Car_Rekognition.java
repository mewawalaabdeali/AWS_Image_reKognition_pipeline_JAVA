package org.example.car_rekognition;


import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.regions.Region;

public class Car_Rekognition {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("Hello and welcome!");

        /*for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println("i = " + i);*/


        S3Client s3 = S3Client.builder().region(Region.US_EAST_1).build();

        ListBucketsResponse response = s3.listBuckets();

        if (response.buckets().isEmpty()) {
            System.out.println("No buckets found. ");
        } else {
            for (Bucket bucket : response.buckets()) {
                System.out.println(" -- Bucket names: " + bucket.name());
            }
        }
    }
}


