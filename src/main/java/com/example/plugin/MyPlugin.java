package com.example.plugin;

import io.github.krlvm.powertunnel.sdk.ServerAdapter;
import io.github.krlvm.powertunnel.sdk.http.ProxyRequest;
import io.github.krlvm.powertunnel.sdk.http.ProxyResponse;
import io.github.krlvm.powertunnel.sdk.plugin.PowerTunnelPlugin;
import io.github.krlvm.powertunnel.sdk.proxy.ProxyAdapter;
import io.github.krlvm.powertunnel.sdk.proxy.ProxyServer;
import io.github.krlvm.powertunnel.sdk.proxy.ProxyStatus;
import org.jetbrains.annotations.NotNull;

public class MyPlugin extends PowerTunnelPlugin {

    @Override
    public void onProxyInitialization(@NotNull ProxyServer proxy) {
        System.out.println("Hello World!");

        proxy.setMITMEnabled(true);
        proxy.setFullResponse(true);

        registerServerListener(new ServerAdapter() {
            @Override
            public void onProxyStatusChanged(@NotNull ProxyStatus status) {
                System.out.println("status = " + status.name());
            }
        });

        registerProxyListener(new ProxyAdapter() {
            @Override
            public void onClientToProxyRequest(@NotNull ProxyRequest request) {
                if(request.isBlocked()) {
                    // Request is already blocked by another plugin
                    return;
                }
                if(request.getHost().endsWith("github.com")) {
                    request.setResponse(proxy.getResponseBuilder("This website is blocked", 403)
                            .contentType("text/plain")
                            .build()
                    );
                }
            }

            @Override
            public void onProxyToClientResponse(@NotNull ProxyResponse response) {
                if(!proxy.isMITMEnabled()) return;
                if(!response.isDataPacket()) return;
                response.setRaw("<b>Hello World!</b>" + response.raw());
            }
        });
    }
}
