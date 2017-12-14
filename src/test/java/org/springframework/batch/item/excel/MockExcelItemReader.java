package org.springframework.batch.item.excel;

import java.util.Collections;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import com.icashflow.batch.item.excel.AbstractExcelItemReader;
import com.icashflow.batch.item.excel.Sheet;

/**
 * Created by in329dei on 17-9-2014.
 */
public class MockExcelItemReader<T> extends AbstractExcelItemReader<T> {


    private final List<MockSheet> sheets;

    public MockExcelItemReader(MockSheet sheet) {
        this(Collections.singletonList(sheet));
    }

    public MockExcelItemReader(List<MockSheet> sheets) {
        this.sheets=sheets;
        super.setResource(new ByteArrayResource(new byte[0]));
    }

    @Override
    public Sheet getSheet(int sheet) {
        return sheets.get(sheet);
    }

    @Override
    public int getNumberOfSheets() {
        return sheets.size();
    }

    @Override
    public void openExcelFile(Resource resource) throws Exception {

    }

    @Override
    public void doClose() throws Exception {
        sheets.clear();
    }
}
