package com.oushangfeng.lsj.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static android.R.attr.path;
import static com.tencent.open.utils.Global.getPackageName;

public class Utils {


	/**
	 * 解析url key-value
	 *
	 * @param url
	 * @return
	 */
	public static HashMap<String, String> getUrlParams(String url) {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			String parm = url.substring(url.indexOf("?") + 1);
			String[] arr = parm.split("&");
			for (String string : arr) {
				String[] kv = string.split("=");
				if (kv.length == 1) {
					map.put(kv[0], "");
				} else {
					map.put(kv[0], kv[1]);
				}
			}
		} catch (Exception e) {
		}
		return map;
		// return "data=" + mapToJson(map);
	}

	public static boolean saveImageToGallery(Context context, Bitmap bmp) {
		boolean result = false;

		try {
			// 其次把文件插入到系统图库
			String imageUrl = MediaStore.Images.Media.insertImage(context.getContentResolver(),bmp,"","");
			// 最后通知图库更新
			context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(imageUrl)));
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}



	public static boolean isIntentAvailable(Context context, Intent intent) {
		final PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	public static boolean isEmpty(String str) {
		if (str == null || str.trim().equals("") || str.trim().equals("null")) {
			return true;
		}
		return false;
	}

	/**
	 *
	 * @param bitmap
	 *            原图
	 * @param edgeLength
	 *            希望得到的正方形部分的边长
	 * @return 缩放截取正中部分后的位图。
	 */
	public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
		if (null == bitmap || edgeLength <= 0) {
			return null;
		}

		Bitmap result = bitmap;
		int widthOrg = bitmap.getWidth();
		int heightOrg = bitmap.getHeight();

		if (widthOrg > edgeLength && heightOrg > edgeLength) {
			// 压缩到一个最小长度是edgeLength的bitmap
			int longerEdge = (int) (edgeLength * Math.max(widthOrg, heightOrg) / Math
					.min(widthOrg, heightOrg));
			int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
			int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
			Bitmap scaledBitmap;

			try {
				scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth,
						scaledHeight, true);
			} catch (Exception e) {
				return null;
			}

			// 从图中截取正中间的正方形部分。
			int xTopLeft = (scaledWidth - edgeLength) / 2;
			int yTopLeft = (scaledHeight - edgeLength) / 2;

			try {
				result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft,
						edgeLength, edgeLength);
				scaledBitmap.recycle();
			} catch (Exception e) {
				return null;
			}
		}

		return result;
	}
    public static HashMap<String, String>getDeviceInfo(Context context) {
//        String params = "";
//        String deviceId = getDevId(context);
//        String screenDensity = "";
//        String screenLayoutSize = "";
//        String screenWidth = "";
//        String screenHeight = "";
//        try {
//            DisplayMetrics metrics = new DisplayMetrics();
//            WindowManager windowManager = (WindowManager) context
//                    .getSystemService(Context.WINDOW_SERVICE);
//            windowManager.getDefaultDisplay().getMetrics(metrics);
//            Configuration configuration = context.getResources()
//                    .getConfiguration();
//            screenDensity = "" + metrics.densityDpi;
//            screenLayoutSize = ""
//                    + (configuration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
//            screenWidth = "" + metrics.widthPixels;
//            screenHeight = "" + metrics.heightPixels;
//            params = MainConstants.DEVICE_ID + "=" + deviceId + "&";
//            params += MainConstants.IMSI_ID + "="
//                    + getSubscriberImsiId(context) + "&";
//            params += MainConstants.DEVICE_NAME + "="
//                    + URLEncoder.encode(Build.MODEL) + "&";
//            params += MainConstants.OS_TYPE + "=android&";
//            params += MainConstants.OS_VERSION
//                    + "="
//                    + URLEncoder
//                    .encode(Build.VERSION.RELEASE) + "&";
//            params += MainConstants.COUNTRY_CODE + "="
//                    + Locale.getDefault().getCountry() + "&";
//            params += MainConstants.LANGUAGE + "="
//                    + Locale.getDefault().getLanguage() + "&";
//            params += MainConstants.CURRENT_PACKAGE_NAME + "="
//                    + context.getPackageName() + "&";
//            params += MainConstants.APP_VERSION + "="
//                    + getAppVerInfo(context, 1) + "&";
//            params += MainConstants.CURRENT_VERSION_CODE + "="
//                    + getAppVerInfo(context, 2) + "&";
//            params += MainConstants.SCREEN_DENSITY + "=" + screenDensity + "&";
//            params += MainConstants.SCREEN_LAYOUT_SIZE + "=" + screenLayoutSize;
//            params += "&";
//            params += MainConstants.SCREEN_WIDTH + "=" + screenWidth;
//            params += "&";
//            params += MainConstants.SCREEN_HEIGHT + "=" + screenHeight;
//            params += "&";
//            params += MainConstants.SERIAL_NUMBER + "=" + getSerialNumber(context);
//            params += "&";
//            params += MainConstants.ANDROID_ID + "=" + getAndroidId(context);
//            params += "&" + MainConstants.NET_WORK_TYPE + "="
//                    + getNetworkInfo(context);
//            params+="&"+MainConstants.CHANNEL+"="+"需要配合打包脚本";
//            params+="&"+MainConstants.UUID+"="+"邀请码，需要配合打包脚本";
//			params+="&"+MainConstants.DEVICE_FIRM+"="+ Build.MANUFACTURER;
//
//
//
//        } catch (Exception ex) {
//
//        }

		HashMap<String,String> data = new HashMap<>();
		data.put(MainConstants.DEVICE_ID,Utils.getDevId(context));
		data.put(MainConstants.IMSI_ID,getSubscriberImsiId(context)+"");
		data.put(MainConstants.CURRENT_VERSION_CODE,getAppVerInfo(context, 2));
		data.put(MainConstants.APP_VERSION,getAppVerInfo(context, 1));
		data.put(MainConstants.OS_TYPE,"android");
		data.put(MainConstants.OS_VERSION,Build.VERSION.RELEASE);
		data.put(MainConstants.DEVICE_FIRM,Build.MANUFACTURER);
		data.put(MainConstants.CHANNEL,getChannel(context));
		data.put(MainConstants.UUID,getInviteID(context));
		return data;
    }

    /**
     * 獲取androidid
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        String androidId = "";
        try {
            androidId = android.provider.Settings.Secure.getString(
                    context.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
        } catch (Exception ex) {
        }
        return androidId;
    }

    /**
     * 獲取手機的serialnumber
     *
     * @param context
     * @return
     */
    public static String getSerialNumber(Context context) {
        String tmSerial = "";
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getApplicationContext().getSystemService(
                            Context.TELEPHONY_SERVICE);
            tmSerial = "" + tm.getSimSerialNumber();
        } catch (Exception ex) {
        }
        return tmSerial;
    }

    public static HashMap<String, String> getGetKeyValue(String getUrlString) {
        String[] arr = getUrlString.split("&");
        HashMap<String, String> map = new HashMap<String, String>();
        for (String string : arr) {
            String[] kv = string.split("=");
            if (kv.length == 1) {
                map.put(kv[0], "");
            } else {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
        // return "data=" + mapToJson(map);
    }


    public static String mapToJson(Map<String, String> map) {
        if (map.isEmpty())
            return "{}";
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        Set<String> keys = map.keySet();
        for (String key : keys) {
            Object value = map.get(key);
            sb.append('\"');
            sb.append(key);
            sb.append('\"');
            sb.append(':');
            sb.append('\"');
            sb.append(value);
            sb.append('\"');
            sb.append(',');
        }
        sb.setCharAt(sb.length() - 1, '}');
        String data = "";
        try {
            data = URLEncoder.encode(sb.toString(), "UTF-8");
        } catch (Exception e) {

        }
        return "data=" + data;
    }

    public static String getAppVerInfo(Context context, int type) {
        String appVersion = "";
        try {
            // Get the package info
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            if (type == 1) {
                appVersion = pi.versionName;
            } else if (type == 2) {
                appVersion = pi.versionCode + "";
            }
        } catch (Exception e) {
            return appVersion;
        }
        return appVersion;
    }

    public static String getSubscriberImsiId(Context context) {
        String subscriberId = null;
        if (context
                .checkCallingOrSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                subscriberId = tm.getSubscriberId();
            }
            if (subscriberId == null) {
                subscriberId = "";
            }
        }
        return subscriberId;
    }

    public static String getDevId(Context context) {
        String deviceID = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            if (telephonyManager != null) {
                deviceID = telephonyManager.getDeviceId();
            }

            boolean invalidDeviceID = false;
            if (deviceID == null) {
                invalidDeviceID = true;
            } else if (deviceID.length() == 0
                    || deviceID.equals("000000000000000")
                    || deviceID.equals("0")) {
                invalidDeviceID = true;
            } else {
                deviceID = deviceID.toLowerCase();
            }

            if (invalidDeviceID) {
                deviceID = MainConstants.EMULATOR_ID;
            }
        } catch (Exception e) {
            deviceID = MainConstants.EMULATOR_ID;
        }
        return deviceID;
    }

	public synchronized static void setPreferenceStr(Context context,
													 String name, String value) {
		int mode = 0;
		if (android.os.Build.VERSION.SDK_INT > 11) {
			// 4.0以上
			mode = Context.MODE_MULTI_PROCESS;
		}
		SharedPreferences preferences = context.getSharedPreferences(
				"preferences", mode);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(name, value);
		editor.commit();
	}

	public static String MD5(String src) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			StringBuffer deviceIDString = new StringBuffer(src);
			src = convertToHex(md.digest(deviceIDString.toString().getBytes()));
		} catch (Exception e) {
			src = "00000000000000000000000000000000";
		}
		return src;
	}

	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	public synchronized static String getPreferenceStr(Context context,
													   String name, String defValue) {
		int mode = 0;
		if (android.os.Build.VERSION.SDK_INT > 11) {
			// 4.0以上
			mode = Context.MODE_MULTI_PROCESS;
		}
		SharedPreferences preferences = context.getSharedPreferences(
				"preferences", mode);
		String result = preferences.getString(name, defValue);
		return result;
	}

    static String getNetworkType(Context context) {
        if (context
                .checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }

        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager == null) {
            return "";
        }
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();
        if (netInfo == null) {
            return "";
        }
        return netInfo.getTypeName();
    }

    public static String getNetworkInfo(Context context) {
        ConnectivityManager localConnectivityManager = (ConnectivityManager) context
                .getSystemService("connectivity");
        NetworkInfo localNetworkInfo = localConnectivityManager
                .getActiveNetworkInfo();
        if (localNetworkInfo == null)
            return getNetworkType(context);
        if (localNetworkInfo.getType() == 1)
            return getNetworkType(context);
        String str = getNetworkType(context) + ","
                + localNetworkInfo.getExtraInfo() + ","
                + localNetworkInfo.getSubtypeName() + ","
                + localNetworkInfo.getSubtype();
        return URLEncoder.encode(str);
    }


    public static boolean getAppInfo(Context context, String packagename) {
        boolean temp = true;
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(
                    packagename, 0);
        } catch (NameNotFoundException e) {
        }
        if (packageInfo != null) {
            temp = false;
        }
        return temp;
    }


    // 由于targetSdkVersion低于17，只能通过反射获取
    public static String getUserId(Context context) {
        Object userManager = context.getSystemService("user");
        if (userManager == null) {
            return "";
        }
        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod(
                    "myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(
                    android.os.Process.class, (Object[]) null);

            Method getSerialNumberForUser = userManager.getClass().getMethod(
                    "getSerialNumberForUser", myUserHandle.getClass());
            long userSerial = (Long) getSerialNumberForUser.invoke(userManager,
                    myUserHandle);
            // Log.i("tag", String.valueOf(userSerial));
            return String.valueOf(userSerial);
        } catch (NoSuchMethodException e) {

        } catch (IllegalArgumentException e) {

        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException e) {

        }

        return "";
    }

	/* 函数段end */

    /**
     * 获取bssid
     */
    public static String getBSSID(Context context) {
        WifiManager wm = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo winfo = wm.getConnectionInfo();
        if (winfo != null) {
            return winfo.getBSSID();
        }
        return "";
    }

    /**
     * 获取ssid
     */
    public static String getSSID(Context context) {
        WifiManager wm = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo winfo = wm.getConnectionInfo();
        if (winfo != null) {
            return URLEncoder.encode(winfo.getSSID().replace("\"", ""));
        }
        return "";
    }

    /**
     * 获取目录下所有文件(按时间排序) 如果图片数量大于10的话 则删除时间最早的
     *
     * @param path
     * @return
     */
    public static void getFileSort(File[] files) {
        List<File> list = new ArrayList<File>();
        for (File subFile : files) {
            if (!subFile.isDirectory()) {
                list.add(subFile);
            }
        }
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if (file.lastModified() < newFile.lastModified()) {
                        return -1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            });
        }
        if (list.size() > 10) {
            for (int i = 0; i < list.size() - 10; i++) {
                list.get(i).delete();
            }
        }
    }

    public static void openApp(Context context, String packageName) {
        PackageManager localPackageManager = context.getPackageManager();
        Intent localIntent = localPackageManager
                .getLaunchIntentForPackage(packageName);
        if (localIntent != null) {
            context.startActivity(localIntent);
        }
    }

    /**
     * 获取mac地址
     *
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
        String mac = "";
        try {
            if (Build.VERSION.SDK_INT > 22) {
                //6.0以上
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface iF = interfaces.nextElement();
                    byte[] addr = iF.getHardwareAddress();
                    if (addr == null || addr.length == 0) {
                        continue;
                    }
                    StringBuilder buf = new StringBuilder();
                    for (byte b : addr) {
                        buf.append(String.format("%02X:", b));
                    }
                    if (buf.length() > 0) {
                        buf.deleteCharAt(buf.length() - 1);
                    }
                    if (iF.getName().equals("wlan0")) {
                        mac = buf.toString();
                    }
                }

            } else {
                // 获取wifi管理器
                WifiManager wifiMng = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiMng.getConnectionInfo();
                mac = wifiInfo.getMacAddress();
            }
        } catch (Exception ex) {
        }
        return mac;
    }

    public static String getCarrier(Context mContext) {
        //0移动   1联通   2电信
        String carrier = "-1";
        try {
            TelephonyManager telManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            String operator = telManager.getSimOperator();
            if (operator != null) {
                if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {
                    carrier = "0";
                } else if (operator.equals("46001")) {
                    carrier = "1";
                } else if (operator.equals("46003")) {
                    carrier = "2";
                }
            }
        } catch (Exception ex) {
        }
        return carrier;
    }


    public static Bitmap mergeBitmap(Context activity, Bitmap firstBitmap, Bitmap secondBitmap, String title) {
        Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth(), firstBitmap.getHeight(),
                Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(secondBitmap, 56, 284, null);
        canvas.drawBitmap(firstBitmap, new Matrix(), null);
        Paint mPaint = new Paint();
        mPaint.setStrokeWidth(3);
        mPaint.setTextSize(50);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.LEFT);
        Rect bounds = new Rect();
        mPaint.getTextBounds(title, 0, title.length(), bounds);
        canvas.drawText(title, 360 - bounds.width() / 2, 900, mPaint);
        return bitmap;
    }

    //使用Bitmap加Matrix来缩放
    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    public static Bitmap getImageFromAssetsFile(Context activity, String fileName) {
        Bitmap image = null;
        AssetManager am = activity.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
        }
        return image;
    }

	private static String getStringFromZip(Context context) {
		String channel = "";
		String start_flag = "META-INF/dchannel";
		ApplicationInfo appinfo = context.getApplicationInfo();
		String sourceDir = appinfo.sourceDir;
		ZipFile zipfile = null;
		try {
			zipfile = new ZipFile(sourceDir);
			Enumeration<?> entries = zipfile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = ((ZipEntry) entries.nextElement());
				String entryName = entry.getName();
				if (entryName.contains(start_flag)) {
//					channel = entryName.replace(start_flag, "");
					String strs[] = entryName.split("@");
					if(strs != null && strs.length >=2){
						channel = strs[1];
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (zipfile != null) {
				try {
					zipfile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return channel;
	}


	public static boolean isNewVersionApk(Context context) {
		String filePath = context.getExternalFilesDir(null)+ File.separator+context.getPackageName()+".apk";
			final File file = new File(filePath);
			if (file.exists()) {
				PackageManager pm = context.getPackageManager();
				PackageInfo pInfo = pm.getPackageArchiveInfo(filePath,
						PackageManager.GET_ACTIVITIES);
				if (pInfo != null) {
					int currentVersionCode = Integer.parseInt(Utils.getAppVerInfo(context,2));
					if (pInfo.versionCode > currentVersionCode) {
						return true;
					}
				}
			}
		return false;
	}

	public static void installApkFile(Context mContext, File file) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}

	public static String getInviteID(Context context) {
		String invite = "00000000";
		String start_flag = "META-INF/dchannel";
		ApplicationInfo appinfo = context.getApplicationInfo();
		String sourceDir = appinfo.sourceDir;
		ZipFile zipfile = null;
		try {
			zipfile = new ZipFile(sourceDir);
			Enumeration<?> entries = zipfile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = ((ZipEntry) entries.nextElement());
				String entryName = entry.getName();
				if (entryName.contains(start_flag)) {
					String strs[] = entryName.split("@");
					if(strs != null && strs.length ==3){
						invite = strs[2];
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (zipfile != null) {
				try {
					zipfile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return invite;
	}


	public static String getChannel(Context context){
		String channel = "default";
		String start_flag = "META-INF/dchannel";
		ApplicationInfo appinfo = context.getApplicationInfo();
		String sourceDir = appinfo.sourceDir;
		ZipFile zipfile = null;
		try {
			zipfile = new ZipFile(sourceDir);
			Enumeration<?> entries = zipfile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = ((ZipEntry) entries.nextElement());
				String entryName = entry.getName();
				if (entryName.contains(start_flag)) {
//					channel = entryName.replace(start_flag, "");
					String strs[] = entryName.split("@");
					if(strs != null && strs.length >=2){
						channel = strs[1];
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (zipfile != null) {
				try {
					zipfile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return channel;
	}



}
