package com.spoty.guilletystats_backend.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

@Service
public class OAuth2Config {

    @Value("${redirect.server.ip}")
    private String customIp;

    @Value("${spring.security.oauth2.client.registration.spotify.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.spotify.client-secret}")
    private String secretId;

    public SpotifyApi getSpotifyObject() {
        URI redirectedURL =  SpotifyHttpManager.makeUri(customIp + "/api/get-user-code/");

        return new SpotifyApi
                .Builder()
                .setClientId(clientId)
                .setClientSecret(secretId)
                .setRedirectUri(redirectedURL)
                .build();
    }
}

