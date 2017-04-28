package guozifeng.zshx.com.threehuancun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 姓名：郭子锋
 * Created by Administrator on 2017/4/27.
 * 作用：
 */

public class MyImageLoader {

    private LruCache<String, Bitmap> lruCache;
    private File derectory;
    private ExecutorService newFixedThreadPool;
    private Handler handler;

    private MyImageLoader() {
        handler = new Handler();
        //避免内存溢出
        int maxSize= (int) (Runtime.getRuntime().maxMemory()/8);
        //内存处理10M 10-1M 4M怎么知道图片有多大
        //获取单张图片的大小
        lruCache = new LruCache<String,Bitmap>(
                maxSize){
            //获取单张图片的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {

                return value.getHeight()*value.getRowBytes();
            }
        };
        //磁盘缓存
        derectory = new File(Environment.getExternalStorageDirectory(),"imageloader");
        if(!derectory.exists()){
            derectory.mkdirs();
        }
        //网络缓存
        newFixedThreadPool = Executors.newFixedThreadPool(5);
    }
    private static MyImageLoader myImageLoader=null;

    public static MyImageLoader getInstance(){
        if(myImageLoader==null){
            myImageLoader=new MyImageLoader();
        }
        return myImageLoader;
    }

    public void display(ImageView imageView,String path){

        //先去内存中获取图片
        Bitmap bitmap=lruCache.get(path);
        if(bitmap!=null){
            imageView.setImageBitmap(bitmap);
            Log.d("zzz","从内存中获取");
            return;
        }

        //内存没获取到图片--到磁盘获取
        bitmap=getBitmapFromLocal(path);
        if(bitmap!=null){
            Log.d("zzz","从磁盘中获取");

            imageView.setImageBitmap(bitmap);
            //到处到内存中一份
            lruCache.put(path,bitmap);
            return;
        }
        //磁盘没有获取到图片--到网络去请求
        getBitmapFromNet(imageView,path);
    }

    //网络请求图片
    private void getBitmapFromNet(final ImageView imageView, final String path) {



        newFixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //设置标记
                    imageView.setTag(path);

                    URL url=new URL(path);
                    HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(3000);
                    if(connection.getResponseCode()==200){
                        //获取流文件
                        InputStream inputStream=connection.getInputStream();
                        final Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                        //展示
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(path.equals(imageView.getTag())){
                                    Log.d("zzz","从网络中获取");
                                    imageView.setImageBitmap(bitmap);
                                    //保存到文件中一份
                                    writeBitmapTolocal(bitmap,path);
                                    //保存到内存中一份
                                    lruCache.put(path,bitmap);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //将图片写到SD卡
    private void writeBitmapTolocal(Bitmap bitmap, String path) {

        String pwd = MD5Utils.getPwd(path);
        File file=new File(derectory,pwd);
        try {
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    //从磁盘中读取图片
    private Bitmap getBitmapFromLocal(String path) {

        try {
            String pwd = MD5Utils.getPwd(path);
            File file=new File(derectory,pwd);
            FileInputStream fileInputStream=new FileInputStream(file);
            Bitmap bitmap=BitmapFactory.decodeStream(fileInputStream);
            //进行展示
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }





}
