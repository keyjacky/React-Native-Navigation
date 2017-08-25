package com.reactnativenavigation.params;

import android.os.Build;
import android.support.annotation.Nullable;

public enum StatusBarTextColorScheme {
    Light, Dark, Undefined;

    public static StatusBarTextColorScheme fromString(@Nullable String colorScheme) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || colorScheme == null) return Undefined;
        switch (colorScheme) {
            case "light":
                return Light;
            case "dark":
                return Dark;
            default:
                return Undefined;
        }
    }
}
