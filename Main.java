import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.datatransfer.SystemFlavorMap;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;


public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        String sqsURL = "https://sqs.us-east-1.amazonaws.com/623750023256/TweetsQueue";
        String sentiment ="http://sentiment.vivekn.com/api/text/";

        int count = 0;
        try{
            String msg;
            while(true){
                msg = receiveMessage(sqsURL);
                while(getBody(msg) == null){
                    Thread.sleep(5000);
                    msg = receiveMessage(sqsURL);
                }
                System.out.println(getBody(msg));
                //deleteMessage(sqsURL, msg);

            }




        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static String receiveMessage(String url) throws Exception{

        String receiveMessageParams = "?Action=ReceiveMessage";
        URL tweetURL = new URL(url + receiveMessageParams);
        HttpURLConnection tweetc = (HttpURLConnection) tweetURL.openConnection();
        tweetc.setRequestMethod("GET");
        tweetc.setDoOutput(true);

        BufferedReader messageInput = new BufferedReader(new InputStreamReader(tweetc.getInputStream(), "UTF-8"));
        String tmp;
        String output = "";
        while((tmp=messageInput.readLine())!=null){
            output = output + tmp;
        }

        return output;
    }

    public static void deleteMessage(String url, String message) throws Exception{
        String action = "?Action=DeleteMessage";
        String rhTag = "<ReceiptHandle>";
        String rhTagEnd = "</ReceiptHandle>" ;
        //System.out.println(message.indexOf(rhTag));
        //System.out.println(message.indexOf(rhTagEnd));
        String rhBody = message.substring(message.indexOf(rhTag) + rhTag.length(), message.indexOf(rhTagEnd));

        String params = "&ReceiptHandle="+rhBody;
        System.out.println(url + action + params);
        URL tweetURL = new URL(url + action + params);
        HttpURLConnection tweetc = (HttpURLConnection) tweetURL.openConnection();
        tweetc.setRequestMethod("GET");
        tweetc.setDoOutput(true);


    }

    public static BufferedReader sendMessage(String destination, String body)throws Exception{
        String param = "txt="+body;
        byte[] paramBytes = param.getBytes();

        URL url = new URL(destination);
        HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
        urlc.setDoOutput(true);
        urlc.setInstanceFollowRedirects(false);
        urlc.setRequestMethod("POST");
        urlc.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
        urlc.setRequestProperty( "charset", "utf-8");
        urlc.setRequestProperty( "Content-Length", Integer.toString(param.length()));
        urlc.setUseCaches(false);
        urlc.setDoOutput(true);
        urlc.getOutputStream().write(paramBytes);
        return new BufferedReader(new InputStreamReader(urlc.getInputStream(), "UTF-8"));

    }

    public static String getBody(String message) throws Exception{

        if(!message.contains("<Body>")) return null;
        return message.substring(message.indexOf("<Body>")+6,message.indexOf("</Body>"));

    }

    public static String getID(String message) throws Exception{
        return message.substring(message.indexOf("<Body>")+6,message.toLowerCase().indexOf("</body>"));

    }
}
