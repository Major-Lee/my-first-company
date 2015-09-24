import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

/**
 * Created by bluesand on 4/13/15.
 */
public class TestHttpClient {

    public static void main(String[] args) throws IOException {
        System.out.println("hello world");
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://192.168.66.7/bhu_api/v1/user/captcha/fetch_captcha");
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("acc", "18601002857"));
        post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        HttpResponse response = client.execute(post);
        System.out.println(response.getEntity().getContent());
    }
}
