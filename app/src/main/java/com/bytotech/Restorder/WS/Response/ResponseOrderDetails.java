package com.bytotech.Restorder.WS.Response;

import java.util.List;

public class ResponseOrderDetails {

    public String message;
    public int code;
    public List<orderList> order_list;

    public class orderList{
        public String order_unique_id;
        public String order_date;
        public String status;
        public String total_price;
        public String menu_id;
        public String menu_name;
        public String menu_price;
        public String menu_qty;
        public String menu_image;
        public String tax;
    }
}