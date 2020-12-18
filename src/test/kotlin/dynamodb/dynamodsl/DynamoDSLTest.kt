package dynamodb.dynamodsl

import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import org.junit.jupiter.api.Test
import shoppingproject.dynamodb.dsl.*
import shoppingproject.dynamodb.dsl.conditions.AND
import shoppingproject.dynamodb.dsl.filters.*

class DynamoDSLTest {
    @Test
    fun `it should build nested filter queries correctly`(){
        val queryResult = DynamoDSL(AmazonDynamoDBClientBuilder.standard().withRegion(Regions.EU_WEST_1).build())
            .query("mytable") {
                hashKey("myHashKey") {
                    eq(2)
                }
                sortKey("mysortkey"){
                    eq (2)
                    between ( 2 AND 3)
                }
                filtering {
                    attribute("a") {
                        lteq(1)
                    } and attribute("b"){
                        gt(2)
                    } or {
                        attribute("c"){
                            eq(3)
                        } and attributeExists("d") or {
                            attribute("e"){
                                noteq(4)
                            }
                        }
                    } or attributeExists("f")

                }
            }


        if(queryResult.hasNext()){
            queryResult.next()
        }


    }


}