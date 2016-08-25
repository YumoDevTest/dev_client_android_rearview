#本文是一些开发中文档描述的速记


=============================

模拟器VIN：     1A1JC5444R7252367
张磊给的VIN:    1A1JC5444R7252367

=============================
#模块划分

*车辆体检
*车辆信息
*车辆状态
*保养
*设置
    更改vin
    更改手机号
    关于

=====================================
#引用或依赖
#内部引用
    依赖Com.mapbar.obd.jar    由sdk组开发
#外部引用
    友盟统计分析
    picasso
    buse-adapter-helper
    eventbus

============================
#在收到推送消息时，可以获得参数 type 和 state ，意义如下:

             * type  0 扫码  1 注册  2 绑定vin 3 vin扫码
             * state 1 成功  2 失败  3 已注册

内网测试方法，在浏览器里输入下面的网址，token即表示 设备的唯一表示，type和state是其他参数。

    http://119.255.37.167:8010/api2/rearview/push?token=113323225539582313&type=1&state=1
    收到的响应内容可能为：
        {"type":1,"state":1,"token":"","sevTime":1469584555213,"msg":"您已通过手机成功完善爱车信息"}
外网测试方法
    http://119.255.37.167:8010/api2/rearview/push?token=113323225539582313&type=1&state=1&userId=55f8cb82fea6b70e720003fe

修改手机号时，可能发送的内容如下：
http://weixin.mapbar.com/obd/userRegister?imei=77726%%fsdffsdfssdfsdfsdfsd&pushToken=113323225539582313&token=GiOm/2xBR5s8OmsMg+w9ekdRxQ3Wy1BqML1Ty7F6SlfbsvoJMOFLgnRn2Zyjw6wY

======================

1. app 刷固件前，停止数据采集流程.stopThread...

刷固件流程
1.  发 ATE1 设置回显等。 响应以>结束
    发 AT@LOCK0  。 响应以>结束
2. 让固件重启。发 ATBOOT，等待固件重启。   atboot结尾，第一次atboot没有响应
3. 固件会启动 booloader.
4. bootloader的响应不再是普通指令，以回车符结束，本次和后续指令都以回车符结束
5. 再次发 atboot, 固件在1.5内再次收到 atboot,会进入刷固件模式，第二次响应 nakb，整数15

如果需要验证权限，发  AT@AUTH+密码

6. 多次数据请求和应答。。。 升级过程中有进度事件
7. 最后，抛出升级成功事件
8. 升级完成后 重启