package com.vcontrol.vcontroliot.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.vcontrol.vcontroliot.R;
import com.vcontrol.vcontroliot.VcontrolApplication;
import com.vcontrol.vcontroliot.log.Log;
import com.vilyever.socketclient.SocketClient;
import com.vilyever.socketclient.helper.SocketClientDelegate;
import com.vilyever.socketclient.helper.SocketClientSendingDelegate;
import com.vilyever.socketclient.helper.SocketPacket;
import com.vilyever.socketclient.helper.SocketResponsePacket;
import com.vilyever.socketclient.util.CharsetUtil;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class SocketUtil
{

    private static final String TAG = SocketUtil.class.getSimpleName();
    private static Socket socket = null;

    private int search = 0 ;

    private static DataOutputStream runTimeDos;
    private static DataOutputStream dos;
    private static BufferedReader reader;
    private static PrintWriter writer;
    private static DataInputStream dis;
    private static FileOutputStream imgFos;
    private FileOutputStream imageFos;
    private FileOutputStream historyFos;
    private boolean isReSend = true;

    private static long beforeTime = 0;
//被覆盖了
    private static void init(final String ip, final int port)
    {

        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    Log.info(TAG, "init::ip:" + ip + ",port:" + port);
                    socket = new Socket(ip, port);
                    // 设置超时
                    socket.setSoTimeout(1500);

                    EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.CONNCT_OK);
                } catch (Exception e)
                {
                    EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.CONNCT_FAIL);
                    Log.exception(TAG, e);
                }
            }
        }).start();
    }

    /**
     * 获得socket实例，建立连接
     *
     * @param ip
     * @param port
     * @return
     */
    public static Socket connectServer(String ip, int port)
    {
        closeSocket();
        if (socket == null)
        {
            init(ip, port);
        }
        return socket;
    }

    public static boolean isConnect()
    {
        if (socket != null)
        {
            return socket.isConnected() && !socket.isClosed();
        }
        return false;
    }

    /**
     * 发送数据
     *
     * @param content
     */
    public static void sendData(final String content)
    {
        if (TextUtils.isEmpty(content))
        {
            return;
        }

        if (System.currentTimeMillis() - beforeTime < 500)
        {
            return;
        }
        beforeTime = System.currentTimeMillis();

        Log.info(TAG, "sendData::content:" + content);
        ServiceUtils.getSocketThreads().execute(new Runnable()
        {
            @Override
            public void run()
            {
                if (socket != null)
                {
                    try
                    {

//                        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
//                                socket.getOutputStream())), true);
////                        writer.println(content);
//                        writer.write(content);
                        dos = new DataOutputStream(socket.getOutputStream());
                        dos.writeUTF(content + "\r\n");
//                        dos.writeBytes(content + "\r\n");
                        dos.flush();

                        readData(content);
                    } catch (Exception e)
                    {
                        ToastUtil.showToastLong(VcontrolApplication.getCurrentContext().getString(R.string.Data_failed));
                        ProgressBarUtil.dismissProgressDialog();
                        EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.CONNCT_AGAIN);
                        Log.exception(TAG, e);
                    } finally
                    {
                    }

                }
                else
                {
                    EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.CONNCT_AGAIN);
                    ToastUtil.showToastLong("设备未连接！！");
                }
            }
        });

    }

    /**
     * 发送实时数据的方法
     *
     * @param content
     */
    public static void sendRunTimeData(final String content)
    {
        if (TextUtils.isEmpty(content))
        {
            return;
        }

        ServiceUtils.getSocketThreads().execute(new Runnable()
        {
            @Override
            public void run()
            {
                Log.info(TAG, "sendRunTimeData::content:" + content);
                if (socket != null)
                {
                    try
                    {
                        runTimeDos = new DataOutputStream(socket.getOutputStream());
                        runTimeDos.writeUTF(content + "\r\n");
                        runTimeDos.flush();
                    } catch (Exception e)
                    {
                        ToastUtil.showToastLong(VcontrolApplication.getCurrentContext().getString(R.string.Data_failed));
                        EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.CONNCT_AGAIN);
                        isReadImage = false;
                        ProgressBarUtil.dismissProgressDialog();
                        Log.exception(TAG, e);
                    }
                }
                else
                {
                    isReadImage = false;
                    EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.CONNCT_AGAIN);
                    ProgressBarUtil.dismissProgressDialog();
                    ToastUtil.showToastLong(VcontrolApplication.getCurrentContext().getString(R.string.device_not_connected));
                }
            }
        });
    }

    private static boolean isReadImage = true;
    /**
     * 功能：分包获取图片
     */
    private static Runnable runTimeRunnable = new Runnable()
    {

        @Override
        public void run()
        {

            isReadImage = true;
            Log.info(TAG, "runTimeRunnable::isReadImage:" + isReadImage);

            int temp = 0;
            String tempCurrent = "";
            // 上一包
            String before = "000";
            // 当前包数
            String current = "000";
            // 总包数
            String length = "";

            // RTU传过来的图片每包是 1047个字节
//            byte[] buffer = new byte[1047];
//
//
//            byte[] imageInfoBuffer = new byte[20];
//            byte[] imageBuffer = new byte[1024];
            int next = 0;

            File file = new File(ConfigParams.ImagePath + ConfigParams.ImageName);
            try
            {
                if (!file.exists())
                {
                    // 图片文件不存在 创建文件
                    file.createNewFile();
                }
                imgFos = new FileOutputStream(file);
            } catch (Exception e)
            {
                Log.debug(TAG, "file is empty!");
                Log.exception(TAG, e);
                return;
            }
            while (isReadImage)
            {
                if (socket != null)
                {
                    if (!socket.isClosed())
                    {

                        try
                        {
                            InputStream is = socket.getInputStream();
                            dis = new DataInputStream(is);
                            byte[] buffer = new byte[1047];


                            byte[] imageInfoBuffer = new byte[20];
                            byte[] imageBuffer = new byte[1024];

                            while ((next = dis.read(buffer)) > 0)
                            {
                                temp = 0;
                                Log.info(TAG, Arrays.toString(buffer));
                                // 跳过前19个字节
                                System.arraycopy(buffer, 20, imageBuffer, 0, 1024);
                                // 获取图片包信息
                                System.arraycopy(buffer, 0, imageInfoBuffer, 0, 20);
                                Log.info(TAG, "imageBuffer:" + imageBuffer.length + "，buffer:" + buffer.length);
                                // crc校验
                                imgFos.write(imageBuffer, 0, imageBuffer.length);
                                imgFos.flush();

                                byte[] lengthByte = new byte[10];
                                for (int i = 0; i < 7; i++)
                                {
                                    lengthByte[i] = imageInfoBuffer[i + 13];
                                }
                                String lengthS = new String(lengthByte);

                                current = lengthS.substring(0, 3);
                                length = lengthS.substring(4, 7);

                                Log.info(TAG, "currentLength:" + current);
                                Log.info(TAG, "length:" + length);

                                if (ServiceUtils.isGarbledCode(current))
                                {// 如果当前包有乱码，再次获取上一包数据
                                    SocketUtil.sendRunTimeData(ConfigParams.ReadImage + before);
                                }
                                else
                                {
                                    before = current;
                                }

                                // 总包数等于当前包数
                                if (!TextUtils.isEmpty(current) && !TextUtils.isEmpty(length) && current.equals(length))
                                {
//                                    Log.info(TAG,Arrays.toString(buffer));
                                    EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.READ_IMAGE_SUCCESS);
                                    stopReadRunTimeData();
                                    return;
                                }

                                // 取完一包，在去获取下一包
                                if (!ServiceUtils.isGarbledCode(current))
                                {
                                    SocketUtil.sendRunTimeData(ConfigParams.ReadImage + current);
                                }

                            }
                        } catch (Exception e)
                        {
                            temp++;
                            if (temp < 3)
                            {
                                // 取完一包，在去获取下一包
                                if (!ServiceUtils.isGarbledCode(current))
                                {
                                    SocketUtil.sendRunTimeData(ConfigParams.ReadImage + current);

                                }
                            }
                            else
                            {
                                isReadImage = false;
                                ProgressBarUtil.dismissProgressDialog();
                                ToastUtil.showToastLong(VcontrolApplication.getCurrentContext().getString(R.string.Image_failed));
                                temp = 0;
                            }
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        isReadImage = false;
                        ProgressBarUtil.dismissProgressDialog();
                        ToastUtil.showToastLong(VcontrolApplication.getCurrentContext().getString(R.string.Image_failed));
                    }
                }

            }
        }
    };

    public static void startReadRunTimeData()
    {
        Log.info(TAG, "startReadRunTimeData::");
        File file = new File(ConfigParams.ImagePath + ConfigParams.ImageName);
        if (file.exists())
        {//获取图片之前，如果有图片就先删除
            file.delete();
        }
        ServiceUtils.getSocketThreads().execute(runTimeRunnable);
    }

    public static void stopReadRunTimeData()
    {
        Log.info(TAG, "stopReadRunTimeData::");
        isReadImage = false;
        ProgressBarUtil.dismissProgressDialog();
    }

    private static void readData(final String senData)
    {

        try
        {
            if (socket != null)
            {
                try
                {
//                    byte[] buffer = new byte[1024];
//                    Log.info(TAG, "readData::run:");
//                    socket.getInputStream().read(buffer);
//                    Log.info(TAG,Arrays.toString(buffer));
//                    DataInputStream disread = new DataInputStream(socket.getInputStream());
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String result = "";
                    Log.info(TAG, "readData::senData:" + senData);
//                    Log.info(TAG, "readData::reader:" + reader.readLine());
//                    Log.info(TAG, "readData::disread:" + disread.readUTF());
                    while ((result = reader.readLine()) != null)
                    {
                        if (senData.contains(ConfigParams.ReadImage))
                        {// 如果请求的是图片回放，摄像头状态，通信状态不做处理
                        }
                        else
                        {
                            Log.info(TAG, "result:" + result);
                            if (ServiceUtils.isGarbledCode(result))
                            {
                                return;
                            }
                            if (ConfigParams.SetBatteryHigh.equals(senData) || ConfigParams.SetBatteryLow.equals(senData) || ConfigParams.ReadBatteryHighStatus.equals(senData) || ConfigParams.ReadBatteryLowStatus.equals(senData))
                            {//AD电压采集单独处理
                                EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.READ_AD_LV, result);
                            }
                            else
                            {
                                if (result != null && result.toUpperCase().contains("OK"))
                                {
                                    EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.READ_RESULT_OK, result, senData);
                                }
                                else if (result != null && result.toUpperCase().contains("ERROR"))
                                {
                                    EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.READ_RESULT_ERROR, result, senData);
                                }
                                else
                                {
                                    EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.READ_DATA, result, senData);
                                }
                            }
                        }
                    }

                    Log.info(TAG, "finish::");
                } catch (Exception e)
                {
                    Log.exception(TAG, e);
                }
            }
            else
            {
                ToastUtil.showToastLong(VcontrolApplication.getCurrentContext().getString(R.string.device_not_connected));
            }
        } catch (Exception e)
        {
            Log.exception(TAG, e);
        }
    }

    /**
     * 关闭Socket连接
     */
    public static void closeSocket()
    {
        Log.info(TAG, "closeSocket::");
        ServiceUtils.getSocketThreads().execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if (reader != null)
                    {
                        reader.close();
                    }
                    if (writer != null)
                    {
                        writer.close();
                    }
                    if (dis != null)
                    {
                        dis.close();
                    }
                    if (runTimeDos != null)
                    {
                        runTimeDos.close();
                    }
                    if (dos != null)
                    {
                        dos.close();
                    }
                    if (imgFos != null)
                    {
                        imgFos.close();
                    }
                    if (socket != null)
                    {
                        socket.close();
                        socket = null;
                    }

                } catch (Exception e)
                {
                    Log.exception(TAG, e);
                }
            }
        });
    }


    /**
     * SocketClient 实例测试
     */

    private static SocketUtil socketUtil = null;
    private SocketClient socketClient;
    private boolean isDownload = false;

    private String send;
    private byte[] sendByte;

    public static SocketUtil getSocketUtil()
    {

        if (socketUtil == null)
        {
            socketUtil = new SocketUtil();
        }
        return socketUtil;
    }


    public void connectRTU(String ip, int port)
    {
        try
        {
            Log.info(TAG, "connectRTU::");
            closeSocketClient();
            socketClient = new SocketClient();
            // 远程端IP地址
            socketClient.getAddress().setRemoteIP(ip);
            // 远程端端口号
            socketClient.getAddress().setRemotePort(port);
            // 连接超时时长，单位毫
            socketClient.getAddress().setConnectionTimeout(15 * 1000);
            // 设置编码为UTF-8
            socketClient.setCharsetName(CharsetUtil.UTF_8);

            socketClient.registerSocketClientDelegate(new SocketClientDelegate()
            {
                @Override
                public void onConnected(SocketClient client)
                {
                    Log.info(TAG, "onConnected::");
                    EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.CONNCT_OK);
                }

                @Override
                public void onDisconnected(SocketClient client)
                {
                    Log.info(TAG, "onDisconnected::");
                    ProgressBarUtil.dismissProgressDialog();
                    closeSocketClient();
                    EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.CONNCT_FAIL);
                }

                @Override
                public void onResponse(SocketClient client, @NonNull SocketResponsePacket responsePacket)
                {
                    handReceData(responsePacket);
                }
            });


            socketClient.registerSocketClientSendingDelegate(new SocketClientSendingDelegate()
            {
                @Override
                public void onSendPacketBegin(SocketClient client, SocketPacket packet)
                {

                }

                @Override
                public void onSendPacketCancel(SocketClient client, SocketPacket packet)
                {

                }

                @Override
                public void onSendPacketEnd(SocketClient client, SocketPacket packet)
                {
                    reSendContent();
                }

                @Override
                public void onSendPacketProgress(SocketClient client, SocketPacket packet, float progress)
                {
                }
            });
            socketClient.connect();
        } catch (Exception e)
        {
            Log.exception(TAG, e);
        }

    }

    private void reSendContent()
    {
        if (socketClient == null)
        {
            return;
        }

        if (send.contains(ConfigParams.RDDATA) || send.contains(ConfigParams.RDDATAEND) || send.contains(ConfigParams.ReadImage) || isDownload )
        {
            //2秒未收到重复发送数据
            VcontrolApplication.applicationHandler.removeCallbacks(reSendRunnable);
            VcontrolApplication.applicationHandler.postDelayed(reSendRunnable, UiEventEntry.TIME);
        }

    }


    private Runnable reSendRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (isReSend)
            {
                if (isDownload)
                {
                    if (sendByte == null || sendByte.length == 0)
                    {
                        return;
                    }
                    sendContent(sendByte, true);
                }

                else
                {
                    if (TextUtils.isEmpty(send))
                    {
                        return;
                    }
                    sendContent(send);
                }
            }
        }
    };


    private void handReceData(@NonNull SocketResponsePacket responsePacket)
    {
        Log.info(TAG, "handReceData::");
        String data = responsePacket.getMessage();
        String[] res = data.split("\r\n");
        byte[] data1 = responsePacket.getData();

//        Log.info(TAG, "data:" + data);

        if (isDownload)
        {
            isReSend = false;
            for (String result : res)
            {
                Log.info(TAG, result);
                EventNotifyHelper.getInstance().postNotification(UiEventEntry.DOWNLOADING, result);
                EventNotifyHelper.getInstance().postNotification(UiEventEntry.SetAllConfigInfo, result);

            }
        }
        else
        {

            if (send.contains(ConfigParams.RDDATATIME) || send.contains(ConfigParams.RDDATA))
            {// 请求历史文件
                isReSend = false;
                receiveHistory(data1);
            }
            else if (send.contains(ConfigParams.ReadImage))
            {// 请求图片
                isReSend = false;
                receiveImage(data1);
            }
            else
            {
                for (String result : res)
                {
                    if (ServiceUtils.isGarbledCode(result))
                    {
//                        return;
                    }
                    Log.info(TAG, result);
                    if (ConfigParams.SetBatteryHigh.equals(send) || ConfigParams.SetBatteryLow.equals(send) || ConfigParams.ReadBatteryHighStatus.equals(send) || ConfigParams.ReadBatteryLowStatus.equals(send))
                    {//AD电压采集单独处理
                        EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.READ_AD_LV, result);
                    }
                    else
                    {
                        if (result != null && result.toUpperCase().contains("OK"))
                        {
                            EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.READ_RESULT_OK, result, send);
                        }
                        else if (result != null && result.toUpperCase().contains("ERROR"))
                        {
                            EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.READ_RESULT_ERROR, result, send);
                        }
                        else if (result != null && result.contains("Not Started"))
                        {// System Not Started
                            ToastUtil.showToastLong(VcontrolApplication.getCurrentContext().getString(R.string.Device_again_later));
                        }
                        else
                        {
                            EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.READ_DATA, result, send);
                            if (search == UiEventEntry.TAB_SEARCH_CAMERA){

                                return;
                        }else {
                                if (search == UiEventEntry.TAB_SEARCH_GPRS)
                                {
                                    return;
                                }else {
                                    ToastUtil.showToastLong(VcontrolApplication.getCurrentContext().getString(R.string.Get_data_successfully));
                                }


                            }

                        }
                    }
                }
            }
        }
    }


    /**
     * 发送数据
     *
     * @param content
     */
    public void sendContent(String content)
    {
        if (TextUtils.isEmpty(content))
        {
            return;
        }
        Log.info(TAG, "content:" + content);
        if (socketClient == null)
        {
            return;
        }
        this.send = content;
        isReSend = true;

        socketClient.sendString(content + "\r\n");


    }



    public void setDownload(boolean isDownload)
    {
        this.isDownload = isDownload;
    }




    /**
     * 发送数据
     *
     * @param data
     */
    public void sendContent(byte[] data, boolean isDownload)
    {
        if (data == null)
        {
            return;
        }
        if (socketClient == null)
        {
            return;
        }
        Log.info(TAG, "sendContent::data:");
        this.isDownload = isDownload;
        this.sendByte = data;
        isReSend = true;
        socketClient.sendData(data );
    }




    /**
     * 关闭Socket连接
     */
    public void closeSocketClient()
    {
        if (socketClient != null)
        {
            socketClient.disconnect();
            socketClient = null;
        }
    }

    /**
     * Socket是否连接
     */
    public boolean isConnected()
    {
        if (socketClient != null)
        {
            return socketClient.isConnected();
        }
        return false;
    }


    public void startReceImage()
    {
        try
        {
            File file = new File(ConfigParams.ImagePath + ConfigParams.ImageName);
            if (!file.exists())
            {
                // 图片文件不存在 创建文件
                file.createNewFile();
            }
            imageFos = new FileOutputStream(file);

        } catch (Exception e)
        {
            Log.exception(TAG, e);
        }
    }


    public void startReceHostory()
    {
        try
        {
            if (historyFos != null)
            {
                return;
            }

            Log.info(TAG, "startReceHostory::");
            File file = new File(ConfigParams.ImagePath + ServiceUtils.getStringDate() + ConfigParams.HistoryName);
            if (!file.exists())
            {
                // 图片文件不存在 创建文件
                file.createNewFile();
            }
            historyFos = new FileOutputStream(file);

        } catch (Exception e)
        {
            Log.exception(TAG, e);
        }
    }

    public void stopReceHistory()
    {
        Log.info(TAG, "stopReceHistory::");

        try
        {
            if (historyFos != null)
            {
                historyFos.close();
                historyFos = null;
            }
        } catch (Exception e)
        {
            Log.exception(TAG, e);
        }

    }

    public void stopReceImages()
    {
        Log.info(TAG, "stopReceImages::");

        try
        {
            if (imageFos != null)
            {
                imageFos.close();
            }
            ProgressBarUtil.dismissProgressDialog();
        } catch (Exception e)
        {
            Log.exception(TAG, e);
        }

    }


    private void receiveImage(byte[] buffer)
    {
        try
        {
            if (imageFos == null)
            {
                return;
            }
            // 上一包
            String before = "000";
            // 当前包数
            String current = "000";
            // 总包数
            String length = "";

            int imageSize = buffer.length - 23;


            byte[] imageInfoBuffer = new byte[20];
            byte[] imageBuffer = new byte[imageSize];


            // 跳过前19个字节
            System.arraycopy(buffer, 20, imageBuffer, 0, imageSize);
            // 获取图片包信息、
            System.arraycopy(buffer, 0, imageInfoBuffer, 0, 20);

            Log.info(TAG, "imageBuffer:" + imageBuffer.length + "，buffer:" + buffer.length);
            Log.info(TAG, Arrays.toString(imageBuffer));
            imageFos.write(imageBuffer, 0, imageBuffer.length);
            imageFos.flush();

            byte[] lengthByte = new byte[10];
            for (int i = 0; i < 7; i++)
            {
                lengthByte[i] = imageInfoBuffer[i + 13];
            }
            String lengthS = new String(lengthByte);

            current = lengthS.substring(0, 3);
            length = lengthS.substring(4, 7);

            Log.info(TAG, "currentLength:" + current);
            Log.info(TAG, "length:" + length);

            if (ServiceUtils.isGarbledCode(current))
            {// 如果当前包有乱码，再次获取上一包数据
                sendContent(ConfigParams.ReadImage + before);
            }
            else
            {
                before = current;
            }

            // 总包数等于当前包数
            if (!TextUtils.isEmpty(current) && !TextUtils.isEmpty(length) && current.equals(length))
            {
                EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.READ_IMAGE_SUCCESS);
                stopReceImages();
                return;
            }

            // 取完一包，在去获取下一包
            if (!ServiceUtils.isGarbledCode(current))
            {
                sendContent(ConfigParams.ReadImage + current);
            }
            else
            {
                sendContent(ConfigParams.ReadImage + before);
            }
        } catch (Exception e)
        {
            ProgressBarUtil.dismissProgressDialog();
            ToastUtil.showToastLong(VcontrolApplication.getCurrentContext().getString(R.string.Image_failed));
            Log.exception(TAG, e);
        }

    }

    private void receiveHistory(byte[] buffer)
    {
        try
        {
            if (historyFos == null)
            {
                return;
            }

            String end = new String(buffer);
            Log.info(TAG, "end:" + end);
            // 读取文件完成
            if (!TextUtils.isEmpty(end) && end.contains(ConfigParams.RDDATAEND))
            {
                EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.READ_HISTORY_SUCCESS);
                stopReceHistory();
                VcontrolApplication.applicationHandler.removeCallbacks(reSendRunnable);
                return;
            }

            // 读取文件错误
            if (!TextUtils.isEmpty(end) && end.contains("error"))
            {
                EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.READ_RESULT_ERROR);
                stopReceHistory();
                VcontrolApplication.applicationHandler.removeCallbacks(reSendRunnable);
                return;
            }

            String current = "";
            String result = "";

            byte[] infoBuffer = new byte[13];
            int historySize = buffer.length - 14;
            byte[] historyBuffer = new byte[historySize];

            // 跳过前14个字节
            System.arraycopy(buffer, 14, historyBuffer, 0, historyBuffer.length);
            // 获取文件包信息、
            System.arraycopy(buffer, 0, infoBuffer, 0, 13);


            historyFos.write(historyBuffer, 0, historyBuffer.length);
            historyFos.flush();

            String lengthS = new String(infoBuffer);

            result = lengthS.substring(0, 6);
            current = lengthS.substring(7, 13);

            Log.info(TAG, "result:" + result);
            Log.info(TAG, "current:" + current);
            EventNotifyHelper.getInstance().postUiNotification(UiEventEntry.READ_CURRENT_HISTORY, current);


            // 取完一包，在去获取下一包
            if (!ServiceUtils.isGarbledCode(current))
            {
                sendContent(ConfigParams.RDDATA + current + " OK");
            }
        } catch (Exception e)
        {
//            ToastUtil.showToastLong("历史文件获取失败！");
            Log.exception(TAG, e);
        }

    }
}

