package com.fy.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fy.application.BaseActivity;
import com.fy.application.MyApplication;
import com.fy.service.MsgService;
import com.fy.service.TimeService;
import com.fy.tool.AnimateFirstDisplayListener;
import com.fy.tool.Getmac;
import com.fy.wo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class HelpActivity extends BaseActivity implements OnClickListener {

	private Button ret;
	private TextView text;
	private String mac;
	private Button ipset;
	private Context context;

	private RelativeLayout remain_layout;
	private Button remain_hide;
	private Button remain_all;
	private Button remain_ret;

	private SharedPreferences sp;

	private VideoView myVideoView;
	private String videopath;

	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	// private MediaController mMediaController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		context = this;
		ret = (Button) findViewById(R.id.help_return);
		ipset = (Button) findViewById(R.id.ipaddress);
		text = (TextView) findViewById(R.id.textView1);

		ImageView a = (ImageView) findViewById(R.id.helpbg);
		setbg(a, 0);

		ret.setOnClickListener(this);
		ipset.setOnClickListener(this);
		sp = getSharedPreferences("SP", MODE_PRIVATE);
		mac = sp.getString("mac", "null mac");
		text.setText(mac);
		// 悬浮按钮
		remain_layout = (RelativeLayout) findViewById(R.id.remain_layout);
		remain_hide = (Button) findViewById(R.id.remain_hide);
		remain_all = (Button) findViewById(R.id.remain_all);
		remain_ret = (Button) findViewById(R.id.remain_ret);
		remain_layout.setOnClickListener(this);
		remain_hide.setOnClickListener(this);
		remain_all.setOnClickListener(this);
		remain_ret.setOnClickListener(this);

		initView("helpavi");
	}

	private void setbg(ImageView v, int i) {

		String bgname[] = { "drawable://" + R.drawable.shiyongbangzhu, };

		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(bgname[i], v, options);
	}

	private void initView(String videoflag) {

		myVideoView = (VideoView) findViewById(R.id.hvideo);
		// videopath =
		// Environment.getExternalStorageDirectory().getAbsolutePath()
		// + "/ZHDJ/" + videoflag + ".mp4";

		videopath = "android.resource://" + getPackageName() + "/"
				+ R.raw.help_avi;

		myVideoView.setVideoPath(videopath);
		myVideoView.requestFocus();

		// mMediaController = new MediaController(this);
		// myVideoView.setMediaController(mMediaController);
		myVideoView.start();
		myVideoView
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {

						// videopath = Environment.getExternalStorageDirectory()
						// .getAbsolutePath()
						// + "/ZHDJ/"
						// + videoflag
						// + ".mp4";

						myVideoView.setVideoPath(videopath);
						myVideoView.start();

					}
				});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.help_return:
			Intent intent2 = new Intent();
			intent2.setClass(this, MainActivity.class);
			intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			startActivity(intent2);
			// finish();
			break;
		case R.id.remain_layout:
			remain_layout.setVisibility(View.GONE);
			remain_hide.setVisibility(View.VISIBLE);
			break;
		case R.id.remain_hide:
			remain_layout.setVisibility(View.VISIBLE);
			remain_hide.setVisibility(View.GONE);
			break;
		case R.id.remain_all:
			remain_layout.setVisibility(View.GONE);
			remain_hide.setVisibility(View.VISIBLE);
			Intent intent = new Intent();
			intent.setClass(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case R.id.remain_ret:
			remain_layout.setVisibility(View.GONE);
			remain_hide.setVisibility(View.VISIBLE);
			Intent intent3 = new Intent();
			intent3.setClass(this, MainActivity.class);
			intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent3);
			break;
		case R.id.ipaddress:
			CharSequence[] items = { "IP设置", "楼层IP设置", "跳转时长设置", "获取全屏任务时长设置",
//					"校园发布系统修复选项",
					"退出程序", "取消" };
			new AlertDialog.Builder(HelpActivity.this).setTitle("")
					.setItems(items, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							LayoutInflater inflater = getLayoutInflater();
							View layout;
							final EditText ip;
							final EditText psw;
							switch (which) {
							case 0:
								layout = inflater
										.inflate(
												R.layout.ipset,
												(ViewGroup) findViewById(R.id.ipdialog));
								ip = (EditText) layout.findViewById(R.id.ipadd);
								psw = (EditText) layout
										.findViewById(R.id.ippassword);
								ip.setVisibility(View.VISIBLE);
								Dialog ipdialog = new AlertDialog.Builder(
										HelpActivity.this)
										.setTitle("请设置网关地址")
										.setView(layout)
										.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int whichButton) {

														if (psw.getText()
																.toString()
																.equals("ipsetting")) {
															System.out
																	.println(ip
																			.getText()
																			.toString());
															Editor editor = sp
																	.edit();
															System.out
																	.println("ip:"
																			+ ip.getText()
																					.toString());
															editor.putString(
																	"IPaddress",
																	ip.getText()
																			.toString());
															editor.commit();
															Toast.makeText(
																	getApplicationContext(),
																	"设置成功",
																	Toast.LENGTH_SHORT)
																	.show();
														} else {
															Toast.makeText(
																	getApplicationContext(),
																	"密码错误",
																	Toast.LENGTH_SHORT)
																	.show();
														}
													}
												})// 设置确定按钮
										.setNegativeButton(
												"取消",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int whichButton) {
													}
												})// 设置取消按钮
										.create();
								ipdialog.show();
								break;
							case 1:
								layout = inflater
										.inflate(
												R.layout.ipset,
												(ViewGroup) findViewById(R.id.ipdialog));
								ip = (EditText) layout.findViewById(R.id.ipadd);
								psw = (EditText) layout
										.findViewById(R.id.ippassword);
								ip.setVisibility(View.VISIBLE);
								Dialog floordialog = new AlertDialog.Builder(
										HelpActivity.this)
										.setTitle("请设置楼层IP地址")
										.setView(layout)
										.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int whichButton) {

														if (psw.getText()
																.toString()
																.equals("ipsetting")) {
															System.out
																	.println(ip
																			.getText()
																			.toString());
															Editor editor = sp
																	.edit();
															System.out
																	.println("ip:"
																			+ ip.getText()
																					.toString());
															editor.putString(
																	"IPaddress",
																	ip.getText()
																			.toString());
															editor.commit();
															Toast.makeText(
																	getApplicationContext(),
																	"设置成功",
																	Toast.LENGTH_SHORT)
																	.show();
														} else {
															Toast.makeText(
																	getApplicationContext(),
																	"密码错误",
																	Toast.LENGTH_SHORT)
																	.show();
														}
													}
												})// 设置确定按钮
										.setNegativeButton(
												"取消",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int whichButton) {
													}
												})// 设置取消按钮
										.create();
								floordialog.show();
								break;
							case 2:
								layout = inflater
										.inflate(
												R.layout.ipset,
												(ViewGroup) findViewById(R.id.ipdialog));
								ip = (EditText) layout.findViewById(R.id.ipadd);
								ip.setHint("分钟为单位，如填2表示2分钟，只可以输入数字");
								psw = (EditText) layout
										.findViewById(R.id.ippassword);
								ip.setVisibility(View.VISIBLE);
								Dialog jumptimedialog = new AlertDialog.Builder(
										HelpActivity.this)
										.setTitle("请设置跳转时长")
										.setView(layout)
										.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int whichButton) {

														if (psw.getText()
																.toString()
																.equals("jumptime")) {
															Pattern p = Pattern
																	.compile("[0-9]*");
															Matcher m = p
																	.matcher(ip
																			.getText()
																			.toString());
															if (m.matches()) {
																Editor editor = sp
																		.edit();
																System.out
																		.println("ip:"
																				+ ip.getText()
																						.toString());
																editor.putString(
																		"JumpTime",
																		ip.getText()
																				.toString());
																editor.commit();
																Toast.makeText(
																		getApplicationContext(),
																		"设置成功",
																		Toast.LENGTH_SHORT)
																		.show();
															} else {
																Toast.makeText(
																		getApplicationContext(),
																		"请输入纯数字",
																		Toast.LENGTH_SHORT)
																		.show();
															}

														} else {
															Toast.makeText(
																	getApplicationContext(),
																	"密码错误",
																	Toast.LENGTH_SHORT)
																	.show();
														}
													}
												})// 设置确定按钮
										.setNegativeButton(
												"取消",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int whichButton) {
													}
												})// 设置取消按钮
										.create();
								jumptimedialog.show();
								break;
							case 3:
								layout = inflater
										.inflate(
												R.layout.ipset,
												(ViewGroup) findViewById(R.id.ipdialog));
								ip = (EditText) layout.findViewById(R.id.ipadd);
								ip.setHint("分钟为单位，如填2表示2分钟，只可以输入数字");
								psw = (EditText) layout
										.findViewById(R.id.ippassword);
								ip.setVisibility(View.VISIBLE);
								Dialog datatimedialog = new AlertDialog.Builder(
										HelpActivity.this)
										.setTitle("请设置获取数据间隔时长")
										.setView(layout)
										.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int whichButton) {

														if (psw.getText()
																.toString()
																.equals("datatime")) {
															Pattern p = Pattern
																	.compile("[0-9]*");
															Matcher m = p
																	.matcher(ip
																			.getText()
																			.toString());
															if (m.matches()) {
																Editor editor = sp
																		.edit();
																System.out
																		.println("ip:"
																				+ ip.getText()
																						.toString());
																editor.putString(
																		"DataTime",
																		ip.getText()
																				.toString());
																editor.commit();
																Toast.makeText(
																		getApplicationContext(),
																		"设置成功",
																		Toast.LENGTH_SHORT)
																		.show();
															} else {
																Toast.makeText(
																		getApplicationContext(),
																		"请输入纯数字",
																		Toast.LENGTH_SHORT)
																		.show();
															}

														} else {
															Toast.makeText(
																	getApplicationContext(),
																	"密码错误",
																	Toast.LENGTH_SHORT)
																	.show();
														}
													}
												})// 设置确定按钮
										.setNegativeButton(
												"取消",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int whichButton) {
													}
												})// 设置取消按钮
										.create();
								datatimedialog.show();
								break;
							case 4:
								layout = inflater
										.inflate(
												R.layout.ipset,
												(ViewGroup) findViewById(R.id.ipdialog));
								ip = (EditText) layout.findViewById(R.id.ipadd);
								psw = (EditText) layout
										.findViewById(R.id.ippassword);
								ip.setVisibility(View.GONE);
								Dialog exitdialog = new AlertDialog.Builder(
										HelpActivity.this)
										.setTitle("请输入密码")
										.setView(layout)
										.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int whichButton) {
														if (psw.getText()
																.toString()
																.equals("fyzyp")) {
															setForegroundflag(false);
															Intent intent = new Intent();
															intent.setAction("com.fy.exit");
															sendBroadcast(intent);
															Intent open = new Intent();
															open.setAction("com.checkzyp.quit");
															sendBroadcast(open);

															finish();
														} else {
															Toast.makeText(
																	getApplicationContext(),
																	"密码错误",
																	Toast.LENGTH_SHORT)
																	.show();
														}
													}
												})// 设置确定按钮
										.setNegativeButton(
												"取消",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int whichButton) {
													}
												})// 设置取消按钮
										.create();
								exitdialog.show();
								break;
//							case 5:
//								layout = inflater
//										.inflate(
//												R.layout.ipset,
//												(ViewGroup) findViewById(R.id.ipdialog));
//								ip = (EditText) layout.findViewById(R.id.ipadd);
//								ip.setHint("请确认此时发布系统已经不再运行时在此处输入‘1’。若因人为因素导致发布系统瘫痪，请尝试重启机器解决");
//								psw = (EditText) layout
//										.findViewById(R.id.ippassword);
//								ip.setVisibility(View.GONE);
//								Dialog refix = new AlertDialog.Builder(
//										HelpActivity.this)
//										.setTitle("警告：此选项为校园发布系统修复按钮")
//										.setView(layout)
//										.setPositiveButton(
//												"确定",
//												new DialogInterface.OnClickListener() {
//													public void onClick(
//															DialogInterface dialog,
//															int whichButton) {
//
//														if (psw.getText()
//																.toString()
//																.equals("reset")) {
//
//															Toast.makeText(
//																	getApplicationContext(),
//																	"设置成功",
//																	Toast.LENGTH_SHORT)
//																	.show();
//															Intent timeService;
//															Intent msgService;
//															msgService = new Intent(
//																	context,
//																	MsgService.class);
//															timeService = new Intent(
//																	context,
//																	TimeService.class);
//															System.out
//																	.println("停止msgService timeService");
//															context.stopService(msgService);
//															context.stopService(timeService);
//
//														} else {
//															Toast.makeText(
//																	getApplicationContext(),
//																	"密码错误",
//																	Toast.LENGTH_SHORT)
//																	.show();
//														}
//													}
//												})// 设置确定按钮
//										.setNegativeButton(
//												"取消",
//												new DialogInterface.OnClickListener() {
//													public void onClick(
//															DialogInterface dialog,
//															int whichButton) {
//													}
//												})// 设置取消按钮
//										.create();
//								refix.show();
//
//								break;
							case 5:
								break;
							}
						}
					}).create().show();
			break;
		}
	}

}
