package com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.util;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

@UtilityClass
public class Utils {
    @SneakyThrows
    public static File createTempFile(String suffix) {
        String tempFolderPath = System.getProperty("user.dir") + File.separator + "temp";
        File directory = new File(tempFolderPath);

        if (!directory.exists()) directory.mkdir();

        File file = new File(tempFolderPath + File.separator + System.currentTimeMillis() + suffix);

        if (file.createNewFile()) {
            return file;
        }

        return null;
    }

    @SneakyThrows
    public static void createLogFile(String dir, String data) {
        String path = System.getProperty("user.dir") + File.separator + "StudentsJob" + File.separator + "StudentsProcessStep" + File.separator + dir;
        File file = new File(path);

        if(!file.exists()) file.createNewFile();

        try (FileWriter fileWriter = new FileWriter(file, true)) {
            fileWriter.write("RECORD: " + data + "," + " DATE: " + new Date() + System.lineSeparator());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
