# <center>分级诊疗系统Android开发文档  

## 项目介绍

### 开发目的  

为了简化医生的日常工作，提高工作效率而开发此款Android客户端。

### 项目背景  

1. 项目名称：分级诊疗系统
2. 项目提出者：新疆
3. 项目负责人：朱明成
4. Android程序员：李平
5. Code recode by:Allen

## 项目概述

### 待开发软件的简要描述

1. 开发软件背景：  
	IDE：Android studio 3.3  
	编程语言：Java,xml 
	JDK：jdk1.8    
	Android SDK：sdk 28  
	构建工具：gradle v3.1.2  
  
	```
	 classpath 'com.android.tools.build:gradle:3.3.0-alpha13'	
	```  
  
	单元测试：junit v4.12  
  
	  
	```
	 testImplementation 'junit:junit:4.12'	
	``` 
  
	依赖注入：butterknife v8.8.1  
 
	```
	 implementation 'com.jakewharton:butterknife:8.5.1'	
	```    

	Github:[https://github.com/JakeWharton/butterknife](https://github.com/JakeWharton/butterknife)
		
	fragment：fragmentation v1.1.8  
  
	```
	 implementation 'me.yokeyword:fragmentation:1.1.8'
	```  

	Github:[https://github.com/YoKeyword/Fragmentation](https://github.com/YoKeyword/Fragmentation)	  
  
	事件：eventbus v3.0.0  
 
	```
	 implementation 'org.greenrobot:eventbus:3.0.0'
	```  
	
	Github:[https://github.com/greenrobot/EventBus](https://github.com/greenrobot/EventBus)
	  
	tab：materialtabstrip v1.1.1  
  
	```
	implementation'com.jpardogo.materialtabstrip:library:1.1.1'
	```  

	Github:[https://github.com/jpardogo/PagerSlidingTabStrip](https://github.com/jpardogo/PagerSlidingTabStrip)  
  
	列表：easyrecyclerview v4.3.6

  	```
	 implementation 'com.jude:easyrecyclerview:4.3.6'
	```  

	Github:[https://github.com/Jude95/EasyRecyclerView](https://github.com/Jude95/EasyRecyclerView)    
	
	Gson：gson v2.3.1

  	```
	 implementation 'com.google.code.gson:gson:2.3.1'
	```  
	
	Github:[https://github.com/google/gson](https://github.com/google/gson)   

	网络：retrofit v2.1.0  

	```
	 implementation 'com.squareup.retrofit2:retrofit:2.4.0'
	```  
	```
	 implementation 'com.squareup.retrofit2:converter-gson:2.4.0'
	```   
	```
	 implementation 'com.squareup.retrofit2:adapter-rxjava:2.4.0'
	```  
  	```
	 implementation 'com.squareup.okhttp3:okhttp-urlconnection:3.2.0'
	```   

	Github:[https://github.com/square/retrofit](https://github.com/square/retrofit)  

	函数式编程：rxjava v1.2.1，rxandroid v1.2.1  
  
	```
	 implementation 'io.reactivex:rxjava:1.3.0'  
	```  
	```
	 implementation 'io.reactivex:rxandroid:1.2.1'
	```  
	
	Github:[https://github.com/ReactiveX/RxJava](https://github.com/ReactiveX/RxJava)   

	Github:[https://github.com/ReactiveX/RxAndroid](https://github.com/ReactiveX/RxAndroid) 
  
	运行时权限：rxpermissions v0.9.3  
  
	```
	 implementation 'com.tbruyelle.rxpermissions:rxpermissions:0.9.3@aar'
	```  
	
	Github:[https://github.com/tbruyelle/RxPermissions](https://github.com/tbruyelle/RxPermissions)  

	图片加载：gilde v4.8.0  

	```
	 implementation "com.github.bumptech.glide:glide:4.1.1"
	```   

	Github:[https://github.com/bumptech/glide](https://github.com/bumptech/glide)  
	
	图片选择器：PictureSelector v2.2.3  
  
	```
	 implementation 'com.github.LuckSiege.PictureSelector:picture_library:v2.2.3'
	```   

	Github:[https://github.com/LuckSiege/PictureSelector](https://github.com/LuckSiege/PictureSelector) 

	对话框：material-dialogs v0.9.0.2，materialdatetimepicker v3.5.1  
  
	```
	 implementation 'com.afollestad.material-dialogs:core:0.9.0.2'
	```  
	```
	 implementation 'com.wdullaer:materialdatetimepicker:3.5.1'
	```   

	Github:[https://github.com/afollestad/material-dialogs](https://github.com/afollestad/material-dialogs)  

	Github:[https://github.com/wdullaer/MaterialDateTimePicker](https://github.com/wdullaer/MaterialDateTimePicker)  
  	
	底部弹出框：bottom sheet v1.5.3  

	```
	 implementation 'com.flipboard:bottomsheet-core:1.5.3'
	```  
	```
	 implementation 'com.flipboard:bottomsheet-commons:1.5.3' // optional
	```	 

	Github:[https://github.com/Flipboard/bottomsheet](https://github.com/Flipboard/bottomsheet) 

	轮播：banner v1.4.9  
  
	```
	 implementation 'com.youth.banner:banner:1.4.9'
	```   

	Github:[https://github.com/youth5201314/banner](https://github.com/youth5201314/banner)  
	
	二维码：bga-qrcode-zxing v1.2.1  
	
	```
	 implementation 'cn.bingoogolapple:bga-qrcode-zxing:1.2.1'
	```  

	Github:[https://github.com/bingoogolapple/BGAQRCode-Android](https://github.com/bingoogolapple/BGAQRCode-Android)
	
	圆角图片：roundedimageview v2.2.1  
	
	```
	 implementation 'com.makeramen:roundedimageview:2.2.1'
	```   

	Github:[https://github.com/vinc3m1/RoundedImageView](https://github.com/vinc3m1/RoundedImageView)
	
	角标控件：badgeview v1.1.3  

	```
	 implementation 'q.rorbin:badgeview:1.1.3'
	```  

	Github:[https://github.com/qstumn/BadgeView](https://github.com/qstumn/BadgeView)  
	
	图片缩放：PhotoView v1.2.6  

	```
	 implementation 'com.github.chrisbanes:PhotoView:1.2.6'
	```  

	Github:[https://github.com/chrisbanes/PhotoView](https://github.com/chrisbanes/PhotoView)   

	流式标签：androidtagview v1.1.4  
	
	```
	 implementation 'co.lujun:androidtagview:1.1.4'
	```   

	Github:[https://github.com/whilu/AndroidTagView](https://github.com/whilu/AndroidTagView)  

	推送：JPush v3.1.1  
	
   	```
	 implementation 'cn.jiguang.sdk:jpush:3.1.1'
	```  

	及时通讯：JMessage v2.5.0  

	```
	 implementation 'cn.jiguang.sdk:jmessage:2.5.0'
	```  
  	```
	 implementation 'cn.jiguang.sdk:jcore:1.1.9'
	```  	  
	   
	
	
2. 所要达到的目的：开发出完整，可用，用户体验好的Android客户端。

### 软件主要功能
  
1. 患者管理：通过标签的形式对患者进行分类。可以添加患者，查看患者详情，改变患者描述，编辑患者标签，为患者添加病历以及对各个病历中患者的资料进行分类管理。  
2. 会诊管理：分为**"我的会诊"**和**"会诊中心"**两大模块，医生可以通过点名会诊（通过邀请指定的医生）和会诊中心（通过一些条件筛选医生）的方式来创建一张会诊单，根据会诊单的内容展开会诊并与参与会诊单的医生进行交流。
3. 云门诊：业务开展有今日门诊 和预约门诊，今日门诊针对开展云门诊并且本日在线医生进行在线门诊业务。
4. 转诊：  分为**“我发起的“**和**发给我的"**,医生可以通过新增转诊，发起对指定医生进行指定病人转诊，也可以跟转诊医生，病人通过电话交流。
5. 直播：开通直播功能医院的医生可以发起在线直播功能，微信用户 或者app用户可以查看直播内容（可以设置资费信息）。
6. 患者咨询：患者通过微信企业号可以向医生发起电话或实时消息（文字，语音，视频）咨询。
7. 预约管理：分为**“我发起的“**和**发给我的"**两大模块，医生可以通过新增预约帮助病人预约挂号未来一周的在职医生号源。
  
### 用户特征和水平  

主要针对从事医疗行业的工作人员使用。

### 运行环境  

Android移动设备（Android操作系统需要大于4.1）。

### 条件与限制 
 
项目采用MVC模式开发。  

## 功能需求 

### 功能划分

1. 注册
2. 登录
3. 忘记密码
4. 患者管理
5. 会诊管理
6. 系统消息
7. 即时通讯
8. 用户信息修改
9. 用户签名
10. 免打扰
11. 多语言
12. 修改密码
13. 清除缓存
14. 登出
15. 推送
16. 云门诊
17. 转诊
18. 直播
19. 患者咨询
20. 预约管理

		
### 功能描述
1. 注册：根据必要的用户信息创建用户。
2. 登录：app登录、华为登录、极光登录。
3. 忘记密码：通过手机短信方式重置密码。
4. 患者管理：通过标签的形式管理患者，主要有添加患者、修改患者标签、修改患者描述、创建病历、修改病历资料。
5. 会诊管理：创建点名会诊和会诊中心，根据角色对会诊单进行操作、与会诊单中的医生开视频会议。
6. 系统消息：查看系统发出的消息。
7. 即时通讯：实时与其他医生进行交流（文字、图片）。
8. 用户信息修改：修改用户的可修改信息。
9. 用户签名：用户在手机上通过画板的方式手写签名、保存。
10. 免打扰：开启或关闭系统发出的推送消息。
11. 多语言：简体中文、英语、俄语的替换。
12. 修改密码：根据旧密码来修改当前用户密码。
13. 清除缓存：清除app运行期间的缓存（主要是图片缓存）。
14. 登出：退出当前用户帐号。
15. 推送：集成极光推送。
16. 云门诊： 云门诊业务
17. 转诊： 转诊业务
18. 直播：直播业务
19. 患者咨询：咨询业务
20. 预约管理：预约业务

## 其他

### 用户界面
	
主要采用Android 5.0的Material Design风格。

### 软件接口

1. 公共接口：全局使用的接口。
2. 用户接口：保存用户信息数据的接口。
3. 登录接口：操作用户登入、登出的接口。
4. 患者管理接口：患者管理模块的接口。
5. 会诊管理接口：会诊管理模块的接口。

### 华为视频会议

接入华为eSDK CloudVC 1.1.0，文档、jar、Demo详见**/eSDK**文件夹。


 

	
	





