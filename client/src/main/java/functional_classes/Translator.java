package functional_classes;

import com.cloudmersive.client.LanguageTranslationApi;
import com.cloudmersive.client.invoker.ApiClient;
import com.cloudmersive.client.invoker.ApiException;
import com.cloudmersive.client.invoker.Configuration;
import com.cloudmersive.client.invoker.auth.ApiKeyAuth;
import com.cloudmersive.client.model.LanguageTranslationRequest;
import com.cloudmersive.client.model.LanguageTranslationResponse;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


public class Translator {
    private static int i = 0;
    public static void main(String[] args) {

    }

    public static String translate(String toLang, String text){
        String input = "Что-то для перевода";
        String lang = "en";
        try {
            String urlStr = "https://translate.api.cloud.yandex.net/translate/v2/translate";
            URL urlObj = new URL(urlStr);
            HttpsURLConnection connection = (HttpsURLConnection)urlObj.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes("text=" + URLEncoder.encode(input, StandardCharsets.UTF_8) + "&lang=" + lang);

            InputStream response = connection.getInputStream();
            String json = new java.util.Scanner(response).nextLine();
            int start = json.indexOf("[");
            int end = json.indexOf("]");
            String translated = json.substring(start + 2, end - 1);
            i++;
            if (translated.equals(input) && i < 2) {
                // if return equal of entered text - we need change direction of translation
                return translate("en", input);
            } else return translated;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}