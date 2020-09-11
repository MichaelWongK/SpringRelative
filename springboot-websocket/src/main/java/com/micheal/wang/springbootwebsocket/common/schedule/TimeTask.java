package com.micheal.wang.springbootwebsocket.common.schedule;

import com.micheal.wang.springbootwebsocket.websocket.WebSocketServer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/9/11 16:23
 * @Description 使用 spring 的 Schedule 建立定时任务
 * @EnableScheduling 开启 spring 定时任务功能
 * @Scheduled(cron = "0/10 * * * * ?") 用于标识定时执行的方法，此处主要方法返回值一定是 void，没有入参。
 * 对应定时时间配置可以百度 cron 语法，根据自己的业务选择合适的周期
 * 在这类中，我们通过上面 MyWebSocket 提供的静态方法获取其中的 webSocketSet ，
 * 来获取所有此业务相关的所有 websocketsession，可以在定时任务中对 session 内容进行验证判断（权限验证等），
 * 进行发送消息
 */
@Component
@EnableScheduling
public class TimeTask {

//    private static Logger logger = LoggerFactory.getLogger(TimeTask.class);

    @Scheduled(cron = "0/1 * * * * ?")
    public void test(){
        System.err.println("*********   定时任务执行   **************");
        CopyOnWriteArraySet<WebSocketServer> webSocketSet =
                WebSocketServer.getWebSocketSet();
        int i = 0 ;
        webSocketSet.forEach(c->{
            try {
                c.sendMessage("  定时发送  " + new Date().toLocaleString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        System.err.println("/n 定时任务完成.......");
    }
}
