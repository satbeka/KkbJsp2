package kz.kkb.remote;


import comp.KKBSign;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class HttpsURLConnectionR {

    final String merchantId="92061101";
    final String certId="00C182B189";
    /*certificate=	"00C182B189"
    merchant_id=	"92061101"
*/



    private static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    // �������� ����� SSL
    public static String Send_https(String urlStr, String resp, String contType)
            throws CertificateException, InterruptedException, UnrecoverableKeyException, NoSuchAlgorithmException,
            IOException, KeyManagementException, KeyStoreException
    {
        try {
            trustAllHttpsCertificates();
        } catch (Exception e1) {
            return "trustAllHttpsCertificates() Excep="+e1.getMessage();
        }
        String s3 = null;
        try
        {
            URL url = new URL(urlStr);
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

            /*
            con.setHostnameVerifier(DO_NOT_VERIFY);
            con.setRequestMethod("POST");
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
*/


            con.setDoOutput(true);
            con.setDoInput(true);
            con.setHostnameVerifier(DO_NOT_VERIFY);
            con.setRequestMethod("POST");
            if (contType.length()!=0)
                con.setRequestProperty("content-type", contType);
            DataOutputStream dataoutputstream = new DataOutputStream(con.getOutputStream());
            dataoutputstream.writeBytes(resp);
            dataoutputstream.flush();
            dataoutputstream.close();
            InputStream inputstream = con.getInputStream();
            s3 = ReadMsg(inputstream);
            inputstream.close();
        }catch(Exception exception ) {
            s3=exception+"";
        }
        return s3;
    }
    public static void trustAllHttpsCertificates() throws Exception {
        //Create a trust manager that does not validate certificate chains:
        javax.net.ssl.TrustManager[] trustAllCerts =             new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc =            javax.net.ssl.SSLContext.getInstance("TLSv1.2");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
    public static class miTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
        public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }
        public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }
        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
            return;
        }
        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
            return;

        }
    }
    public static String ReadMsg(InputStream inputstream)
            throws IOException
    {
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream));
        StringBuffer stringbuffer = new StringBuffer();
        String s;
        while((s = bufferedreader.readLine()) != null)
            stringbuffer.append(s);
        bufferedreader.close();
        return stringbuffer.toString().trim();
    }



    public static void main(String[] args) {

        testTls();

        HttpsURLConnectionR http = new HttpsURLConnectionR();
        System.out.println("Testing 1 - Send Http GET request");
                    http.sendGet("100000", "10501444.00", "150519122832", "122832", "complete");

    }


    public static String testTls(){
        String rez="";
        String urlS = "https://www.dvb.de";

        /*
        System.setProperty("http.proxyHost", "172.22.223.247");
        System.setProperty("http.proxyPort", "8080");
*/
        try {
            trustAllHttpsCertificates();
        } catch (Exception e1) {
            return "trustAllHttpsCertificates() Excep="+e1.getMessage();
        }
        String s3 = null;
        URL url = null;
        try {
            url = new URL(urlS);
            try {



                HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setHostnameVerifier(DO_NOT_VERIFY);
                con.setRequestMethod("POST");

                con.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                DataOutputStream dataoutputstream = new DataOutputStream(con.getOutputStream());

                String resp="";
                dataoutputstream.writeBytes(resp);
                dataoutputstream.flush();
                dataoutputstream.close();
                InputStream inputstream = con.getInputStream();
                s3 = ReadMsg(inputstream);
                inputstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        }



            /*
            con.setHostnameVerifier(DO_NOT_VERIFY);
            con.setRequestMethod("POST");
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
*/
        return rez;
    };


    // HTTP GET request 2
    public String sendGet(String orderId, String amount,String reference,String approval_code, String commandType ) {

        String rez="0";
        String url = "https://3dsecure.kkb.kz/jsp/remote/control.jsp?";


        /*
        System.setProperty("http.proxyHost", "172.22.223.247");
        System.setProperty("http.proxyPort", "8443");
*/

        //System.setProperty("https.protocols","TLSv1.2");
        String sCommandText ="";
        KKBSign test=new KKBSign();
        //HttpURLConnection con =null;


        try {
            Document pageXml= DocumentHelper.createDocument();
            Element Root = pageXml.addElement("document");
            Element merchantElem=Root.addElement("merchant");
            merchantElem.addAttribute("id",  merchantId);
            Element commandElem=merchantElem.addElement("command");
            commandElem.addAttribute("type",  commandType);
            Element paymentElem=merchantElem.addElement("payment");
            paymentElem.addAttribute("reference",  reference);
            paymentElem.addAttribute("approval_code",  approval_code);
            paymentElem.addAttribute("orderid",  orderId);
            paymentElem.addAttribute("amount",  amount);
            paymentElem.addAttribute("currency_code",  "398");
            Element reasonElem=merchantElem.addElement("reason");
            reasonElem.addText("Only for reverse");

            sCommandText  =(merchantElem.asXML()).toString();

            System.out.println("merchantElem: "+ sCommandText +"<p>");

            String ks=System.getProperty("user.dir")+"\\src\\kz\\kkb\\remote\\sign_resources\\test.jks" ;
            ks = "C:\\bv\\kkb\\test.jks";

            //textSign=textSign.replaceAll(" ", "+");
            //String ks= "D:\\tisr job\\kkb\\MERCHANT\\kkbsign_java\\test.jks" ;
            String keystore=ks;
            String alias="cert";
            String keypass="patrol";
            String storepass="nissan";

            String Base64Content=test.sign64(sCommandText,keystore,alias,"patrol","nissan");
            Element merchant_signElem=Root.addElement("merchant_sign");
            merchant_signElem.addAttribute("type",  "RSA");
            merchant_signElem.addAttribute("cert_id",  certId);

            merchant_signElem.addText(Base64Content);

            System.out.println("Document: "+ Root.asXML() +"<p>");

            String Document =Root.asXML()+"";

            sCommandText= URLEncoder.encode(Document) ;
            url=url+sCommandText;
            System.out.println("url="+url);
            String response=null;
            String s3=Send_https(url,response,"application/x-www-form-urlencoded");


            /* 160915
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");
            System.out.println("int responseCode = con.getResponseCode(); ");

            System.out.println("con.getResponseMessage();="+con.getResponseMessage());
            System.out.println("con.usingProxy()="+con.usingProxy());
            int responseCode = con.getResponseCode();

            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            con.disconnect();
            */


            //print result
            System.out.println("resp=="+response.toString());
            rez="1";
            return rez;
        }
        catch(Exception e){
            System.out.println(" catch httpsURL Unable to create file:" + e.getMessage());
//    TODO update tTISR_CLIENT_KKB_1C_LOG apprv kkb reason
            /*
            if (con != null) {
                con.disconnect();
            }
            */

            return rez;
        }

        //    return rez;
    }


/*
    // HTTP GET request
    public String sendGet(String orderId, String amount,String reference,String approval_code, String commandType ) {

        String rez="0";
        String url = "https://3dsecure.kkb.kz/jsp/remote/control.jsp?";

        System.setProperty("http.proxyHost", "172.22.223.247");
        System.setProperty("http.proxyPort", "8443");

        String sCommandText ="";
        KKBSign test=new KKBSign();
        HttpURLConnection con =null;
        try {
            trustAllHttpsCertificates();
        } catch (Exception e1) {
            return "";
        }

try {
        Document pageXml= DocumentHelper.createDocument();
        Element Root = pageXml.addElement("document");
        Element merchantElem=Root.addElement("merchant");
        merchantElem.addAttribute("id",  merchantId);
        Element commandElem=merchantElem.addElement("command");
        commandElem.addAttribute("type",  commandType);
        Element paymentElem=merchantElem.addElement("payment");
        paymentElem.addAttribute("reference",  reference);
        paymentElem.addAttribute("approval_code",  approval_code);
        paymentElem.addAttribute("orderid",  orderId);
        paymentElem.addAttribute("amount",  amount);
        paymentElem.addAttribute("currency_code",  "398");
        Element reasonElem=merchantElem.addElement("reason");
        reasonElem.addText("Only for reverse");

        sCommandText  =(merchantElem.asXML()).toString();

        System.out.println("merchantElem: "+ sCommandText +"<p>");

        String ks=System.getProperty("user.dir")+"\\src\\kz\\kkb\\remote\\sign_resources\\test.jks" ;
        ks = "C:\\bv\\kkb\\test.jks";

        //textSign=textSign.replaceAll(" ", "+");
        //String ks= "D:\\tisr job\\kkb\\MERCHANT\\kkbsign_java\\test.jks" ;
        String keystore=ks;
        String alias="cert";
        String keypass="patrol";
        String storepass="nissan";

        String Base64Content=test.sign64(sCommandText,keystore,alias,"patrol","nissan");
        Element merchant_signElem=Root.addElement("merchant_sign");
        merchant_signElem.addAttribute("type",  "RSA");
        merchant_signElem.addAttribute("cert_id",  certId);

        merchant_signElem.addText(Base64Content);

        System.out.println("Document: "+ Root.asXML() +"<p>");

        String Document =Root.asXML()+"";

        sCommandText= URLEncoder.encode(Document) ;
        url=url+sCommandText;
        System.out.println("url="+url);
        URL obj = new URL(url);

        con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");
    System.out.println("int responseCode = con.getResponseCode(); ");

    System.out.println("con.getResponseMessage();="+con.getResponseMessage());
    System.out.println("con.usingProxy()="+con.usingProxy());



        int responseCode = con.getResponseCode();

   System.out.println("\nSending 'GET' request to URL : " + url);
   System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        con.disconnect();

        //print result
        System.out.println("resp=="+response.toString());
        rez="1";
        return rez;
}
catch(Exception e){
    System.out.println(" catch httpsURL Unable to create file:" + e.getMessage());
//    TODO update tTISR_CLIENT_KKB_1C_LOG apprv kkb reason
    if (con != null) {
        con.disconnect();
    }

    return rez;
}

    //    return rez;
    }
    */
}
