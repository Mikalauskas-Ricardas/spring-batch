package com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.batch.listener;

import com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.model.Student;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;
import org.springframework.batch.core.ItemProcessListener;

import java.io.File;

import static com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.util.Utils.createLogFile;


@Component
public class ProcessorExceptionListener implements ItemProcessListener<Student, Student> {
    @Override
    public void beforeProcess(Student item) {

    }

    @Override
    public void afterProcess(Student item, Student result) {

    }

    @Override
    public void onProcessError(Student item, Exception e) {
        createLogFile("processor" + File.separator + System.currentTimeMillis() + ".txt", item.toString() + ", EXCEPTION: " + e.toString());
    }
}
