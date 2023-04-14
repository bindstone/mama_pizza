package com.bindstone;

import software.amazon.awscdk.BundlingOptions;
import software.amazon.awscdk.DockerVolume;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.s3.assets.AssetOptions;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static software.amazon.awscdk.BundlingOutput.ARCHIVED;

public class FillStockLambda {

    public static Function build(Stack stack) {

        List<String> functionOnePackagingInstructions = Arrays.asList(
                "/bin/sh",
                "-c",
                "cd lambda_fill_stock " +
                        "&& mvn clean install " +
                        "&& cp /asset-input/lambda_fill_stock/target/lambda_fill_stock.jar /asset-output/"
        );

        BundlingOptions.Builder builderOptions = BundlingOptions.builder()
                .command(functionOnePackagingInstructions)
                .image(Runtime.JAVA_11.getBundlingImage())
                .volumes(singletonList(
                        // Mount local .m2 repo to avoid download all the dependencies again inside the container
                        DockerVolume.builder()
                                .hostPath(System.getProperty("user.home") + "/.m2/")
                                .containerPath("/root/.m2/")
                                .build()
                ))
                .user("root")
                .outputType(ARCHIVED);

        Function lambdaFillStock = new Function(stack, "lambda_fill_stock", FunctionProps.builder()
                .runtime(Runtime.JAVA_11)
                .code(Code.fromAsset("../", AssetOptions.builder()
                        .bundling(builderOptions
                                .command(functionOnePackagingInstructions)
                                .build())
                        .build()))
                .handler("com.bindstone.lambda_fill_stock.Main")
                .memorySize(1024)
                .timeout(Duration.seconds(10))
                .logRetention(RetentionDays.ONE_WEEK)
                .build());

        return lambdaFillStock;
    }
}
