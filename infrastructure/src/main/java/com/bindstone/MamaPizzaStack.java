package com.bindstone;

import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.*;
import software.amazon.awscdk.services.apigatewayv2.alpha.*;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.HttpLambdaIntegration;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.HttpLambdaIntegrationProps;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.assets.AssetOptions;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static software.amazon.awscdk.BundlingOutput.ARCHIVED;

public class MamaPizzaStack extends Stack {
    public MamaPizzaStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public MamaPizzaStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        var bucket = Bucket.Builder.create(this, "mama_pizza_menu_card")
                //.versioned(true)
                //.encryption(BucketEncryption.KMS_MANAGED)
                .build();


        HttpApi httpApi = createFillStockLambda();

        new CfnOutput(this, "fill_stock_api", CfnOutputProps.builder()
                .description("Url for Fill Stock Api")
                .value(httpApi.getApiEndpoint())
                .build());
    }

    @NotNull
    private HttpApi createFillStockLambda() {
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

        Function lambdaFillStock = new Function(this, "lambda_fill_stock", FunctionProps.builder()
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

        HttpApi httpApi = new HttpApi(this, "lambda-fill-stock-api", HttpApiProps.builder()
                .apiName("lambda-fill-stock-api")
                .build());

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/lambda-fill-stock")
                .methods(singletonList(HttpMethod.GET))
                .integration(new HttpLambdaIntegration("lambda_fill_stock", lambdaFillStock, HttpLambdaIntegrationProps.builder()
                        .payloadFormatVersion(PayloadFormatVersion.VERSION_2_0)
                        .build()))
                .build());
        return httpApi;
    }
}
