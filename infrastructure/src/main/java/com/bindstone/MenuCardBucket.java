package com.bindstone;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.s3.Bucket;

public class MenuCardBucket {

    public static Bucket build(Stack stack) {

        return Bucket.Builder.create(stack, "mama_pizza_menu_card")
                //.bucketName("mama_pizza_menu_card")
                //.versioned(true)
                //.encryption(BucketEncryption.KMS_MANAGED)
                .build();
    }
}
