package com.wd.tech;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.wd.tech.utils.RetrofitManager;
import com.wd.tech.utils.rsa.RsaCoder;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private String realPassWord = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void register(View view) {
        //随机手机号，为了demo不用手动设置手机号
        String phone = String.valueOf((long) (15501186526L + Math.random() * 1000));
        //随机昵称
        String nickName = "wang" + (long) (Math.random() * 10000);

        ILoginApi iLoginApi = RetrofitManager.getDefault().create(ILoginApi.class);
        String encryptPassWord = "";
        Log.e("TAG", "encyptPwd =   " + encryptPassWord);
        try {
            //使用RSA公钥对密码加密
            encryptPassWord = RsaCoder.encryptByPublicKey(realPassWord);
        } catch (Exception e) {
            e.printStackTrace();
        }
        iLoginApi.register(phone, nickName, encryptPassWord)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RegisterBean>() {
                    @Override
                    public void accept(RegisterBean loginBean) throws Exception {
                        Log.e("TAG", "loginBean ==  " + loginBean.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("TAG", "loginBean ==  " + throwable.toString());
                    }
                });
    }
}
