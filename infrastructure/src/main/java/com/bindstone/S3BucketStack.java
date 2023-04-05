package com.bindstone;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.s3.Bucket;
import software.constructs.Construct;

public class S3BucketStack extends Stack {
    public S3BucketStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public S3BucketStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Bucket bucket = Bucket.Builder.create(this, "mama_pizza_menu_card")
                //.versioned(true)
                //.encryption(BucketEncryption.KMS_MANAGED)
                .build();
    }
}
