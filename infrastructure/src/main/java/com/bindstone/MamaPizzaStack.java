package com.bindstone;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

public class MamaPizzaStack extends Stack {
    public MamaPizzaStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public MamaPizzaStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        var menuCardBucket = MenuCardBucket.build(this);
        var fillStockLambda = FillStockLambda.build(this);
        var fillStockApi = FillStockApi.build(this, fillStockLambda);
        var database = StockDatabase.build(this);

        new CfnOutput(this, "fill_stock_api", CfnOutputProps.builder()
                .description("Url for Fill Stock Api")
                .value(fillStockApi.getApiEndpoint())
                .build());
    }
}
