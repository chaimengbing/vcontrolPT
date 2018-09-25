package com.vcontrol.vcontroliot.act;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.VcontrolApplication;
import com.vcontrol.vcontroliot.adapter.FtpImageAdapter;
import com.vcontrol.vcontroliot.log.Log;
import com.vcontrol.vcontroliot.model.FtpImageModel;
import com.vcontrol.vcontroliot.util.ConfigParams;
import com.vcontrol.vcontroliot.util.EventNotifyHelper;
import com.vcontrol.vcontroliot.util.FTPManager;
import com.vcontrol.vcontroliot.util.ServiceUtils;
import com.vcontrol.vcontroliot.util.ToastUtil;
import com.vcontrol.vcontroliot.util.UiEventEntry;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by linxi on 2017/4/18.
 */

public class FTPImageActivity extends BaseActivity implements EventNotifyHelper.NotificationCenterDelegate
{

    private static final String TAG = FTPImageActivity.class.getSimpleName();
    private GridView ftpGridView;
    private List<FtpImageModel> imageList = new ArrayList<>();
    private FtpImageAdapter imageAdapter;
    private com.nostra13.universalimageloader.core.ImageLoader imageLoader;
    private TextView backTextView;
    private TextView titleTextView;

    private String currentPath = "/";

    @Override
    public int getLayoutView()
    {
        return R.layout.activity_ftp_image;
    }

    @Override
    public void initViewData()
    {
        showToolbar();
        setTitleName(getString(R.string.Image_query));
        setTitleMain(getString(R.string.Return));
    }

    @Override
    public void initComponentViews()
    {
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.READ_IMAGE_LIST);
        EventNotifyHelper.getInstance().addObserver(this, UiEventEntry.UPDATE_IMAGE_LIST);
        VcontrolApplication.getInstance().addActivity(this);
        ToastUtil.setCurrentContext(getApplication());
        ftpGridView = (GridView) findViewById(R.id.ftp_image_gridview);
        backTextView = (TextView) findViewById(R.id.back_textview);
        titleTextView = (TextView) findViewById(R.id.title_folder_textview);

        backTextView.setOnClickListener(this);

        imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(FTPImageActivity.this));
        initData();

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.READ_IMAGE_LIST);
        EventNotifyHelper.getInstance().removeObserver(this, UiEventEntry.UPDATE_IMAGE_LIST);
    }

    private void initData()
    {

        imageAdapter = new FtpImageAdapter(getApplicationContext(), imageLoader);
        ftpGridView.setAdapter(imageAdapter);

        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.empty_view, null);
        ftpGridView.setEmptyView(view);

        File image = new File(ConfigParams.FtpImagePath);
        if (!image.exists())
        {
            image.mkdir();
        }


        requstData("/");

//        ServiceUtils.getSocketThreads().execute(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                try
//                {
//
//                    Log.info(TAG, "initData::");
//                    FTPManager.getFtpManager().connect();
//
//                    FTPFile[] fileList = FTPManager.getFtpManager().getFtpFileList("/");
//                    Log.info(TAG, fileList.length + "");
////                    if (fileList.length > 0)
////                    {
////                        Log.info(TAG, fileList[0].getName());
////                        FTPManager.getFtpManager().downloadFile(ConfigParams.FtpImagePath, "/" + fileList[0].getName());
////                    }
//                    for (FTPFile file : fileList)
//                    {
//                        FtpImageModel ftpImageModel = new FtpImageModel();
//                        ftpImageModel.setName(file.getName());
//                        ftpImageModel.setFile(file.isFile());
//                        imageList.add(ftpImageModel);
//                    }
//                    EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.READ_IMAGE_LIST);
//                } catch (Exception e)
//                {
//                    Log.exception(TAG, e);
//                }
//            }
//        });


        ftpGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {


                final FtpImageModel ftpImageModel = imageList.get(position);

                if (ftpImageModel.isFile())
                {
                    ServiceUtils.getSocketThreads().execute(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {

                                String imagePath = ConfigParams.FtpImagePath + ftpImageModel.getName();
                                File imageFile = new File(imagePath);
                                if (imageFile.exists())
                                {
                                    ToastUtil.showToastLong(getString(R.string.Image_already_exists));
                                    return;
                                }

                                Log.info(TAG, "position:" + position + ",imagePath:" + imagePath);
                                FTPManager.getFtpManager().downloadFile(ConfigParams.FtpImagePath, currentPath + "/" + ftpImageModel.getName(),getApplicationContext());
                                EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.UPDATE_IMAGE_LIST, position, imagePath);
                            } catch (Exception e)
                            {
                                Log.exception(TAG, e);
                            }

                        }
                    });
                }
                else
                {
                    currentPath += "/" + ftpImageModel.getName();
                    requstData(currentPath);
                }


            }
        });
    }

    private void requstData(final String path)
    {
        imageList.clear();
        currentPath = path;
        if ("/".equals(path))
        {
            backTextView.setVisibility(View.INVISIBLE);
        }
        else
        {
            backTextView.setVisibility(View.VISIBLE);
        }

        titleTextView.setText(path);
        ServiceUtils.getSocketThreads().execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {

                    Log.info(TAG, "initData::");
                    FTPManager.getFtpManager().connect();

                    FTPFile[] fileList = FTPManager.getFtpManager().getFtpFileList(path);
                    Log.info(TAG, fileList.length + "");
//                    if (fileList.length > 0)
//                    {
//                        Log.info(TAG, fileList[0].getName());
//                        FTPManager.getFtpManager().downloadFile(ConfigParams.FtpImagePath, "/" + fileList[0].getName());
//                    }
                    for (FTPFile file : fileList)
                    {
                        FtpImageModel ftpImageModel = new FtpImageModel();
                        ftpImageModel.setName(file.getName());
                        ftpImageModel.setFile(file.isFile());
                        imageList.add(ftpImageModel);
                    }
                    EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.READ_IMAGE_LIST);
                } catch (Exception e)
                {
                    Log.exception(TAG, e);
                }
            }
        });
    }


    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        return false;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.title_main:
                onBackPressed();
                break;
            case R.id.back_textview:
                String p = currentPath.substring(0, currentPath.lastIndexOf("/"));
                requstData(p);
                break;
            default:
                break;
        }
    }

    @Override
    public void didReceivedNotification(int id, Object... args)
    {
        if (id == UiEventEntry.READ_IMAGE_LIST)
        {
            if (imageAdapter != null && ftpGridView != null)
            {
                imageAdapter.updateData(imageList);
            }
            else
            {
                imageAdapter = new FtpImageAdapter(getApplicationContext(), imageLoader);
                ftpGridView.setAdapter(imageAdapter);
            }
        }
        else if (id == UiEventEntry.UPDATE_IMAGE_LIST)
        {
            if (imageAdapter != null && ftpGridView != null)
            {
                int position = (int) args[0];
                String imagePath = (String) args[1];
                Log.info(TAG, "position:" + position + ",imagePath:" + imagePath);
                View childAt = ftpGridView.getChildAt(position
                        - ftpGridView.getFirstVisiblePosition());
                imageAdapter.updateItem(position, childAt, imagePath);

            }
        }
    }


}
