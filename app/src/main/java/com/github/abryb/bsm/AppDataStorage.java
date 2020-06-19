package com.github.abryb.bsm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AppDataStorage {

    private File file;

    AppDataStorage(File file) {

        this.file = file;
    }

    public void saveData(AppData data) throws AppException {
        try {
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write objects to file
            o.writeObject(data);

            o.close();
            f.close();


        } catch (IOException e) {
            e.printStackTrace();
            throw new AppException(e.getMessage(), e);
        }
    }

    public AppData loadData() throws AppException {
        try {
            FileInputStream fi = new FileInputStream(file);
            ObjectInputStream oi = new ObjectInputStream(fi);

            // Read objects
            AppData data = (AppData) oi.readObject();

            oi.close();
            fi.close();

            return data;

        } catch (InvalidClassException e) {
            return new AppData();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new AppData();
        }
    }
}
