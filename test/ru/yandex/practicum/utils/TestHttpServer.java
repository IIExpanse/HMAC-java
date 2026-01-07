package ru.yandex.practicum.utils;

import com.sun.net.httpserver.HttpServer;

public record TestHttpServer(HttpServer server) {
     public void stop() {
         server.stop(0);
     }

     public int getPort() {
         return server.getAddress().getPort();
     }
}
