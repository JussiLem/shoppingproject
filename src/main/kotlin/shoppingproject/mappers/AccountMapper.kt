package shoppingproject.mappers

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import shoppingproject.dynamodb.batch.DynamoMapper
import shoppingproject.model.Account

class AccountMapper : DynamoMapper<Account> {
    override fun mapToDynamoItem(account: Account): Map<String, AttributeValue> {
        return mapOf(Pair("name", AttributeValue(account.name)),
            Pair("email", AttributeValue(account.email)))
    }
}