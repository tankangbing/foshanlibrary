package com.fy.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.fy.application.BaseActivity;
import com.fy.tool.JavaScriptObject;
import com.fy.wo.R;

public class CultrueActivity extends BaseActivity implements OnClickListener {

	private WebView webView;
	private ProgressDialog waitdialog = null;
	private myWebChromeClient xwebchromeclient;
	private RelativeLayout remain_layout;
	private Button remain_hide;
	private Button remain_all;
	private Button remain_ret;
	private Context context;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				// finish();
				Intent intent2 = new Intent();
				intent2.setClass(CultrueActivity.this, MainActivity.class);
				intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent2);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.cultrue_activity);

		// 悬浮按钮
		remain_layout = (RelativeLayout) findViewById(R.id.remain_layout);
		remain_hide = (Button) findViewById(R.id.remain_hide);
		remain_all = (Button) findViewById(R.id.remain_all);
		remain_ret = (Button) findViewById(R.id.remain_ret);
		remain_layout.setOnClickListener(this);
		remain_hide.setOnClickListener(this);
		remain_all.setOnClickListener(this);
		remain_ret.setOnClickListener(this);

		waitdialog = new ProgressDialog(this);
		waitdialog.setTitle("提示");
		waitdialog.setMessage("页面加载中...");
		waitdialog.setIndeterminate(true);
		waitdialog.setCancelable(true);
		waitdialog.show();

		webView = (WebView) findViewById(R.id.net_webview);

		WebSettings ws = webView.getSettings();
		ws.setBuiltInZoomControls(true);// 隐藏缩放按钮

		ws.setSupportZoom(false);

		ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);// 排版适应屏幕

		ws.setUseWideViewPort(true);// 可任意比例缩放,setUseWideViewPort方法设置webview推荐使用的窗口。
		ws.setLoadWithOverviewMode(true);// setLoadWithOverviewMode方法是设置webview加载的页面的模式。

		ws.setSavePassword(true);
		ws.setSaveFormData(true);// 保存表单数据
		ws.setJavaScriptEnabled(true);// js
		ws.setDomStorageEnabled(true);
		ws.setSupportMultipleWindows(true);// 新加
		webView.addJavascriptInterface(new JavaScriptObject(context, handler),
				"myObj");
		xwebchromeclient = new myWebChromeClient();
		webView.setWebChromeClient(xwebchromeclient);
		webView.setWebViewClient(new myWebViewClient());
//		ws.setPluginState(PluginState.ON);
		// webView.loadUrl("file:///mnt/sdcard/xy/xy/index.html");
		SharedPreferences sp;
		sp = getSharedPreferences("SP", MODE_PRIVATE);
		String url = sp.getString("IPaddress", "http://192.168.20.160:8898")
				+ getString(R.string.url_cultrue);
		 webView.loadUrl(url);
//		 webView.loadUrl("http://192.168.20.160:8080/notify/fullscreen/8189be2e-1f93-1470969089744");
//		 webView.loadUrl("http://192.168.20.160:8888/notify/video/http%3A//192.168.20.160/cirmSystemFile/camInfo/video/d6a7b832-c79b-448a-9443-f165de703524_cirm_VID_20160520_155939.mp4");
//		 webView.loadUrl("http://data.iego.net:88/m3/play2.aspx?lesson_id=5332790b4fd706ec2c72a9d4&video_id=533279c64fd706ec2c72a9d7");

//		webView.loadUrl("http://192.168.20.160:8888/notify/video/http%3A//192.168.20.160/cirmSystemFile/camInfo/video/d6a7b832-c79b-448a-9443-f165de703524_cirm_VID_20160520_155939.mp4");

		webView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) { // 表示按返回键时的操作
						webView.goBack(); // 后退

						// webview.goForward();//前进
						return true; // 已处理
					}
				}
				return false;
			}
		});
	}

	public class myWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return false;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			waitdialog.dismiss();
			view.loadUrl("javascript:(function() { var videos = document.getElementsByTagName('video'); for(var i=0;i<videos.length;i++){videos[i].play();}})()");
//			view.performClick();
		}
	}

	public class myWebChromeClient extends WebChromeClient {
		private View xprogressvideo;

//		// 播放网络视频时全屏会被调用的方法
//		@Override
//		public void onShowCustomView(View view, CustomViewCallback callback) {
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//			webView.setVisibility(View.INVISIBLE);
//			// 如果一个视图已经存在，那么立刻终止并新建一个
//			if (xCustomView != null) {
//				callback.onCustomViewHidden();
//				return;
//			}
//			video_fullView.addView(view);
//			xCustomView = view;
//			xCustomViewCallback = callback;
//			video_fullView.setVisibility(View.VISIBLE);
//		}
//
//		// 视频播放退出全屏会被调用的
//		@Override
//		public void onHideCustomView() {
//			if (xCustomView == null)// 不是全屏播放状态
//				return;
//
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//			xCustomView.setVisibility(View.GONE);
//			video_fullView.removeView(xCustomView);
//			xCustomView = null;
//			video_fullView.setVisibility(View.GONE);
//			xCustomViewCallback.onCustomViewHidden();
//			webView.setVisibility(View.VISIBLE);
//		}

		// 视频加载时进程loading
		@Override
		public View getVideoLoadingProgressView() {
			if (xprogressvideo == null) {
				LayoutInflater inflater = LayoutInflater
						.from(CultrueActivity.this);
				xprogressvideo = inflater.inflate(
						R.layout.video_loading_progress, null);
			}
			return xprogressvideo;
		}
	}

//	/**
//	 * 判断是否是全屏
//	 * 
//	 * @return
//	 */
//	public boolean inCustomView() {
//		return (xCustomView != null);
//	}

//	/**
//	 * 全屏时按返加键执行退出全屏方法
//	 */
//	public void hideCustomView() {
//		xwebchromeclient.onHideCustomView();
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//	}

	@Override
	protected void onResume() {
		super.onResume();
		webView.onResume();
		webView.resumeTimers();

//		/**
//		 * 设置为横屏
//		 */
//		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		webView.onPause();
		webView.pauseTimers();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// video_fullView.removeAllViews();
		webView.loadUrl("about:blank");
		webView.stopLoading();
		webView.setWebChromeClient(null);
		webView.setWebViewClient(null);
		webView.destroy();
		webView = null;
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if (inCustomView()) {
//				// webViewDetails.loadUrl("about:blank");
//				hideCustomView();
//				return true;
//			} else {
//				webView.loadUrl("about:blank");
//				CultrueActivity.this.finish();
//			}
//		}
//		return false;
//	}

	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
		switch (v.getId()) {
		case R.id.remain_layout:
			remain_layout.setVisibility(View.GONE);
			remain_hide.setVisibility(View.VISIBLE);
			break;
		case R.id.remain_hide:
			remain_layout.setVisibility(View.VISIBLE);
			remain_hide.setVisibility(View.GONE);
			break;
		case R.id.remain_ret:
			remain_layout.setVisibility(View.GONE);
			remain_hide.setVisibility(View.VISIBLE);
			webView.goBack();
			break;
		case R.id.remain_all:
			remain_layout.setVisibility(View.GONE);
			remain_hide.setVisibility(View.VISIBLE);
			Intent intent = new Intent();
			intent.setClass(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		ViewGroup view = (ViewGroup) getWindow().getDecorView();
		view.removeAllViews();
		super.finish();
	}
}
