package com.vcontrol.vcontroliot.model;

import com.vcontrol.vcontroliot.service.RetrofitService;
import com.vcontrol.vcontroliot.service.RetrofitServiceInstance;
import com.vcontrol.vcontroliot.util.AdContract;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by zone on 2017/03/26
 */

public class AdModelImpl implements AdContract.Model {
    RetrofitService retrofitService;
    public AdModelImpl() {
        retrofitService = RetrofitServiceInstance.getInstance();
    }
    public Observable<LoginCheckBean> getLoginCheck() {//假装服务器要展示广告
        return Observable.create(new Observable.OnSubscribe<LoginCheckBean>() {
            @Override
            public void call(Subscriber<? super LoginCheckBean> subscriber) {
                subscriber.onNext(new LoginCheckBean(true));
                subscriber.onCompleted();
            }
        });
    }

    public Observable<AdMessageBean> getAdMessage() {
        return Observable.create(new Observable.OnSubscribe<AdMessageBean>() {
            @Override
            public void call(Subscriber<? super AdMessageBean> subscriber) {//假装要展示 3 秒广告，且广告图为如下图
                subscriber.onNext(new AdMessageBean(3,"http://www.wvcontrol.com.cn/uploads/2017/12/261315358720.png","http://www.wvcontrol.com.cn/"));
                subscriber.onCompleted();
            }
        });
    }

    public Observable<ResponseBody> downLoadFile(String fileUrl) {
        return retrofitService.downLoadFile(fileUrl);
    }
}

//"http://www.wvcontrol.com.cn/uploads/2017/12/261315358720.png","http://www.wvcontrol.com.cn/"