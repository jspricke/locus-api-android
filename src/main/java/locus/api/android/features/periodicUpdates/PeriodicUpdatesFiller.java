package locus.api.android.features.periodicUpdates;

import locus.api.android.utils.LocusUtils;
import android.content.Intent;

public class PeriodicUpdatesFiller {

	public static UpdateContainer intentToUpdate(Intent i, PeriodicUpdatesHandler pu) {
		// prepare data container
		UpdateContainer update = new UpdateContainer();
		
		// LOCATION, GPS, BASIC VALUES
		
		// check current GPS/network location
		update.enabledMyLocation = i.getBooleanExtra(
				PeriodicUpdatesConst.VAR_B_MY_LOCATION_ON, false);
		update.newMyLocation = false;
		update.locMyLocation = LocusUtils.getLocationFromIntent(
				i, PeriodicUpdatesConst.VAR_LOC_MY_LOCATION);
		if (update.enabledMyLocation) {
			// check if location is updated
			if (pu.mLastGps == null || pu.mLastGps.distanceTo(
					update.locMyLocation) > pu.mLocMinDistance) {
				pu.mLastGps = update.locMyLocation;
				update.newMyLocation = true;
			} 
		}
		
		// get basic variables
		update.gpsSatsUsed = i.getIntExtra(
				PeriodicUpdatesConst.VAR_I_GPS_SATS_USED, 0);
		update.gpsSatsAll = i.getIntExtra(
				PeriodicUpdatesConst.VAR_I_GPS_SATS_ALL, 0);
		update.declination = i.getFloatExtra(
				PeriodicUpdatesConst.VAR_F_DECLINATION, 0.0f);
		update.speedVertical = i.getFloatExtra(
				PeriodicUpdatesConst.VAR_F_SPEED_VERTICAL, 0.0f);
		update.slope = i.getFloatExtra(
				PeriodicUpdatesConst.VAR_F_SLOPE, 0.0f);

		update.orientGpsAngle = i.getFloatExtra(
				PeriodicUpdatesConst.VAR_F_ORIENT_GPS_ANGLE, 0.0f);
				
		update.orientHeading = i.getFloatExtra(
				PeriodicUpdatesConst.VAR_F_ORIENT_HEADING, 0.0f);
		update.orientHeadingOpposit = i.getFloatExtra(
				PeriodicUpdatesConst.VAR_F_ORIENT_HEADING_OPPOSIT, 0.0f);
        update.orientCourse = i.getFloatExtra(
                PeriodicUpdatesConst.VAR_F_ORIENT_COURSE, 0.0f);

        update.orientPitch = i.getFloatExtra(
				PeriodicUpdatesConst.VAR_F_ORIENT_PITCH, 0.0f);
		update.orientRoll = i.getFloatExtra(
				PeriodicUpdatesConst.VAR_F_ORIENT_ROLL, 0.0f);
				

		// MAP STUFF

		update.mapVisible = i.getBooleanExtra(
				PeriodicUpdatesConst.VAR_B_MAP_VISIBLE, false);
		update.newMapCenter = false;
		update.locMapCenter = LocusUtils.getLocationFromIntent(
				i, PeriodicUpdatesConst.VAR_LOC_MAP_CENTER);
		if (pu.mLastMapCenter == null || pu.mLastMapCenter.distanceTo(
				update.locMapCenter) > pu.mLocMinDistance) {
			pu.mLastMapCenter = update.locMapCenter;
			update.newMapCenter = true;
		}

		// check MAP
		update.mapTopLeft = LocusUtils.getLocationFromIntent(
				i, PeriodicUpdatesConst.VAR_LOC_MAP_BBOX_TOP_LEFT);
		update.mapBottomRight = LocusUtils.getLocationFromIntent(
				i, PeriodicUpdatesConst.VAR_LOC_MAP_BBOX_BOTTOM_RIGHT);
		update.mapZoomLevel = i.getIntExtra(
				PeriodicUpdatesConst.VAR_I_MAP_ZOOM_LEVEL, 0);
		update.newZoomLevel = update.mapZoomLevel != pu.mLastZoomLevel;
		pu.mLastZoomLevel = update.mapZoomLevel;
		update.mapRotate = i.getFloatExtra(
				PeriodicUpdatesConst.VAR_F_MAP_ROTATE, 0.0f);
		update.isUserTouching = i.getBooleanExtra(
				PeriodicUpdatesConst.VAR_B_MAP_USER_TOUCHES, false);
		
		// TRACK RECORDING PART
		
		update.trackRecRecording = i.getBooleanExtra(
				PeriodicUpdatesConst.VAR_B_REC_RECORDING, false);
		if (update.trackRecRecording) {
            update.trackRecPaused = i.getBooleanExtra(
                PeriodicUpdatesConst.VAR_B_REC_PAUSED, false);
            update.trackRecProfileName = i.getStringExtra(
                    PeriodicUpdatesConst.VAR_S_REC_PROFILE_NAME);
			update.trackRecDist = i.getDoubleExtra(
					PeriodicUpdatesConst.VAR_D_REC_DIST, 0.0);
			update.trackRecDistDownhill = i.getDoubleExtra(
					PeriodicUpdatesConst.VAR_D_REC_DIST_DOWNHILL, 0.0);
			update.trackRecDistUphill = i.getDoubleExtra(
					PeriodicUpdatesConst.VAR_D_REC_DIST_UPHILL, 0.0);
			update.trackRecAltMin = i.getFloatExtra(
					PeriodicUpdatesConst.VAR_F_REC_ALT_MIN, 0.0f);
			update.trackRecAltMax = i.getFloatExtra(
					PeriodicUpdatesConst.VAR_F_REC_ALT_MAX, 0.0f);
			update.trackRecAltDownhill = i.getFloatExtra(
					PeriodicUpdatesConst.VAR_F_REC_ALT_DOWNHILL, 0.0f);
			update.trackRecAltUphill = i.getFloatExtra(
					PeriodicUpdatesConst.VAR_F_REC_ALT_UPHILL, 0.0f);
			update.trackRecAltCumulative = i.getFloatExtra(
					PeriodicUpdatesConst.VAR_F_REC_ALT_CUMULATIVE, 0.0f);
			update.trackRecTime = i.getLongExtra(
					PeriodicUpdatesConst.VAR_L_REC_TIME, 0L);
			update.trackRecTimeMove = i.getLongExtra(
					PeriodicUpdatesConst.VAR_L_REC_TIME_MOVE, 0L);
			update.trackRecSpeedAvg = i.getFloatExtra(
					PeriodicUpdatesConst.VAR_F_REC_SPEED_AVG, 0.0f);
			update.trackRecSpeedAvgMove = i.getFloatExtra(
					PeriodicUpdatesConst.VAR_F_REC_SPEED_AVG_MOVE, 0.0f);
			update.trackRecSpeedMax = i.getFloatExtra(
					PeriodicUpdatesConst.VAR_F_REC_SPEED_MAX, 0.0f);
			update.trackRecPoints = i.getIntExtra(
					PeriodicUpdatesConst.VAR_I_REC_POINTS, 0);
		}
		
		// GUIDING PART
		
        update.guideType = i.getIntExtra(
                PeriodicUpdatesConst.VAR_I_GUIDE_TYPE, UpdateContainer.GUIDE_TYPE_DISABLED);

        // load guiding if not disabled
		if (update.guideType != UpdateContainer.GUIDE_TYPE_DISABLED) {
            update.guideWptName = i.getStringExtra(
					PeriodicUpdatesConst.VAR_S_GUIDE_WPT_NAME);
			update.guideWptLoc = LocusUtils.getLocationFromIntent(
					i, PeriodicUpdatesConst.VAR_LOC_GUIDE_WPT);
			update.guideWptDist = i.getDoubleExtra(
					PeriodicUpdatesConst.VAR_D_GUIDE_WPT_DIST, 0.0);
			update.guideWptAzim = i.getFloatExtra(
					PeriodicUpdatesConst.VAR_F_GUIDE_WPT_AZIM, 0.0f);
			update.guideWptAngle = i.getFloatExtra(
					PeriodicUpdatesConst.VAR_F_GUIDE_WPT_ANGLE, 0.0f);
			update.guideWptTime = i.getLongExtra(
					PeriodicUpdatesConst.VAR_L_GUIDE_WPT_TIME, 0L);

            // TRACK PART

            update.guideDistFromStart = i.getDoubleExtra(
                    PeriodicUpdatesConst.VAR_D_GUIDE_DIST_FROM_START, 0.0);
            update.guideDistToFinish = i.getDoubleExtra(
                    PeriodicUpdatesConst.VAR_D_GUIDE_DIST_TO_FINISH, 0.0);
			update.guideTimeToFinish = i.getLongExtra(
					PeriodicUpdatesConst.VAR_L_GUIDE_TIME_TO_FINISH, 0L);

            // get first navigation point
            update.guideNavPoint1Loc = LocusUtils.getLocationFromIntent(
                    i, PeriodicUpdatesConst.VAR_LOC_GUIDE_NAV_POINT1_LOC);
            if (update.guideNavPoint1Loc != null) {
                update.guideNavPoint1Name = i.getStringExtra(
                        PeriodicUpdatesConst.VAR_S_GUIDE_NAV_POINT1_NAME);
                update.guideNavPoint1Dist = i.getDoubleExtra(
                        PeriodicUpdatesConst.VAR_D_GUIDE_NAV_POINT1_DIST, 0.0);
                update.guideNavPoint1Time = i.getLongExtra(
                        PeriodicUpdatesConst.VAR_L_GUIDE_NAV_POINT1_TIME, 0L);
                update.guideNavPoint1Action = i.getIntExtra(
                        PeriodicUpdatesConst.VAR_L_GUIDE_NAV_POINT1_ACTION,
                        update.guideNavPoint1Action);
            }

            // get second navigation point
            update.guideNavPoint2Loc = LocusUtils.getLocationFromIntent(
                    i, PeriodicUpdatesConst.VAR_LOC_GUIDE_NAV_POINT2_LOC);
            if (update.guideNavPoint2Loc != null) {
                update.guideNavPoint2Name = i.getStringExtra(
                        PeriodicUpdatesConst.VAR_S_GUIDE_NAV_POINT2_NAME);
                update.guideNavPoint2Dist = i.getDoubleExtra(
                        PeriodicUpdatesConst.VAR_D_GUIDE_NAV_POINT2_DIST, 0.0);
                update.guideNavPoint2Time = i.getLongExtra(
                        PeriodicUpdatesConst.VAR_L_GUIDE_NAV_POINT2_TIME, 0L);
                update.guideNavPoint2Action = i.getIntExtra(
                        PeriodicUpdatesConst.VAR_L_GUIDE_NAV_POINT2_ACTION,
                        update.guideNavPoint2Action);
            }
		}
		
		// VARIOUS
		
		update.deviceBatteryValue = i.getIntExtra(
				PeriodicUpdatesConst.VAR_I_DEVICE_BATTERY_VALUE, 0);
		update.deviceBatteryTemperature = i.getFloatExtra(
				PeriodicUpdatesConst.VAR_F_DEVICE_BATTERY_TEMPERATURE, 0.0f);
		
		// return filled result
		return update;
	}
	
	public static Intent updateToIntent(String action, UpdateContainer cont) {
		Intent i = new Intent(action);

		// main statistics and angles
		addValuesBasicLocation(cont, i);

		// map values
		addValuesMap(cont, i);
		
		// add track recording feature
		addValuesTrackRecording(cont, i);
		
		// guiding stuff
		addValuesGuiding(cont, i);
		
		// other various variables
		addValuesVarious(cont, i);

		// return result
		return i;
	}
	
	private static void addValuesBasicLocation(UpdateContainer cont, Intent i) {
		// is GPS enabled or not
		i.putExtra(PeriodicUpdatesConst.VAR_B_MY_LOCATION_ON,
				cont.enabledMyLocation);

		// add current user location
		i.putExtra(PeriodicUpdatesConst.VAR_LOC_MY_LOCATION,
				cont.locMyLocation.getAsBytes());
		
		// add basic variables
		i.putExtra(PeriodicUpdatesConst.VAR_I_GPS_SATS_USED,
				cont.gpsSatsUsed);
		i.putExtra(PeriodicUpdatesConst.VAR_I_GPS_SATS_ALL,
				cont.gpsSatsAll);
		i.putExtra(PeriodicUpdatesConst.VAR_F_DECLINATION,
				cont.declination);
		i.putExtra(PeriodicUpdatesConst.VAR_F_SPEED_VERTICAL,
				cont.speedVertical);
		i.putExtra(PeriodicUpdatesConst.VAR_F_SLOPE,
				cont.slope);

		i.putExtra(PeriodicUpdatesConst.VAR_F_ORIENT_GPS_ANGLE,
				cont.orientGpsAngle);

		i.putExtra(PeriodicUpdatesConst.VAR_F_ORIENT_HEADING,
				cont.orientHeading);
		i.putExtra(PeriodicUpdatesConst.VAR_F_ORIENT_HEADING_OPPOSIT,
				cont.orientHeadingOpposit);
        i.putExtra(PeriodicUpdatesConst.VAR_F_ORIENT_COURSE,
                cont.orientCourse);

        i.putExtra(PeriodicUpdatesConst.VAR_F_ORIENT_PITCH,
				cont.orientPitch);
		i.putExtra(PeriodicUpdatesConst.VAR_F_ORIENT_ROLL,
				cont.orientRoll);
	}
	
	private static void addValuesMap(UpdateContainer cont, Intent i) {
		i.putExtra(PeriodicUpdatesConst.VAR_B_MAP_VISIBLE,
				cont.mapVisible);
		i.putExtra(PeriodicUpdatesConst.VAR_LOC_MAP_CENTER,
				cont.locMapCenter.getAsBytes());

		i.putExtra(PeriodicUpdatesConst.VAR_LOC_MAP_BBOX_TOP_LEFT,
				cont.mapTopLeft.getAsBytes());
		i.putExtra(PeriodicUpdatesConst.VAR_LOC_MAP_BBOX_BOTTOM_RIGHT,
				cont.mapBottomRight.getAsBytes());
		i.putExtra(PeriodicUpdatesConst.VAR_I_MAP_ZOOM_LEVEL,
				cont.mapZoomLevel);
		i.putExtra(PeriodicUpdatesConst.VAR_F_MAP_ROTATE,
				cont.mapRotate);
		i.putExtra(PeriodicUpdatesConst.VAR_B_MAP_USER_TOUCHES,
				cont.isUserTouching);
	}

	private static void addValuesTrackRecording(UpdateContainer cont, Intent i) {
		i.putExtra(PeriodicUpdatesConst.VAR_B_REC_RECORDING,
				cont.trackRecRecording);
		i.putExtra(PeriodicUpdatesConst.VAR_B_REC_PAUSED,
				cont.trackRecPaused);

        // prepare some variables
        if (cont.trackRecRecording || cont.trackRecPaused) {
            i.putExtra(PeriodicUpdatesConst.VAR_S_REC_PROFILE_NAME,
                    cont.trackRecProfileName);
			i.putExtra(PeriodicUpdatesConst.VAR_D_REC_DIST,
					cont.trackRecDist);
			i.putExtra(PeriodicUpdatesConst.VAR_D_REC_DIST_DOWNHILL,
					cont.trackRecDistDownhill);
			i.putExtra(PeriodicUpdatesConst.VAR_D_REC_DIST_UPHILL,
					cont.trackRecDistUphill);
			i.putExtra(PeriodicUpdatesConst.VAR_F_REC_ALT_MIN,
					cont.trackRecAltMin);
			i.putExtra(PeriodicUpdatesConst.VAR_F_REC_ALT_MAX,
					cont.trackRecAltMax);
			i.putExtra(PeriodicUpdatesConst.VAR_F_REC_ALT_DOWNHILL,
					cont.trackRecAltDownhill);
			i.putExtra(PeriodicUpdatesConst.VAR_F_REC_ALT_UPHILL,
					cont.trackRecAltUphill);
			i.putExtra(PeriodicUpdatesConst.VAR_F_REC_ALT_CUMULATIVE,
					cont.trackRecAltCumulative);
			i.putExtra(PeriodicUpdatesConst.VAR_L_REC_TIME,
					cont.trackRecTime);
			i.putExtra(PeriodicUpdatesConst.VAR_L_REC_TIME_MOVE,
					cont.trackRecTimeMove);
			i.putExtra(PeriodicUpdatesConst.VAR_F_REC_SPEED_AVG,
					cont.trackRecSpeedAvg);
			i.putExtra(PeriodicUpdatesConst.VAR_F_REC_SPEED_AVG_MOVE,
					cont.trackRecSpeedAvgMove);
			i.putExtra(PeriodicUpdatesConst.VAR_F_REC_SPEED_MAX,
					cont.trackRecSpeedMax);
			i.putExtra(PeriodicUpdatesConst.VAR_I_REC_POINTS,
					cont.trackRecPoints);
		}
	}
	
	private static void addValuesGuiding(UpdateContainer cont, Intent i) {
        i.putExtra(PeriodicUpdatesConst.VAR_I_GUIDE_TYPE,
                cont.guideType);

        // continue only if guiding is enabled
		if (cont.guideType == UpdateContainer.GUIDE_TYPE_DISABLED) {
            return;
        }

        // insert basic data
        i.putExtra(PeriodicUpdatesConst.VAR_S_GUIDE_WPT_NAME,
                cont.guideWptName);
        i.putExtra(PeriodicUpdatesConst.VAR_LOC_GUIDE_WPT,
                cont.guideWptLoc.getAsBytes());
        i.putExtra(PeriodicUpdatesConst.VAR_D_GUIDE_WPT_DIST,
                cont.guideWptDist);
        i.putExtra(PeriodicUpdatesConst.VAR_F_GUIDE_WPT_AZIM,
                cont.guideWptAzim);
        i.putExtra(PeriodicUpdatesConst.VAR_F_GUIDE_WPT_ANGLE,
                cont.guideWptAngle);
        i.putExtra(PeriodicUpdatesConst.VAR_L_GUIDE_WPT_TIME,
                cont.guideWptTime);

        // TRACK PART

        i.putExtra(PeriodicUpdatesConst.VAR_D_GUIDE_DIST_FROM_START,
                cont.guideDistFromStart);
        i.putExtra(PeriodicUpdatesConst.VAR_D_GUIDE_DIST_TO_FINISH,
                cont.guideDistToFinish);
        i.putExtra(PeriodicUpdatesConst.VAR_L_GUIDE_TIME_TO_FINISH,
                cont.guideTimeToFinish);

        // first navigation waypoint
        if (cont.guideNavPoint1Loc != null) {
            i.putExtra(PeriodicUpdatesConst.VAR_LOC_GUIDE_NAV_POINT1_LOC,
                    cont.guideNavPoint1Loc.getAsBytes());
            i.putExtra(PeriodicUpdatesConst.VAR_S_GUIDE_NAV_POINT1_NAME,
                    cont.guideNavPoint1Name);
            i.putExtra(PeriodicUpdatesConst.VAR_D_GUIDE_NAV_POINT1_DIST,
                    cont.guideNavPoint1Dist);
            i.putExtra(PeriodicUpdatesConst.VAR_L_GUIDE_NAV_POINT1_TIME,
                    cont.guideNavPoint1Time);
            i.putExtra(PeriodicUpdatesConst.VAR_L_GUIDE_NAV_POINT1_ACTION,
                    cont.guideNavPoint1Action);
        }

        // second navigation waypoint
        if (cont.guideNavPoint2Loc != null) {
            i.putExtra(PeriodicUpdatesConst.VAR_LOC_GUIDE_NAV_POINT2_LOC,
                    cont.guideNavPoint2Loc.getAsBytes());
            i.putExtra(PeriodicUpdatesConst.VAR_S_GUIDE_NAV_POINT2_NAME,
                    cont.guideNavPoint2Name);
            i.putExtra(PeriodicUpdatesConst.VAR_D_GUIDE_NAV_POINT2_DIST,
                    cont.guideNavPoint2Dist);
            i.putExtra(PeriodicUpdatesConst.VAR_L_GUIDE_NAV_POINT2_TIME,
                    cont.guideNavPoint2Time);
            i.putExtra(PeriodicUpdatesConst.VAR_L_GUIDE_NAV_POINT2_ACTION,
                    cont.guideNavPoint2Action);
        }
	}
	
	private static void addValuesVarious(UpdateContainer cont, Intent i) {
		i.putExtra(PeriodicUpdatesConst.VAR_I_DEVICE_BATTERY_VALUE,
				cont.deviceBatteryValue);
		i.putExtra(PeriodicUpdatesConst.VAR_F_DEVICE_BATTERY_TEMPERATURE,
				cont.deviceBatteryTemperature);
	}
}
