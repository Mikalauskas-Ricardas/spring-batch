package com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.batch.listener;

import com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.model.Student;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.util.Utils.createLogFile;

@Component
public class ReaderExceptionListener implements ItemReadListener<Student> {
    @Override
    public void beforeRead() {

    }

    @Override
    public void afterRead(Student item) {

    }

    @Override
    public void onReadError(Exception ex) {
        if(ex instanceof FlatFileParseException) {
            createLogFile("reader" + File.separator + System.currentTimeMillis() + ".txt",
                    ((FlatFileParseException) ex).getInput());
        }
    }
}
