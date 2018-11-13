import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.decode.AESOperator;

import net.sf.json.JSONObject;

/**
 * 
 * "曲线救国"的方法进行校园学生身份认证
 * 登陆信息门户，获取相关信息
 * 
 */
public class GdufeIdentify {
	
	private static String name = "";
	private static String myClass = "";
	private static int statusCode = -1;
	private static String userName = "";
	private static String passWord = "";
	private static String loginUrl = "http://jwxt.gdufe.edu.cn/jsxsd/xk/LoginToXkLdap";	//登陆地址，抓包获得

	public GdufeCheckService(String msg) throws Exception{
		//msg是前端传回来的json
		//获取userName、passWord,并解密
		GdufeCheckService.userName = new String( aes.decrypt(json.getString("userName"),keybytes));
		GdufeCheckService.passWord = new String( aes.decrypt(json.getString("passWord"),keybytes));
		
		HttpClient client = new DefaultHttpClient();	//这里的client就包括了你的cookie等信息，重定向的时候不能重新new一个HttpClient，不然cookie等信息对不上，重定向就会失败
		loginPost(client);
		
		get(client,"http://jwxt.gdufe.edu.cn/jsxsd/grxx/xsxx");	//获取学生信息地址，链接抓包获得
		client.getConnectionManager().shutdown();
	}

	/**
	 * 模拟POST请求
	 * 
	 * @param client
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	private static void loginPost(HttpClient client)
			throws UnsupportedEncodingException, IOException, ClientProtocolException {
		// System.out.println("==========登录：====================================");
		HttpPost post = new HttpPost(loginUrl);
		// 创建表单参数列表
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("PASSWORD", passWord));
		qparams.add(new BasicNameValuePair("USERNAME", userName));
		// 填充表单
		post.setEntity(new UrlEncodedFormEntity(qparams, "UTF-8"));
		//发送post请求
		HttpResponse response2 = client.execute(post);
		//获取请求返回的状态码
		statusCode = response2.getStatusLine().getStatusCode();
		
		
		if (statusCode == 200) {		//200错误，用户名或密码错误()
			statusCode = 200;
			post.abort();			//关闭连接
		} else if (statusCode == 302) {		// 302表示重定向
			statusCode = 302;
			Header[] hs = response2.getHeaders("Location");
			if (hs.length > 0) {
				String url = hs[0].getValue();	//获取重定向链接
				post.abort();		//关闭连接
				get(client, url);	//对重定向链接进行get请求
			}
		}
	}

	/**
	 * 模拟GET请求
	 * 
	 * @param client
	 * @param url
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	private static void get(HttpClient client, String url) throws IOException, ClientProtocolException {
//		System.out.println("======GET:" + url + "===========================");
		HttpGet get = new HttpGet(url);
		HttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();
		BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
		String buffer = null;
		while ((buffer = reader.readLine()) != null) {	//while里对返回的html数据进行处理
			if(buffer.indexOf("姓名：")!=-1){
				int indexStart = buffer.indexOf("姓名：");
				int indexEnd = buffer.indexOf("<br/>");
				name = buffer.substring(indexStart+3, indexEnd);	//截取姓名
			}
			if(buffer.indexOf("班级：")!=-1){
				int indexStart = buffer.indexOf("班级：");
				int indexEnd = buffer.indexOf("</td>");
				myClass = buffer.substring(indexStart+3, indexEnd);	//截取班级
			}
//			System.out.println(buffer);测试，查看html
		}
		EntityUtils.consume(entity);
		get.abort();//关闭连接
	}

	public String getName() {
		return name;
	}

	public static void setName(String name) {
		GdufeCheckService.name = name;
	}

	public String getMyClass() {
		return myClass;
	}

	public static void setMyClass(String myClass) {
		GdufeCheckService.myClass = myClass;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public static void setStatusCode(int statusCode) {
		GdufeCheckService.statusCode = statusCode;
	}
	
}
