package com.instancy.instancylearning.helper;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnzipUtility {

    private String _zipFile;
    private String _location;
    private static int BUFFER_SIZE = 6 * 1024;

    public UnzipUtility(String zipFile, String location) {
        _zipFile = zipFile;
        _location = location;
        ZipFile zipFileNew;

        try {
            System.out.println(" Calling unZip...");
            zipFileNew = new ZipFile(_zipFile);
            unzipFileIntoDirectory(zipFileNew, new File(_location));
        } catch (Exception e) {
            Log.d("UnZip", e.getMessage());
        }
    }

    @SuppressWarnings("rawtypes")
    public static void unzipFileIntoDirectory(ZipFile zipFile,
                                              File jiniHomeParentDir) {
        Enumeration files = zipFile.entries();
        File f = null;
        FileOutputStream fos = null;

        while (files.hasMoreElements()) {
            try {
                ZipEntry entry = (ZipEntry) files.nextElement();
                InputStream eis = zipFile.getInputStream(entry);
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = 0;

                // //Log.d("getAbsolute Path " ,
                // jiniHomeParentDir.getAbsolutePath());
                // f = new File(jiniHomeParentDir.getAbsolutePath() +
                // File.separator + entry.getName());
                String strFileName = entry.getName();
                if (strFileName.contains("\\")) {
                    strFileName = strFileName.replace("\\", "/");
                }

                f = new File(jiniHomeParentDir.getAbsolutePath() + "/"
                        + strFileName);

                // //Log.d("After updated file name ", f.getName());
                if (entry.isDirectory()) {
                    f.mkdirs();
                    continue;
                } else {
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                }

                fos = new FileOutputStream(f);

                while ((bytesRead = eis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        Log.d("[] IOEXCepton : ",
                                e.getMessage());

                    }
                }
            }
        }
    }

}
