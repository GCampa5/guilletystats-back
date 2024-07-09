package com.spoty.guilletystats_backend.controller;

import java.io.IOException;
import java.net.URI;

import com.spoty.guilletystats_backend.config.OAuth2Config;
import com.spoty.guilletystats_backend.service.SpotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class SpotifyController {

    private final SpotifyService spotifyService;
    private final OAuth2Config spotifyConfiguration;

    @Autowired
    public SpotifyController(SpotifyService spotifyService, OAuth2Config spotifyConfiguration) {
        this.spotifyService = spotifyService;
        this.spotifyConfiguration = spotifyConfiguration;
    }

    @Value("${custom.server.ip}")
    private String customIp;

    @GetMapping("login")
    public ResponseEntity<String> spotifyLogin() {
        SpotifyApi object = spotifyConfiguration.getSpotifyObject();

        AuthorizationCodeUriRequest authorizationCodeUriRequest = object.authorizationCodeUri()
                .scope("user-library-read")
                .show_dialog(true)
                .build();

        final URI uri = authorizationCodeUriRequest.execute();
        return ResponseEntity.ok(uri.toString());
    }

    @GetMapping("get-user-code/")
    public void getSpotifyUserCode(@RequestParam("code") String userCode, HttpServletResponse response) throws IOException {
        SpotifyApi object = spotifyConfiguration.getSpotifyObject();
        System.out.println(object.toString());
        AuthorizationCodeRequest authorizationCodeRequest = object.authorizationCode(userCode).build();
        User user;

        try {
            final AuthorizationCodeCredentials authorizationCode = authorizationCodeRequest.execute();

            object.setAccessToken(authorizationCode.getAccessToken());
            object.setRefreshToken(authorizationCode.getRefreshToken());

            final GetCurrentUsersProfileRequest getCurrentUsersProfile = object.getCurrentUsersProfile().build();
            user = getCurrentUsersProfile.execute();
            System.out.println(user.toString());

        } catch (Exception e) {
            System.out.println("Exception occurred while getting user code: " + e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        response.sendRedirect(customIp + "/home?id=" + user.getId());
    }

    @GetMapping("/search")
    public ResponseEntity<String> searchArtists(@RequestParam String query) {
        String result = spotifyService.searchArtists(query);
        return ResponseEntity.ok(result);
    }
}