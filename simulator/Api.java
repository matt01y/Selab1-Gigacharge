import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.*;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Api {
    private static final String USER_AGENT = "Mozilla/5.0";

    public static List<Charger> getChargers(){
        try{
            ArrayList<Charger> res = new ArrayList<>();
            JSONArray chargers = getRequest("/locations/" + Config.locationId + "/chargers").getJSONArray("collection");
            for(int i = 0; i < chargers.length(); i++ ){
                JSONObject charger = chargers.getJSONObject(i);
                res.add(new Charger(
                        charger.getString("docid"),
                        Charger.Status.valueOf(charger.getString("status").toUpperCase()),
                        charger.getString("description"),
                        charger.has("user") ? charger.getString("user") : null,
                        charger.has("assignedJoin") ? charger.getString("assignedJoin") : null,
                        charger.has("usertype") ? Charger.UserType.valueOf(charger.getString("usertype").toUpperCase()) : null
                ));
            }
            return res;
        }catch(Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public static Charger getCharger(String id) throws Exception {
        try{
            ArrayList<Charger> res = new ArrayList<>();
            JSONObject charger = getRequest("/locations/" + Config.locationId + "/chargers/" + id);
            return new Charger(
                    charger.getString("docid"),
                    Charger.Status.valueOf(charger.getString("status").toUpperCase()),
                    charger.getString("description"),
                    charger.has("user") ? charger.getString("user") : null,
                    charger.has("assignedJoin") ? charger.getString("assignedJoin") : null,
                    charger.has("usertype") ? Charger.UserType.valueOf(charger.getString("usertype").toUpperCase()) : null
                );
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception("charger get mislukt");
        }        
    }
    
    public static void updateCharger(Charger charger){
        JSONObject res = new JSONObject();
        res.put("status", charger.status);
        res.put("user", charger.userId.toString());
        res.put("usertype", charger.userType.toString());
        
        try
        {
            String url  = "/locations/" + Config.locationId + "/chargers/" + charger.id;
            postRequest(url , res);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    private static JSONObject getRequest(String url) throws Exception {
        String fullurl = Config.endpoint + url;
        System.out.println("GET naar " + fullurl);
        URL obj = new URL(fullurl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());

            return new JSONObject(response.toString());
        } else {
            throw new Exception("get request failed");
        }
    }
    
    private static void postRequest(String url, JSONObject data) throws IOException {
        String fullurl = Config.endpoint + url;
        System.out.println("POST naar " + fullurl);
        URL obj = new URL(fullurl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Content-Type", "application/json");

        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(data.toString().getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.close();
        con.connect();
        // For POST only - END

        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("POST request did not work.");
        }
    }
    
    

}
