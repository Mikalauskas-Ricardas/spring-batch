package com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.batch.listener;

import com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.model.Student;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.util.Utils.createLogFile;

@Component
public class StepSkipListener implements SkipListener<Student, Student> {
    @Override
    public void onSkipInRead(Throwable t) {
        if(t instanceof FlatFileParseException) {
            createLogFile("reader" + File.separator + System.currentTimeMillis() + ".txt",
                    ((FlatFileParseException) t).getInput());
        }
    }

    @Override
    public void onSkipInWrite(Student item, Throwable t) {
        createLogFile("writer" + File.separator + System.currentTimeMillis() + ".txt", item + ", EXCEPTION: " + t.toString());
    }

    @Override
    public void onSkipInProcess(Student item, Throwable t) {
        createLogFile("processor" + File.separator + System.currentTimeMillis() + ".txt", item.toString() + ", EXCEPTION: " + t.toString());
    }
}
