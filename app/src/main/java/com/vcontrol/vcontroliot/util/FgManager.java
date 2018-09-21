package com.vcontrol.vcontroliot.util;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import com.vcontrol.vcontroliot.VcontrolApplication;
import com.vcontrol.vcontroliot.log.Log;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 说明：Fragment管理工具类
 */
public class FgManager
{
    private static final String TAG = FgManager.class.getSimpleName();
    private static final int singleTop = 0;
    private static final int singleTask = 1;
    private static final int standard = 2;
    private static final int stackMode = singleTask;
    // 资源栈，Fragment单例
    private static Map<String, Fragment> fragmentsStack = new ConcurrentHashMap<String, Fragment>();
    // 回退栈，layout ID：Fragment栈
    private static Map<Integer, CopyOnWriteArrayList<Fragment>> fragmentsStackForBack = new ConcurrentHashMap<Integer, CopyOnWriteArrayList<Fragment>>();
    /**
     * 消息队列
     */
    private static ConcurrentLinkedQueue<QueueData> uiQueue = new ConcurrentLinkedQueue<QueueData>();
    // 跳转界面
    private static int UI_TURNTOFRAGMENTSTACK = 1;
    // 回退界面
    private static int UI_BACKTOPREFRAGMENT = 2;
    // 删除界面
    private static int UI_REMOVEFRAGMENTFROMBACKSTACK = 3;
    // 清空界面
    private static int UI_CLEARFRAGMENTSTACK = 4;
    private static long uiLastTime;
    // 界面跳转操作handler
    private static Handler handler = new Handler();
    // 界面执行
    private static Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            try
            {
                if ((SystemClock.elapsedRealtime() - uiLastTime) < 50)
                {
                    // 频繁界面跳转时间隔执行，避免界面崩溃
                    VcontrolApplication.applicationHandler.postDelayed(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            handleUiEvent();
                        }
                    }, 50);
                }
                else
                {
                    VcontrolApplication.applicationHandler.post(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            handleUiEvent();
                        }
                    });
                }
                uiLastTime = SystemClock.elapsedRealtime();
            }
            catch (Exception e)
            {
                Log.exception(TAG, e);
            }
        }
    };

    /**
     * 处理UI界面跳转事件
     */
    private static void handleUiEvent()
    {
        try
        {
            QueueData queueData = uiQueue.poll();
            if (queueData == null)
            {
                return;
            }
            int ui = queueData.ui;
            if (ui == UI_TURNTOFRAGMENTSTACK)
            {
                turnToFragmentStackQueue(queueData.fm, queueData.containerViewId, queueData.toFragmentClass, queueData.args);
            }
            else if (ui == UI_BACKTOPREFRAGMENT)
            {
                backToPreFragmentQueue(queueData.fm, queueData.containerViewId, queueData.args, queueData.backCount);
            }
            else if (ui == UI_REMOVEFRAGMENTFROMBACKSTACK)
            {
                removeFragmentFromBackStackQueue(queueData.fm, queueData.containerViewId, queueData.removeFragmentClass);
            }
            else if (ui == UI_CLEARFRAGMENTSTACK)
            {
                if (queueData.containerViewIds == null)
                {
                    queueData.containerViewIds = new int[1];
                    queueData.containerViewIds[0] = queueData.containerViewId;
                }
                clearFragmentStackQueue(queueData.fm, queueData.containerViewIds, queueData.callback);
            }
            // 清空引用，避免内存无法释放
            queueData.ui = 0;
            queueData.toFragmentClass = null;
            queueData.containerViewId = 0;
            queueData.containerViewIds = null;
            queueData.args = null;
            queueData.fm = null;
            queueData.callback = null;
            queueData = null;
        }
        catch (Exception e)
        {
            Log.exception(TAG, e);
        }
    }

    /**
     * 方法说明 :跳转到Fragment 加入栈中
     *
     * @param fm
     * @param containerViewId
     * @param toFragmentClass
     * @param args
     */
    private static boolean turnToFragmentStackQueue(FragmentManager fm, int containerViewId, Class<? extends Fragment> toFragmentClass, Bundle args)
    {
        if (Build.VERSION.SDK_INT >= 17)
        {
            if (fm.isDestroyed())
            {
                Log.debug(TAG, "turnToFragmentStackQueue::fmDestroyed");
                return false;
            }
        }
        try
        {
            // 初始资源栈中无论有没有都加到对应ID的资源栈中
            Fragment toFragment = getFragment(toFragmentClass);
            if (toFragment == null)
            {
                Log.debug(TAG, "turnToFragmentStackQueue::toFragment null.");
                return false;
            }
            CopyOnWriteArrayList<Fragment> fragmentList = null;
            if (fragmentsStackForBack.containsKey(containerViewId))
            {
                fragmentList = fragmentsStackForBack.get(containerViewId);
            }
            else
            {
                fragmentList = new CopyOnWriteArrayList<Fragment>();
                fragmentsStackForBack.put(containerViewId, fragmentList);
            }
            switch (stackMode)
            {
                case singleTask:
                    if (fragmentList.contains(toFragment))
                    {
                        fragmentList.remove(toFragment);
                    }
                    fragmentList.add(toFragment);
                    break;
                case standard:
                    fragmentList.add(toFragment);
                    break;
                case singleTop:
                    int size = fragmentList.size();
                    if (!(size > 0 && fragmentList.get(size - 1) == toFragment))
                    {
                        fragmentList.add(toFragment);
                    }
                    break;
            }
            // 如果有参数传递，
            putArguments(toFragment, args);

            boolean canReplace = !toFragment.isVisible();
            Log.debug(TAG, "turnToFragmentStackQueue::canReplace:" + canReplace + ", fragment:" + toFragmentClass.getSimpleName());
            if (canReplace)
            {
                FragmentTransaction ft = fm.beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(containerViewId, toFragment);
                ft.commitAllowingStateLoss();
                fm.executePendingTransactions();
            }
            else
            {// 界面显示
                EventNotifyHelper.getInstance().postNotification(UiEventEntry.NOTIFY_BUNDLE, args);
            }
            return canReplace;
        }
        catch (Exception e)
        {
            Log.exception(TAG, e);
        }
        return false;
    }

    /**
     * 有参数传递参数，无参数清空
     */
    private static void putArguments(Fragment toFragment, Bundle args)
    {
        if (args == null || args.isEmpty())
        {// 无参数
            if (toFragment.getArguments() != null)
            {
                toFragment.setArguments(null);
            }
        }
        else
        {// 有参数传递
            if (toFragment != null && toFragment.getArguments() != null)
            {
//                toFragment.getArguments().clear();
                toFragment.getArguments().putAll(args);
            }
            else
            {
                toFragment.setArguments(args);
            }
        }
    }

    /**
     * 跳转到上一个Fragment
     */
    private static boolean backToPreFragmentQueue(FragmentManager fm, int containerViewId, Bundle args, int backCount)
    {
        if (Build.VERSION.SDK_INT >= 17)
        {
            if (fm.isDestroyed())
            {
                Log.debug(TAG, "backToPreFragmentQueue::fmDestroyed");
                return false;
            }
        }
        try
        {
            CopyOnWriteArrayList<Fragment> fragments = fragmentsStackForBack.get(containerViewId);
            if (fragments == null || fragments.size() <= backCount)
            {
                Log.debug(TAG, "backToPreFragmentQueue::returnSize:" + fragments.size());
                return false;
            }

            Fragment fromFragment = null;
            int size;
            for (int i = 0; i < backCount; i++)
            {
                size = fragments.size();
                fromFragment = fragments.get(size - 2);
                if (fromFragment == null)
                {
                    Log.debug(TAG, "backToPreFragmentQueue::fromFragment null.");
                    return false;
                }
                fragments.remove(size - 1);
            }

            // 如果有参数传递，
            if ((args != null) && (!args.isEmpty()))
            {
                if (fromFragment.getArguments() != null)
                {
                    fromFragment.getArguments().clear();
                    fromFragment.getArguments().putAll(args);
                }
                else
                {
                    fromFragment.setArguments(args);
                }
            }

            boolean canReplace = !fromFragment.isVisible();
            Log.debug(TAG, "backToPreFragmentQueue::canReplace:" + canReplace + ", fragment:" + fromFragment.getClass().getSimpleName());
            if (canReplace)
            {
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(containerViewId, fromFragment);
                ft.commitAllowingStateLoss();
                fm.executePendingTransactions();
            }
            return canReplace;
        }
        catch (Exception e)
        {
            Log.exception(TAG, e);
        }
        return false;
    }

    /**
     * 删除一个栈里面的Fragment
     */
    private static void removeFragmentFromBackStackQueue(FragmentManager fm, int containerViewId, Class<? extends Fragment>[] removeFragmentClass)
    {
        try
        {
            if (Build.VERSION.SDK_INT >= 17)
            {
                if (fm.isDestroyed())
                {
                    Log.debug(TAG, "removeFragmentFromBackStackQueue::fmDestroyed");
                    return;
                }
            }
            if (removeFragmentClass == null || removeFragmentClass.length == 0)
            {
                Log.debug(TAG, "removeFragmentFromBackStackQueue::fragment null.");
                return;
            }
            ArrayList<Fragment> tempFragmentList = new ArrayList<Fragment>();
            Fragment fragment = null;
            for (Class<? extends Fragment> tempFragmentClass : removeFragmentClass)
            {
                Log.debug(TAG, "removeFragmentFromBackStackQueue::removeFragmentClass:" + tempFragmentClass.getSimpleName());
                fragment = fragmentsStack.get(tempFragmentClass.getSimpleName() + VcontrolApplication.getCurrentContext().getClass().getSimpleName());
                tempFragmentList.add(fragment);
            }
            CopyOnWriteArrayList<Fragment> fragmentList = fragmentsStackForBack.get(containerViewId);
            if (fragmentList == null)
            {
                Log.debug(TAG, "removeFragmentFromBackStackQueue::fragmentList null.");
                return;
            }
            fragmentList.removeAll(tempFragmentList);
            int size = fragmentList.size();
            FragmentTransaction ft = fm.beginTransaction();
            if (size >= 1)
            {
                Fragment fg = fragmentList.get(size - 1);
                boolean canReplace = fg.isVisible();
                Log.debug(TAG, "removeFragmentFromBackStackQueue::canReplace:" + canReplace + ", fragment:" + fg.getClass().getSimpleName());
                if (!fg.isVisible())
                {
                    ft.replace(containerViewId, fg);
                    ft.commitAllowingStateLoss();
                    fm.executePendingTransactions();
                }
            }
            else
            {
                Log.debug(TAG, "removeFragmentFromBackStackQueue::returnSize:" + size);
            }
        }
        catch (Exception e)
        {
            Log.exception(TAG, e);
        }
    }

    /**
     * 方法说明 :清除某个容器栈中fragment
     *
     * @param fm
     * @param containerViewIds
     * @param callback         是否删除初始栈中fragment
     */
    private static void clearFragmentStackQueue(FragmentManager fm, int[] containerViewIds, Callback callback)
    {
        Log.debug(TAG, "clearFragmentStack:: containerViewIds:" + containerViewIds);
        try
        {
            FragmentTransaction ft = fm.beginTransaction();
            CopyOnWriteArrayList<Fragment> fragmentList = null;
            for (int containerViewId : containerViewIds)
            {
                fragmentList = fragmentsStackForBack.get(containerViewId);
                if (fragmentList != null)
                {
                    for (Fragment fragment : fragmentList)
                    {
                        ft.remove(fragment);
                        fragment = null;
                    }
                    fragmentList.clear();
                }
            }
            ft.commitAllowingStateLoss();
            fm.executePendingTransactions();
            if (callback != null)
            {
                callback.callback();
            }
        }
        catch (Exception e)
        {
            Log.exception(TAG, e);
        }
    }

    /**
     * 加入资源栈中，初始化Fragment，可以通过EventNotifyHelper更新fragment的数据信息。但界面信息比如控件尚未初始化，不要使用。
     * <p/>
     * 在界面显示时，把引用数据更新界面即可。
     */
    public static Fragment getFragment(Class<? extends Fragment> toFragmentClass)
    {
        String toTag = toFragmentClass.getSimpleName();
//        int toTagHashCode = toTag.hashCode();
//        Log.debug(TAG, "getFragment:: toTag:" + toTag + ",toTagHashCode = " + toTagHashCode);
        Fragment toFragment = fragmentsStack.get(toTag + VcontrolApplication.getCurrentContext().getClass().getSimpleName());
        // 如果这个fragment不在容器中，存入容器中
        if (toFragment == null)
        {
            try
            {
                toFragment = toFragmentClass.newInstance();
                fragmentsStack.put(toTag + VcontrolApplication.getCurrentContext().getClass().getSimpleName(), toFragment);
            }
            catch (InstantiationException e)
            {
                Log.exception(TAG, e);
                return null;
            }
            catch (IllegalAccessException e)
            {
                Log.exception(TAG, e);
                return null;
            }
        }
        return toFragment;
    }

    /**
     * 删除栈里面一个或多个Fragment
     */
    public static void removeFragmentFromBackStack(FragmentManager fm, int containerViewId, Class<? extends Fragment>... removeFragmentClass)
    {
        Log.debug(TAG, "removeFragmentFromBackStack::removeFragmentClass:");
        QueueData queueData = new QueueData();
        queueData.ui = UI_REMOVEFRAGMENTFROMBACKSTACK;
        queueData.fm = fm;
        queueData.containerViewId = containerViewId;
        queueData.removeFragmentClass = removeFragmentClass;
        uiQueue.add(queueData);
        handler.post(runnable);// 执行
    }

    /**
     * 方法说明 :跳转到Fragment 加入栈中
     *
     * @param fm
     * @param containerViewId
     * @param toFragmentClass
     * @param args
     */
    public static boolean turnToFragmentStack(FragmentManager fm, int containerViewId, Class<? extends Fragment> toFragmentClass, Bundle args)
    {
        Log.debug(TAG, "turnToFragmentStack::toFragmentClass:" + toFragmentClass.getSimpleName());
        Fragment toFragment = getFragment(toFragmentClass);
        if (toFragment != null && toFragment.isVisible())
        {// 界面已显示
            // 如果有参数传递，
            EventNotifyHelper.getInstance().postNotification(UiEventEntry.NOTIFY_BUNDLE, args);
        }
        else
        {// 打开界面
            QueueData queueData = new QueueData();
            queueData.ui = UI_TURNTOFRAGMENTSTACK;
            queueData.fm = fm;
            queueData.containerViewId = containerViewId;
            queueData.toFragmentClass = toFragmentClass;
            queueData.args = args;
            uiQueue.add(queueData);
        }
        return handler.post(runnable);// 执行
    }

    /**
     * 跳转到上一个Fragment
     */
    public static boolean backToPreFragment(FragmentManager fm, int containerViewId, Bundle args, int backCount)
    {
        Log.debug(TAG, "backToPreFragment::");
        QueueData queueData = new QueueData();
        queueData.ui = UI_BACKTOPREFRAGMENT;
        queueData.fm = fm;
        queueData.containerViewId = containerViewId;
        queueData.args = args;
        queueData.backCount = backCount;
        uiQueue.add(queueData);
        return handler.post(runnable);// 执行
    }

    /**
     * 方法说明 :清除某个容器栈中fragment
     *
     * @param fm
     * @param containerViewId
     */
    public static void clearFragmentStack(FragmentManager fm, int containerViewId)
    {
        Log.debug(TAG, "clearFragmentStack::");
        QueueData queueData = new QueueData();
        queueData.ui = UI_CLEARFRAGMENTSTACK;
        queueData.fm = fm;
        queueData.containerViewId = containerViewId;
        uiQueue.add(queueData);
        handler.post(runnable);// 执行
    }

    /**
     * 方法说明 :清除某个容器栈中fragment
     *
     * @param fm
     * @param containerViewIds
     * @param callback         执行完毕回调
     */
    public static void clearFragmentStack(FragmentManager fm, int[] containerViewIds, Callback callback)
    {
        Log.debug(TAG, "clearFragmentStack::containerViewIds:");
        QueueData queueData = new QueueData();
        queueData.ui = UI_CLEARFRAGMENTSTACK;
        queueData.fm = fm;
        queueData.containerViewIds = containerViewIds;
        queueData.callback = callback;
        uiQueue.add(queueData);
        handler.post(runnable);// 执行
    }

    public interface Callback
    {
        void callback();
    }

    static class QueueData
    {
        int ui;
        FragmentManager fm;
        int containerViewId;
        int[] containerViewIds;
        Class<? extends Fragment> toFragmentClass;
        Class<? extends Fragment>[] removeFragmentClass;
        Bundle args;
        Callback callback;
        int backCount;
    }

}
