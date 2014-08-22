package cn.m4399.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import cn.m4399.operate.OperateCenter;
import cn.m4399.operate.OperateCenter.OnLoginFinishedListener;
import cn.m4399.operate.OperateCenter.OnLogoutFinishedListener;
import cn.m4399.operate.OperateCenter.OnRechargeFinishedListener;
import cn.m4399.operate.OperateCenterConfig;
import cn.m4399.operate.User;

public class MainActivity extends Activity
{
    public static final String TAG = "GameActivityFTNN";

    String toastPre = "【DEMO】";

    OperateCenter mOpeCenter;
    OperateCenterConfig mOpeConfig;
    TextView txtVersion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);
	requestWindowFeature(Window.FEATURE_NO_TITLE);

	setContentView(R.layout.main);

	// 游戏接入SDK；
	mOpeCenter = OperateCenter.getInstance();
	// 配置sdk属性,比如可扩展横竖屏配置
	mOpeConfig = new OperateCenterConfig.Builder(this)
		.setDebugEnabled(true)
		.setOrientation(getResources().getConfiguration().orientation)
		.setShowPopWindow(true)
		.setSupportExcess(true)
		.setGameKey("40027")
		.setGameName("测试游戏").
		build();
	mOpeCenter.setConfig(mOpeConfig);
	mOpeCenter.init(new OperateCenter.OnInitGloabListener() {

	    @Override
	    public void onInitFinished(boolean isLogin, User userInfo)
	    {
		Log.d(TAG, "Ope inited: " + userInfo.toString());
		Toast.makeText(MainActivity.this,
			toastPre + (isLogin ? getString(R.string.logging) : getString(R.string.not_logging) + ": " + userInfo
				.toString()),
			Toast.LENGTH_SHORT).show();
	    }

	    @Override
	    public void onUserAccountLogout()
	    {

	    }
	});

	txtVersion = (TextView) findViewById(R.id.text_version);
	txtVersion.setText("运营SDK v" + mOpeCenter.getVersion());
    }

    @Override
    protected void onDestroy()
    {
	super.onDestroy();

	mOpeCenter.destroy();
	mOpeCenter = null;
    }

    public void onLoginButtonClicked(View view)
    {
	mOpeCenter.login(MainActivity.this, new OnLoginFinishedListener() {

	    @Override
	    public void onLoginFinished(boolean success, int resultCode,
		    User userInfo)
	    {
		Log.d(TAG,
				"Login: " + success + ", " + resultCode + ": " + userInfo
					.toString());
		String messageString = OperateCenter.getResultMsg(resultCode) + ": " + userInfo;
		Toast.makeText(MainActivity.this,
			toastPre + messageString,
			Toast.LENGTH_SHORT).show();
	    }
	});
    }

    public void onIsLoginButtonClicked(View view)
    {
	boolean isLogin = mOpeCenter.isLogin();
	String login = isLogin ? getString(R.string.logging) : getString(R.string.not_logging);
	Toast.makeText(MainActivity.this, toastPre + login, Toast.LENGTH_SHORT)
		.show();
    }

    public void onLogoutButtonClicked(View view)
    {
	Log.d(TAG, "Logout Button Clicked...");

	mOpeCenter.logout(new OnLogoutFinishedListener() {
	    @Override
	    public void onLogoutFinished(boolean success, int resultCode)
	    {
		Log.d(TAG, "Logout: [" + success + ", " + resultCode + "]");
		Toast.makeText(MainActivity.this,
			toastPre + OperateCenter.getResultMsg(resultCode),
			Toast.LENGTH_SHORT).show();
	    }
	});
    }

    public void onServerButtonClicked(View view)
    {
	Log.d(TAG, "Server Button Clicked...");
	String servers[][] = new String[][] {
			{ "Server 1", "Server 2", "Server 3" },
			{ "Server 4", "Server 5", "Server 6" },
			{ "Server 7", "Server 8", "Server 9" } };
	TableLayout serverTable = new TableLayout(MainActivity.this);
	TableLayout.LayoutParams layoutParam = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
		ViewGroup.LayoutParams.WRAP_CONTENT);
	serverTable.setLayoutParams(layoutParam);
	serverTable.setColumnStretchable(3, true);
	for (int i = 0; i < servers.length; i++)
	{
	    final int a = i;
	    TableRow tr = new TableRow(MainActivity.this);

	    for (int j = 0; j < servers[i].length; j++)
	    {
		final int b = j;
		Button button = new Button(MainActivity.this);
		button.setOnClickListener(new OnClickListener() {

		    @Override
		    public void onClick(View v)
		    {
			mOpeCenter.setServer(String.valueOf(a * 3 + b + 1));
			Toast.makeText(MainActivity.this,
				toastPre + "Select Server " + (a * 3 + b + 1),
				Toast.LENGTH_SHORT).show();
		    }
		});
		button.setText(servers[i][j]);
		tr.addView(button, new TableRow.LayoutParams(0,
			LayoutParams.WRAP_CONTENT,
			1));
	    }

	    serverTable.addView(tr);
	}

	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
	builder.setTitle("Select server").setView(serverTable)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which)
		    {
			dialog.dismiss();
		    }
		}).create().show();
    }

    public void onUpdateButtonClicked(View view)
    {
	Log.d(TAG, "Update Button Clicked...");

	mOpeCenter.checkUpdateApk(MainActivity.this);
    }

    public void onUserButtonClicked(View view)
    {
	Log.d(TAG, "Get User info Button Clicked...");

	User user = mOpeCenter.getCurrentAccount();
	Log.d(TAG, user.toString());
	Toast.makeText(MainActivity.this,
		toastPre + user.toString(),
		Toast.LENGTH_SHORT).show();
    }

    public void onRechargeButtonClicked(View view)
    {
	Log.d(TAG, "Pay Button Clicked...");

	LayoutInflater inflator = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
	final ScrollView sv = (ScrollView) inflator
		.inflate(R.layout.params_scrollview, null);
	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

	builder.setTitle(R.string.start_pay)
		.setView(sv)
		.setNegativeButton("Cancel",
			new DialogInterface.OnClickListener() {

			    @Override
			    public void onClick(DialogInterface dialog,
				    int which)
			    {
				dialog.dismiss();
			    }
			})
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which)
		    {
			TextView jeTV = (TextView) sv.findViewById(R.id.je);
			String je = jeTV.getText().toString();
			// TextView markTV = (TextView)
			// sv.findViewById(R.id.mark);
			String mark = "20140417150717" + System
				.currentTimeMillis();
			if (mark.length() > 22)
			    mark = mark.substring(0, 22);
			TextView subjectTV = (TextView) sv
				.findViewById(R.id.subject);
			String productName = subjectTV.getText().toString();

			CheckBox chCB = (CheckBox) sv
				.findViewById(R.id.changable);
			boolean supportExcess = chCB.isChecked();
			mOpeCenter.setSupportExcess(supportExcess); // TODO:
								    // 临时接口，正式发布时只在OperateCenterConfig.Builder中设置是否超出超额充值

			CheckBox hsCB = (CheckBox) (CheckBox) sv
				.findViewById(R.id.have_subject);
			boolean hasSubject = hsCB.isChecked();
			if (!hasSubject)
			{
			    productName = null;
			}

			Log
				.d(TAG,
					"[" + je + ", " + mark + ", " + productName + ", " + supportExcess + "]");
			mOpeCenter.recharge(MainActivity.this,
				je,
				mark,
				productName,
				new OnRechargeFinishedListener() {

				    @Override
				    public void onRechargeFinished(
					    boolean success, int resultCode,
					    String msg)
				    {
					Log
						.d(TAG,
							"Pay: [" + success + ", " + resultCode + ", " + msg + "]");
					Toast.makeText(MainActivity.this,
						toastPre + resultCode + ": " + msg,
						Toast.LENGTH_SHORT).show();
				    }
				});
			dialog.dismiss();
		    }
		}).create().show();
    }

    public void onRemoveAccountButtonClicked(View view)
    {
	LayoutInflater inflator = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
	final LinearLayout layout = (LinearLayout) inflator
		.inflate(R.layout.remove_account_dialog, null);
	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
	builder.setTitle(R.string.delete_account).setView(layout)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which)
		    {
			EditText editText = (EditText) layout
				.findViewById(R.id.account_edittext);
			Log
				.d("MainActivityFTNN", editText
					.getText().toString());
			boolean result = mOpeCenter.removeCacheAccount(editText
				.getText().toString());
			String res = result ? getString(R.string.success) : getString(R.string.fail);
			Toast.makeText(MainActivity.this,
				toastPre + "delete account: " + editText
					.getText().toString() + res,
				Toast.LENGTH_SHORT).show();
			dialog.dismiss();
		    }
		}).create().show();
    }
}