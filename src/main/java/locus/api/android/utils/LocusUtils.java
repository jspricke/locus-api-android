package locus.api.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import locus.api.android.ActionTools;
import locus.api.android.utils.exceptions.RequiredVersionMissingException;
import locus.api.objects.Storable;
import locus.api.objects.extra.Location;
import locus.api.objects.extra.Track;
import locus.api.objects.extra.Waypoint;
import locus.api.utils.DataReaderBigEndian;
import locus.api.utils.DataWriterBigEndian;
import locus.api.utils.Logger;
import locus.api.utils.Utils;

public class LocusUtils {
	
	private static final String TAG = LocusUtils.class.getSimpleName();
	
	/**************************************************/
	/*                 CHECKING PART                  */
	/**************************************************/
	
	/**
	 * Locus versions used in this API
	 */
	public enum VersionCode {
		
		/**
		 * <ul>
		 * <li>Base Locus versions.</li>
		 * </ul>
		 * Locus Free/Pro 2.7.0, Gis 1.0.0 (235, 235, 1)
		 */
		UPDATE_01(235, 235, 0),
		/**
		 * <ul>
		 * <li>Control of track recording</li>
		 * <li>Ability to add/hide Circle map items</li>
		 * </ul>
		 * Locus Free/Pro 2.8.4 (242, 242, 1)
		 */
		UPDATE_02(242, 242, 0),
		/**
		 * <ul>
		 * <li>Get waypoint by ID</li>
		 * </ul>
		 * Locus Free/Pro 2.17.3 (269, 269, 1)
		 */
		UPDATE_03(269, 269, 0),
		/**
		 * <ul>
		 * <li>Added MapPreview</li>
		 * </ul>
		 * Locus Free/Pro 2.20.2.4 (278, 278, 1)
		 */
		UPDATE_04(278, 278, 0),
        /**
         * <ul>
         * <li>Added Compute track service</li>
         * <li>Added Geocaching field notes support</li>
         * </ul><br />
         * Locus Free/Pro 3.2.0 (296) <br />
         * Locus GIS (no news)
         */
        UPDATE_05(296, 296, 0),
		/**
		 * <ul>
		 * <li>Added request on purchase state of item in Locus Store</li>
		 * </ul><br />
		 * Locus Free/Pro 3.2.3(311), GIS 0.5.3 (5)<br />
		 * Locus GIS (no news)
		 */
		UPDATE_06(311, 311, 5),
		/**
		 * <ul>
		 * <li>Added request to display Point detail screen</li>
		 * </ul><br />
		 * Locus Free/Pro 3.3.0(317)<br />
		 */
		UPDATE_07(317, 317, 0),
        /**
         * <ul>
         * <li>Added "Navigation" on address</li>
         * </ul><br />
         * Locus Free/Pro 3.5.3(343)<br />
         */
        UPDATE_08(343, 343, 0),
        /**
         * <ul>
         * <li>Added "Track record profiles"</li>
         * </ul><br />
         * Locus Free/Pro 3.8.0(357)<br />
         */
        UPDATE_09(357, 357, 0),
        /**
         * <ul>
         * <li>Added "Get track by ID"</li>
         * </ul><br />
         * Locus Free/Pro 3.9.0(370)<br />
         */
        UPDATE_10(370, 370, 0),
        /**
         * <ul>
         * <li>Added parameters to Locus Info</li>
         * </ul><br />
         * Locus Free/Pro 3.9.2(380)<br />
         */
        UPDATE_11(380, 380, 0),
        /**
         * <ul>
         * <li>Added parameters to display certain item in Locus Store</li>
         * </ul><br />
         * Locus Free/Pro 3.13.0(421)<br />
         */
        UPDATE_12(421, 421, 0);

		/**
		 * Version code for a Free version.
		 */
		public final int vcFree;
		/**
		 * Version code for a Pro version.
		 */
		public final int vcPro;
		/**
		 * Version code for a GIS version.
		 */
		public final int vcGis;
		
		VersionCode(int vcFree, int vcPro, int vcGis) {
			this.vcFree = vcFree;
			this.vcPro = vcPro;
			this.vcGis = vcGis;
		}
	}
	
	/**
	 * Main container that keeps information about existing versions in
	 * whole current Android system.  
	 */
	public static class LocusVersion extends Storable {
		
		/**
		 * Version package name defined in manifest of Locus app. Main parameter for
		 * calls to Locus.
		 */
		private String mPackageName;
		/**
		 * Version name defined in manifest of Locus app. This is just textual information of
		 * versionCode.
		 */
		private String mVersionName;
		/**
		 * Version code defined in manifest of Locus app. Core information used for
		 * checks, if Locus versions already has requested feature.
		 */
		private int mVersionCode;

        /**
         * Empty constructor for Storable class.
         */
        public LocusVersion() {
            super();
        }

        /**
         * Basic constructor.
         * @param packageName name of application package
         * @param versionName name of version
         * @param versionCode unique ID code
         */
		private LocusVersion(String packageName, String versionName, int versionCode) {
			if (packageName == null) {
				packageName = "";
			}
			this.mPackageName = packageName;
			if (versionName == null) {
				versionName = "";
			}
			this.mVersionName = versionName;
			if (versionCode < 0) {
				versionCode = 0;
			}
			this.mVersionCode = versionCode;
		}

        /**
         * Test if current app is "Free version".
         * @return <code>true</code> if app is "Free version"
         */
		public boolean isVersionFree() {
			return !isVersionPro() && !isVersionGis();
		}

        /**
         * Test if current app is "Pro version".
         * @return <code>true</code> if app is "Pro version"
         */
        public boolean isVersionPro() {
			return mPackageName.contains(".pro");
		}

        /**
         * Test if current app is "GIS version".
         * @return <code>true</code> if app is "GIS version"
         */
        public boolean isVersionGis() {
			return mPackageName.contains(".gis");
		}

        public String getPackageName() {
            return mPackageName;
        }

        public String getVersionName() {
            return mVersionName;
        }

        public int getVersionCode() {
            return mVersionCode;
        }

		/**
		 * Check if current version is valid compare to required VersionCode
		 * @param code code of version
		 * @return <code>true</code> if version, compared to code, is valid
		 */
		public boolean isVersionValid(VersionCode code) {
			if (isVersionFree()) {
				return code.vcFree != 0 && mVersionCode >= code.vcFree;
			} else if (isVersionPro()) {
				return code.vcPro != 0 && mVersionCode >= code.vcPro;
			} else if (isVersionGis()) {
				return code.vcGis != 0 && mVersionCode >= code.vcGis;
			}
			return false;
		}

        @Override
        public String toString() {
            return Utils.toString(this);
        }

        // STORABLE PART
		
        @Override
        protected int getVersion() {
            return 0;
        }

        @Override
        public void reset() {
            mPackageName = "";
            mVersionName = "";
            mVersionCode = 0;
        }

        @Override
        protected void readObject(int version, DataReaderBigEndian dr) throws IOException {
            mPackageName = dr.readString();
            mVersionName = dr.readString();
            mVersionCode = dr.readInt();
        }

        @Override
        protected void writeObject(DataWriterBigEndian dw) throws IOException {
            dw.writeString(mPackageName);
            dw.writeString(mVersionName);
            dw.writeInt(mVersionCode);
        }
    }
	
	/**
	 * Search for existing (and better also running) version of Locus. This function
	 * search for Locus, but also grab data from all instances. So it's suggested
	 * not to use this function too often.
	 * @param ctx current context
	 * @return active version
	 */
	public static LocusVersion getActiveVersion(Context ctx) {
        return getActiveVersion(ctx, 0);
	}

    /**
     * Search for existing (and better also running) version of Locus. This function
     * search for Locus, but also grab data from all instances. So it's suggested
     * not to use this function too often. It is also possible to define minimum versionCode.
     * @param ctx current context
     * @param minVersionCode minimal version code of Locus
     * @return active version
     */
    public static LocusVersion getActiveVersion(Context ctx, int minVersionCode) {
        // get valid Locus version for any actions
        List<LocusVersion> versions = getAvailableVersions(ctx);
        if (versions.size() == 0) {
            return null;
        }

        // check locus
        LocusVersion backupVersion = null;
        for (int i = 0, m = versions.size(); i < m; i++) {
            try {
                LocusVersion lv = versions.get(i);
                if (lv.getVersionCode() < minVersionCode) {
                    continue;
                }

                // get LocusInfo container
                LocusInfo li = ActionTools.getLocusInfo(ctx, lv);

                // check if Locus runs and if so, set it as active version
                if (li != null) {
                    // backup valid version
                    backupVersion = lv;

                    // check if is running
                    if (li.isRunning()) {
                        return lv;
                    }
                }
            } catch (RequiredVersionMissingException e) {
                Logger.logE(TAG, "prepareActiveLocus()", e);
            }
        }

        // if version is not set, use backup
        if (backupVersion != null) {
            return backupVersion;
        }
        return versions.get(0);
    }

	/**
	 * Search through whole Android system and search for existing versions of Locus
	 * @param ctx current context
	 * @return list of available versions
	 */
	public static List<LocusVersion> getAvailableVersions(Context ctx) {
		// prepare container for existing versions
		List<LocusVersion> versions = new ArrayList<>();
		
		// get all applications and search for locus
		PackageManager pm = ctx.getPackageManager();
		List<ApplicationInfo> appInfos = pm.getInstalledApplications(0);
		for (int i = 0, m = appInfos.size(); i < m; i++) {
			ApplicationInfo appInfo = appInfos.get(i);
			if (isPackageNameLocus(appInfo.packageName)) {

				// get information about version
				LocusVersion lv = createLocusVersion(ctx, appInfo.packageName);
				if (lv != null) {
					versions.add(lv);
				}
			}
		}

		// return result
		return versions;
	}

	/**
	 * CHeck if package name define any version of Locus (so it's possible to
	 * create LocusVersion for this packageName).
	 * @param packageName name to test
	 * @return <code>true</code> if name define Locus app
	 */
	private static boolean isPackageNameLocus(String packageName) {
		// check package name
		if (packageName == null || packageName.length() == 0) {
			return false;
		}

		// quick check
		if (!packageName.startsWith("menion")) {
			return false;
		}

		// check on Locus Map
		if (packageName.equals("menion.android.locus") ||
				packageName.startsWith("menion.android.locus.free") ||
				packageName.startsWith("menion.android.locus.pro")) {
			return true;
		}

//		// check on Locus GIS - TODO currently disabled
//		if (packageName.startsWith("menion.android.locus.gis")) {
//			return true;
//		}

		// not valid package name
		return false;
	}
	
	/**
	 * Get LocusVersion for specific, known packageName. This method should not be used
	 * in common work-flow. Better is receive list of versions and pick correct, or create
	 * LocusVersion from received intent.
	 * @param ctx current context
	 * @param packageName Locus package name
	 * @return generated Locus version
	 */
	public static LocusVersion createLocusVersion(Context ctx, String packageName) {
		try {
			// check package name
			if (packageName == null || packageName.length() == 0 ||
					!packageName.startsWith("menion.android.locus")) {
				return null;
			}
			
			// get information about version			
			PackageManager pm = ctx.getPackageManager();
			PackageInfo info = pm.getPackageInfo(packageName, 0);
			if (info == null) {
				return null;
			}
			
			// finally add item to list
			return new LocusVersion(packageName, info.versionName, info.versionCode);
		} catch (Exception e) {
			Logger.logE(TAG, "getLocusVersion(" + ctx + ", " + packageName + ")", e);
			return null;
		}
	}
	
	/**
	 * Get LocusVersion based on received Intent object. Since Locus version 279,
	 * all Intents contains valid <code>packageName</code>, so it's simple possible
	 * to get valid LocusVersion object. If user has older version of Locus, it's
	 * called function that just return first available version.
	 * @param ctx current context
	 * @param intent received intent from Locus
	 * @return generated Locus version
	 */
	public static LocusVersion createLocusVersion(Context ctx, Intent intent) {
		// check parameters
		if (ctx == null || intent == null) {
			return null;
		}
		
		String packageName = intent.getStringExtra(LocusConst.INTENT_EXTRA_PACKAGE_NAME);
		if (packageName != null && packageName.length() > 0) {
			return createLocusVersion(ctx, packageName);
		} else {
			return createLocusVersion(ctx);
		}
	}
	
	/**
	 * Old method that returns 'random' existing version of Locus installed on this 
	 * device. In most cases, users has only one version, so it's not a big problem.
	 * But in cases, where exists more then one version, this method may cause troubles
	 * so it's not recommended to use it.
	 * @param ctx current context
	 * @return generated Locus version
	 */
	public static LocusVersion createLocusVersion(Context ctx) {
		// check parameters
		if (ctx == null) {
			return null;
		}
		
		// older versions of Locus do not send package name in it's intents. 
		// So we return closest valid Locus version (Pro/Free)
		Logger.logW(TAG, "getLocusVersion(" + ctx + "), " +
				"Warning: old version of Locus: Correct package name is not known!");
		List<LocusVersion> versions = getAvailableVersions(ctx);
		for (int i = 0, m = versions.size(); i < m; i++) {
			LocusVersion lv = versions.get(i);
			if (lv.isVersionFree() || lv.isVersionPro()) {
				return lv;
			}
		}
		return null;
	}
	
	/**************************************************/
	// CHECK LOCUS AVAILABILITY
	/**************************************************/
	
	/**
	 * Returns <code>true</code> if Locus in required version is installed.
	 * 
	 * @param ctx actual {@link Context}
	 * @return <code>true</code> if any Locus version is available
	 */
	public static boolean isLocusAvailable(Context ctx) {
		return isLocusAvailable(ctx, VersionCode.UPDATE_01);
	}
	
	/**
	 * Returns <code>true</code> if Locus in required version is installed.
	 * @param ctx actual {@link Context}
	 * @param vc required version code
	 * @return <code>true</code> if Locus is available
	 */
	public static boolean isLocusAvailable(Context ctx, VersionCode vc) {
		return isLocusAvailable(ctx, vc.vcFree, vc.vcPro, vc.vcGis);
	}
	
	/**
	 * Check if Locus in required version is installed on current system.
	 * @param ctx Context
	 * @param versionFree minimum required version of 
	 * 		Locus Free (or '0' if we don't want Locus Free)
	 * @param versionPro minimum required version of 
	 * 		Locus Pro (or '0' if we don't want Locus Pro)
	 * @param versionGis minimum required version of 
	 * 		Locus Gis (or '0' if we don't want Locus Gis)
	 * @return <code>true</code> if required Locus is installed
	 */
	public static boolean isLocusAvailable(Context ctx, 
			int versionFree, int versionPro, int versionGis) {
		List<LocusVersion> versions = getAvailableVersions(ctx);
		for (int i = 0, m = versions.size(); i < m; i++) {
			LocusVersion lv = versions.get(i);
			if (lv.isVersionFree() && versionFree > 0 &&
					lv.getVersionCode() >= versionFree) {
				return true;
			}
			if (lv.isVersionPro() && versionPro > 0 &&
					lv.getVersionCode() >= versionPro) {
				return true;
			}
			if (lv.isVersionGis() && versionGis > 0 &&
					lv.getVersionCode() >= versionGis) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if LocusVersion if basic 'Locus Free' or 'Locus Pro' in required 
	 * minimal version.
	 * @param lv version of Locus
	 * @param minVersion required minimal version
	 * @return <code>true</code> if LocusVersion is Free/Pro version of
	 * Locus, otherwise returns <code>false</code>
	 */
	public static boolean isLocusFreePro(LocusVersion lv, int minVersion) {
		// check parameters
		if (lv == null) {
			return false;
		}
		
		// check on versions		
		if (lv.isVersionFree() && lv.getVersionCode() >= minVersion) {
			return true;
		}
		if (lv.isVersionPro() && lv.getVersionCode() >= minVersion) {
			return true;
		}
		return true;
	}
	
	/**
	 * Start intent that allows install Free version of Locus
	 * @param ctx current context
	 */
	public static void callInstallLocus(Context ctx) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
	            "http://market.android.com/details?id=menion.android.locus"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
	}

    /**
     * Start existing Locus installation in device.
     * @param ctx current context
     */
    public static void callStartLocusMap(Context ctx) {
        Intent intent = new Intent("com.asamm.locus.map.START_APP");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }
	
	/**************************************************/
	// RESPONSE HANDLING
	/**************************************************/
	
	/*
	   Add POI from your application
	  -------------------------------
	   - on places where is location needed, you may add link to your application. So for example, when
	   you display Edit point dialog, you may see next to coordinates button "New". This is list of
	   location sources and also place when your application appear with this method
	   
		1. register intent-filter for your activity
	   
		<intent-filter>
			<action android:name="locus.api.android.INTENT_ITEM_GET_LOCATION" />
			<category android:name="android.intent.category.DEFAULT" />
		</intent-filter>
    
		2. register intent receiver in your application

		if (getIntent().getAction().equals(LocusConst.INTENT_GET_POINT)) {
			// get some data here and finally return value back, more below
		}
	 */
	
	public static boolean isIntentGetLocation(Intent intent) {
		return isRequiredAction(intent, LocusConst.INTENT_ITEM_GET_LOCATION);
	}
	
	public interface OnIntentGetLocation {
		/**
		 * Handle received request
		 * @param locGps if GPS is enabled, location is included (may be null)
		 * @param locMapCenter center location of displayed map (may be null)
		 */
        void onReceived(Location locGps, Location locMapCenter);
		/**
		 * If intent is not INTENT_GET_LOCATION intent or other problem occur
		 */
        void onFailed();
	}
	
	public static void handleIntentGetLocation(Context context, Intent intent,
			OnIntentGetLocation handler) throws NullPointerException {
		// check source data
		if (intent == null) {
			throw new NullPointerException("Intent cannot be null");
		}
		
		// check intent itself
		if (!isIntentGetLocation(intent)) {
			handler.onFailed();
			return;
		}

		// variables that may be obtain from intent
		handler.onReceived(
				getLocationFromIntent(intent, LocusConst.INTENT_EXTRA_LOCATION_GPS),
				getLocationFromIntent(intent, LocusConst.INTENT_EXTRA_LOCATION_MAP_CENTER));
	}
	
	public static boolean sendGetLocationData(Activity activity, String name, Location loc) {
		if (loc == null) {
			return false;
		} else {
			Intent intent = new Intent();
			// string value name - OPTIONAL
			if (!TextUtils.isEmpty(name))
				intent.putExtra(LocusConst.INTENT_EXTRA_NAME, name);
			intent.putExtra(LocusConst.INTENT_EXTRA_LOCATION, loc.getAsBytes());
			activity.setResult(Activity.RESULT_OK, intent);
			activity.finish();
			return true;
		}
	}
	
	/*
	   Add action under point sub-menu
	  -------------------------------
	   - when you tap on any point on map or in Point screen, under tools bottom button, are functions for 
	   calling to some external application. Under this menu appear also your application. If you want specify
	   action only on your points displayed in Locus, use 'setExtraCallback' function on 'Point' object instead
	   of this. It has same functionality but allow displaying only on your 'own' points.
	   
	   1. register intent-filter for your activity
	   
		<intent-filter>
			<action android:name="locus.api.android.INTENT_ITEM_POINT_TOOLS" />
			<category android:name="android.intent.category.DEFAULT" />
		</intent-filter>
		
		- extra possibility to act only on geocache point
		<intent-filter>
			<action android:name="locus.api.android.INTENT_ITEM_POINT_TOOLS" />
			<category android:name="android.intent.category.DEFAULT" />
			
			<data android:scheme="locus" />
			<data android:path="menion.android.locus/point_geocache" />
		</intent-filter>
    
	   2. register intent receiver in your application or use functions below

		if (isIntentOnPointAction(intent) {
			Waypoint wpt = LocusUtils.getWaypointFromIntent(intent);
        	if (wpt == null) {
        		... problem
        	} else {
         		... handle waypoint
        	}
		}
	 */
	
	public static boolean isIntentPointTools(Intent intent) {
		return isRequiredAction(intent, LocusConst.INTENT_ITEM_POINT_TOOLS);
	}
	
	public static Waypoint handleIntentPointTools(Context ctx, Intent intent) 
			throws RequiredVersionMissingException {
		long wptId = intent.getLongExtra(LocusConst.INTENT_EXTRA_ITEM_ID, -1L);
		if (wptId < 0) {
			return null;
		} else {
			return ActionTools.getLocusWaypoint(ctx, 
					createLocusVersion(ctx, intent),
					wptId);
		}
	}

    public static boolean isIntentTrackTools(Intent intent) {
        return isRequiredAction(intent, LocusConst.INTENT_ITEM_TRACK_TOOLS);
    }

    public static Track handleIntentTrackTools(Context ctx, Intent intent)
            throws RequiredVersionMissingException {
        long trackId = intent.getLongExtra(LocusConst.INTENT_EXTRA_ITEM_ID, -1L);
        if (trackId < 0) {
            return null;
        } else {
            return ActionTools.getLocusTrack(ctx,
                    createLocusVersion(ctx, intent),
                    trackId);
        }
    }
	
	/*
	   Add action under MAIN function menu or SEARCH list 
	  -------------------------------------
	   - when you display menu->functions, your application appear here. Also you application (activity) may
	    be added to right quick menu. Application will be called with current map center coordinates
	   
	   1. register intent-filter for your activity
	   
		<intent-filter>
			<action android:name="locus.api.android.INTENT_ITEM_MAIN_FUNCTION" />
			<category android:name="android.intent.category.DEFAULT" />
		</intent-filter>
		
		<intent-filter>
			<action android:name="locus.api.android.INTENT_ITEM_SEARCH_LIST" />
			<category android:name="android.intent.category.DEFAULT" />
		</intent-filter>
    
    2. register intent receiver in your application

		if (isIntentMainFunction(LocusConst.INTENT_ITEM_MAIN_FUNCTION)) {
			// more below ...
		}
	 */


    /**
     * Check if received intent is response on MAIN_FUNCTION intent.
     * @param intent received intent
     * @return <code>true</code> if intent is base on MAIN_FUNCTION parameter
     */
    public static boolean isIntentMainFunction(Intent intent) {
        return isRequiredAction(intent, LocusConst.INTENT_ITEM_MAIN_FUNCTION);
    }

    /**
     * Handle received intent from section MAIN_FUNCTION.
     * @param intent received intent
     * @param handler handler for events
     * @throws NullPointerException exception if any required data are missing
     */
    public static void handleIntentMainFunction(Context ctx, Intent intent,
            OnIntentMainFunction handler) throws NullPointerException {
        handleIntentMenuItem(ctx, intent, handler, LocusConst.INTENT_ITEM_MAIN_FUNCTION);
    }

    /**
     * Check if received intent is response on MAIN_FUNCTION_GC intent.
     * @param intent received intent
     * @return <code>true</code> if intent is base on MAIN_FUNCTION_GC parameter
     */
    public static boolean isIntentMainFunctionGc(Intent intent) {
        return isRequiredAction(intent, LocusConst.INTENT_ITEM_MAIN_FUNCTION_GC);
    }

    /**
     * Handle received intent from section MAIN_FUNCTION_GC.
     * @param intent received intent
     * @param handler handler for events
     * @throws NullPointerException exception if any required data are missing
     */
    public static void handleIntentMainFunctionGc(Context ctx, Intent intent,
            OnIntentMainFunction handler) throws NullPointerException {
        handleIntentMenuItem(ctx, intent, handler, LocusConst.INTENT_ITEM_MAIN_FUNCTION_GC);
    }

    /**
     * Check if received intent is response on SEARCH_LIST intent.
     * @param intent received intent
     * @return <code>true</code> if intent is base on SEARCH_LIST parameter
     */
    public static boolean isIntentSearchList(Intent intent) {
        return isRequiredAction(intent, LocusConst.INTENT_ITEM_SEARCH_LIST);
    }

    /**
     * Handle received intent from section SEARCH_LIST.
     * @param intent received intent
     * @param handler handler for events
     * @throws NullPointerException exception if any required data are missing
     */
    public static void handleIntentSearchList(Context ctx, Intent intent,
            OnIntentMainFunction handler) throws NullPointerException {
        handleIntentMenuItem(ctx, intent, handler, LocusConst.INTENT_ITEM_SEARCH_LIST);
    }

	private static void handleIntentMenuItem(Context ctx, Intent intent,
			OnIntentMainFunction handler, String item) 
			throws NullPointerException {
		// check source data
		if (intent == null) {
			throw new NullPointerException("Intent cannot be null");
		}
		if (handler == null) {
			throw new NullPointerException("Handler cannot be null");
		}
		// check intent itself
		if (!isRequiredAction(intent, item)) {
			handler.onFailed();
			return;
		}
		
		handler.onReceived(
				createLocusVersion(ctx, intent),
				getLocationFromIntent(intent, LocusConst.INTENT_EXTRA_LOCATION_GPS),
				getLocationFromIntent(intent, LocusConst.INTENT_EXTRA_LOCATION_MAP_CENTER));
	}
	
	public interface OnIntentMainFunction {
		/**
		 * When intent really contain location, result is returned by this function
		 * @param lv version of Locus
		 * @param locGps if gpsEnabled is true, variable contain location, otherwise <code>null</code>
		 * @param locMapCenter contain current map center location
		 */
		public void onReceived(LocusVersion lv, Location locGps, Location locMapCenter);
		public void onFailed();
	}
	

	public static boolean isIntentPointsScreenTools(Intent intent) {
		return isRequiredAction(intent, LocusConst.INTENT_ITEM_POINTS_SCREEN_TOOLS);
	}

	public static long[] handleIntentPointsScreenTools(Intent intent) {
		long[] waypointIds = null;
		if (intent.hasExtra(LocusConst.INTENT_EXTRA_ITEMS_ID)) {
			waypointIds = intent.getLongArrayExtra(LocusConst.INTENT_EXTRA_ITEMS_ID);
		}
		return waypointIds;
	}
	
	/*
	   Pick location from Locus
	  -------------------------------
	   - this feature can be used to obtain location from Locus, from same dialog (locus usually pick location). 
	   Because GetLocation dialog, used in Locus need to have already initialized whole core of Locus, this dialog
	   cannot be called directly, but needs to be started from Main map screen. This screen have anyway flag
	   android:launchMode="singleTask", so there is no possibility to use startActivityForResult in this way.
	   
	   Be careful with this function, because Locus will after "pick location" action, call new intent with 
	   ON_LOCATION_RECEIVE action, which will start your activity again without "singleTask" or similar flag
	   
	   Current functionality can be created by
	   
	   1. register intent-filter for your activity
	   
		<intent-filter>
			<action android:name="locus.api.android.ACTION_RECEIVE_LOCATION" />
			<category android:name="android.intent.category.DEFAULT" />
		</intent-filter>
 
		2. register intent receiver in your application
		
		check sample application, where this functionality is implemented

	 */
	
	public static boolean isIntentReceiveLocation(Intent intent) {
		return isRequiredAction(intent, LocusConst.ACTION_RECEIVE_LOCATION);
	}
	
	/**************************************************/
	/*              SOME HANDY FUNCTIONS              */
	/**************************************************/

    /**
     * Check if received intent contains required action.
     * @param intent received intent
     * @param action action that we expect
     * @return <code>true</code> if intent is valid and contains required action
     */
	private static boolean isRequiredAction(Intent intent, String action) {
		return intent != null &&
                intent.getAction() != null &&
				intent.getAction().equals(action);
	}
	
	public static Intent prepareResultExtraOnDisplayIntent(Waypoint wpt, boolean overridePoint) {
		Intent intent = new Intent();
		addWaypointToIntent(intent, wpt);
		intent.putExtra(LocusConst.INTENT_EXTRA_POINT_OVERWRITE, overridePoint);
		return intent;
	}
	
	public static void addWaypointToIntent(Intent intent, Waypoint wpt) {
		intent.putExtra(LocusConst.INTENT_EXTRA_POINT, wpt.getAsBytes());
	}
	
	public static Waypoint getWaypointFromIntent(Intent intent) {
		try {
			return new Waypoint(intent.getByteArrayExtra(LocusConst.INTENT_EXTRA_POINT));
		} catch (Exception e) {
			Logger.logE(TAG, "getWaypointFromIntent(" + intent + ")", e);
			return null;
		}
	}
	
	public static Location getLocationFromIntent(Intent intent, String intentExtra) {
		try {
			// check if intent has required extra parameter
			if (!intent.hasExtra(intentExtra)) {
				return null;
			}
			
			// convert data to valid Location object
			return new Location(intent.getByteArrayExtra(intentExtra));
		} catch (Exception e) {
			Logger.logE(TAG, "getLocationFromIntent(" + intent + ")", e);
			return null;
		}
	}
	
	/**************************************************/
	// LOCATION CONVERSION
	/**************************************************/
	
	/**
	 * Convert a Location object from Android to Locus format
	 * @param oldLoc location in Android object
	 * @return new Locus object
	 */
	public static Location convertToL(android.location.Location oldLoc) {
		Location loc = new Location(oldLoc.getProvider());
		loc.setLongitude(oldLoc.getLongitude());
		loc.setLatitude(oldLoc.getLatitude());
		loc.setTime(oldLoc.getTime());
		if (oldLoc.hasAccuracy()) {
			loc.setAccuracy(oldLoc.getAccuracy());
		}
		if (oldLoc.hasAltitude()) {
			loc.setAltitude(oldLoc.getAltitude());
		}
		if (oldLoc.hasBearing()) {
			loc.setBearing(oldLoc.getBearing());
		}
		if (oldLoc.hasSpeed()) {
			loc.setSpeed(oldLoc.getSpeed());
		}
		return loc;
	}
	
	/**
	 * Convert a Location object from Locus to Android format
	 * @param oldLoc location in Locus object
	 * @return converted location to Android object
	 */
	public static android.location.Location convertToA(Location oldLoc) {
		android.location.Location loc = new android.location.Location(oldLoc.getProvider());
		loc.setLongitude(oldLoc.getLongitude());
		loc.setLatitude(oldLoc.getLatitude());
		loc.setTime(oldLoc.getTime());
		if (oldLoc.hasAccuracy()) {
			loc.setAccuracy(oldLoc.getAccuracy());
		}
		if (oldLoc.hasAltitude()) {
			loc.setAltitude(oldLoc.getAltitude());
		}
		if (oldLoc.hasBearing()) {
			loc.setBearing(oldLoc.getBearing());
		}
		if (oldLoc.hasSpeed()) { 
			loc.setSpeed(oldLoc.getSpeed());
		}
		return loc;
	}
	
	/**************************************************/
	// VARIOUS TOOLS
	/**************************************************/
	
	/**
	 * General check if 'any' app defined by it's package name is available in system.
	 * @param ctx current context
	 * @param packageName package name of tested app
	 * @param version required application version
	 * @return <code>true</code> if required application is available
	 */
	public static boolean isAppAvailable(Context ctx, String packageName, int version) {
		try {
			PackageInfo info = ctx.getPackageManager().getPackageInfo(packageName, 0);
            return info != null && info.versionCode >= version;
        } catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}
}
