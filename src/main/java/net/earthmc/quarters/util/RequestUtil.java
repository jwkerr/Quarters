package net.earthmc.quarters.util;

import com.google.gson.JsonObject;
import net.earthmc.quarters.api.manager.JSONManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class RequestUtil {

    public static @Nullable JsonObject getUrlAsJsonObject(@NotNull URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        int code = connection.getResponseCode();
        if (code != 200) throw new IOException("Connection was not successful, response code: " + code);

        String inline = "";
        Scanner scanner = new Scanner(url.openStream());

        while (scanner.hasNext()) {
            inline += scanner.nextLine();
        }

        scanner.close();

        try {
            return JSONManager.getInstance().getGson().fromJson(inline, JsonObject.class);
        } catch (Exception e) {
            return null;
        }
    }
}
