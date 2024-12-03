/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.test.data.datatable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.test.support.EnumResolver;

@RequiredArgsConstructor
@Getter
public enum DatatableColumnType {

    STRING("string"), NUMBER("number"), BOOLEAN("boolean"), DECIMAL("decimal"), DATE("date"), DATETIME("datetime"), TEXT("text"), DROPDOWN(
            "dropdown");

    private final String typeString;

    public static DatatableColumnType fromTypeString(String columnType) {
        return EnumResolver.from(DatatableColumnType.class, columnType, DatatableColumnType::getTypeString);
    }
}
