package test;

import comp.KKBSign;
import kz.kkb.remote.HttpsURLConnectionR;
import oracle.sql.CLOB;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.net.URLDecoder;
import java.sql.*;


public class DoPl {

    public static void doPl(){

        //Вот что ВАЖНО !!!!!!!!!!! //Вот что ВАЖНО !!!!!!!!!!! //Вот что ВАЖНО !!!!!!!!!!!
        try {
            //request.setCharacterEncoding("Cp1251"); //Вот что ВАЖНО !!!!!!!!!!!
            //Вот что ВАЖНО !!!!!!!!!!! //Вот что ВАЖНО !!!!!!!!!!! //Вот что ВАЖНО !!!!!!!!!!!

            System.out.println("dopl====");

            String logs = "<document><bank name=\"Kazkommertsbank JSC\"><customer name=\"fgff\" mail=\"sabdikalikov@tisr.kz\" phone=\"\"><merchant cert_id=\"00C182B189\" name=\"null\"><order order_id=\"399849\" amount=\"200\" currency=\"398\"><department merchant_id=\"92061101\" amount=\"200\" abonent_id=\"IIN745896125463\" rl=\"15-307589\"/></order></merchant><merchant_sign type=\"RSA\"/></customer><customer_sign type=\"RSA\"/><results timestamp=\"2015-07-16 10:09:21\"><payment merchant_id=\"92061101\" card=\"440564-XX-XXXX-6150\" amount=\"200\" reference=\"150716100916\" approval_code=\"100916\" recur=\"1\" response_code=\"00\" Secure=\"Yes\" card_bin=\"KAZ\" c_hash=\"13988BBF7C6649F799F36A4808490A3E\"/></results></bank><bank_sign cert_id=\"00C18327E8\" type=\"SHA/RSA\">zdmRFcdkm8RTqYM2UYP4qTszxQJf6vSli8Y0T3ywmfAwcDjQr6CxP1qZutvIHP5cindw78vT/8rXWJsPVoeRsxOZ58VEjwOcjIxlRNd0FsQFR15jaTCUec0etl0J1bursRHT2PAwdns4ACLdewP6X4I0SwaHEKgjc8QqcZ/1HGk=</bank_sign></document>\n";//request.getParameter("response");

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

                //request.setAttribute("res", "0");


                Driver myDriver = new oracle.jdbc.driver.OracleDriver();

                Connection conn = null;
                String insSQL = "insert into TISR_CLIENT_KKB_1C_LOG\n" +
                        "                (\n" +
                        "                        USER_INPUT_NAME,\n" +
                        "                        postlink_xml,\n" +

                        "                        ORDER_ID_KKB,\n" +

                        "                        ORDER_N_ERCB,\n" +
                        "                        REFERENCE_KKB,\n" +
                        "                        res_kkb \n"+

                        "                    )\n" +
                        "values (?,?,?,?,?,?)";
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
            //request.setAttribute("res", "1");
            //System.out.println("eeeXCep   =" + request.getAttribute("res").toString());

        }

        /*
        finally{ //0
                System.out.println("finaly0");
            }
        */


    }


    public static void main(String[] args) {

        DoPl.doPl();

    }

}
