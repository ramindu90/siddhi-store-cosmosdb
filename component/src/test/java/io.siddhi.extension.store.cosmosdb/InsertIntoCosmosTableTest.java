/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package io.siddhi.extension.store.cosmosdb;

import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.SiddhiManager;
import io.siddhi.core.exception.SiddhiAppCreationException;
import io.siddhi.core.stream.input.InputHandler;
import io.siddhi.query.api.exception.DuplicateDefinitionException;
import io.siddhi.query.api.exception.SiddhiAppValidationException;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;

public class InsertIntoCosmosTableTest {

    private static final Logger log = Logger.getLogger(InsertIntoCosmosTableTest.class);
    private static String uri = CosmosTableTestUtils.resolveBaseUri();
    private static final String key = CosmosTableTestUtils.resolveMasterKey();
    private static final String database = CosmosTableTestUtils.resolveDatabase();

    @BeforeClass
    public void init() {
        log.info("== Cosmos Table INSERT tests started ==");
    }

    @AfterClass
    public void shutdown() {
        log.info("== Cosmos Table INSERT tests completed ==");
    }

    @Test
    public void insertIntoCosmosTableTest1() throws InterruptedException {
        log.info("insertIntoCosmosTableTest1 - Insert events to a CosmosDB table successfully");
        String collectionLink = String.format("/dbs/%s/colls/%s", database, "FooTable");
        CosmosTableTestUtils.dropCollection(uri, key, collectionLink);
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" +
                "@source(type='inMemory', topic='stock') " +
                "define stream FooStream (symbol string, price float, volume long); " +
                "@store(type = 'cosmosdb' , uri='" + uri + "', access.key='" + key + "', " +
                "database.name='" + database + "')" +
                "define table FooTable (symbol string, price float, volume long);";
        String query = "" +
                "@info(name = 'query1') " +
                "from FooStream " +
                "select symbol, price, volume " +
                "insert into FooTable;";
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler fooStream = siddhiAppRuntime.getInputHandler("FooStream");
        siddhiAppRuntime.start();
        fooStream.send(new Object[]{"WSO2", 55.6f, 100L});
        siddhiAppRuntime.shutdown();
        long totalDocumentsInCollection = CosmosTableTestUtils.getDocumentsCount(uri, key, "FooTable",
                collectionLink);
        Assert.assertEquals(totalDocumentsInCollection, 1, "Insertion failed");
    }

    @Test(expectedExceptions = DuplicateDefinitionException.class)
    public void insertIntoCosmosTableTest2() {
        log.info("insertIntoCosmosTableTest2 - " +
                "Insert events to a CosmosDB table when query has less attributes to select from");
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" +
                "@source(type='inMemory', topic='stock') " +
                "define stream FooStream (symbol string, price float, volume long); " +
                "@store(type = 'cosmosdb' , uri='" + uri + "', access.key='" + key + "', " +
                "database.name='" + database + "')" +
                "define table FooTable (symbol string, price float, volume long);";
        String query = "" +
                "@info(name = 'query1') " +
                "from FooStream " +
                "select symbol, volume " +
                "insert into FooTable;";
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        siddhiAppRuntime.start();
        siddhiAppRuntime.shutdown();
    }

    @Test(expectedExceptions = SiddhiAppValidationException.class)
    public void insertIntoCosmosTableTest3() {
        log.info("insertIntoCosmosTableTest3 - " +
                "Insert events to a CosmosDB table when query has more attributes to select from");
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" +
                "@source(type='inMemory', topic='stock') " +
                "define stream FooStream (symbol string, price float, volume long); " +
                "@store(type = 'cosmosdb' , uri='" + uri + "', access.key='" + key + "', " +
                "database.name='" + database + "')" +
                "define table FooTable (symbol string, price float, volume long);";
        String query = "" +
                "@info(name = 'query1') " +
                "from FooStream " +
                "select symbol, price, length, volume " +
                "insert into FooTable;";
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        siddhiAppRuntime.start();
        siddhiAppRuntime.shutdown();
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void insertIntoCosmosTableTest4() {
        log.info("insertIntoCosmosTableTest4 - " +
                "Insert events to a CosmosDB table by selecting from non existing stream");
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" +
                "@source(type='inMemory', topic='stock') " +
                "define stream FooStream (symbol string, price float, volume long); " +
                "@store(type = 'cosmosdb' , uri='" + uri + "', access.key='" + key + "', " +
                "database.name='" + database + "')" +
                "define table FooTable (symbol string, price float, volume long);";
        String query = "" +
                "@info(name = 'query1') " +
                "from FooStream123 " +
                "select symbol, price, volume " +
                "insert into FooTable;";
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        siddhiAppRuntime.start();
        siddhiAppRuntime.shutdown();
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void insertIntoCosmosTableTest5() {
        log.info("insertIntoCosmosTableTest5 - " +
                "Insert events to a CosmosDB table when the stream has not defined");
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" +
                "@source(type='inMemory', topic='stock') " +
                "@store(type = 'cosmosdb' , uri='" + uri + "', access.key='" + key + "', " +
                "database.name='" + database + "')" +
                "define table FooTable (symbol string, price float, volume long);";
        String query = "" +
                "@info(name = 'query1') " +
                "from FooStream " +
                "select symbol, price, volume " +
                "insert into FooTable;";
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        siddhiAppRuntime.start();
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void insertIntoCosmosTableTest6() {
        log.info("insertIntoCosmosTableTest6 - " +
                "Insert events data to CosmosDB table when the table has not defined");
        String collectionLink = String.format("/dbs/%s/colls/%s", database, "FooTable");
        CosmosTableTestUtils.dropCollection(uri, key, collectionLink);
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" +
                "@source(type='inMemory', topic='stock') " +
                "define stream FooStream (symbol string, price float, volume long); " +
                "@store(type = 'cosmosdb' , uri='" + uri + "', access.key='" + key + "', " +
                "database.name='" + database + "')";
        String query = "" +
                "@info(name = 'query1') " +
                "from FooStream " +
                "select symbol, price, volume " +
                "insert into FooTable;";
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        siddhiAppRuntime.start();
        siddhiAppRuntime.shutdown();
        String databaseLink = String.format("/dbs/%s", database);
        boolean doesCollectionExists = CosmosTableTestUtils.doesCollectionExists(uri, key, databaseLink,
                "FooTable");
        Assert.assertEquals(doesCollectionExists, false, "Definition was created");
    }

    @Test
    public void insertIntoCosmosTableTest7() throws InterruptedException {
        log.info("insertIntoCosmosTableTest7");
        //Object inserts
        String collectionLink = String.format("/dbs/%s/colls/%s", database, "FooTable");
        CosmosTableTestUtils.dropCollection(uri, key, collectionLink);
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" +
                "@source(type='inMemory', topic='stock') " +
                "define stream FooStream (symbol string, price float, input Object); " +
                "@store(type = 'cosmosdb' , uri='" + uri + "', access.key='" + key + "', " +
                "database.name='" + database + "')" +
                "define table FooTable (symbol string, price float, input Object);";
        String query = "" +
                "@info(name = 'query1') " +
                "from FooStream " +
                "select symbol, price, input " +
                "insert into FooTable;";
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler fooStream = siddhiAppRuntime.getInputHandler("FooStream");
        siddhiAppRuntime.start();
        HashMap<String, String> input = new HashMap<>();
        input.put("symbol", "IBM");
        fooStream.send(new Object[]{"WSO2", 55.6f, input});
        siddhiAppRuntime.shutdown();
        long totalDocumentsInCollection = CosmosTableTestUtils.getDocumentsCount(uri, key, "FooTable",
                collectionLink);
        Assert.assertEquals(totalDocumentsInCollection, 1, "Insertion failed");
    }
}
