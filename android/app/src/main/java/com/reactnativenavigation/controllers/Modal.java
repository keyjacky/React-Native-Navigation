package com.reactnativenavigation.controllers;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.react.bridge.Callback;
import com.reactnativenavigation.NavigationApplication;
import com.reactnativenavigation.R;
import com.reactnativenavigation.layouts.Layout;
import com.reactnativenavigation.layouts.ModalScreenLayout;
import com.reactnativenavigation.layouts.ScreenStackContainer;
import com.reactnativenavigation.params.ContextualMenuParams;
import com.reactnativenavigation.params.FabParams;
import com.reactnativenavigation.params.Orientation;
import com.reactnativenavigation.params.ScreenParams;
import com.reactnativenavigation.params.SlidingOverlayParams;
import com.reactnativenavigation.params.TitleBarButtonParams;
import com.reactnativenavigation.params.TitleBarLeftButtonParams;
import com.reactnativenavigation.screens.NavigationType;

import java.util.List;

public class Modal extends Dialog implements DialogInterface.OnDismissListener, ScreenStackContainer {

    private final AppCompatActivity activity;
    private final OnModalDismissedListener onModalDismissedListener;
    private final ScreenParams screenParams;
    private Layout layout;
    private boolean isDestroyed;

    public void setTopBarVisible(String screenInstanceId, boolean hidden, boolean animated) {
        layout.setTopBarVisible(screenInstanceId, hidden, animated);
    }

    void setTitleBarTitle(String screenInstanceId, String title) {
        layout.setTitleBarTitle(screenInstanceId, title);
    }

    void setTitleBarSubtitle(String screenInstanceId, String subtitle) {
        layout.setTitleBarSubtitle(screenInstanceId, subtitle);
    }

    void setTitleBarRightButtons(String screenInstanceId, String navigatorEventId, List<TitleBarButtonParams> titleBarButtons) {
        layout.setTitleBarRightButtons(screenInstanceId, navigatorEventId, titleBarButtons);
    }

    public void setTitleBarLeftButton(String screenInstanceId, String navigatorEventId, TitleBarLeftButtonParams titleBarLeftButton) {
        layout.setTitleBarLeftButton(screenInstanceId, navigatorEventId, titleBarLeftButton);
    }

    void setFab(String screenInstanceId, String navigatorEventId, FabParams fab) {
        layout.setFab(screenInstanceId, navigatorEventId, fab);
    }

    void updateScreenStyle(String screenInstanceId, Bundle styleParams) {
        layout.updateScreenStyle(screenInstanceId, styleParams);
    }

    public void showContextualMenu(String screenInstanceId, ContextualMenuParams params, Callback onButtonClicked) {
        layout.showContextualMenu(screenInstanceId, params, onButtonClicked);
    }

    public void dismissContextualMenu(String screenInstanceId) {
        layout.dismissContextualMenu(screenInstanceId);
    }

    void showSlidingOverlay(SlidingOverlayParams params) {
        layout.showSlidingOverlay(params);
    }

    void hideSlidingOverlay() {
        layout.hideSlidingOverlay();
    }

    @Override
    public boolean onTitleBarBackButtonClick() {
        if (!layout.onBackPressed()) {
            onBackPressed();
        }
        return true;
    }

    public void onSideMenuButtonClick() {
    }

    void selectTopTabByScreen(String screenInstanceId) {
        layout.selectTopTabByScreen(screenInstanceId);
    }

    public void selectTopTabByTabIndex(String screenInstanceId, int index) {
        layout.selectTopTabByTabIndex(screenInstanceId, index);
    }

    String getCurrentlyVisibleScreenId() {
        return layout.getCurrentlyVisibleScreenId();
    }

    interface OnModalDismissedListener {
        void onModalDismissed(Modal modal);
    }

    public Modal(AppCompatActivity activity, OnModalDismissedListener onModalDismissedListener, ScreenParams screenParams) {
        super(activity, R.style.Modal);
        this.activity = activity;
        this.onModalDismissedListener = onModalDismissedListener;
        this.screenParams = screenParams;
        createContent();
        setAnimation();
    }

    public AppCompatActivity getActivity() {
        return activity;
    }

    private void createContent() {
        setCancelable(true);
        setOnDismissListener(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        layout = new ModalScreenLayout(getActivity(), screenParams, this);
        setWindowFlags();
        setOrientation(screenParams.styleParams.orientation);
        setContentView(layout.asView());
    }

    private void setWindowFlags() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

    private void setAnimation() {
        if (!screenParams.animateScreenTransitions) {
            if (getWindow() != null) {
                getWindow().setWindowAnimations(android.R.style.Animation);
            }
        }
    }

    @Override
    public void push(ScreenParams params) {
        layout.push(params);
    }

    @Override
    public void pop(ScreenParams screenParams) {
        layout.pop(screenParams);
    }

    @Override
    public void popToRoot(ScreenParams params) {
        layout.popToRoot(params);
    }

    @Override
    public void newStack(ScreenParams params) {
        layout.newStack(params);
    }

    boolean containsNavigator(String navigatorId) {
        return layout.containsNavigator(navigatorId);
    }

    @Override
    public void destroy() {
        isDestroyed = true;
        layout.destroy();
    }

    @Override
    public void onBackPressed() {
        if (!layout.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void dismiss() {
        if (!isDestroyed) {
            NavigationApplication.instance.getEventEmitter().sendWillDisappearEvent(layout.getCurrentScreen().getScreenParams(), NavigationType.DismissModal);
            NavigationApplication.instance.getEventEmitter().sendDidDisappearEvent(layout.getCurrentScreen().getScreenParams(), NavigationType.DismissModal);
        }
        super.dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (isDestroyed) {
            return;
        }
        destroy();
        onModalDismissedListener.onModalDismissed(this);
    }

    void onModalDismissed() {
        setOrientation(screenParams.styleParams.orientation);
        layout.onModalDismissed();
    }

    private void setOrientation(Orientation orientation) {
        getActivity().setRequestedOrientation(orientation.orientationCode);
    }
}
