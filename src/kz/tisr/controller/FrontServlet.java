package kz.tisr.controller;

import comp.KKBSign;
import kz.kkb.remote.HttpsURLConnectionR;
import oracle.sql.CLOB;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.sql.*;


public class FrontServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method=req.getMethod();
        System.out.println(" metod=" + req.getMethod());
        String uri=req.getRequestURI();

        System.out.println("req.getRemoteAddr()==="+req.getRemoteAddr());
        System.out.println("servlet path= " + req.getServletPath());
        System.out.println("request URL= " + req.getRequestURL());
        System.out.println("request URI= " + uri);
        System.out.println("req.getSession()=  "+req.getSession().getId());
        //DoPl.doPl();
        if (uri.contentEquals("/do/pl/pl.jsp")) {

                System.out.println("http://185.48.126.77:8080/do/pl/pl.jsp=");
                doPl(req, resp);
            resp.setContentType("text/html");
            PrintWriter out = null;
            out = resp.getWriter();
            out.print(req.getAttribute("res"));
      //out.print("<head><title> 1 0 1 0 10 </title></head>");
      //resp.sendRedirect(req.getRemoteAddr());

            System.out.println("   resp.sendRedirect();  res="+req.getAttribute("res"));
            out.close();
            return;}

        if (uri.contentEquals("/do/pl/pl_err.jsp")) {

            System.out.println("http://185.48.126.77:8080/do/pl/pl_err.jsp");
            doPl_err(req, resp);
            return;}


        /*--100715
        String ord=req.getParameter("orderId");
        System.out.println("1 orderId="+ord);
        String sum=req.getParameter("sumPaid");
        System.out.println("1 sum="+sum);
        String abonentId=req.getParameter("abonentId");
        System.out.println("1 abonentId="+abonentId);

        KKBSign test=new KKBSign();

        Scanner templateXml = new Scanner(new FileReader("D:\\tisr job\\kkb\\MERCHANT\\kkbsign_java\\template.xml"));
        String aContentsIn= templateXml.nextLine();
        String aContentsOut= aContentsIn.replaceAll("<department([^<]*)/>",
                "<department merchant_id=\"%merchant_id%\" abonent_id=\""+abonentId+"\" amount=\"%amount%\"/>")
                ;
        templateXml.close();
        File aFile=new File("D:\\tisr job\\kkb\\MERCHANT\\kkbsign_java\\template.xml");
        Writer output = new BufferedWriter(new FileWriter(aFile));
        try {
            output.write( aContentsOut );
        }
        finally {
            output.close();
        }



        String Base64Content=test.build64(
                "D:\\tisr job\\kkb\\MERCHANT\\kkbsign_java\\kkbsign.cfg",sum, ord);

        String ticket="<document><item number=\""+ord+"\" name=\"приказ N "+ord+"\" quantity=\"1\" amount=\""+sum+"\"/>" +
                "</document>";

        String send = new String( comp.Base64.encode((  ticket ).getBytes()));
        req.setAttribute("send",send);
        req.setAttribute("Base64Content", Base64Content);

        String send22 = "-------send22---"+ticket.replaceAll("<","===ttt====")+"---send22------";
        String Base64Content22="----Base64Content22---"+new String( comp.Base64.decode((  Base64Content.toCharArray() ))).replace("<","===ttt===")+"---Base64Content22----";
        req.setAttribute("send22",send22);
        req.setAttribute("Base64Content22", Base64Content22);
        req.getRequestDispatcher("/pay.jsp").forward(req, resp);

        */


        return;

    }

    void doPl(HttpServletRequest request, HttpServletResponse response) {

        //Вот что ВАЖНО !!!!!!!!!!! //Вот что ВАЖНО !!!!!!!!!!! //Вот что ВАЖНО !!!!!!!!!!!
        try {
            request.setCharacterEncoding("Cp1251"); //Вот что ВАЖНО !!!!!!!!!!!
            //Вот что ВАЖНО !!!!!!!!!!! //Вот что ВАЖНО !!!!!!!!!!! //Вот что ВАЖНО !!!!!!!!!!!

            System.out.println("dopl====");

            String logs = request.getParameter("response");

            logs = URLDecoder.decode(logs, "Cp1251");
            System.out.println("logs====" + logs);
            //System.out.println(System.getProperty("java.library.path") );
            ////снова в ХМЛ

            Document
                    confXml = DocumentHelper.parseText(logs);
            Element eBank = (Element) confXml.getRootElement().selectSingleNode("//bank");

            KKBSign ksig = new KKBSign();
            String textXml = eBank.asXML();
            String textSign = ((Element) confXml.getRootElement().selectSingleNode("//bank_sign")).getText();
            String ks = "C:\\bv\\kkb\\test.jks";
            textSign = textSign.replaceAll(" ", "+");
            String res = ksig.verify(textXml, textSign, ks, "kkbca", "nissan") + "";
            System.out.println("res==" + res); //our client!
            System.out.println("res.indexOf(true)=" + res.indexOf("true"));
             if (res.indexOf("true") == 0) {

                Element order = (Element) eBank.selectSingleNode("//order");
                String orderID = order.attributeValue("order_id");
                String amount = order.attributeValue("amount");

                System.out.println("orderId====" + orderID);
                System.out.println("amount====" + amount);

                Element departament = (Element) order.selectSingleNode("//department");
                String abonentId = departament.attributeValue("abonent_id");
                System.out.println("abonentId====" + abonentId);

                String rl = departament.attributeValue("rl");
                System.out.println("rl====" + rl);

                String reference = ((Element) confXml.getRootElement().selectSingleNode("//payment")).attributeValue("reference");
                String merchant_id = ((Element) confXml.getRootElement().selectSingleNode("//payment")).attributeValue("merchant_id");
                System.out.println("merchant_id====" + merchant_id);
                String payDate = ((Element) confXml.getRootElement().selectSingleNode("//results")).attributeValue("timestamp");
                String payDateNew = payDate.replace("-", ".").replace(" ", "_").replace(":", ".");
                System.out.println("payDateNew====" + payDateNew);

                 request.setAttribute("res", "0");


                 Driver myDriver = new oracle.jdbc.driver.OracleDriver();

                Connection conn = null;
                String insSQL = "insert into TISR_CLIENT_KKB_1C_LOG\n" +
                        "                (\n" +
                        "                        USER_INPUT_NAME,\n" +
                        "                        postlink_xml,\n" +

                        "                        ORDER_ID_KKB,\n" +

                        "                        ORDER_N_ERCB,\n" +
                        "                        REFERENCE_KKB,\n" +
                        "                        res_kkb, \n"+
                        "                        amount, \n"+
                        "                        BANK_ID_1C, \n"+
                        "                        APAYDATE_1C \n"+


                        "                    )\n" +
                        "values (?,?,?,?,?,?,?,?,?)";
                PreparedStatement pS = null;

                try {

                    DriverManager.registerDriver(myDriver);

                    String URL = "jdbc:oracle:thin:@ala-srv-db-tst1.tisr.kz:1521:Test01";
                    String USER = "ercb";
                    String PASS = "ercb";
                    conn = DriverManager.getConnection(URL, USER, PASS);
                    pS = conn.prepareStatement(insSQL);

                    pS.setString(1, abonentId);

                    CLOB clob = null;
                    clob = CLOB.createTemporary(conn, false, CLOB.DURATION_SESSION);
                    clob.setString(1, logs);
                    pS.setClob(2, clob);

                    pS.setString(3, orderID);
                    pS.setString(4, rl);
                    pS.setString(5, reference);
                    pS.setInt(6, 1);
                    pS.setString(7, amount);
                    pS.setString(8, "KZKOKZKX");
                    pS.setString(9, payDateNew);
                    //APAYDATE_1C

                    System.out.println("1 insSQL=====" + insSQL);
                    pS.executeUpdate();
                    System.out.println(" 1  pS.executeUpdate().......");
                    conn.commit();

                    Connection conn2 = null;
                    String insSQL2 = "update TISR_CLIENT_KKB_1C_LOG \n" +
                            "set " +
                            "RES_1C=? ,\n" +
                            "APPROVE_KKB=?  ,\n" +
                            "ERR_APPROVE_KKB=?  \n" +

                            "where ORDER_N_ERCB='" + rl + "'";
                    PreparedStatement pS2 = null;


                    try {
                        conn2 = DriverManager.getConnection(URL, USER, PASS);
                        pS2 = conn.prepareStatement(insSQL2);

                        System.out.println("conn2");

                        String res1C = "0";
                        String resAPPROVE_KKB = "0";


                            try {

                                    pS2.setString(1, res1C);

                                    //aprove kkb
                                    String approval_code = "122832";
                                    String commandType = "complete";
                                    HttpsURLConnectionR httpsURLConnectionR = new HttpsURLConnectionR();
                                    resAPPROVE_KKB = httpsURLConnectionR.sendGet(orderID, amount, reference, approval_code, commandType);
                                    pS2.setString(2, resAPPROVE_KKB);
                                    pS2.setString(3, "");
                                    //

                                System.out.println("insSQL2=====" + insSQL2);
                                pS2.executeUpdate();
                                System.out.println(" dopl end 2  pS.executeUpdate().......");
                                conn2.commit();


                            } catch (Exception e) {
                                pS2.setString(1, res1C);
                                pS2.setString(2, resAPPROVE_KKB);
                                pS2.setString(3, e.toString());
                                conn2.commit();

                            }
                            /*
                            finally {  //3
                                System.out.println("finnaly 3");
                                if (pS2 != null) {
                                    pS2.close();
                                }

                                if (conn2 != null) {
                                    conn2.close();
                                }
                            }
                            */


         /*   */



                   } //2
                    catch (Exception e) {
                        System.out.println("catch2");
                        if (pS2 != null) {
                            pS2.close();
                        }

                        if (conn2 != null) {
                            conn2.close();
                        }

                    }
                    /*
                    finally {  //2
                        System.out.println("finnaly 2");
                        if (pS2 != null) {
                            pS2.close();
                        }

                        if (conn2 != null) {
                            conn2.close();
                        }
                    }
                    */




                } catch (Exception e) {//1
                    System.out.println("catch1");
                    if (pS != null) {
                        try {
                            pS.close();
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    }

                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    }

                }
                /*
                finally {  //1
                    System.out.println("finnaly1 ");
                    if (pS != null) {
                        try {
                            pS.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
                 */




             }//if

            }catch(Exception e){ //0
                System.out.println("catch0  e.toString()=====" + e.toString());
                request.setAttribute("res", "1");
                System.out.println("eeeXCep   =" + request.getAttribute("res").toString());

            }

        /*
        finally{ //0
                System.out.println("finaly0");
            }
        */

    }










    void doPl_err(HttpServletRequest request, HttpServletResponse response){

        //Вот что ВАЖНО !!!!!!!!!!! //Вот что ВАЖНО !!!!!!!!!!! //Вот что ВАЖНО !!!!!!!!!!!
        try {
            request.setCharacterEncoding("Cp1251"); //Вот что ВАЖНО !!!!!!!!!!!
            //Вот что ВАЖНО !!!!!!!!!!! //Вот что ВАЖНО !!!!!!!!!!! //Вот что ВАЖНО !!!!!!!!!!!

            System.out.println("dopl_err====");

            String logs=request.getReader().readLine();

            logs= URLDecoder.decode(logs, "Cp1251");
            System.out.println("logs===="+logs);
            //System.out.println(System.getProperty("java.library.path") );
            ////снова в ХМЛ

            Document
                    confXml=DocumentHelper.parseText(logs);
            Element eBank=(Element)confXml.getRootElement().selectSingleNode("//response");
            String orderID=eBank.attributeValue("order_id");
            Element sess= (Element)eBank.selectSingleNode("//session");
            String sessID=sess.attributeValue("id");

            System.out.println("sessId===="+sessID);

            Driver myDriver = new oracle.jdbc.driver.OracleDriver();
            DriverManager.registerDriver( myDriver );

            String URL = "jdbc:oracle:thin:@ala-srv-db-tst1.tisr.kz:1521:Test01";
            String USER = "ercb";
            String PASS = "ercb";
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            String insSQL = "insert into TISR_KLIENT_LOG_KKB_ERR\n" +
                    "                (\n" +
                    "                        session_id,\n" +
                    "                        failurePostLink_xml,\n" +
                    "                        order_id\n" +
                    "                    )\n" +
                    "values (?,?,?)";
            PreparedStatement pS=conn.prepareStatement(insSQL);
            pS.setString(1, sessID);

            CLOB clob = null;
            clob = CLOB.createTemporary(conn, false, CLOB.DURATION_SESSION);
            clob.setString(1, logs);
            pS.setClob(2, clob);

            pS.setString(3, orderID);

            pS.executeUpdate();
            System.out.println("   Err pS.executeUpdate().......");
            conn.commit();
            if (pS != null) {
                pS.close();
            }

            if (conn != null) {
                conn.close();
            }

         /*   */
        } catch (Exception e) {
            System.out.println("e.toString()=====" + e.toString());
            System.out.println("eeeXCep   ="+request.getAttribute("res").toString());

        }


    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
