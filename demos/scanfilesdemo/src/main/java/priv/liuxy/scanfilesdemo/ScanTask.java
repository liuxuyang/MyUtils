package priv.liuxy.scanfilesdemo;

import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Liuxy on 2016/5/10.
 */
public class ScanTask implements Runnable {
    //
    private Handler mHandler;
    //
    private Message mMessage;
    //搜索目录
    private String mPath;
    //同步view刷新
    private final Semaphore mUpdateSemaphore;
    //暂停/继续信号
    private volatile boolean stopped = false;
    //锁
    private Lock mLock;
    //条件变量
    private Condition mCondition;

    public ScanTask(Handler handler, String path) {
        mHandler = handler;
        mPath = path;
        mUpdateSemaphore = new Semaphore(1);
        mLock = new ReentrantLock();
        mCondition = mLock.newCondition();
    }

    @Override
    public void run() {
        try {
            File file = new File(mPath);
            if (file.exists()) {
                scan(file);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 递归遍历目录树
     *
     * @param file
     * @throws InterruptedException
     */
    public void scan(File file) throws InterruptedException {
        if (file.isFile()) {
            if (stopped) {
                mLock.lock();
                try {
                    while (stopped) {
                        mCondition.await();
                    }
                } finally {
                    mLock.unlock();
                }
            }

            mMessage = mHandler.obtainMessage(MainActivity.WHAT_SACN_RESULT);
            mMessage.obj = file.getAbsolutePath();
            mUpdateSemaphore.acquire();
            mHandler.sendMessage(mMessage);
        } else if (file.isDirectory() && file.listFiles() != null) {
            for (File subFile : file.listFiles()) {
                scan(subFile);
            }
        }
    }

    /**
     * 释放信号
     */
    public void releaseSemaphore() {
        if (mUpdateSemaphore.availablePermits() == 0) {
            mUpdateSemaphore.release();
        }
    }

    /**
     * 请求线程暂停执行
     */
    public void requestSuspend() {
        stopped = true;
    }

    /**
     * 请求线程继续执行
     */
    public void requestResume() {
        stopped = false;
        mLock.lock();
        try {
            mCondition.signalAll();
        } finally {
            mLock.unlock();
        }
    }
}
