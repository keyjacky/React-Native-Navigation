package com.reactnativenavigation.params;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.text.TextUtils;

import com.reactnativenavigation.utils.TypefaceLoader;

public class StyleParams {
    public Bundle params;

    public StyleParams(Bundle params) {
        this.params = params;
    }
    public static class Color {
        @ColorInt
        private Integer color = null;

        public Color() {
            color = null;
        }

        public Color(Integer color) {
            this.color = color;
        }

        public boolean hasColor() {
            return color != null;
        }

        @ColorInt
        public int getColor() {
            if (!hasColor()) {
                throw new RuntimeException("Color undefined");
            }
            return color;
        }

        public static Color parse(Bundle bundle, String key) {
            return bundle.containsKey(key) ? new Color(bundle.getInt(key)) : new Color();
        }

        public String getHexColor() {
            return String.format("#%06X", (0xFFFFFF & getColor()));
        }

        public int getColor(int defaultColor) {
            return hasColor() ? getColor() : defaultColor;
        }
    }

    public static class Font {
        private Typeface typeface;

        public Font(String font) {
            typeface = new TypefaceLoader(font).getTypeFace();
        }

        public Font() {
        }

        public boolean hasFont() {
            return typeface != null;
        }

        public Typeface get() {
            if (typeface == null) {
                throw new RuntimeException("Font undefined");
            }
            return typeface;
        }
    }

    public Orientation orientation;
    public StatusBarTextColorScheme statusBarTextColorScheme;
    public Color statusBarColor;
    public Color contextualMenuStatusBarColor;
    public Color contextualMenuButtonsColor;
    public Color contextualMenuBackgroundColor;

    public Color topBarColor;
    public String topBarReactView;
    public String topBarReactViewAlignment;
    public Bundle topBarReactViewInitialProps;
    public CollapsingTopBarParams collapsingTopBarParams;
    public boolean topBarCollapseOnScroll;
    public boolean topBarElevationShadowEnabled;
    public boolean topTabsHidden;
    public boolean drawScreenBelowTopBar;

    public boolean titleBarHidden;
    public boolean titleBarHideOnScroll;
    public boolean topBarTransparent;
    public boolean topBarTranslucent;
    public Color titleBarTitleColor;
    public Color titleBarSubtitleColor;
    public Color titleBarButtonColor;
    public Color titleBarDisabledButtonColor;
    public Font titleBarTitleFont;
    public int titleBarTitleFontSize;
    public boolean titleBarTitleTextCentered;
    public boolean backButtonHidden;

    public Color topTabTextColor;
    public Color topTabIconColor;
    public Color selectedTopTabTextColor;
    public Color selectedTopTabIconColor;
    public int selectedTopTabIndicatorHeight;
    public Color selectedTopTabIndicatorColor;
    public boolean topTabsScrollable;

    public Color screenBackgroundColor;

    public boolean drawScreenAboveBottomTabs;

    public Color snackbarButtonColor;

    public boolean bottomTabsHidden;
    public boolean bottomTabsHiddenOnScroll;
    public Color bottomTabsColor;
    public Color selectedBottomTabsButtonColor;
    public Color bottomTabsButtonColor;
    public boolean forceTitlesDisplay;
    public Color bottomTabBadgeTextColor;
    public Color bottomTabBadgeBackgroundColor;
    public Font bottomTabFontFamily;

    public Color navigationBarColor;

    public boolean hasTopBarCustomComponent() {
        return !TextUtils.isEmpty(topBarReactView);
    }
}
