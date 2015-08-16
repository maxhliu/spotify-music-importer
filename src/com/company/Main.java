package com.company;

import com.jayway.jsonpath.JsonPath;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.util.List;

public class Main {

    final static String CLIENT_ID = "e298b66f85ca413685e2ad506a11c8da";
    final static String CLIENT_SECRET = "2811fe31f47845549a201df42d46bcc8";

    static void authorize() {

    }

    public static void main(String[] args) throws IOException, UnirestException {
        //search tracks
        String songName = "van go";
        HttpResponse<JsonNode> jsonResponse = Unirest
                .get("https://api.spotify.com/v1/search")
                .queryString("q", songName)
                .queryString("type", "track")
                .asJson();

        //get the uri so you can save it later
        String uri = JsonPath.read(jsonResponse.getBody().toString(), "$.tracks.items[0].uri");

        //get the access token in order to save things
        jsonResponse = Unirest.post("https://accounts.spotify.com/api/token")
                .header("Authorization", "Basic " + Base64.encodeBase64String((CLIENT_ID + ":" + CLIENT_SECRET).getBytes()))
                .field("grant_type", "client_credentials")
                .asJson();
        String accessToken = JsonPath.read(jsonResponse.getBody().toString(), "$.access_token");
        Unirest.setDefaultHeader("Authorization", accessToken);

        //get current user
        jsonResponse = Unirest.post("https://api.spotify.com/v1/me").asJson();
        String userId = JsonPath.read(jsonResponse, "$.id");

//        //get a list of the current user's playlists and get the proper playlist id
//        jsonResponse = Unirest.post("https://api.spotify.com/v1/users/" + userId + "/playlists").asJson();
//        String playlistId = JsonPath.read(jsonResponse, "$.items[?(@.name == 'Apple Ad Music')].id");

        //create a new playlist
        jsonResponse = Unirest.post("https://api.spotify.com/v1/users/matresscow/playlists")
                .body("{name:\"Apple Ad Music\"}")
                .asJson();
        String playlistId = JsonPath.read(jsonResponse, "$.id");

        //add the track to the playlist
        Unirest.post("https://api.spotify.com/v1/users/matresscow/playlists/" + playlistId + "/tracks").field("uris", uri);
    }
}
