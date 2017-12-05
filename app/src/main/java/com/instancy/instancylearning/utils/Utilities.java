package com.instancy.instancylearning.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.ColorRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import com.instancy.instancylearning.BuildConfig;
import com.instancy.instancylearning.mainactivities.Splash_activity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Utilities {

    static Toast toast = null;


    public static void showSweetAlert(Context context) {

        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("Something went wrong!")
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
     * @author Venu
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

    /**
     * To format Date time string from {@code currentFormat} to {@code newFormat}.
     *
     * @param currentFormat The current date time format.
     * @param newFormat     Format in which the date time is to be returned.
     * @return The new Date time string in {@code newFormat}.
     * @author Venu
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

    /**
     * To copy the data from {@code InputStream} to {@code OutputStream} .
     *
     * @param in  InputStream
     * @param out OutputStream
     * @throws IOException
     * @author Venu
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
     * @author Venu
     */
    public static boolean isValidString(String str) {
        try {
            if (str == null || str.equals("") || str.contains("null") || str.equals("undefined") || str.equals("null\n")) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
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
     * @author Venu
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
     * @author Venu
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
     * @author Venu
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
     * @author Venu
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
     * @author Venu
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
     * @author Venu
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
     * @author Venu
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
     * @author Venu
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
     * @author Venu
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
     * @author Venu
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
     * @author Venu
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
     * @author Venu
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
     * @author Venu
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
     * @author Venu
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
     * @author Venu
     */
    public static String getProfileImageSourcePath(String siteUrl, String siteId, String picName) {

        String profileImageDir = siteUrl + "content/SiteFiles/" + siteId + "/ProfileImages/"
                + picName;

        return profileImageDir;
    }

    /**
     * To highlight the search text.
     *
     * @param ctx
     * @param textView
     * @param text
     * @param spanText
     * @author Venu
     */


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
//     * @author Venu
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

    /**
     * To log the message to LogCat.
     *
     * @param tag      Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param message  The message you would like logged.
     * @param priority One of the Log.DEBUG, Log.ERROR OR Log.INFO
     * @author Venu
     */
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
     * @author Venu
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

}
