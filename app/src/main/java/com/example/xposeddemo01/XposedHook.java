package com.example.xposeddemo01;

import android.content.Context;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 *
 */
public class XposedHook implements IXposedHookLoadPackage {
    /**
     * 该方法就是xposed插件的入口点
     *
     * @param loadPackageParam
     * @throws Throwable
     */
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        //XposedBridge 该类提供打印日志信息的方法
        XposedBridge.log("该插件配置没有任何问题");
        //loadPackageParam.packageName:当前启动应用程序的包名
        XposedBridge.log("package:" + loadPackageParam.packageName);

        //判断当前启动的目标程序是否是com.qianyu.helloworld
        if ("com.qianyu.helloworld".equals(loadPackageParam.packageName)) {
            //hook 普通方法、静态方法
            XposedHelpers.findAndHookMethod(
                    "com.qianyu.helloworld.LoginActivity",//完整包名+类名
                    loadPackageParam.classLoader, //类加载器
                    "login",//指定要hook的方法名
                    String.class, String.class,//指定要hook的方法的参数列表 参数类型.class
                    new XC_MethodHook() {
                        @Override //需要hook的方法执行之前，需要做的操作
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            //修改之前的
                            XposedBridge.log("args1:" + param.args[0]);
                            XposedBridge.log("args2:" + param.args[1]);

                            //修改之后的 (修改了参数)
                            param.args[0] = "yijincc";
                            param.args[1] = "123456789";
                        }

                        @Override //需要hook的方法执行之后，需要做的操作
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            //获取方法返回值getResult
                            XposedBridge.log("result:" + param.getResult());

                            //修改返回值
                            param.setResult(true);
                        }
                    });

            //hook 构造方法
            XposedHelpers.findAndHookConstructor(
                    "com.qianyu.helloworld.MySQLiteDatabase",//完整包名+类名
                    loadPackageParam.classLoader, //类加载器
                    Context.class,//参数
                    new XC_MethodHook() {
                        @Override//构造方法执行之前
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);

                            XposedBridge.log("args1:" + param.args[0]);
                            Toast.makeText((Context) param.args[0], "构造方法被调用了", Toast.LENGTH_LONG).show();
                        }
                    });
        }

        /**
         * 貪吃蛇app
         */
        if ("com.yunhaoge.tanchishe.egame".equals(loadPackageParam.packageName)) {

            Class<?> clazz = XposedHelpers.findClass("com.qy.zombie.zombie$3",
                    loadPackageParam.classLoader);

            //方法替换
            XposedHelpers.findAndHookMethod(clazz, "payCancel", Map.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    //主动调用
                    Map<String, String> map = new HashMap<>();
                    XposedHelpers.callMethod(methodHookParam.thisObject, "paySuccess", map);

                    return null;
                }
            });

            //方法替换
            XposedHelpers.findAndHookMethod(clazz, "payFailed", Map.class, int.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    //主动调用静态方法，需要传入该方法所在的类
                    Class<?> clazz02 = XposedHelpers.findClass("com.qy.zombie.zombie",
                            loadPackageParam.classLoader);
                    XposedHelpers.callStaticMethod(clazz02, "BuySccess");
                    return null;
                }
            });
        }
    }


}
