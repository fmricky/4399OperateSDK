4399运营SDK Android客户端v2.0接入说明
====================== 

##修改记录 
版本号  |   修改日期    |   修改者  |   修改内容
--------|---------------|-----------|-------------------------
v1.0.0  |   2014-07-08  |   张生    |   创建文档
v1.0.1  |   2014-07-14  |   张生    |   增加部分接口参数的说明
v2.0.0  |   2014-07-31  |   郑旭    |   增加全局监听、修改SDK部署配置、修改部分接口调用方式

# 文档说明
## 功能描述
4399运营SDK（以下简称：SDK）主要用来向第三方游戏开发者提供便捷、安全一级可靠的4399账户登录、多渠道充值付费、版本升级检测等功能。本文主要描述SDK接口的使用方法，供合作伙伴的开发者接入使用。

## 阅读对象
本文档面向具有一定Android客户端开发能力，了解Android客户端的开发和管理人员。

## SDK内容

 - 4399运营SDK（Android）接入说明：SDK接入文档，即本文档
 - m4399RechargeSDK：SDK资源文件工程内含SDK jar包
 - m4399OperateSDKDemo工程：Demo工程

# 集成流程
## 接入前期准备
接入前期准备工作包括签约、申请GameKey、配置Game信息。

## SDK集成流程
假设现在你的工程目录名字叫project，下面将具体介绍如何将SDK接入project中。

### 关联依赖工程
1. 将m4399OperateSDK工程关联到project：将m4399OperateSDK导入到eclipse中→右键点击4399OperateSDK工程名→Properties→Android→勾选Is Library→OK；
右键点击project工程名→Properties→Add→在弹出的对话框中点选资源工程m4399OperateSDK→OK→对话框关闭，点击OK
2. 将alipay_msp.apk 拷贝到你的project的asserts目录下

### 配置AndroidManifest.xml文件
- 添加SDK所需的权限 
``` xml
<uses-permission android:name="android.permission.CHANGE_CONFIGURATION"></uses-permission>
<uses-permission android:name="android.permission.CALL_PHONE"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE" /> 
<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" 	/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.SEND_SMS" />
``` 
- 注册SDK相关Activity
``` xml
待定
```

### 代码混淆配置
如果游戏有需要进行代码混淆，请再项目根目录下的project.properties里加入`proguard.config=proguard.cfg`并且在根目录下的proguard.cfg文件里追加以下配置

``` 
-libraryjars ../m4399RechargeSDK/libs/m4399RechargeSDK.jar
-libraryjars ../m4399RechargeSDK/libs/alipay.jar
-libraryjars ../m4399RechargeSDK/libs/huafubao_sdk_1.1.13.jar
-libraryjars ../m4399RechargeSDK/libs/m4399OperateSDK.jar

-keep class cn.m4399.operate.** {*;}
-keep class cn.m4399.recharge.** {*;}
-keep class cn.m4399.recharge.thirdparty.** {*;}
-keepclassmembers class cn.m4399.recharge.R$* {*;}
-keep public class com.alipay.android.app.lib.ResourceMap {*;}

-keep class com.alipay.android.app.IAliPay{*;}
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;} 
```
# SDK接入流程
## 初始化与析构
初始化推荐在游戏初始化过程中进行，析构函数则在游戏退出前执行。
```java
OperateCenter mOpeCenter = OperateCenter.getInstance(MainActivity.this);
mOpCenter.init(new OnInitFinishedListener(){
    @Override
    public void onInitFinished(boolean isLogin, String uid, String name, String token){
        //TODO: SDK初始化完成后的操作
    }
});
```
设置__是否支持处理超出部分金额__
```java
mOpeCenter.setSupportExcess(support);
```
析构
```java
mOpeCenter.finalize();
```

*注：代码中MainActivity为当前Activity,下同。*

__能否支持处理超出部分金额__指在使用SDK充值时，由于用户选择的充值渠道不同，可能造成实际充值金额超出游戏下单时传入的金额。如果游戏服务端能够正确处理超出部分的金额，则本接口传入true。如果无法支持处理超出部分的金额，则传入false，SDK将会根据传入金额自动隐藏无法满足充值金额的渠道（例：开发者设置SupportExcess为false，充值时传入7元，此时4399一卡通中无7元面额的充值卡，此时4399一卡通的充值渠道将自动隐藏）。*SupportExcess*默认为false。
## 用户登录
用户在触发登录时，调用该接口，如果SDK内已包含未注销的用户凭证，将自动返回用户信息。如需强制调出登录界面，请使用【用户切换】接口。
```java
mOpeCenter.login(MainActivity.this, new OnLoginFinishedListener() {

	@Override
	public void onLoginFinished(boolean success, int resultCode, User userInfo)
	{
	    //登录结束后的游戏逻辑
	}
});
```
SDK会自动识别用户手机中是否安装了新版的4399游戏盒1.4.1以上版本，如果已安装，自动跳转至游戏盒授权登录。如果未安装，则弹出Web版4399统一登录界面。

*注：登录后如果未注销，登录状态将一直保持直至登录凭证过期或失效（若用户修改平台账户密码，所有游戏授权凭证将失效，需重新登录）。建议游戏在初始化完成后调用【登录状态查询】接口查询用户当前登录状态。* 

## 获取当前登录用户信息
在SDK处于登录状态时，可通过该接口获取当前用户的信息（UID、用户名、昵称、登录凭证）。
```java
User user = mOpeCenter.getCurrentAccount();
```

## 用户切换
当用户需要注销当前登录状态，且同时弹出登录界面时，使用本接口。本接口的监听器类型与【用户登录】接口相同。
```java
mOpeCenter.switchAccount(MainActivity.this, new OnLoginFinishedListener() {

	@Override
	public void onLoginFinished(boolean success, int resultCode, User userInfo)
	{
	    //用户账号切换结束后的游戏逻辑
	}
});
```

## 用户注销
当用户需要注销当前登录状态时，使用本接口。
```java
mOpeCenter.logout(new OnLogoutFinishedListener() {
	@Override
	public void onLogoutFinished(boolean success, int resultCode)
	{
        //用户注销后的游戏逻辑
	}
});
```

## 登录状态查询
查询当前客户端是否有账号登录
```java
boolean isLogin = mOpeCenter.isLogin();
```
## 获取缓存用户名列表
当使用WEB版4399统一登录时，系统会记忆最多5次登录的用户名,用于下一次在登录界面提供用户候选。
```java
String[] accounts = mOpeCenter.getCacheAccounts();
```

## 删除缓存用户名
当需要充缓存中删除已登录用户信息时，可调用该接口，删除历史登录用户信息。（最后一次登录用户无法删除）
```java
mOpeCenterremoveCacheAccount("USER_NAME");
```

## 设置用户所在服务器ID
当游戏有分服时，在用户选择角色进入分服时，请务必立即通过本接口设置所在服的ID。如果无分服，则可不设置。
```java
mOpeCenter.setServer("SERVER_ID");
```
## 检查更新
当游戏调用本接口时，SDK将检查后台是否有新版本游戏上线，如果有，则显示更新内容，并提示用户升级。该升级为增量升级，后台在提交新版游戏时自动制作差分包，更新时用户只需下载APK文件中新旧版本有差异的部分。相关更新内容和版本提交事宜，请联系4399相关运营对接人员。
```java
mOpeCenter.updateApk(MainActivity.this, new OnUpdateFinishedListener() {
	    @Override
	    public void onUpdateFinished(boolean success, int resultCode)
	    {
            
	    }
	});
```

## 充值
当用户需要充值时，可调用本接口启动充值中心界面。
```java
mOpeCenter.recharge(MainActivity.this,
				je,     //充值金额（元）
				mark,   //游戏方订单号
				productName,    //商品名称
				new OnRechargeFinishedListener() {

				    @Override
				    public void onRechargeFinished(
					    boolean success, int resultCode)
				    {
			            //充值完成后的游戏逻辑
				    }
				});
```
* je __充值金额__4399充值中心仅支持整数金额充值，最小充值金额1元。
* mark __订单号__最大长度32位，由包含字母或数字组成的唯一字符串，该字段不可为空。
* productName __商品名称__ 如果传入商品名，充值中心将直接显示改商品名称，如果充值金额大于下单时传入的je时，将显示商品名+XXX游戏币，相关游戏币的兑换比例在接入时提供给运营人员配置。如果未传入商品名，则直接显示XXX游戏币。

## 获取状态信息
工具接口，用于将解析回调函数中的resultCode解析为中文的说明。
```java
String resultMessage = OperateCenter.getResultMsg(resultCode);
```