package com.ycg.sbgrpc;

import com.softwaremill.jox.Channel;
import com.softwaremill.jox.ChannelClosed;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Jox2Test {

    @Test
    public void channels() throws InterruptedException {
        var start = System.currentTimeMillis();
        // 创建三个 channel，缓冲区大小用默认
        var ch1 = Channel.<Integer>newBufferedChannel(1000);
        var sendLock = new CountDownLatch(3);
        // ch1 的生产者：每隔 0.1 秒往 ch1 里发送数字
        int count = 1_000_000;
        Thread.startVirtualThread(() -> {
            try {
                // 修正循环条件，确保发送的数字数量正确
                for (int i = 0; i < count; i++) {
                    ch1.send(1);
                }
            } catch (InterruptedException e) {
                // 捕获中断异常
            } finally {
                log("t1 退出!");
                sendLock.countDown();
            }
        });

        Thread.startVirtualThread(() -> {
            try {
                // 修正循环条件，确保发送的数字数量正确
                for (int i = 0; i < count; i++) {
                    ch1.send(20);
                }
            } catch (InterruptedException e) {
                // 捕获中断异常
            } finally {
                log("t2 退出!");
                sendLock.countDown();
            }
        });

        Thread.startVirtualThread(() -> {
            try {
                // 修正循环条件，确保发送的数字数量正确
                for (int i = 0; i < count; i++) {
                    ch1.send(100);
                }
            } catch (InterruptedException e) {
                // 捕获中断异常
            } finally {
                log("t3 退出!");
                sendLock.countDown();
            }
        });
        var receiveLock = new CountDownLatch(1);
        Thread.startVirtualThread(() -> {
            long total = 0;
            while (true) {
                try {
                    var receive = ch1.receiveOrClosed();
                    if (receive instanceof ChannelClosed) {
                        log("退出");
                        break;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                total++;
            }
            log("所有 channel 都已 done，退出循环" + total);
            receiveLock.countDown();
        });
        sendLock.await();
        ch1.done();
        receiveLock.await();
        log("耗时: " + (System.currentTimeMillis() - start) + " ms");

    }

    public static void Sleep(double seconds) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (seconds * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 重新设置中断标志
        }
    }

    public static void log(String obj) {
        IO.println(LocalDateTime.now() + ": " + obj);
    }
}