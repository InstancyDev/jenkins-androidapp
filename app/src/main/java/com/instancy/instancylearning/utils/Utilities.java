package com.instancy.instancylearning.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.instancy.instancylearning.BuildConfig;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.mainactivities.Splash_activity;

import org.json.JSONObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;

public class Utilities {

    static Toast toast = null;


    public static void showSweetAlert(Context context) {

        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.error_alerttitle_stringoops, context))
                .setContentText(JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.error_alertsubtitle_somethingwentwrong, context))
                .show();


    }

    public static void tintMenuIcon(Context context, MenuItem item, @ColorRes int color) {
        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, context.getResources().getColor(color));

        item.setIcon(wrapDrawable);
    }


    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtmlForYourNExt(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }


    public static String fromHtmlToString(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }

        String resultWithReplace = result.toString().replaceAll("\n\n", "\n");

        return resultWithReplace;
    }

    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     *
     * @param milliseconds
     * @return
     */
    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }


    public static String replace(String str) {
        return str.replaceAll(" ", "%20");
    }

    /**
     * Function to get Progress percentage
     *
     * @param currentDuration
     * @param totalDuration
     */
    public int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     *
     * @param progress      -
     * @param totalDuration returns current duration in milliseconds
     */
    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = totalDuration / 1000;
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    /**
     * To get current system date time in the format passed as parameter.
     *
     * @param format Format in which the datetime is to be returned
     * @return Date time in specified format.
     * Date time in yyyy-MM-dd HH:mm:ss format if empty string is passed
     * @author UpendraNathReddy
     */
    public static String getCurrentDateTime(String format) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = null;
        if (isValidString(format)) {
            df = new SimpleDateFormat(format);
        } else {
            df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        return df.format(c.getTime());
    }

    public static String getOneWeekBeforeData(String format) {

        Calendar newCalendar = Calendar.getInstance();
        newCalendar.add(Calendar.DATE, -7);
        SimpleDateFormat df = null;
        if (isValidString(format)) {
            df = new SimpleDateFormat(format);
        } else {
            df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        return df.format(newCalendar.getTime());
    }


    public static Date getCurrentDateTimeInDate(String dateStr) {

        Date date = null;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = format.parse(dateStr);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    /**
     * To format Date time string from {@code currentFormat} to {@code newFormat}.
     *
     * @param currentFormat The current date time format.
     * @param newFormat     Format in which the date time is to be returned.
     * @return The new Date time string in {@code newFormat}.
     * @author UpendraNathReddy
     */
    public static String formatDate(String dateTime, String currentFormat, String newFormat) {

        SimpleDateFormat preFormat = new SimpleDateFormat(currentFormat);
        SimpleDateFormat postFormater = new SimpleDateFormat(newFormat);
        String newDate = null;
        try {
            newDate = postFormater.format(preFormat.parse(dateTime));
        } catch (Exception e) {
            Log.d("In getCurrentDateTime", e.getMessage());
        }
        return newDate;
    }

    public static String convertDateToDayFormat(String dateTime, String currentFormat) {

        SimpleDateFormat preFormat = new SimpleDateFormat(currentFormat);
        SimpleDateFormat postFormater = new SimpleDateFormat("EEEE, MMM d, yyyy");
        String newDate = null;
        try {
            newDate = postFormater.format(preFormat.parse(dateTime));
        } catch (Exception e) {
            Log.d("In getCurrentDateTime", e.getMessage());
        }
        return newDate;
    }


    public static String convertDateToSortDateFormat(String dateTime) {

        SimpleDateFormat preFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat postFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String newDate = null;
        try {
            newDate = postFormater.format(preFormat.parse(dateTime));
        } catch (Exception e) {
            Log.d("In getCurrentDateTime", e.getMessage());
        }
        return newDate;
    }


    public static String convertDateToSortDateFormatUpdated(String dateTime) {

        SimpleDateFormat preFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat postFormater = new SimpleDateFormat("dd/MM/yyyy");
        String newDate = null;
        try {
            newDate = postFormater.format(preFormat.parse(dateTime));
        } catch (Exception e) {
            Log.d("In getCurrentDateTime", e.getMessage());
        }
        return newDate;
    }


    /**
     * To copy the data from {@code InputStream} to {@code OutputStream} .
     *
     * @param in  InputStream
     * @param out OutputStream
     * @throws IOException
     * @author UpendraNathReddy
     */
    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read = 0;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
//	    in.close();
//	    out.flush();
//	    out.close();
//	    in = null;
//	    out = null;
    }

    /**
     * To check whether the {@code String} is valid or not.
     *
     * @param str String to validate
     * @return {@code true} if {@code String} is valid, {@code false} otherwise.
     * @author UpendraNathReddy
     */
    public static boolean isValidString(String str) {
        try {
            if (str == null || str.equals("") || str.equals("null") || str.equals("undefined") || str.equals("null\n")) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * To get value of {@code String} if it is valid otherwise {@code ""}.
     *
     * @param str String
     * @return value of {@code String} if {@code String} is valid, otherwise empty String ("")
     * @author UpendraNathReddy
     */
    public static String getStringOrEmpty(String str) {
        if (isValidString(str)) {
            return str;
        } else {
            return "";
        }
    }

    /**
     * To get value of {@code String} or default value.
     *
     * @param str          String
     * @param defaultValue
     * @return value of {@code String} if {@code String} is valid, otherwise the defaultValue
     * @author UpendraNathReddy
     */
    public static String getStringOrDefault(String str, String defaultValue) {
        if (isValidString(str)) {
            return str;
        } else {
            return defaultValue;
        }
    }

    /**
     * To get value of {@code String}.
     *
     * @param str       String
     * @param ifValid
     * @param ifInValid
     * @return value of ifValid if {@code String} is valid, otherwise the value of ifInValid
     * @author UpendraNathReddy
     */
    public static String getStringOneOrTwo(String str, String ifValid, String ifInValid) {
        if (isValidString(str)) {
            return ifValid;
        } else {
            return ifInValid;
        }
    }

    /**
     * To convert input stream to string
     *
     * @param is Input stream
     * @return Input stream as string
     * @author UpendraNathReddy
     */
    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * To convert Dimensions in dp to px
     *
     * @param ctx Context
     * @param dp  Dimension to be converted to px
     * @return Dimension in px
     * @author UpendraNathReddy
     */
    public static int dpToPx(Context ctx, int dp) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    /**
     * To convert Dimensions in px to dp
     *
     * @param ctx Context
     * @param px  Dimension to be converted to dp
     * @return Dimension in dp
     * @author UpendraNathReddy
     */
    public static int pxToDp(Context ctx, int px) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static int getPxFromDip(Context ctx, float px) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (px * scale + 0.5f);
    }

    /**
     * To exit the app.
     *
     * @param ctx Context
     * @author UpendraNathReddy
     */
    public static void exitApp(Context ctx) {
        Intent intent = new Intent(ctx, Splash_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        ctx.startActivity(intent);
    }

    /**
     * To get the status of Internet connection.
     *
     * @param ctx         Context
     * @param networkType The network type integer specifying for which the status is to be known for ex. {@code ConnectivityManager.TYPE_WIFI}.
     *                    Pass '-1' if want to know the Active Internet connection status.
     * @return {@code true} if the requested network is available, {@code false } otherwise.
     * @author UpendraNathReddy
     */
    public static boolean isNetworkConnectionAvailable(Context ctx, int networkType) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo;
        if (networkType == -1) {
            netInfo = cm.getActiveNetworkInfo();
        } else {
            netInfo = cm.getNetworkInfo(networkType);
        }

        if (netInfo != null && netInfo.isConnectedOrConnecting()
                && netInfo.isAvailable()) {
            return true;
        } else {
            return false;

        }

    }

    /**
     * To make the app go to background
     *
     * @param ctx Context
     * @author UpendraNathReddy
     */
    public static void sendAppToBackground(Context ctx) {
        Intent startHome = new Intent(Intent.ACTION_MAIN);
        startHome.addCategory(Intent.CATEGORY_HOME);
        startHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(startHome);

    }

    /**
     * To show a {@code Toast} on the UI thread with specified message.
     *
     * @param ctx        Context
     * @param strMessage Message to show in {@code Toast}.
     * @author UpendraNathReddy
     */
    public static void showToast(final Context ctx, final String strMessage) {
        ((Activity) ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (toast != null) {
                    toast.cancel();
                    toast = null;
                }
                toast = Toast.makeText(ctx.getApplicationContext(),
                        strMessage, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
            }
        });
    }

    /**
     * To show a {@code Toast} on the UI thread with specified message.
     *
     * @param ctx          Context
     * @param messageResId Resource Id of the message to show.
     * @author UpendraNathReddy
     */
    public static void showToast(final Context ctx, final int messageResId) {
        ((Activity) ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (toast != null) {
                    toast.cancel();
                    toast = null;
                }
                toast = Toast.makeText(ctx.getApplicationContext(),
                        messageResId, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();

            }
        });
    }

    /**
     * To format the site URL.
     *
     * @param url URL to format
     * @return {@code URL} String with "/" appended at the end.
     * @author UpendraNathReddy
     */
    public static String formatURL(String url) {
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        return url;
    }

    /**
     * To limit the text to the predefined length of characters
     *
     * @param str       {@code String} to check the length.
     * @param maxLength Maximum characters.
     * @return if the {@code String} length is greater than the {@code maxLength} returns a {@code String} with "..." appended to the end. Other wise the original {@code String}.
     * @author UpendraNathReddy
     */
    public static String getSafeString(String str, int maxLength) {
        if (str.length() > maxLength) {
            return str.substring(0, maxLength) + "...";
        } else {
            return str;
        }
    }

    /**
     * To get the {@code Typeface} class with the specified font(Which is available in assets directory.
     *
     * @param context
     * @param font    Name of the fornt for which {@code Typeface} class is required.
     * @return
     */
    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }

    /**
     * To get the local path of user profile image(creates the path If it doesn't already exists).
     *
     * @param ctx         The application context.
     * @param siteId      {@code siteId}.
     * @param userId      {@code userId}.
     * @param folderName  Foldername to create.
     * @param displayName User displayname to save profile image with that name.
     * @param extension   Extension of the profile image to create.
     * @param createNew   {@code true} means creates the folder if not exists, otherwise {@code false}.
     * @return The local path where the user profile image is stored.
     * @author UpendraNathReddy
     */
    public static String getProfileImageLocalPath(Context ctx, String siteId, String userId, String folderName, String displayName, String extension, boolean createNew) {

        String profileImageDir = ctx.getExternalFilesDir(null) + "/Mydownloads/" + siteId + "/" + folderName + "/" + userId;

        File f = new File(profileImageDir);
        if (!f.exists() && createNew) {
            boolean success = (new File(profileImageDir)).mkdirs();
            if (success) {
                Log.d("ProfileImageLocalPath", "Created profileImageDir");
            }
        }
        if (isValidString(extension)) {
            profileImageDir = profileImageDir + "/" + displayName + extension;
        } else {
            profileImageDir = profileImageDir + "/" + displayName + ".png";
        }


        return profileImageDir;
    }

    /**
     * To get the user profile image source path for downloading.
     *
     * @param siteUrl {@code siteUrl}.
     * @param siteId  {@code siteId}.
     * @param picName Profile image name along with extension.
     * @return The path of the source profile image.
     * @author UpendraNathReddy
     */
    public static String getProfileImageSourcePath(String siteUrl, String siteId, String picName) {

        String profileImageDir = siteUrl + "content/SiteFiles/" + siteId + "/ProfileImages/"
                + picName;

        return profileImageDir;
    }


//	public static void highlightSearchText(Context ctx, TextView textView, String text,
//			String spanText) {
//		String wholeText = text.toLowerCase();
//		String searchText = spanText.toLowerCase();
//
//		SpannableStringBuilder sb = new SpannableStringBuilder(text);
//		int start = wholeText.indexOf(searchText);
//		if (start >= 0) {
//			int end = start + searchText.length();
//			BackgroundColorSpan searchTextStyle = new BackgroundColorSpan(
//					Color.parseColor(ctx.getString(R.color.colorRed)));
//			sb.setSpan(searchTextStyle, start, end,
//					Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//			textView.setText(sb);
//		}
//	}

//    /**
//     * To get the rounded corner {@code Bitmap} image.
//     *
//     * @param bitmap Image for which the rounded corners to be generated.
//     * @param pixels Radius of rounded corner.
//     * @author UpendraNathReddy
//     * @return {@code Bitmap} image with rounded corners.
//     */
//	public static Bitmap getRoundedRectBitmap(Bitmap bitmap, int pixels) {
//		Bitmap imageOut = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
//				Bitmap.Config.ARGB_8888);
//		Canvas canvas = new Canvas(imageOut);
//		canvas.drawARGB(0, 0, 0, 0);
//		
//		int color = 0xff424242;
//		
//		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//		
//		Paint paint = new Paint();
//		paint.setAntiAlias(true);
//		
//		paint.setColor(color);
//		canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
//				(bitmap.getHeight() / 4) * 3, paint);
//
//		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//		canvas.drawBitmap(bitmap, rect, rect, paint);
//
//		return imageOut;
//	}


    /*
    Program output
    LDPI: 165.0 X 60.0
    MDPI: 220.0 X 80.0
    HDPI: 330.0 X 120.0
    XHDPI: 440.0 X 160.0
    XXHDPI: 660.0 X 240.0
    XXXHDPI: 880.0 X 320.0
    */
    public class DPICalculator {

        private final float LDPI = 120;
        private final float MDPI = 160;
        private final float HDPI = 240;
        private final float XHDPI = 320;
        private final float XXHDPI = 480;
        private final float XXXHDPI = 640;

        private float forDeviceDensity;
        private float width;
        private float height;

        public DPICalculator() {
        }

        public DPICalculator(float forDeviceDensity, float width, float height) {
            this.forDeviceDensity = forDeviceDensity;
            this.width = width;
            this.height = height;
        }

//	public static void main(String... args) {
//	    DPICalculator dpiCalculator = new DPICalculator(240,330,120);
//	    dpiCalculator.calculateDPI();
//	}


        private float getPx(float dp, float value) {
            float px = dp * (value / forDeviceDensity);
            return px;
        }

        private void calculateDPI() {

            float ldpiW = getPx(LDPI, width);
            float ldpiH = getPx(LDPI, height);
            float mdpiW = getPx(MDPI, width);
            float mdpiH = getPx(MDPI, height);
            float hdpiW = getPx(HDPI, width);
            float hdpiH = getPx(HDPI, height);
            float xdpiW = getPx(XHDPI, width);
            float xdpiH = getPx(XHDPI, height);
            float xxdpiW = getPx(XXHDPI, width);
            float xxdpiH = getPx(XXHDPI, height);
            float xxxdpiW = getPx(XXXHDPI, width);
            float xxxdpiH = getPx(XXXHDPI, height);

            System.out.println("LDPI: " + ldpiW + " X " + ldpiH);
            System.out.println("MDPI: " + mdpiW + " X " + mdpiH);
            System.out.println("HDPI: " + hdpiW + " X " + hdpiH);
            System.out.println("XHDPI: " + xdpiW + " X " + xdpiH);
            System.out.println("XXHDPI: " + xxdpiW + " X " + xxdpiH);
            System.out.println("XXXHDPI: " + xxxdpiW + " X " + xxxdpiH);
        }
    }

    public static Boolean hasKeyOrNot(String stringisthere, JSONObject jsonObJ) {

        if (jsonObJ.has(stringisthere)) {


            return true;
        }

        return false;
    }


    public void LogMessage(String Tag, String message, int priority) {
        switch (priority) {
            case Log.DEBUG:
                if (BuildConfig.DEBUG) {
                    Log.d(Tag, message);
                }
                break;
            case Log.ERROR:
                Log.e(Tag, message);
                break;
            case Log.INFO:
            default:
                Log.i(Tag, message);
                break;
        }
    }

    /**
     * To get the available space on the file system for application (in kB).
     *
     * @return
     * @author UpendraNathReddy
     */
    @SuppressWarnings("deprecation")
    public static long getAvailableSpace() {
        StatFs stat = new StatFs(Environment
                .getExternalStorageDirectory()
                .getPath());
        return ((long) stat.getAvailableBlocks() * (long) stat.getBlockSize()) / 1024L;
    }

    public static HashMap<String, String> generateHashMap(String[] conditionsArray) {

        HashMap<String, String> map = new HashMap<String, String>();

        if (conditionsArray.length != 0) {
            // split on ':' and on '::'
            // String[] parts = conditionsArray.split("=");

            // String[] filterArray = conditionsArray[i].split("=");

            for (int i = 0; i < conditionsArray.length; i++) {
                String[] filterArray = conditionsArray[i].split("=");

//                System.out.println(" forvalue " + filterArray);

                if (filterArray.length > 1)
                    map.put(filterArray[0], filterArray[1]);
            }

//			for (String s : map.keySet()) {
//
//				System.out.println(s + " forvalue " + map.get(s));
//			}
        } else {

        }

        return map;
    }

    public static Date ConvertToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date convertedDate = new Date();
        if (!isValidString(dateString))
            return convertedDate;
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertedDate;
    }

    public static Date GetZeroTimeDate(Date fecha) {
        Date res = fecha;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(fecha);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        res = calendar.getTime();

        return res;
    }

    public static String upperCaseWords(String sentence) {
        String words[] = sentence.replaceAll("\\s+", " ").trim().split(" ");
        String newSentence = "";
        for (String word : words) {
            for (int i = 0; i < word.length(); i++)
                newSentence = newSentence + ((i == 0) ? word.substring(i, i + 1).toUpperCase() :
                        (i != word.length() - 1) ? word.substring(i, i + 1).toLowerCase() : word.substring(i, i + 1).toLowerCase().toLowerCase() + " ");
        }

        return newSentence;
    }

    public static String getFirstCaseWords(String sentence) {
        String words[] = sentence.replaceAll("\\s+", " ").trim().split(" ");
        String newSentence = "";
        int moreThan2 = 0;
        for (String word : words) {
            moreThan2++;
            if (moreThan2 > 2)
                break;
            for (int i = 0; i < word.length(); i++) {

                newSentence = newSentence + ((i == 0) ? word.substring(i, i + 1).toUpperCase() : "");
            }
        }

        return newSentence;
    }

    public Drawable generateDrawableFromString(String nameStr) {


        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(nameStr, color);

        return drawable;
    }

    public static String returnDateIfNotToday(String dateTime) {

        String originalString = dateTime;
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(originalString);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        String newString = new SimpleDateFormat("H:mm").format(date);

        return originalString;
    }

    public static String getFileExtensionWithPlaceHolderImage(String fileName) {

        if (isValidString(fileName) && fileName.contains("."))
            return fileName.substring(fileName.lastIndexOf("."));
        else
            return "";
    }

    public static String getFileNameFromPath(Uri contentURI, Context context) {

        String fileName = "";
        if (contentURI.getScheme().equals("file")) {
            fileName = contentURI.getLastPathSegment();
        } else {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(contentURI, new String[]{
                        MediaStore.Images.ImageColumns.DISPLAY_NAME
                }, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                    Log.d("UTILS", "name is " + fileName);
                }
            } finally {

                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return fileName;
    }


    public static String getFileExtension(Uri selectedUri) {

        String fileExtension = "";
        if (selectedUri != null) {
            fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());

        }

        return fileExtension;
    }


    public static boolean isFilevalidFileFound(String fileTypeStr, String fileFound) {
        boolean isValidFile = false;

        List<String> fileTypesArray = new ArrayList<>();

        if (!isValidString(fileFound))
            isValidFile = false;

        if (!isValidString(fileTypeStr))
            isValidFile = false;

        fileTypesArray = Arrays.asList(fileTypeStr.split(","));

        if (fileTypesArray.size() > 0) {

            for (int k = 0; k < fileTypesArray.size(); k++) {

                if (fileFound.toLowerCase().contains(fileTypesArray.get(k).toLowerCase())) {
                    isValidFile = true;
                    break;
                }

            }
        }

        return isValidFile;
    }

    public static String getMimeTypeFromUri(Uri selectedUri) {

        String mimeType = "";
        if (selectedUri != null) {
            String fileExtension
                    = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
            mimeType
                    = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);

        }

        return mimeType;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String yourRealPath = cursor.getString(column_index);
            return yourRealPath;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    //  content://com.android.providers.downloads.documents/document/raw%3A%2Fstorage%2Femulated%2F0%2FDownload%2Fflutter-logo-round.png.png

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {


                final String id = DocumentsContract.getDocumentId(uri);
                if (!TextUtils.isEmpty(id)) {
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:", "");
                    }
                    try {
                        final Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                        return getDataColumn(context, contentUri, null, null);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }

//                final String id = DocumentsContract.getDocumentId(uri);
//                final Uri contentUri = ContentUris.withAppendedId(
//                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

//                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    // This method  converts String to RequestBody
    public static RequestBody toRequestBody(String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }

    public static String toStringS(Object[] a) {
        if (a == null)
            return "";

        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();

        String value = "";

        b.append('[');
        for (int i = 0; ; i++) {
            b.append(String.valueOf(a[i]));
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }


    public static Drawable getDrawableFromStringMethod(int resourceID, Context context, String colorString) {

        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(context).inflate(R.layout.iconimage, null);
        TextView iconText = (TextView) customNav.findViewById(R.id.imageicon);
        iconText.setTextColor(Color.parseColor(colorString));
        iconText.setText(resourceID);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.imageicon), iconFont);
        Drawable d = new BitmapDrawable(context.getResources(), createBitmapFromView(context, customNav));

        return d;
    }

    public static Drawable getDrawableFromStringHOmeMethod(int resourceID, Context context, String colorString) {

        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(context).inflate(R.layout.homebutton, null);
        TextView iconText = (TextView) customNav.findViewById(R.id.homeicon);
        iconText.setTextColor(Color.parseColor(colorString));
        iconText.setText(resourceID);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.homeicon), iconFont);
        Drawable d = new BitmapDrawable(context.getResources(), createBitmapFromView(context, customNav));

        return d;
    }

    public static Drawable getButtonDrawable(int resourceID, Context context, String colorString) {

        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(context).inflate(R.layout.buttondrawable, null);
        TextView iconText = (TextView) customNav.findViewById(R.id.buttondrawables);
        iconText.setTextColor(Color.parseColor(colorString));
        iconText.setText(resourceID);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.buttondrawables), iconFont);
        Drawable d = new BitmapDrawable(context.getResources(), createBitmapFromView(context, customNav));

        return d;
    }

    public static boolean isCourseEndDateCompleted(String endDate) {

        boolean isCompleted = true;

        Date strDate = ConvertToDate(endDate);

        if (new Date().after(strDate)) {
// today is after date 2
            isCompleted = true;

        } else {
            isCompleted = false;
        }

        return isCompleted;
    }

    public static boolean isMemberyExpry(String memberExpiryDate) {
        boolean isCompleted = true;

        if (memberExpiryDate.length() == 0)
            return isCompleted = true;

        String endDate = memberExpiryDate;
        Date strDate = ConvertToDate(endDate);

        if (new Date().after(strDate)) {
// today is after date 2
            isCompleted = true;

        } else {
            isCompleted = false;
        }

        return isCompleted;
    }

    public static ArrayList<String> getFromYearToYear(int fromY, int toYear) {

        ArrayList<String> yearsList = new ArrayList<>();

        for (int i = fromY; i <= toYear; i++)
            yearsList.add("" + i);

        return yearsList;
    }

    public static String getMonthFromint(int selectedMonth) {

        String month = "Jan";

        switch (selectedMonth) {
            case 1:
                month = "Jan";
                break;
            case 2:
                month = "Feb";
                break;
            case 3:
                month = "Mar";
                break;
            case 4:
                month = "Apr";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "Jun";
                break;
            case 7:
                month = "Jul";
                break;
            case 8:
                month = "Aug";
                break;
            case 9:
                month = "Sep";
                break;
            case 10:
                month = "Oct";
                break;
            case 11:
                month = "Nov";
                break;
            case 12:
                month = "Dec";
                break;

        }

        return month;
    }

    public static int getIntFromMonth(String selectedMonth) {

        int month = 1;

        switch (selectedMonth) {
            case "Jan":
                month = 1;
                break;
            case "Feb":
                month = 2;
                break;
            case "Mar":
                month = 3;
                break;
            case "Apr":
                month = 4;
                break;
            case "May":
                month = 5;
                break;
            case "Jun":
                month = 6;
                break;
            case "Jul":
                month = 7;
                break;
            case "Aug":
                month = 8;
                break;
            case "Sep":
                month = 9;
                break;
            case "Oct":
                month = 10;
                break;
            case "Nov":
                month = 11;
                break;
            case "Dec":
                month = 12;
                break;

        }

        return month;
    }

    public static String getMonthName() {

        String[] monthName = {"January", "February",
                "March", "April", "May", "June", "July",
                "August", "September", "October", "November",
                "December"};

        Calendar cal = Calendar.getInstance();
        String month = monthName[cal.get(Calendar.MONTH)];

        System.out.println("Month name: " + month);

        return month;
    }


    public static void setCursorColor(EditText view, @ColorInt int color) {
        try {
            // Get the cursor resource id
            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            int drawableResId = field.getInt(view);

            // Get the editor
            field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(view);

            // Get the drawable and set a color filter
            Drawable drawable = ContextCompat.getDrawable(view.getContext(), drawableResId);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            Drawable[] drawables = {drawable, drawable};

            // Set the drawables
            field = editor.getClass().getDeclaredField("mCursorDrawable");
            field.setAccessible(true);
            field.set(editor, drawables);
        } catch (Exception ignored) {
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void showCustomAlert(Context context) {

        // Create layout inflator object to inflate toast.xml file
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Call toast.xml file for toast layout
        View toastRoot = inflater.inflate(R.layout.customtoast, null);

        Toast toast = new Toast(context);

        // Set layout to toast
        toast.setView(toastRoot);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
                0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();

    }
//    https://github.com/TellH/RecyclerTreeView


    public static long convertStringToLong(String dateString) {

        long timeInMilliseconds = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date mDate = sdf.parse(dateString);
            timeInMilliseconds = mDate.getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timeInMilliseconds;
    }


    public static boolean returnEventCompleted(String eventDate) {

        if (eventDate == null)
            return false;

        boolean isCompleted = false;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date strDate = null;

        if (!isValidString(eventDate))
            return false;

        try {
            strDate = sdf.parse(eventDate);
        } catch (ParseException e) {
            e.printStackTrace();
            isCompleted = false;
        }
        if (strDate == null)
            return false;

        if (new Date().after(strDate)) {
            isCompleted = true;

        } else {
            isCompleted = false;
        }

        return isCompleted;
    }


    public static String convertToEventDisplayDateFormat(String dateTime, String currentFormat) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat preFormat = new SimpleDateFormat(currentFormat);
        SimpleDateFormat postFormater = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        String newDate = null;
        try {
            newDate = postFormater.format(preFormat.parse(dateTime));
        } catch (Exception e) {
            Log.d("In getCurrentDateTime", e.getMessage());
        }
        return newDate;
    }

    public static String convertToEventDisplayDateFormatCreatedOn(String dateTime, String currentFormat) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat preFormat = new SimpleDateFormat(currentFormat);
        SimpleDateFormat postFormater = new SimpleDateFormat("MM/dd/yyyy");
        String newDate = null;
        try {
            newDate = postFormater.format(preFormat.parse(dateTime));
        } catch (Exception e) {
            Log.d("In getCurrentDateTime", e.getMessage());
        }
        return newDate;
    }

    public static String getCurrentDateTimeInUTC(String format) {

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date myDate = new Date();
        Log.d("TIME", "getCurrentDateTimeInNormal: " + myDate);
        c.setTime(myDate);
        Date dateTimeInUtc = c.getTime();

        Log.d("TIME", "getCurrentDateTimeInUTC: " + dateTimeInUtc);
        SimpleDateFormat df = null;
        if (isValidString(format)) {
            df = new SimpleDateFormat(format);
        } else {
            df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        return df.format(dateTimeInUtc);
    }


    public static String getLastDateOfMonth(int year, int month) {

        SimpleDateFormat postFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String endDateStr = "";
        Calendar calendar = new GregorianCalendar(year, month,
                Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH));


        endDateStr = postFormater.format(calendar.getTime());

        return endDateStr;

    }

    public static Drawable setTintDrawable(Drawable drawable, @ColorInt int color) {
        drawable.clearColorFilter();
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        drawable.invalidateSelf();
        Drawable wrapDrawable = DrawableCompat.wrap(drawable).mutate();
        DrawableCompat.setTint(wrapDrawable, color);
        return wrapDrawable;
    }

    public static Drawable getAttachedFileTypeDrawable(String typeFile, Context context, String colorString) {

        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(context).inflate(R.layout.homebutton, null);
        TextView iconText = (TextView) customNav.findViewById(R.id.homeicon);
        iconText.setTextColor(Color.parseColor(colorString));
        iconText.setText(gettheContentType(typeFile));
        FontManager.markAsIconContainer(customNav.findViewById(R.id.homeicon), iconFont);
        Drawable d = new BitmapDrawable(context.getResources(), createBitmapFromView(context, customNav));

        return d;
    }

    public static int gettheContentType(String typeFile) {

        switch (typeFile.toLowerCase()) {
            case "xls":
            case "xlsx":

            case ".xls":
            case ".xlsx":
                return R.string.fa_icon_file_excel_o;
            case "pdf":
            case ".pdf":

                return R.string.fa_icon_file_pdf_o;
            case "ppt":
            case "pptx":

            case ".ppt":
            case ".pptx":
                return R.string.fa_icon_file_powerpoint_o;
            case "doc":
            case ".docx":
                return R.string.fa_icon_file_word_o;
            case "mpp":
            case ".mpp":
                return R.string.fa_icon_file;
            case "mp3":
            case "wav":
            case "rmj":
            case "m3u":
            case "ogg":
            case "webm":

            case ".mp3":
            case ".wav":
            case ".rmj":
            case ".m3u":
            case ".ogg":
            case ".webm":
                return R.string.fa_icon_file_audio_o;
            case "m4a":
            case "dat":
            case "wmi":
            case "avi":
            case "wm":
            case "wmv":
            case "flv":
            case "rmvb":
            case "mp4":
            case "ogv":

            case ".m4a":
            case ".dat":
            case ".wmi":
            case ".avi":
            case ".wm":
            case ".wmv":
            case ".flv":
            case ".rmvb":
            case ".mp4":
            case ".ogv":
                return R.string.fa_icon_file_video_o;
            default:
                return R.string.fa_icon_file;

        }
    }

    public static int gettheContentTypeNotImg(String typeFile) {

        switch (typeFile.toLowerCase()) {
            case ".xls":
            case ".xlsx":
                return R.string.fa_icon_file_excel_o;
            case ".pdf":
                return R.string.fa_icon_file_pdf_o;
            case ".ppt":
            case ".pptx":
                return R.string.fa_icon_file_powerpoint_o;
            case ".doc":
            case ".docx":
                return R.string.fa_icon_file_word_o;
            case ".mpp":
                return R.string.fa_icon_file;
            case ".mp3":
            case ".wav":
            case ".rmj":
            case ".m3u":
            case ".ogg":
            case ".webm":
                return R.string.fa_icon_file_audio_o;
            case ".m4a":
            case ".dat":
            case ".wmi":
            case ".avi":
            case ".wm":
            case ".wmv":
            case ".flv":
            case ".rmvb":
            case ".mp4":
            case ".ogv":
                return R.string.fa_icon_file_video_o;
            case ".jpg":
            case ".png":
            case ".jpeg":
            case ".tiff":
            case ".gif":
            case ".webp":
            case ".svg":
                return 0;
            default:
                return R.string.fa_icon_file;

        }
    }


//    public String getDate(String format)
//    {
//        Date ourDate = new Date();
//        try
//        {
//            SimpleDateFormat formatter = new SimpleDateFormat(format);
//            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
//            Date value = formatter.parse(ourDate);
//
//            SimpleDateFormat dateFormatter = new SimpleDateFormat(format); //this format changeable
//            dateFormatter.setTimeZone(TimeZone.getDefault());
//            ourDate = dateFormatter.format(value);
//
//            //Log.d("ourDate", ourDate);
//        }
//        catch (Exception e)
//        {
//            ourDate = "00-00-0000 00:00";
//        }
//        return ourDate;
//    }

    public static boolean getEventCompletedUTC(String eventDate) {
        boolean isCompleted = false;

        if (!isValidString(eventDate))
            return isCompleted;

        Date myDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTime(myDate);
        Date dateTimeInUtc = calendar.getTime();
        SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(dateTimeInUtc);
        Log.d("UTC", "getEventCompletedUTC: " + dateTimeInUtc);
        Date strDate = null;
        try {
            strDate = outputFmt.parse(eventDate);
        } catch (ParseException e) {
            e.printStackTrace();
            isCompleted = false;
        }
        if (dateTimeInUtc.after(strDate)) {
            isCompleted = true;

        } else {
            isCompleted = false;
        }

        return isCompleted;
    }

    public static InputFilter checkInputFilter(final String blockCharacterSet) {

        InputFilter filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                if (source != null && blockCharacterSet.contains(("" + source))) {
                    return "";
                }
                return null;
            }
        };

        return filter;
    }


    /**
     * Unzip a ZIP file, keeping the directory structure.
     *
     * @param zipFile        A valid ZIP file.
     * @param destinationDir The destination directory. It will be created if it doesn't exist.
     * @return {@code true} if the ZIP file was successfully decompressed.
     */
    public static boolean unzip(File zipFile, File destinationDir) {
        int BUFFER_SIZE = 6 * 1024;
        ZipFile zip = null;
        try {
            destinationDir.mkdirs();
            zip = new ZipFile(zipFile);
            Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();
            while (zipFileEntries.hasMoreElements()) {
                ZipEntry entry = zipFileEntries.nextElement();
                String entryName = entry.getName();
                File destFile = new File(destinationDir, entryName);
                File destinationParent = destFile.getParentFile();
                if (destinationParent != null && !destinationParent.exists()) {
                    destinationParent.mkdirs();
                }
                if (!entry.isDirectory()) {
                    BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
                    int currentByte;
                    byte data[] = new byte[BUFFER_SIZE];
                    FileOutputStream fos = new FileOutputStream(destFile);
                    BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER_SIZE);
                    while ((currentByte = is.read(data, 0, BUFFER_SIZE)) != 0) {
                        dest.write(data, 0, currentByte);
                    }
                    dest.flush();
                    dest.close();
                    is.close();
                }
            }
        } catch (Exception e) {
            return false;
        } finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (IOException ignored) {
                }
            }
        }
        return true;
    }


    //   https://github.com/RusticiSoftware/TinCanAndroid-Offline/tree/master/TinCanJava-Offline/src/com/rs

    @SuppressLint("ResourceAsColor")
    public static Drawable getDrawableFromStringWithColor(Context context, int resourceID, String colorString) {

        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(context).inflate(R.layout.iconimage, null);
        TextView iconText = (TextView) customNav.findViewById(R.id.imageicon);
        iconText.setTextColor(Color.parseColor(colorString));
        iconText.setText(resourceID);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.imageicon), iconFont);
        Drawable d = new BitmapDrawable(context.getResources(), createBitmapFromView(context, customNav));

        return d;
    }

    public static Drawable getDrawableForStars(float resourceID, Context context) {

        View customNav = LayoutInflater.from(context).inflate(R.layout.sortrating, null);
        RatingBar ratingDrawable = (RatingBar) customNav.findViewById(R.id.ratingDrawable);

        ratingDrawable.setRating(resourceID);

        LayerDrawable stars = (LayerDrawable) ratingDrawable.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(context.getResources().getColor(R.color.colorRating), PorterDuff.Mode.SRC_ATOP);

        Drawable d = new BitmapDrawable(context.getResources(), createBitmapFromView(context, customNav));

        return d;
    }

    @SuppressLint("ResourceAsColor")
    public static Drawable getDrawableFromStringWithColorWithSize(Context context, int resourceID, String colorString) {

        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(context).inflate(R.layout.iconimage, null);
        TextView iconText = (TextView) customNav.findViewById(R.id.imageicon);
        iconText.setTextColor(Color.parseColor(colorString));
        iconText.setText(resourceID);
        iconText.setTextSize(20);
        iconText.setTypeface(iconText.getTypeface(), Typeface.BOLD);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.imageicon), iconFont);
        Drawable d = new BitmapDrawable(context.getResources(), createBitmapFromView(context, customNav));

        return d;
    }


    public static boolean isBigFileThanExpected(String expectedFileSize, File file) {
        boolean isCompleted = true;

        if (expectedFileSize == null || !file.exists())
            return false;

        int requiredSize = 0;
        try {
            requiredSize = Integer.parseInt(expectedFileSize) / 1048576;
        } catch (NumberFormatException nmEx) {
            nmEx.printStackTrace();
            requiredSize = 0;
            return false;
        }

        double bytes = file.length();
        double kilobytes = (bytes / 1024);
        double megabytes = (kilobytes / 1024);

        int foundFileSize = (int) Math.floor(megabytes);


        Log.d("isBigFileThanExpected", "isBigFileThanExpected: FileSize " + requiredSize / 1048576);

        Log.d("isBigFileThanExpected", "file: FileSize " + foundFileSize);

        if (foundFileSize < requiredSize)
            isCompleted = false;

        return isCompleted;
    }


    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            Log.d("CACHE", "deleteDir: " + dir.getAbsolutePath());
            return dir.delete();
        } else {
            return false;
        }
    }

    public static boolean isJwFileExist(File file) {

        boolean isJwExist = false;
        File[] list = file.listFiles();
        if (list == null)
            return false;
        int count = 0;
        for (File f : list) {
            if (f != null && f.isDirectory()) {
                File[] listChilds = f.listFiles();
                for (File cFIle : listChilds) {
                    String name = cFIle.getName();
                    if (name.contains("jwvideoslist.xml")) {
                        isJwExist = true;
                        return true;
                    }
                }
            } else if (f != null && f.isFile()) {

                String name = f.getName();
                if (name.contains("jwvideoslist.xml")) {
                    count++;
                    System.out.println("jwvideoslist " + count);
                    System.out.println("jwvideoslist " + f.getAbsolutePath());
                    isJwExist = true;
                    return true;
                }
            }

        }
        return isJwExist;
    }


    public static List<String> getAllJwFileLocalPaths(File file) {

        List<String> jwFileURLArray = new ArrayList<>();

        File[] list = file.listFiles();
        if (list == null)
            return jwFileURLArray;
        int count = 0;
        for (File f : list) {
            if (f != null && f.isDirectory()) {
                File[] listChilds = f.listFiles();
                for (File cFIle : listChilds) {
                    String name = cFIle.getName();
                    if (name.contains("jwvideoslist.xml")) {

                        jwFileURLArray.add(cFIle.getAbsolutePath());
                    }
                }
            } else if (f != null && f.isFile()) {

                String name = f.getName();
                if (name.contains("jwvideoslist.xml")) {
                    count++;
                    System.out.println("jwvideoslist " + count);
                    System.out.println("jwvideoslist " + f.getAbsolutePath());

                    jwFileURLArray.add(f.getAbsolutePath());
                }
            }

        }
        return jwFileURLArray;
    }

    public static List<String> getAllJwUrlPath(List<String> xmlFilePaths) throws IOException, SAXException, ParserConfigurationException {

        List<String> jwFileURLArray = new ArrayList<>();

        if (xmlFilePaths != null && xmlFilePaths.size() > 0) {

            for (int i = 0; i < xmlFilePaths.size(); i++) {

                File fileAtPosition = new File(xmlFilePaths.get(i));
                if (fileAtPosition.exists()) {

                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                            .newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = null;
                    try {
                        doc = dBuilder.parse(fileAtPosition);
                    } catch (DOMException sax) {
                        sax.printStackTrace();
                        return jwFileURLArray;
                    }

                    doc.getDocumentElement().normalize();
                    NodeList workflowList = doc.getElementsByTagName("jwvideos");
                    int workflowCount = workflowList.getLength();


                    for (int wfItem = 0; wfItem < workflowCount; wfItem++) {

                        Node workflowNode = workflowList.item(wfItem);

                        if (workflowNode.getNodeType() == Node.ELEMENT_NODE) {

                            Element workflowElement = (Element) workflowNode;

                            String jwVideo = getValue("jwvideo", workflowElement);

                            Log.d("JW", "getTagName: " + jwVideo);
                            if (isValidString(jwVideo))
                                jwFileURLArray.add(jwVideo);
                        }

                    }
                }

            }

        }
        return jwFileURLArray;
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }


    public static String toCSVString(List<String> array) {
        String result = "";
        if (array.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String s : array) {
                sb.append(s).append(",");
            }
            result = sb.deleteCharAt(sb.length() - 1).toString();
        }
        return result;
    }


    public static String caluculateDuration(String duration, String hoursStr, String minStr) {
        String result = "";

        if (!isValidString(duration))
            return result;


        if (isValidString(duration)) {
            int intTime = 0;
            try {
                intTime = Integer.parseInt(duration);

                int intHr = intTime / 60;

                int intMin = intTime % 60;

                if (intHr > 0) {
                    result = "" + intHr + " " + hoursStr;
                }
                if (intMin > 0) {
                    result = result + " " + intMin + " " + minStr;
                }

            } catch (NumberFormatException exception) {
                intTime = 0;
                return result;
            }

        }

        return result;
    }
}