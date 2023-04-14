package com.bindstone;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.Table;

public class StockDatabase {

    public static Table build(Stack stack) {

        var table = Table.Builder.create(stack, "mama-pizza-stack")
                .tableName("mama-pizza-stack")
                .partitionKey(Attribute.builder().name("id").type(AttributeType.STRING).build())
                .sortKey(Attribute.builder().name("category").type(AttributeType.STRING).build())
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();

        return table;
    }
}
