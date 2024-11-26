package com.extrime.getlocation;

/**
 * TODO: This class work with External Storage !!!
 * Work with root storage producted in Main-Class (GetLocation.java)
 * For fast search been created LABEL...
 **/

import java.io.*;
import android.util.Log;

public class WorkWithFiles{

    final String LOG_TAG = "LOG";
    String PathDir = "files/";
    String InternalStoragePath = "/data/data/com.extrime.getlocation/";
    String FullInternalStoragePath = "/data/data/com.extrime.getlocation/files/";
    String ExternalStoragePath = "/storage/emulated/0/Android/com.extrime.getlocation/";

    public void CreateDir(){
        File files = new File(InternalStoragePath + PathDir);
        if (!files.exists()) {
            File pathDir = new File(InternalStoragePath + PathDir);
            pathDir.mkdirs();
            Log.e(LOG_TAG, "Папка создана * " + pathDir);
        }
    }

    public void WriteFile_InternalStorage(String text, String FileName) {
        CreateDir();
        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter((new FileWriter(FullInternalStoragePath + FileName)));
            // пишем данные
            bw.write(text);
            // закрываем поток
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ReadFile_InternalStorage(String FileName) {
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(FullInternalStoragePath));
            File myFile = new File(FileName);
            String str = "";
            String str1 = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                Log.e(LOG_TAG, str);
                str1 += str;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}