package com.fy.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import u.aly.db;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fy.activity.MainActivity;
import com.fy.activity.MsgActivity;
import com.fy.tool.DownDBHelper;
import com.fy.tool.EmergencyDBHelper;
import com.fy.tool.FullscreenDBHelper;
import com.fy.tool.MultiDownLoadUtil;
import com.fy.tool.MyTools;
import com.fy.tool.MultiDownLoadUtil.DownloadStateListener;
import com.fy.tool.ParseJson;
import com.fy.wo.R;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

public class MsgService extends Service {

	// private Context context;
	private int progress = 0;
	private Message msg;
	private String mac;
	private SharedPreferences sp;

	private int settime = 60;// 设置初始时间
	private int timetoget = 300;// 获取时间
	private MultiDownLoadUtil downvideo;

	@Override  
	public int onStartCommand(Intent intent, int flags, int startId) {  
	    flags = START_STICKY;  
	    return super.onStartCommand(intent, flags, startId);  
	}  
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		// context = MyApplication.getInstance().getContext();
		sp = getSharedPreferences("SP", MODE_PRIVATE);
		timetoget = Integer.parseInt(sp.getString("DataTime", "5")) * 60;
		mac = sp.getString("mac", "null mac");
		msg = handler.obtainMessage();
		msg.what = 1;
		if (!handler.hasMessages(1)) {
			handler.sendMessage(msg);
			System.out.println("任务获取计时开始");
		}
		super.onStart(intent, startId);
	}

	public void onCreate() {
		super.onCreate();
	}

	private void gethttp(String url) {
		// TODO Auto-generated method stub

		System.out.println(url);
		RequestQueue queue = Volley.newRequestQueue(this);
		StringRequest request = new StringRequest(url, new Listener<String>() {
			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				System.out.println(response);
				ParseJson parseJson = new ParseJson();
				try {
					parseJson.parseMsgdata(response, getApplicationContext());
					handler.sendEmptyMessage(3);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "网络异常，获取服务端数据失败，请返回重试",
						Toast.LENGTH_SHORT).show();
			}
		});
		queue.add(request);
		handler.sendEmptyMessage(1);

	}

	public void onDestroy() {
		super.onDestroy();
		// 结束服务时需要结束的线程等；

	}

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {

			case 1:
				progress++;
				if (progress < settime) {
					if (progress % 20 == 0) {
						System.out.println("progress==" + progress);
					}
					Message msg2 = handler.obtainMessage();
					msg2.what = 1;
					handler.sendMessageDelayed(msg2, 1000);

				} else {
					// 重置时间
					settime = timetoget;
					progress = 0;
					System.out.println("计时完成，获取数据并录入");
					// 数据解析及录入
					gethttp(sp.getString("IPaddress",
							"http://192.168.20.160:8000")
							+ getString(R.string.url_msg) + "mac=" + mac);
					// 执行紧急跳转操作
					EmergencyDBHelper emergencyDBHelper = new EmergencyDBHelper(
							getApplicationContext());

					Cursor cursor = emergencyDBHelper.select(null, null, null);
					if (cursor.moveToNext()) {
						if (sp.getBoolean("jinji", false)) {

							ActivityManager manager = (ActivityManager) getApplicationContext()
									.getSystemService(Context.ACTIVITY_SERVICE);
							List<RunningTaskInfo> task_info = manager
									.getRunningTasks(20);

							String className = "com.fy.activity.MsgActivity";

							if (className.equals(task_info.get(0).topActivity
									.getClassName())) {
								System.out.println("紧急跳转：在MsgActivity上");
								System.out.println("全屏切换紧急");
								Intent jinji = new Intent();
								jinji.setAction("com.fy.jinji");
								sendBroadcast(jinji);

							} else {
								System.out.println("紧急跳转：不在MsgActivity上");
								System.out.println("紧急跳转开始");
								System.out.println(task_info.get(0).topActivity
										.getClassName());

								Bundle bundle = new Bundle();
								bundle.putBoolean("jinjitest", true);
								Intent intent = new Intent();
								intent.putExtras(bundle);
								intent.setClass(getApplicationContext(),
										MsgActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							}

						}

					} else {
						System.out.println("紧急任务为空");
						Editor a = sp.edit();
						if (sp.getBoolean("msgjinji", false)) {
							a.putBoolean("msgjinji", false);
							a.putBoolean("jinji", true);
							a.commit();
							Intent intent = new Intent();
							intent.setClass(getApplicationContext(),
									MainActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						} else {
							a.putBoolean("msgjinji", false);
							a.putBoolean("jinji", true);
							a.commit();
						}
					}
					cursor.close();

				}
				break;
			case 3:
				FullscreenDBHelper fulldbhelper = new FullscreenDBHelper(
						getApplicationContext());
				DownDBHelper downDBHelper = new DownDBHelper(
						getApplicationContext());
				Cursor cursor = fulldbhelper.select(null,
						"model = ? and areaid = ? and dlpath=?", new String[] {
								"2", "0", "false" });//数据库表的所有下载链接

				List<String> urls = new ArrayList<String>();// 下载链接集合
				List<String> name = new ArrayList<String>();// 新的视频名字list
				List<String> kname = new ArrayList<String>();// 旧的视频名字list
				
				//获取DOWN表里已有的名字
				Cursor getnameC = downDBHelper.select(null, null, null);
				//将下载过的名字存进Kname
				while (getnameC.moveToNext()) {
					kname.add(getnameC.getString(0));
				}
				getnameC.close();
				while (cursor.moveToNext()) {
					// 下载链接集合
					String geturl = cursor.getString(cursor
							.getColumnIndex("src"));
					String downName = url2Name(geturl);

					Cursor dbcursor = downDBHelper.select(null,
							"down_name = ?", new String[] { downName });// 查找当前名字的数据是否存在
					if (dbcursor.moveToNext()) {
						// 存在相同名字 不操作跳过
					} else {
						// 存在不同名字 加入下载列表urls
						urls.add(geturl);
					}
					name.add(downName);
					dbcursor.close();
				}
				cursor.close();

				String downloadPath = "";
				if (MyTools.getExtSDCardPaths().size() > 0) {
					downloadPath = MyTools.getExtSDCardPaths().get(0)
							+ "/Android/data/com.fy.wo/msgdownload/";
				} else {
					downloadPath = Environment.getExternalStorageDirectory()
							.getAbsolutePath()
							+ "/Android/data/com.fy.wo/msgdownload/";
				}
				// 删除多余文件
				for (int i = 0; i < kname.size(); i++) {
					if (name.contains(kname.get(i))) {
						// 存在新表里
					} else {
						// 没有存在新表里 删除此文件
//						FileDelete(downloadPath + kname.get(i));
					}

				}

				// 文件下载keep表
				downDBHelper.delete();// 清空DOWN表再重新加入
				for (int i = 0; i < name.size(); i++) {
					downDBHelper.insert(name.get(i));
				}

				downvideo = new MultiDownLoadUtil(downloadPath, urls,
						getApplicationContext());
				downvideo.startDownload();

				break;

			}

		};
	};

	public void FileDelete(String sPath) {
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
		}
	}

	private String url2Name(String urlString) {
		// String[] split = urlString.split("/");
		String[] ssss;
		ssss = urlString.split("/");
		String name = ssss[ssss.length - 1];

		return name;
	}

	
}
