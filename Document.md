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

## 获取当前登录用户信息

## 用户切换

## 用户注销

## 登录状态查询

## 删除缓存用户名

## 设置用户所在服务器ID

## 检查更新

## 充值

## 获取状态信息
