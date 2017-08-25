package com.reactnativenavigation.react;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.shell.MainReactPackage;
import com.reactnativenavigation.NavigationApplication;
import com.reactnativenavigation.bridge.NavigationReactEventEmitter;
import com.reactnativenavigation.bridge.NavigationReactPackage;
import com.reactnativenavigation.events.EventBus;
import com.reactnativenavigation.events.JsDevReloadEvent;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class NavigationReactGateway implements ReactGateway {

	public ReactNativeHost host;
	public NavigationReactEventEmitter reactEventEmitter;
	public JsDevReloadHandler jsDevReloadHandler;

	public NavigationReactGateway() {
		host = new ReactNativeHostImpl();
		jsDevReloadHandler = new JsDevReloadHandler();
	}

	@Override
	public void startReactContextOnceInBackgroundAndExecuteJS() {
		getReactInstanceManager().createReactContextInBackground();
	}

	public boolean isInitialized() {
		return host.hasInstance() && getReactInstanceManager().getCurrentReactContext() != null;
	}

	@Override
	public boolean hasStartedCreatingContext() {
		return getReactInstanceManager().hasStartedCreatingInitialContext();
	}

	public ReactContext getReactContext() {
		return getReactInstanceManager().getCurrentReactContext();
	}

	public NavigationReactEventEmitter getReactEventEmitter() {
		return reactEventEmitter;
	}

	@Override
	public ReactInstanceManager getReactInstanceManager() {
		return host.getReactInstanceManager();
	}

	public void onBackPressed() {
		getReactInstanceManager().onBackPressed();
	}

	public void onDestroyApp() {
		getReactInstanceManager().onHostDestroy();
		host.clear();
	}

	public void onPauseActivity() {
		getReactInstanceManager().onHostPause();
		jsDevReloadHandler.onPauseActivity();
	}

	public void onNewIntent(Intent intent) {
		getReactInstanceManager().onNewIntent(intent);
	}

	@Override
	public boolean onKeyUp(View currentFocus, int keyCode) {
		return jsDevReloadHandler.onKeyUp(currentFocus, keyCode);
	}

	public void onResumeActivity(Activity activity, DefaultHardwareBackBtnHandler defaultHardwareBackBtnHandler) {
		getReactInstanceManager().onHostResume(activity, defaultHardwareBackBtnHandler);
		jsDevReloadHandler.onResumeActivity();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		ReactContext reactContext = getReactInstanceManager().getCurrentReactContext();
		if (reactContext != null) {
			Activity currentActivity = reactContext.getCurrentActivity();
			getReactInstanceManager().onActivityResult(currentActivity, requestCode, resultCode, data);
		}
	}

	public ReactNativeHost getReactNativeHost() {
		return host;
	}

	//TODO temp hack
	public void onReactContextInitialized() {
		reactEventEmitter = new NavigationReactEventEmitter(getReactContext());
	}

	public static class ReactNativeHostImpl extends ReactNativeHost implements ReactInstanceManager.ReactInstanceEventListener {

		public ReactNativeHostImpl() {
			super(NavigationApplication.instance);
		}

		@Override
		public boolean getUseDeveloperSupport() {
			return NavigationApplication.instance.isDebug();
		}

		@Override
		protected List<ReactPackage> getPackages() {
			List<ReactPackage> result = new ArrayList<>();

			List<ReactPackage> additionalReactPackages = NavigationApplication.instance.createAdditionalReactPackages();
			if (additionalReactPackages != null)
				result.addAll(additionalReactPackages);

			if (!containsInstanceOfClass(result, MainReactPackage.class)) {
				result.add(new MainReactPackage());
			}
			if (!containsInstanceOfClass(result, NavigationReactPackage.class)) {
				result.add(new NavigationReactPackage());
			}

			return result;
		}

		private <T extends ReactPackage> boolean containsInstanceOfClass(List<ReactPackage> list, Class<T> packageClass) {
			for (ReactPackage reactPackage : list) {
				if (packageClass.isInstance(reactPackage)) return true;
			}
			return false;
		}

		@Override
		public ReactInstanceManager createReactInstanceManager() {
			ReactInstanceManager manager = super.createReactInstanceManager();
			if (NavigationApplication.instance.isDebug()) {
				replaceJsDevReloadListener(manager);
			}
			manager.addReactInstanceEventListener(this);
			return manager;
		}

		private void replaceJsDevReloadListener(ReactInstanceManager manager) {
			new JsDevReloadListenerReplacer(manager, new JsDevReloadListenerReplacer.Listener() {
				@Override
				public void onJsDevReload() {
					EventBus.instance.post(new JsDevReloadEvent());
				}
			}).replace();
		}

		@Override
		public void onReactContextInitialized(ReactContext context) {
			((NavigationReactGateway) NavigationApplication.instance.getReactGateway()).onReactContextInitialized();
			NavigationApplication.instance.onReactInitialized(context);
		}

		@Override
		public void clear() {
			getReactInstanceManager().removeReactInstanceEventListener(this);
			super.clear();
		}

		@Override
		protected String getJSMainModuleName() {
			String jsMainModuleName = NavigationApplication.instance.getJSMainModuleName();
			if (jsMainModuleName != null)
				return jsMainModuleName;
			return super.getJSMainModuleName();
		}

		@Nullable
		@Override
		protected String getJSBundleFile() {
			String jsBundleFile = NavigationApplication.instance.getJSBundleFile();
			if (jsBundleFile != null)
				return jsBundleFile;
			return super.getJSBundleFile();
		}

		@Nullable
		@Override
		protected String getBundleAssetName() {
			String bundleAssetName = NavigationApplication.instance.getBundleAssetName();
			if (bundleAssetName != null)
				return bundleAssetName;
			return super.getBundleAssetName();
		}
	}
}
