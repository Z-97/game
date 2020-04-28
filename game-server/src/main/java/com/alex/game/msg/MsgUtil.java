package com.alex.game.msg;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * 发送短信
 * 
 * @author yejuhua
 *
 */
public class MsgUtil {
	private static String Url = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";
	public static boolean sendMsg( String mobile,String mobileCode) {
		boolean isSend = false;
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(Url);
		client.getParams().setContentCharset("GBK");
		method.setRequestHeader("ContentType", "application/x-www-form-urlencoded;charset=GBK");

		String content = new String("您的验证码是：" + mobileCode + "。请不要把验证码泄露给其他人。");
		NameValuePair[] data = { // 提交短信
				new NameValuePair("password", "eecfea53f2713e8e4496219ba2c9af5b"), // 查看用户名
																					// 登录用户中心->验证码通知短信>产品总览->API接口信息->APIID
				new NameValuePair("account", "C93125802"), // 查看密码 登录用户中心->验证码通知短信>产品总览->API接口信息->APIKEY
				// new NameValuePair("password", util.StringUtil.MD5Encode("密码")),
				new NameValuePair("mobile", mobile), new NameValuePair("content", content), };
		method.setRequestBody(data);
		try {
			client.executeMethod(method);

			String SubmitResult = method.getResponseBodyAsString();

			Document doc = DocumentHelper.parseText(SubmitResult);
			Element root = doc.getRootElement();

			String code = root.elementText("code");
			String msg = root.elementText("msg");
			String smsid = root.elementText("smsid");
			if ("2".equals(code)) {
				isSend = true;
			}
			System.out.println(code);
			System.out.println(msg);
			System.out.println(smsid);
		} catch (HttpException e) {
			
			isSend = false;
		} catch (IOException e) {
			
			isSend = false;
		} catch (DocumentException e) {
			
			isSend = false;
		}
		return isSend;
	}
}
