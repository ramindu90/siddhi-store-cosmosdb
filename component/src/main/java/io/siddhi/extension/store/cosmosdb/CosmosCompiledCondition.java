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

import io.siddhi.core.executor.ExpressionExecutor;
import io.siddhi.core.table.record.UpdateOrInsertReducer;
import io.siddhi.core.util.collection.operator.CompiledCondition;

import java.util.SortedMap;

/**
 * Implementation class of {@link CompiledCondition} corresponding to the CosmosDB Event Table.
 * Maintains the condition string returned by the ConditionVisitor as well as a map of parameters to be used at runtime.
 */
public class CosmosCompiledCondition implements CompiledCondition {

    private String compiledQuery;
    private SortedMap<Integer, Object> parameters;

    public CosmosCompiledCondition(String compiledQuery, SortedMap<Integer, Object> parameters,
                                   UpdateOrInsertReducer updateOrInsertReducer,
                                   ExpressionExecutor inMemorySetExpressionExecutor) {
        this.compiledQuery = compiledQuery;
        this.parameters = parameters;
    }

    public String getCompiledQuery() {
        return compiledQuery;
    }

    public String toString() {
        return getCompiledQuery();
    }

    public SortedMap<Integer, Object> getParameters() {
        return parameters;
    }
}
