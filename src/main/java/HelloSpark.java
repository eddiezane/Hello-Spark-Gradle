import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.factory.CallFactory;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.verbs.*;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class HelloSpark {

    public static void main(String[] args) {

        get("/", (req, res) -> "Hello, World!");

        TwilioRestClient client = new TwilioRestClient("YOUR_TWILIO_ACCOUNT_SID", "YOUR_TWILIO_AUTH_TOKEN"); // TODO
        Account mainAccount = client.getAccount();

        post("/sms", (req, res) -> {
            String ngrokUrl = "YOUR_NGROK_URL"; // TODO
            String body = req.queryParams("Body");
            String to = req.queryParams("From");
            String from = "YOUR_TWILIO_PHONE_NUMBER"; // TODO

            String uri = ngrokUrl + "/call?q=" + URLEncoder.encode(body, "UTF-8");

            CallFactory callFactory = mainAccount.getCallFactory();
            Map<String, String> callParams = new HashMap<String, String>();
            callParams.put("To", to);
            callParams.put("From", from);
            callParams.put("Url", uri);
            callParams.put("Method", "GET");
            callFactory.create(callParams);

            TwiMLResponse twiml = new TwiMLResponse();
            twiml.append(new Message("Your tunes are on the way!"));

            res.type("text/xml");
            return twiml.toXML();
        });

        get("/call", (req, res) -> {
            TwiMLResponse twiml = new TwiMLResponse();

            String query = req.queryParams("q");
            String trackUrl = getTrackUrl(query);

            if (trackUrl != null) {
                twiml.append(new Play(trackUrl));
            } else {
                twiml.append(new Say("Sorry, song not found."));
            }

            res.type("text/xml");
            return twiml.toXML();
        });
    }

    private static String getTrackUrl(String query) {
        String url = "http://api.spotify.com/v1/search";
        HttpResponse<JsonNode> jsonResponse;
        try {
            jsonResponse = Unirest.get(url)
                    .header("accept", "application/json")
                    .queryString("q", query)
                    .queryString("type", "track")
                    .asJson();
            return jsonResponse.getBody().getObject().getJSONObject("tracks")
                    .getJSONArray("items").getJSONObject(0).getString("preview_url");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
