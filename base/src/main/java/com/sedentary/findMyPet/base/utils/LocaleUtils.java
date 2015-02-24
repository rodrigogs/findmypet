package com.sedentary.findMyPet.base.utils;

import android.content.res.Configuration;

import com.sedentary.findMyPet.base.FindMyPetApplication;

import java.util.Locale;

public class LocaleUtils {

    public static String getCurrent() {
        return getLanguageCode(Locale.getDefault());
    }

    public static void setCurrent(Locale locale) {
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;

        FindMyPetApplication.getAppContext().getResources().updateConfiguration(config, FindMyPetApplication.getAppContext().getResources().getDisplayMetrics());
    }

    public static String getLanguageCode(Locale locale) {
        String languageCode = locale.getLanguage();
        if (!locale.getCountry().isEmpty()) {
            languageCode += "-" + locale.getCountry();
        }
        return languageCode;
    }

    public static Locale toLocale(String languageCode) {
        String[] language = languageCode.split("-");
        if (language.length > 1) {
            return new Locale(language[0], language[1]);
        }
        return new Locale(language[0]);
    }

}
