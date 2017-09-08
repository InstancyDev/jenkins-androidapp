package com.instancy.instancylearning.utils;

import android.content.Context;

import com.bigkoo.svprogresshud.SVProgressHUD;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Upendranath on 6/6/2017.
 */


public class SweetAlert {

    private static SVProgressHUD mSVProgressHUD;


    public static void showSvProgressAlert(SVProgressHUD svprogressHud) {
//        svprogressHud.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.None);
        svprogressHud.show();
    }


    public static void dismisSvProgressAlert(SVProgressHUD svprogressHud) {

        svprogressHud.dismiss();
    }


    public static void sweetErrorAlert(Context context, String titleTxt, String contentTxt) {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(titleTxt)
                .setContentText(contentTxt)
                .show();

    }

    public static void sweetAlertSuccess(Context context, String titleTxt, String contentTxt) {

        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(titleTxt)
                .setContentText(contentTxt)
                .show();

    }

    public static void sweetAlertNoNet(Context context, String titleTxt, String contentTxt) {

        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(titleTxt)
                .setContentText(contentTxt)
                .show();

    }
}
