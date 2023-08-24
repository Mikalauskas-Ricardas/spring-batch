package com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.batch.listener;

import com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.model.Student;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

import static com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.util.Utils.createLogFile;

@Component
public class WriterExceptionListener implements ItemWriteListener<Student> {
    @Override
    public void beforeWrite(List<? extends Student> items) {

    }

    @Override
    public void afterWrite(List<? extends Student> items) {

    }

    @Override
    public void onWriteError(Exception exception, List<? extends Student> items) {
        createLogFile("writer" + File.separator + System.currentTimeMillis() + ".txt", items + ", EXCEPTION: " + exception.toString());
    }
}
