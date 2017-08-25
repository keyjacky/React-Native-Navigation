package com.reactnativenavigation.views;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.facebook.react.bridge.Callback;
import com.reactnativenavigation.animation.VisibilityAnimator;
import com.reactnativenavigation.params.BaseScreenParams;
import com.reactnativenavigation.params.ContextualMenuParams;
import com.reactnativenavigation.params.NavigationParams;
import com.reactnativenavigation.params.StyleParams;
import com.reactnativenavigation.params.TitleBarButtonParams;
import com.reactnativenavigation.params.TitleBarLeftButtonParams;
import com.reactnativenavigation.screens.Screen;
import com.reactnativenavigation.utils.ViewUtils;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class TopBar extends AppBarLayout {
    protected TitleBar titleBar;
    private ContextualMenu contextualMenu;
    protected FrameLayout titleBarAndContextualMenuContainer;
    protected TopTabs topTabs;
    private VisibilityAnimator visibilityAnimator;
    @Nullable
    private ContentView reactView;

    public TopBar(Context context) {
        super(context);
        setId(ViewUtils.generateViewId());
        createTopBarVisibilityAnimator();
        createLayout();
    }

    private void createTopBarVisibilityAnimator() {
        ViewUtils.runOnPreDraw(this, new Runnable() {
            @Override
            public void run() {
                visibilityAnimator = new VisibilityAnimator(TopBar.this,
                        VisibilityAnimator.HideDirection.Up,
                        getHeight());
            }
        });
    }

    protected void createLayout() {
        titleBarAndContextualMenuContainer = new FrameLayout(getContext());
        addView(titleBarAndContextualMenuContainer);
    }

    public void addTitleBarAndSetButtons(List<TitleBarButtonParams> rightButtons,
                                         TitleBarLeftButtonParams leftButton,
                                         LeftButtonOnClickListener leftButtonOnClickListener,
                                         String navigatorEventId, boolean overrideBackPressInJs) {
        titleBar = createTitleBar();
        addTitleBar();
        addButtons(rightButtons, leftButton, leftButtonOnClickListener, navigatorEventId, overrideBackPressInJs);
    }

    protected TitleBar createTitleBar() {
        return new TitleBar(getContext());
    }

    protected void addTitleBar() {
        titleBarAndContextualMenuContainer.addView(titleBar, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
    }

    private void addButtons(List<TitleBarButtonParams> rightButtons, TitleBarLeftButtonParams leftButton, LeftButtonOnClickListener leftButtonOnClickListener, String navigatorEventId, boolean overrideBackPressInJs) {
        titleBar.setRightButtons(rightButtons, navigatorEventId);
        titleBar.setLeftButton(leftButton, leftButtonOnClickListener, navigatorEventId, overrideBackPressInJs);
    }

    public void setTitle(String title, StyleParams styleParams) {
        titleBar.setTitle(title, styleParams);
    }

    public void setSubtitle(String subtitle) {
        titleBar.setSubtitle(subtitle);
    }

    public void setReactView(@NonNull StyleParams styleParams) {
        if (styleParams.hasTopBarCustomComponent()) {
            reactView = createReactView(styleParams);
            if ("fill".equals(styleParams.topBarReactViewAlignment)) {
                addReactViewFill(reactView);
            } else {
                addCenteredReactView(reactView);
            }
        }
    }

    private ContentView createReactView(StyleParams styleParams) {
        return new ContentView(getContext(),
                styleParams.topBarReactView,
                NavigationParams.EMPTY,
                styleParams.topBarReactViewInitialProps
        );
    }

    private void addReactViewFill(ContentView view) {
        view.setLayoutParams(new LayoutParams(MATCH_PARENT, ViewUtils.getToolBarHeight()));
        titleBar.addView(view);
    }

    private void addCenteredReactView(final ContentView view) {
        titleBar.addView(view, new LayoutParams(WRAP_CONTENT, ViewUtils.getToolBarHeight()));
        view.setOnDisplayListener(new Screen.OnDisplayListener() {
            @Override
            public void onDisplay() {
                view.getLayoutParams().width = (int) (float) view.getChildAt(0).getMeasuredWidth();
                ((ActionBar.LayoutParams) view.getLayoutParams()).gravity = Gravity.CENTER;
                view.requestLayout();
            }
        });
    }

    public void setButtonColor(StyleParams styleParams) {
        titleBar.setButtonColor(styleParams.titleBarButtonColor);
    }

    public void setStyle(StyleParams styleParams) {
        if (styleParams.topBarColor.hasColor()) {
            setBackgroundColor(styleParams.topBarColor.getColor());
        }
        if (styleParams.topBarTransparent) {
            setTransparent();
        }
        titleBar.setStyle(styleParams);
        setReactView(styleParams);
        setTopTabsStyle(styleParams);
        if (!styleParams.topBarElevationShadowEnabled) {
            disableElevationShadow();
        }
    }

    private void setTransparent() {
        setBackgroundColor(Color.TRANSPARENT);
        disableElevationShadow();
    }

    private void disableElevationShadow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(null);
        }
    }

    public void setTitleBarRightButtons(String navigatorEventId, List<TitleBarButtonParams> titleBarButtons) {
        titleBar.setRightButtons(titleBarButtons, navigatorEventId);
    }

    public TopTabs initTabs() {
        topTabs = new TopTabs(getContext());
        addView(topTabs, new ViewGroup.LayoutParams(MATCH_PARENT, (int) ViewUtils.convertDpToPixel(48)));
        return topTabs;
    }

    public void setTitleBarLeftButton(String navigatorEventId,
                                      LeftButtonOnClickListener leftButtonOnClickListener,
                                      TitleBarLeftButtonParams titleBarLeftButtonParams,
                                      boolean overrideBackPressInJs) {
        titleBar.setLeftButton(titleBarLeftButtonParams, leftButtonOnClickListener, navigatorEventId,
                overrideBackPressInJs);
    }

    private void setTopTabsStyle(StyleParams style) {
        if (topTabs == null) {
            return;
        }
        topTabs.setTopTabsTextColor(style);
        topTabs.setSelectedTabIndicatorStyle(style);
        topTabs.setScrollable(style);
    }

    public void showContextualMenu(final ContextualMenuParams params, StyleParams styleParams, Callback onButtonClicked) {
        final ContextualMenu menuToRemove = contextualMenu != null ? contextualMenu : null;
        contextualMenu = new ContextualMenu(getContext(), params, styleParams, onButtonClicked);
        titleBarAndContextualMenuContainer.addView(contextualMenu, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        ViewUtils.runOnPreDraw(contextualMenu, new Runnable() {
            @Override
            public void run() {
                titleBar.hide();
                contextualMenu.show(new Runnable() {
                    @Override
                    public void run() {
                        if (menuToRemove != null) {
                            titleBarAndContextualMenuContainer.removeView(menuToRemove);
                        }
                    }
                });
            }
        });
    }

    public void onContextualMenuHidden() {
        contextualMenu = null;
        titleBar.show();
    }

    public void dismissContextualMenu() {
        if (contextualMenu != null) {
            contextualMenu.dismiss();
            contextualMenu = null;
            titleBar.show();
        }
    }

    public void destroy() {
        if (reactView != null) {
            reactView.unmountReactView();
        }
    }

    public void onViewPagerScreenChanged(BaseScreenParams screenParams) {
        titleBar.onViewPagerScreenChanged(screenParams);
    }

    public void setVisible(boolean visible, boolean animate) {
        titleBar.setVisibility(!visible);
        visibilityAnimator.setVisible(visible, animate);
    }
}
