package com.nightmare.apktool;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import brut.common.BrutException;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * ApktoolPlugin
 */
public class ApktoolPlugin implements FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "apktool");
        channel.setMethodCallHandler(this);
    }


    private static Socket connect() throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", 4041));
        return socket;
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        String id = call.method;
        switch (id) {
            case "logoutToSocket":
                //重定向java的标准输入输出
                new Thread(() -> {
                    try {
                        Socket apktoolSocket;
                        apktoolSocket = connect();
                        System.setErr(new PrintStream(apktoolSocket.getOutputStream(), false));
                        System.setOut(new PrintStream(apktoolSocket.getOutputStream(), false));
//                        fileOutputStream.
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    handler.post(() -> result.success("重定向java的标准输入输出到套接字回调"));

                }).start();

                break;
            case "logout":
                //重定向java的标准输入输出
                new Thread(() -> {
                    try {
                        String logPath = call.arguments.toString();

                        FileOutputStream fileOutputStream = new FileOutputStream(new File(logPath), false);
                        System.setErr(new PrintStream(fileOutputStream, false));
                        System.setOut(new PrintStream(fileOutputStream, false));
//                        fileOutputStream.
                        System.out.println("重定向java的标准输出到文件");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    handler.post(() -> result.success("重定向java的标准输入输出到文件回调"));

                }).start();

                break;

            case "apktool"://apktool的
                final String[] args
                        = (String[]) ((ArrayList) call.arguments).toArray(new String[0]);
                new Thread(() -> {
                    try {
                        brut.apktool.Main.main(args);
                    } catch (IOException e) {
                        //methodChannel.invokeMethod("Terminal", e.getMessage());
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        //methodChannel.invokeMethod("Terminal", e.getMessage());
                        e.printStackTrace();
                    } catch (BrutException e) {
                        //methodChannel.invokeMethod("Terminal", e.getMessage());
                        e.printStackTrace();
                    }
                    handler.post(() -> result.success("success"));

                }).start();
                break;
            case "smali":

                final String[] arg
                        = (String[]) ((ArrayList) call.arguments).toArray(new String[0]);
                new Thread(() -> {
                    org.jf.smali.Main.main(arg);
                    handler.post(() -> result.success("success"));

                }).start();

                break;
            case "baksmali":
                final String[] arg3
                        = (String[]) ((ArrayList) call.arguments).toArray(new String[0]);
                new Thread(() -> {
                    org.jf.baksmali.Main.main(arg3);
                    handler.post(() -> result.success("success"));

                }).start();

//                org.jf.baksmali.Main.main(args);
//                result.success("");
                //String[] A = new String[]{"d", "/storage/emulated/0/Apktool/jar/1/classes.dex", "-o", "/storage/emulated/0/Apktool/jar/1/sdasd"};
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Dynamic dynamic;
//                            File cacheFile = FileUtils.getCacheDir(getApplicationContext());
//                            //String[] A = new String[]{"d", "/storage/emulated/0/Apktool/jar/1/classes.dex", "-o", "/storage/emulated/0/Apktool/jar/1/sdasd"};
//                            //下面开始加载dex class
//                            @SuppressLint("SdCardPath") DexClassLoader dexClassLoader = new DexClassLoader("/sdcard/Apktool/apktool.dex", cacheFile.getAbsolutePath(), null, getClassLoader());
//                            try {
//                                Class libClazz = dexClassLoader.loadClass("com.nightmare.Decomplie");
//                                dynamic = (Dynamic) libClazz.newInstance();
//                                String[] args= (String[]) ((ArrayList)call.arguments).toArray(new String[0]);
//                                if (dynamic != null)
//                                    dynamic.main(args);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
                //Main.main(A);
                break;
        }


    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }
}