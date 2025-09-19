package com.ycg.sbgrpc;

import com.softwaremill.jox.Channel;
import com.softwaremill.jox.Select;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class TickerTest {

    public static void main(String[] args) throws InterruptedException {

        // 创建一个通道，用于发送 ticker 信号
        var tickerCh = Channel.<String>newBufferedDefaultChannel();

        // 启动一个虚拟线程，作为 ticker 的生产者
        Thread.startVirtualThread(() -> {
            try {
                int count = 0;
                while (true) {
                    // 每隔 1 秒发送一个信号
                    TimeUnit.SECONDS.sleep(count);
                    tickerCh.send("Tick " + count++);
                }
            } catch (InterruptedException e) {
                // 当线程被中断时，关闭通道
                tickerCh.done();
                System.out.println("Ticker 线程退出");
            }
        });

        var timeout = Duration.ofSeconds(3);
        // 在主线程中接收 ticker 信号
        try {
            while (true) {
                // 使用 select 机制接收信号，并设置超时
                var receiveClause = tickerCh.receiveClause();

                // 设置一个接收超时，比如 3 秒
                Object result = Select.selectOrClosedWithin(timeout, "退出", receiveClause);

                if (result.equals("退出")) {
                    // 假设超时后我们退出
                    System.out.println(LocalDateTime.now() + ": Ticker 超时，3秒内没有收到信号。");
                    break;
                } else {
                    System.out.println(LocalDateTime.now() + ": 收到信号 -> " + result);
                }
            }
        } finally {
            // 在主线程退出前，确保 ticker 线程被中断
            Thread.currentThread().interrupt();
            tickerCh.done();
        }
    }
}