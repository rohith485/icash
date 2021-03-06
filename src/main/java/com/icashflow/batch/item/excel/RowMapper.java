/*
 * Copyright 2006-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.icashflow.batch.item.excel;

import com.icashflow.batch.item.excel.support.rowset.RowSet;

/**
 * Map rows from an excel sheet to an object.
 *
 * @param <T> the type
 * @author Rohith Kumar Pingili
 */
public interface RowMapper<T> {

    /**
     * Implementations must implement this method to map the provided row to
     * the parameter type T.  The row number represents the number of rows
     * into a {@link Sheet} the current line resides.
     *
     * @param rs the RowSet used for mapping.
     * @return mapped object of type T
     * @throws Exception if error occured while parsing.
     */
    T mapRow(RowSet rs) throws Exception;

}
