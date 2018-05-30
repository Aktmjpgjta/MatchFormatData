package com.data.operation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataMatchTest {


    public static void main(String[] args)throws Exception{

        File in = new File("match_in.txt");
        File out = new File("back.sql");

        List<String> delList = new ArrayList<String>();
        List<String> firstMoneyList = new ArrayList<String>();
        List<String> firstCreditorList = new ArrayList<String>();
        List<String> secondMoneyList = new ArrayList<String>();
        List<String> secondCreditorList = new ArrayList<String>();
        if(out.isFile()&&out.exists()){
            out.delete();
        }

        BufferedReader reader = new BufferedReader(new FileReader(in));
        FileWriter fw = new FileWriter(out);
        String tempString = null;


        int line = 1;
        if(args[0].equals("busType")){
            while ((tempString = reader.readLine()) != null) {
                tempString = new String(tempString.getBytes(),"UTF-8");
                System.out.println("第"+line+"条 开始   ");
                line++;
                String rsp = null;
                Pattern p = Pattern.compile("(.*)\t(.*)\t(.*)\t(.*)\t(.*)\t(.*)\t(.*)");

                Matcher m = p.matcher(tempString);
                if(m.find()){
                    delList.add("DELETE FROM tb_inverst_creditor_relation WHERE id = "+m.group(1)+";");
                    if(m.group(6).equals("1")){
                        firstMoneyList.add("UPDATE tb_invest_info SET matched_funds = matched_funds - "+m.group(3)+", " +
                                "no_match_funds = no_match_funds + "+m.group(3)+", push_ygzx_status = 0, match_state = '1'  WHERE id = "+m.group(2)+";");

                    }
                    if(m.group(7).equals("1")) {
                        firstCreditorList.add("update tb_creditor_info set business_type=1,end_date = null, not_matched_amount = not_matched_amount + " + m.group(3) + ", " +
                                "matched_amount = matched_amount - " + m.group(3) + ", match_state = '1', is_send_ly = '0', " +
                                "rest_share = IFNULL(rest_share,0) + " + m.group(5) + " where id = " + m.group(4) + ";");
                    }
                    if(m.group(6).equals("2")){
                        secondMoneyList.add("update tb_loan_current_receivable set match_state = 1, no_match_funds = no_match_funds + "+m.group(3)+", " +
                                "matchen_funds = matchen_funds - "+m.group(3)+", push_ygzx_status = 0, match_method = 0 where id = "+m.group(2)+";`");

                    }
                    if(m.group(7).equals("2")) {
                        secondCreditorList.add("update tb_creditor_rights_assignment set no_match_funds = no_match_funds + "+m.group(3)+", " +
                                "matchen_funds = matchen_funds - "+m.group(3)+", match_state = '1', is_send_ly = '0', " +
                                "rest_share = IFNULL(rest_share,0) + "+m.group(5)+" where id = " + m.group(4) + ";");
                    }
                }

            }
        }else{
            while ((tempString = reader.readLine()) != null) {
                tempString = new String(tempString.getBytes(),"UTF-8");
                System.out.println("第"+line+"条 开始   ");
                line++;
                String rsp = null;
                Pattern p = Pattern.compile("(.*)\t(.*)\t(.*)\t(.*)\t(.*)\t(.*)\t(.*)");

                Matcher m = p.matcher(tempString);
                if(m.find()){
                    delList.add("DELETE FROM tb_inverst_creditor_relation WHERE id = "+m.group(1)+";");
                    if(m.group(6).equals("1")){
                        firstMoneyList.add("UPDATE tb_invest_info SET matched_funds = matched_funds - "+m.group(3)+", " +
                                "no_match_funds = no_match_funds + "+m.group(3)+", push_ygzx_status = 0, match_state = '1'  WHERE id = "+m.group(2)+";");

                    }
                    if(m.group(7).equals("1")) {
                        firstCreditorList.add("update tb_creditor_info set end_date = null, not_matched_amount = not_matched_amount + " + m.group(3) + ", " +
                                "matched_amount = matched_amount - " + m.group(3) + ", match_state = '1', is_send_ly = '0', " +
                                "rest_share = IFNULL(rest_share,0) + " + m.group(5) + " where id = " + m.group(4) + ";");
                    }
                    if(m.group(6).equals("2")){
                        secondMoneyList.add("update tb_loan_current_receivable set match_state = 1, no_match_funds = no_match_funds + "+m.group(3)+", " +
                                "matchen_funds = matchen_funds - "+m.group(3)+", push_ygzx_status = 0, match_method = 0 where id = "+m.group(2)+";");

                    }
                    if(m.group(7).equals("2")) {
                        secondCreditorList.add("update tb_creditor_rights_assignment set no_match_funds = no_match_funds + "+m.group(3)+", " +
                                "matchen_funds = matchen_funds - "+m.group(3)+", match_state = '1', is_send_ly = '0', " +
                                "rest_share = IFNULL(rest_share,0) + "+m.group(5)+" where id = " + m.group(4) + ";");
                    }
                }

            }
        }


        for(String tmp:delList){

            fw.write(tmp);
            fw.write("\n");
        }

        for(String tmp:firstMoneyList){

            fw.write(tmp);
            fw.write("\n");
        }

        for(String tmp:firstCreditorList){

            fw.write(tmp);
            fw.write("\n");
        }

        for(String tmp:secondMoneyList){

            fw.write(tmp);
            fw.write("\n");
        }

        for(String tmp:secondCreditorList){

            fw.write(tmp);
            fw.write("\n");
        }

        reader.close();
        fw.flush();
        fw.close();
        System.out.println("完成");

    }


}
