package com.ycg.sbgrpc;

import com.softwaremill.jox.Channel;
import com.softwaremill.jox.ChannelDone;
import com.softwaremill.jox.Select;
import com.softwaremill.jox.SelectClause;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class JoxTest {

    @Test
    public void channels() throws InterruptedException {
        var start = System.currentTimeMillis();
        // 创建三个 channel，缓冲区大小用默认
        var ticker = Channel.<Integer>newBufferedChannel(1000);
        var ch2 = Channel.<Integer>newBufferedChannel(1000);
        var ch3 = Channel.<Integer>newBufferedChannel(1000);

        // ch1 的生产者：每隔 0.1 秒往 ch1 里发送数字
        int count = 1_000_000;
        Thread.startVirtualThread(() -> {
            try {
                // 修正循环条件，确保发送的数字数量正确
                for (int i = 0; i < count; i++) {
                    ticker.send(1);
                }
            } catch (InterruptedException e) {
                // 捕获中断异常
            } finally {
                ticker.done();
                log("ch1 生产者退出");
            }
        });

        Thread.startVirtualThread(() -> {
            try {
                // 修正循环条件，确保发送的数字数量正确
                for (int i = 0; i < count; i++) {
                    ch2.send(20);
                }
            } catch (InterruptedException e) {
                // 捕获中断异常
            } finally {
                ch2.done();
                log("ch2 生产者退出");
            }
        });

        Thread.startVirtualThread(() -> {
            try {
                // 修正循环条件，确保发送的数字数量正确
                for (int i = 0; i < count; i++) {
                    ch3.send(100);
                }
            } catch (InterruptedException e) {
                // 捕获中断异常
            } finally {
                ch3.done();
                log("ch3 生产者退出");
            }
        });

        var channels = Lists.newArrayList(ticker, ch2, ch3);
        while (!channels.isEmpty()) {
            // 这里不能直接用 Select.select，因为只要有一个 channel done 就会抛异常
            Object selected = Select.selectOrClosed(channels.stream()
                    .map(Channel::receiveClause)   // 在每次循环中重新构建 SelectClause 列表
                    .toArray(SelectClause[]::new));
            if (selected instanceof ChannelDone(Channel<?> cc)) {
                // 某个 channel 在 select 时关闭了，移除它，继续下一轮
                // 移除已关闭的 channel
                channels.removeIf(Channel::isClosedForSend);
//                if (cc == ticker) {
//                    log("ch1 主动关闭了!");
//                    return;
//                }
            }
        }

        log("所有 channel 都已 done，退出循环");
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
        System.out.println(LocalDateTime.now() + ": " + obj);
    }
}