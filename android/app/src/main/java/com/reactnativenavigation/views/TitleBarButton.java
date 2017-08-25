package com.reactnativenavigation.views;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.reactnativenavigation.NavigationApplication;
import com.reactnativenavigation.params.TitleBarButtonParams;
import com.reactnativenavigation.utils.TypefaceSpan;
import com.reactnativenavigation.utils.ViewUtils;

import java.util.ArrayList;

class TitleBarButton implements MenuItem.OnMenuItemClickListener {

    protected final Menu menu;
    protected final View parent;
    TitleBarButtonParams buttonParams;
    @Nullable protected String navigatorEventId;

    TitleBarButton(Menu menu, View parent, TitleBarButtonParams buttonParams, @Nullable String navigatorEventId) {
        this.menu = menu;
        this.parent = parent;
        this.buttonParams = buttonParams;
        this.navigatorEventId = navigatorEventId;
    }

    MenuItem addToMenu(int index) {
        MenuItem item = createMenuItem(index);
        item.setShowAsAction(buttonParams.showAsAction.action);
        item.setEnabled(buttonParams.enabled);
        setIcon(item);
        setColor();
        setFont();
        item.setOnMenuItemClickListener(this);
        return item;
    }

    private MenuItem createMenuItem(int index) {
        if (!buttonParams.hasFont() || TextUtils.isEmpty(buttonParams.label)) {
            return menu.add(Menu.NONE, Menu.NONE, index, buttonParams.label);
        }
        TypefaceSpan span = new TypefaceSpan(buttonParams.font.get());
        SpannableStringBuilder title = new SpannableStringBuilder(buttonParams.label);
        title.setSpan(span, 0, title.length(), 0);
        return menu.add(Menu.NONE, Menu.NONE, index, title);
    }

    private void setIcon(MenuItem item) {
        if (hasIcon()) {
            item.setIcon(buttonParams.icon);
        }
    }

    private void setColor() {
        if (!hasColor() || disableIconTint()) {
            return;
        }

        if (hasIcon()) {
            setIconColor();
        } else {
            setTextColor();
        }
    }

    private void setIconColor() {
        ViewUtils.tintDrawable(buttonParams.icon, buttonParams.color.getColor(), buttonParams.enabled);
    }

    private void setTextColor() {
        ViewUtils.runOnPreDraw(parent, new Runnable() {
            @Override
            public void run() {
                ArrayList<View> outViews = findActualTextViewInMenuByLabel();
                setTextColorForFoundButtonViews(outViews);
            }
        });
    }

    private void setFont() {
        if (!buttonParams.hasFont()) {
            return;
        }
        ArrayList<View> buttons = findActualTextViewInMenuByLabel();
        setTextFontForFoundButtonViews(buttons);
    }

    @NonNull
    private ArrayList<View> findActualTextViewInMenuByLabel() {
        ArrayList<View> outViews = new ArrayList<>();
        parent.findViewsWithText(outViews, buttonParams.label, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        return outViews;
    }

    private void setTextColorForFoundButtonViews(ArrayList<View> buttons) {
        for (View button : buttons) {
            ((TextView) button).setTextColor(buttonParams.getColor().getColor());
        }
    }

    private void setTextFontForFoundButtonViews(ArrayList<View> buttons) {
        for (View button : buttons) {
            if (buttonParams.hasFont()) {
                ((TextView) button).setTypeface(buttonParams.font.get());
            }
        }
    }

    private boolean hasIcon() {
        return buttonParams.icon != null;
    }

    private boolean hasColor() {
        return buttonParams.color.hasColor();
    }

    private boolean disableIconTint() {
        return buttonParams.disableIconTint;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        NavigationApplication.instance.getEventEmitter().sendNavigatorEvent(buttonParams.eventId, navigatorEventId);
        return true;
    }
}
