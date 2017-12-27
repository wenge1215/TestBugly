集成腾讯Bugly
    链接：https://bugly.qq.com/v2/index

1.测试异常收集   ---jar包大小 238.7 kb

2.运营情况统计

3.版本升级提示   ---arr包大小 723 kb

4.热更新

1.测试异常收集
    1.通过gradle集成方式
        1.在project 的build下 添加：

                        // 配置自动上传符号表插件，注：为了能快速并准确地定位用户APP发生Crash的代码位置，Bugly使用符号表对APP发生Crash的程序堆栈进行解析和还原
                        classpath 'com.tencent.bugly:symtabfileuploader:1.3.9'

        2.在module 的build下 添加

                        apply plugin: 'bugly'
                            /*
                            除了appId和appKey之外，还可以设置其他属性，属性列表如下：
                            属性	值	说明
                            appId	String	App ID
                            appKey	String	App Key
                            execute	boolean	插件开关
                            upload	boolean	上传开关
                            outputDir	String	符号表文件输出路径
                             */
                            bugly {
                                appId = 'fe08124f74'
                                appKey = 'b4cb72a8-adb5-4e8f-89bd-969b09aac7b4'
                            }

                      //bugly
                         defaultConfig {
                             ndk {
                                 // 设置支持的SO库架构
                                 abiFilters 'armeabi' //, 'x86', 'armeabi-v7a', 'x86_64', 'arm64-v8a'
                             }
                         }


                          //bugly
                             compile 'com.tencent.bugly:crashreport:2.6.6' //其中latest.release指代最新Bugly SDK版本号，也可以指定明确的版本号，例如2.1.9
                             compile 'com.tencent.bugly:nativecrashreport:3.3.1' //其中latest.release指代最新Bugly NDK版本号，也可以指定明确的版本号，例如3.0

            3.在 Application中进行初始化
            4.在 manifest 中注册 Application，添加一下权限：

                       <uses-permission android:name="android.permission.READ_PHONE_STATE" />
                        <uses-permission android:name="android.permission.INTERNET" />
                        <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
                        <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
                        <uses-permission android:name="android.permission.READ_LOGS" />

            5.测试：调用以下代码，
                 CrashReport.testJavaCrash();
              在bugly控制台刷新，查看异常信息

2.应用升级 与 热更新（全量更新 与 热更新）
        集成方式同上，需要注意的是，如果之前已经添加了异常统计的依赖后，再添加了以下依赖

               //bugly  应用升级与热更新
                compile 'com.tencent.bugly:crashreport_upgrade:1.3.4'

           运行项目时，依赖会冲突，报错如下：
           Error:Execution failed for task ':app:transformDexArchiveWithExternalLibsDexMergerForDebug'.
           > com.android.builder.dexing.DexArchiveMergerException: Unable to merge dex
           原因是：应用升级的依赖中已经包含了异常统计所需要的依赖，导致依赖重复
           解决方式：直接注释掉异常统计的依赖就OK了，只添加应用升级的依赖就OK了

       初始化应用升级模块
          /**
            * true表示app启动自动初始化升级模块；
            * false不好自动初始化
            * 开发者如果担心sdk初始化影响app启动速度，可以设置为false
            * 在后面某个时刻手动调用
            */
           Beta.autoInit = true;



 3.完整的 应用升级与 热更新集成方式：

            1.在项目的.gradle 中添加如下依赖：
                // tinkersupport插件(1.0.3以上无须再配置tinker插件）
                classpath "com.tencent.bugly:tinker-support:1.1.1"

            2.在module 的.gradle 中添加如下依赖：
                // 多dex配置
                compile "com.android.support:multidex:1.0.1"
                // 远程仓库集成方式（推荐）
                compile 'com.tencent.bugly:crashreport_upgrade:1.3.4'

            3.在Application中进行初始化：
                // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId,调试时将第三个参数设置为true
                Bugly.init(this, "fe08124f74", true);

                // 安装tinker
                Beta.installTinker();

4.热更新 build 配置

    apply plugin: 'com.tencent.bugly.tinker-support'

    def bakPath = file("${buildDir}/bakApk/")

    /**
     * 此处填写每次构建生成的基准包目录
     */
    def baseApkDir = "app-1226-16-43-11"

    /**
     * 对于插件各参数的详细解析请参考
     */
    tinkerSupport {

        // tinkerEnable功能开关
        tinkerEnable = true

        // 开启tinker-support插件，默认值true
        enable = true

        // 自动生成tinkerId, 你无须关注tinkerId，默认为false
        autoGenerateTinkerId = true

        // 指定归档目录，默认值当前module的子目录tinker
        autoBackupApkDir = "${bakPath}"

        // 是否启用覆盖tinkerPatch配置功能，默认值false
        // 开启后tinkerPatch配置不生效，即无需添加tinkerPatch
        overrideTinkerPatchConfiguration = true

        // 编译补丁包时，必需指定基线版本的apk，默认值为空
        // 如果为空，则表示不是进行补丁包的编译
        // @{link tinkerPatch.oldApk }
        baseApk = "${bakPath}/${baseApkDir}/app-release.apk"
    //    baseApk =  "${bakPath}/${baseApkDir}/app-debug.apk"

        // 对应tinker插件applyMapping
        baseApkProguardMapping = "${bakPath}/${baseApkDir}/app-release-mapping.txt"
    //    baseApkProguardMapping = "${bakPath}/${baseApkDir}/app-debug-mapping.txt"

        // 对应tinker插件applyResourceMapping
        baseApkResourceMapping = "${bakPath}/${baseApkDir}/app-release-R.txt"
    //    baseApkResourceMapping = "${bakPath}/${baseApkDir}/app-debug-R.txt"

        // 构建基准包跟补丁包都要修改tinkerId，主要用于区分
    //    tinkerId = "1.0.3-ccc"

        // 打多渠道补丁时指定目录
        // buildAllFlavorsDir = "${bakPath}/${baseApkDir}"

        // 是否使用加固模式，默认为false
        // isProtectedApp = true

        // 是否采用反射Application的方式集成，无须改造Application
        enableProxyApplication = true

        // 支持新增Activity
        supportHotplugComponent = true

    }

    /**
     * 一般来说,我们无需对下面的参数做任何的修改
     * 对于各参数的详细介绍请参考:
     * https://github.com/Tencent/tinker/wiki/Tinker-%E6%8E%A5%E5%85%A5%E6%8C%87%E5%8D%97
     */
    tinkerPatch {
        tinkerEnable = true
        ignoreWarning = false
        useSign = false
        dex {
            dexMode = "jar"
            pattern = ["classes*.dex"]
            loader = []
        }
        lib {
            pattern = ["lib/*/*.so"]
        }

        res {
            pattern = ["res/*", "r/*", "assets/*", "resources.arsc", "AndroidManifest.xml"]
            ignoreChange = []
            largeModSize = 100
        }

        packageConfig {
        }
        sevenZip {
            zipArtifact = "com.tencent.mm:SevenZip:1.1.10"
    //        path = "/usr/local/bin/7za"
        }
        buildConfig {
            keepDexApply = false
    //      tinkerId = "base-2.0.1"
        }
    }

5.生成基准包 和 补丁包

    1.生成并安装基础包： Gradle --- app --- Tasks --- install ---- installRelease
    运行完成后，会在app下的build文件夹下生成 基础包的目录

    2.生成补丁包：
        1.在tinker配置文件中添加生成的基准包目录
        2.Gradle --- app --- Tasks --- tinker-support ---  buildTinkerPatchRelease
        3.运行完成后，会在app --- build --- outputs --- patch 生成补丁包


6.walle 多渠道打包
    项目地址：https://github.com/Meituan-Dianping/walle

    1.集成方式：
        1.project的build中添加
         dependencies {
                classpath 'com.meituan.android.walle:plugin:1.1.5'
            }

       2.在module 的build 中添加:

       apply plugin: 'walle'

       dependencies {
           compile 'com.meituan.android.walle:library:1.1.5'
       }

       3.配置插件
       walle {
           // 指定渠道包的输出路径
           apkOutputFolder = new File("${project.buildDir}/outputs/channels");
           // 定制渠道包的APK的文件名称
           apkFileNameFormat = '${appName}-${packageName}-${channel}-${buildType}-v${versionName}-${versionCode}-${buildTime}.apk';
           // 渠道配置文件
           channelFile = new File("${project.getProjectDir()}/channel")
       }

       4.添加渠道配置文件 channel，添加到app目录下

       5.使用：
       WalleChannelReader.getChannel(this.getApplicationContext());
