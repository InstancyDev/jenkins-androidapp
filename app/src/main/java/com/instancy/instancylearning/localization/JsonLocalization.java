package com.instancy.instancylearning.localization;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Locale;

public class JsonLocalization {

    private static JsonLocalization mInstance;
    private String language = "en";
    private JSONObject jsonData = new JSONObject();

    public static JsonLocalization getInstance() {
        if (mInstance == null) {
            mInstance = new JsonLocalization();
            mInstance.setLanguage(Locale.getDefault().getLanguage());
        }
        return mInstance;
    }

    private JsonLocalization() {
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void loadFromData(JSONObject jsonData) {
        this.jsonData = jsonData;
    }

    public void loadFromData(String jsonString) {
        try {
            this.loadFromData(new JSONObject(jsonString));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFileName(Context context, String fileName) {
        FileInputStream fis;
        StringBuffer fileContent;
        try {
            fis = context.openFileInput(fileName);
            fileContent = new StringBuffer("");
            byte[] buffer = new byte[1024];
            int n;
            while ((n = fis.read(buffer)) != -1) {
                fileContent.append(new String(buffer, 0, n));
            }
            this.loadFromData(fileContent.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String stringForKey(String key) {
        JSONObject localData = null;
        try {
            localData = this.jsonData.getJSONObject(this.language);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String value = ""; // key is the default value returned if key is not found in json
        if (localData != null) {
            if (localData.has(key)) {
                try {
                    value = localData.getString(key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }


    public String getStringForKey(String key, Context ctx) {

        String value = getStringFromResource(key, ctx);
        //String value = "";
        JSONObject localData = null;
        try {
            localData = new JSONObject(readLocaleFileFromInternalStorage(ctx, PreferencesManager.getInstance().getStringValue(ctx.getResources().getString(R.string.locale_name))));
            // key is the default value returned if key is not found in json
            if (localData != null) {
                if (localData.has(key)) {
                    try {
                        if (!TextUtils.isEmpty(localData.getString(key)))
                            value = localData.getString(key);
                    } catch (JSONException e) {
//                        e.printStackTrace();
                    }
                }
            }
        } catch (JSONException e) {
//            e.printStackTrace();
        }
        return value;
    }

    public String getStringFromResource(String keyName, Context ctx) {

        String valueName = "";
        if (keyName.equals("Rated %.1f out of 5 of %d ratings")) {
            keyName = "rated_out_of_odf_ratings";
        }

        try {
            valueName = ctx.getResources().getString(ctx.getResources().getIdentifier(keyName, "string", ctx.getApplicationInfo().packageName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return valueName;
    }

    public static String getResourceString(String name, Context context) {
        int nameResourceID = context.getResources().getIdentifier(name, "string", context.getApplicationInfo().packageName);
        if (nameResourceID == 0) {
            throw new IllegalArgumentException("No resource string found with name " + name);
        } else {
            return context.getString(nameResourceID);
        }
    }


    public static void saveLocaleFileToInternalStorage(String data, String fileName, Context context) {
        try {
            File path = context.getFilesDir();
            File file = new File(path, fileName + ".json");
            if (file.exists()) {
                file.delete();
            }
            try {
                Writer output = null;
                output = new BufferedWriter(new FileWriter(file));
                output.write(data);
                output.close();
            } catch (Exception e) {
            }
        } catch (Exception e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    private String readLocaleFileFromInternalStorage(Context context, String fileName) {
        File path = context.getFilesDir();
        File file = new File(path, fileName + ".json");
        String resultLocale = "";
        if (file.exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                resultLocale = stringBuilder.toString();

            } catch (FileNotFoundException e) {
                Log.e("login activity", "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e("login activity", "Can not read file: " + e.toString());
            } finally {

            }
        }
        return resultLocale;
    }


}
