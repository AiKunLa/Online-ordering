package com.sky.websocket;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * webSocket服务
 */
@Component
@ServerEndpoint("/ws/{sid}")
public class WebSocketServer {
    public static Map<String, Session> sessionMap = new HashMap<>();

    /**
     * 建立连接后调用该方法
     *
     * @param session
     * @param sid
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        System.out.println("客户端" + sid + "建立连接");
        sessionMap.put(sid, session);
    }

    /**
     * 接收到客户端发送信息调用该方法
     *
     * @param message
     * @param sid
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        System.out.println("收到来自客户端:" + sid + "的信息-->" + message);
    }

    /**
     * 客户端断开连接调用方法
     *
     * @param sid
     */
    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        //移除session
        sessionMap.remove(sid);
        System.out.println("客户端:" + sid + "断开连接");
    }

    /**
     * 群发
     * 对所有客户端发送信息
     *
     * @param message
     */
    public void sendMessageToAll(String message) {
        Collection<Session> sessions = sessionMap.values();
        for (Session session : sessions) {
            try {
                //向客户端发送信息
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
