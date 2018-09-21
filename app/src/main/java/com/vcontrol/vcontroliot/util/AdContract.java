package com.vcontrol.vcontroliot.util;

import android.graphics.Bitmap;

import com.vcontrol.vcontroliot.callback.IBaseView;
import com.vcontrol.vcontroliot.model.LoginCheckBean;

/**
 * Created by zone on 2017/3/26.
 */

public class AdContract
{
    public interface View extends IBaseView{
        void setAdTime(int count);

        void setLoginCheckBean(LoginCheckBean loginCheckBean);

        void setAdImg(Bitmap bitmap);
    }

    public interface Presenter {
    }

    public interface Model{
    }


}