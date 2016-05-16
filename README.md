# 音乐播放器 - 红岩考核
这是红岩网校移动开发部的考核作业。

## 依赖
自己封装的包括    
+ https://github.com/haruue/RedrockExamUtils
+ https://github.com/haruue/HaruueImageLoader
+ https://github.com/haruue/Time_Class_Util    

此外还使用了 Google 的      

```gradle
    compile 'com.android.support:appcompat-v7:23.3.0'    
    compile 'com.android.support:design:23.3.0'    
    compile 'com.android.support:recyclerview-v7:23.3.0'    
    
```

## 下载
https://github.com/haruue/RedrockExamMusicPlayer/raw/master/app/app-release.apk

### 额外的操作说明
+ 使用左边的抽屉切换 Activity
+ 在播放列表中长按项目来弹出删除和下载选项 

### 已实现功能（时间不够了我就直接拿要求改好了）
#### **一、热门榜单**

1. 使用选项卡切换热门内容 (欧美、内地等)
2. 热门内容用列表呈现
3. 每一项显示歌曲名、歌手名、专辑图片 (小图)、点击列表项播放并添加到播放列表

#### **二、查询页面**

1. 查询和显示结果
2. 支持换页

#### **三、歌曲详细**

1. 显示歌曲名、歌手名、专辑图片 (大图)
2. 播放进度显示
3. 开始/暂停播放等常见播放器操作

#### **四、播放列表**

1. 当然得有常见的播放器操作 ( 显示当前播放、换歌、开始、暂停等 )
2. 单曲循环、列表循环、随机播放
3. 移除歌曲（然而貌似需要重新进入 Activity 才能看到移除结果）
4. 数据库存储    

#### **五、歌曲播放**

1. 流媒体播放
2. 下载好的歌曲的播放

#### **六、歌曲下载**

1. 下载成功……
3. 通知栏显示下载进度

#### **七、后台操作**

1. 后台听歌
2. 后台下载