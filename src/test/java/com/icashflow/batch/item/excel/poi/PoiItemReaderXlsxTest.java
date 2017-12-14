/*
 * Copyright 2006-2014 the original author or authors.
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
package com.icashflow.batch.item.excel.poi;

import org.springframework.core.io.ClassPathResource;

import com.icashflow.batch.item.excel.AbstractExcelItemReader;
import com.icashflow.batch.item.excel.AbstractExcelItemReaderTests;
import com.icashflow.batch.item.excel.poi.PoiItemReader;

public class PoiItemReaderXlsxTest extends AbstractExcelItemReaderTests {

    @Override
    public void configureItemReader(AbstractExcelItemReader itemReader) {
        itemReader.setResource(new ClassPathResource("org/springframework/batch/item/excel/player.xlsx"));
    }

    @Override
    public AbstractExcelItemReader createExcelItemReader() {
        return new PoiItemReader();
    }
}
