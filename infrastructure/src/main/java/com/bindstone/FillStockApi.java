package com.bindstone;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.apigatewayv2.alpha.*;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.HttpLambdaIntegration;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.HttpLambdaIntegrationProps;
import software.amazon.awscdk.services.lambda.Function;

import static java.util.Collections.singletonList;

public class FillStockApi {

    public static HttpApi build(Stack stack, Function lambdaFillStock) {

        HttpApi httpApi = new HttpApi(stack, "lambda-fill-stock-api", HttpApiProps.builder()
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
