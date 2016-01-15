package kz.kkb.remote;

import oracle.sql.CLOB;

import java.math.BigDecimal;
import java.sql.*;

public class Lib {

    public static String checkSS(String ercb_orderId,Connection conn){

        String order_n_ercb=null;
        String selSQL= "select t.order_n from D_A9_ACCOUNT t where t.id='" + ercb_orderId + "'";
        //int c=0;
        PreparedStatement pS =null;
     System.out.println(" LLIB ercb_orderId="+ercb_orderId);
        //if (ercb_order.startsWith("SS")){

            try {

                pS = conn.prepareStatement(selSQL);
                ResultSet rs = pS.executeQuery(selSQL);
           System.out.println(" LIB User selSQL.executeQ()="+selSQL);
                conn.commit();

                while (rs.next()) {
                    order_n_ercb = rs.getString(1);
                };
            System.out.println("LIB order_n_ercb="+order_n_ercb);

                if (pS != null) {
                    pS.close();
                }

                if (order_n_ercb!=null&conn != null) {
                    conn.close();
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }

         System.out.println(" LLIB return order_n_ercb="+order_n_ercb);

            if (order_n_ercb!=null){return order_n_ercb;}
            //return false;
        //}

        selSQL= "select t.order_n from D_P4_ORDERINFO t where t.id='" + ercb_orderId + "'";
        try {

            pS = conn.prepareStatement(selSQL);
            ResultSet rs = pS.executeQuery(selSQL);
            System.out.println(" Lib 222 User selSQL.executeQ()="+selSQL);
            conn.commit();

            while (rs.next()) {
                order_n_ercb = rs.getString(1);
            };
            System.out.println("LIb 222 order_n_ercb="+order_n_ercb);

            if (pS != null) {
                pS.close();
            }

            if (conn != null) {
                conn.close();
            }


        } catch (SQLException e) {
            System.out.println("catch LIB");
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


        System.out.println(" LLIB 222 return order_n_ercb="+order_n_ercb);

        if (order_n_ercb!=null){return order_n_ercb;}
        return order_n_ercb;

    }


}
