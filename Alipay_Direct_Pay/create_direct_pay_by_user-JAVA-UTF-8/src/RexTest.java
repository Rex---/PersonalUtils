import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.alipay.config.AlipayConfig;
import com.alipay.sign.MD5;
import com.alipay.util.AlipayCore;

public class RexTest {
	
	public static String partner = "2088911428639245";
	public static String seller_email = "virtualcloud123@hotmail.com";
	public static String key = "ebh0s0xeavgc6twb5nrkoobqiejt73ik";
	public static String log_path = "D:\\";
	public static String input_charset = "utf-8";
	public static String sign_type = "MD5";
	
	 /**
     * 支付宝提供给商户的服务接入网关URL(新)
     */
    private static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";

	public static void main(String[] args) {
		String payment_type = "1";
		String paymethod = "bankPay";
		
		//服务器异步通知页面路径
		String notify_url = "http://商户网关地址/create_direct_pay_by_user-JAVA-UTF-8/notify_url.jsp";
		//需http://格式的完整路径，不能加?id=123这类自定义参数
		
		//页面跳转同步通知页面路径
		String return_url = "http://商户网关地址/create_direct_pay_by_user-JAVA-UTF-8/return_url.jsp";
		//需http://格式的完整路径，不能加?id=123这类自定义参数，不能写成http://localhost/
		
		String out_trade_no = "订单编号";
		String subject = "订单名称";
		String total_fee = "总价";
		String body = "订单描述";
		String defaultbank = "银行简码";
		
		//把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "create_direct_pay_by_user");
		sParaTemp.put("partner", partner);
		sParaTemp.put("seller_email", seller_email);
		sParaTemp.put("_input_charset", input_charset);
		sParaTemp.put("payment_type", payment_type);
		sParaTemp.put("notify_url", notify_url);
		sParaTemp.put("return_url", return_url);
		sParaTemp.put("out_trade_no", out_trade_no);
		sParaTemp.put("subject", subject);
		sParaTemp.put("total_fee", total_fee);
		sParaTemp.put("body", body);
		sParaTemp.put("paymethod", paymethod);
		sParaTemp.put("defaultbank", defaultbank);
		sParaTemp.put("show_url", "");
		sParaTemp.put("anti_phishing_key", "");
		sParaTemp.put("exter_invoke_ip", "");
		
		//建立请求
		String sHtmlText = buildRequest(sParaTemp,"get","确认");
		out.println(sHtmlText);
	}
	
	/**
     * 建立请求，以表单HTML形式构造（默认）
     * @param sParaTemp 请求参数数组
     * @param strMethod 提交方式。两个值可选：post、get
     * @param strButtonName 确认按钮显示文字
     * @return 提交表单HTML文本
     */
    public static String buildRequest(Map<String, String> sParaTemp, String strMethod, String strButtonName) {
        //待请求参数数组
        Map<String, String> sPara = buildRequestPara(sParaTemp);
        List<String> keys = new ArrayList<String>(sPara.keySet());
        StringBuffer sbHtml = new StringBuffer();
        sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\"" + ALIPAY_GATEWAY_NEW
                      + "_input_charset=" + AlipayConfig.input_charset + "\" method=\"" + strMethod + "\">");

        for (int i = 0; i < keys.size(); i++) {
            String name = (String) keys.get(i);
            String value = (String) sPara.get(name);
            sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
        }

        //submit按钮控件请不要含有name属性
        sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + "\" style=\"display:none;\"></form>");
        sbHtml.append("<script>document.forms['alipaysubmit'].submit();</script>");
        return sbHtml.toString();
    }
    
    /**
     * 生成要请求给支付宝的参数数组
     * @param sParaTemp 请求前的参数数组
     * @return 要请求的参数数组
     */
    private static Map<String, String> buildRequestPara(Map<String, String> sParaTemp) {
        //除去数组中的空值和签名参数
        Map<String, String> sPara = AlipayCore.paraFilter(sParaTemp);
        //生成签名结果
        String mysign = buildRequestMysign(sPara);
        //签名结果与签名方式加入请求提交参数组中
        sPara.put("sign", mysign);
        sPara.put("sign_type", AlipayConfig.sign_type);
        return sPara;
    }
    
    /**
     * 生成签名结果
     * @param sPara 要签名的数组
     * @return 签名结果字符串
     */
	public static String buildRequestMysign(Map<String, String> sPara) {
    	String prestr = AlipayCore.createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String mysign = "";
        if(AlipayConfig.sign_type.equals("MD5") ) {
        	mysign = MD5.sign(prestr, AlipayConfig.key, AlipayConfig.input_charset);
        }
        return mysign;
    }

}