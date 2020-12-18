package shoppingproject.dynamodb.dsl

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import java.nio.ByteBuffer

fun toAttributeValue(value: Any): AttributeValue {
    return when(value){
        is ByteBuffer -> AttributeValue().withB(value)
        is String -> AttributeValue(value)
        is Number -> AttributeValue().withN(value.toString())
        is Boolean -> AttributeValue().withBOOL(value)
        is List<*> -> AttributeValue().withL(value.map { toAttributeValue(it!!) })
        is Map<*, *> -> AttributeValue().withM(value.entries.associate { it.key as String to toAttributeValue(it.value!!) })
        is Set<*> -> when(value.first()){
            is ByteBuffer -> AttributeValue().withBS(value.map { it as ByteBuffer })
            is Number -> AttributeValue().withNS(value.map { (it as Number).toString() })
            else -> AttributeValue().withSS(value.map { it as String })
        }
        else -> AttributeValue(value.toString())
    }
    //Do we need to handle NULL type?
}