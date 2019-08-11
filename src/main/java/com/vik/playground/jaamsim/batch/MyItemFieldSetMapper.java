package com.vik.playground.jaamsim.batch;

import com.vik.playground.jaamsim.domain.MyItem;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class MyItemFieldSetMapper implements FieldSetMapper<MyItem> {

    @Override
    public MyItem mapFieldSet(FieldSet fieldSet) {
       return new MyItem(fieldSet.readLong("id"),
                fieldSet.readLong("runDuration"));
    }
}
