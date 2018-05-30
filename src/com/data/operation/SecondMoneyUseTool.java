package com.data.operation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecondMoneyUseTool {


    public static void main(String[] args)throws Exception{

        if(args==null){
            System.exit(0);
        }

        File in = new File("use_money.txt");
        File out = new File("money.sql");

        List<String> delList = new ArrayList<String>();
        List<String> firstMoneyList = new ArrayList<String>();
        List<String> secondMoneyList = new ArrayList<String>();

        String dataStr = new SimpleDateFormat("yyyyMMdd").format(new Date());

        String planSql = "INSERT INTO tb_ygonline_plan (id, plan_id, status, source_type, match_method,create_time ) VALUES ("+dataStr+", -"+dataStr+", 1, 3, '1', now());";

        String transSql = "UPDATE tb_creditor_info SET business_type = 2, path_id = "+dataStr+" WHERE business_type = 0 AND match_state = 0 AND creditor_rights_status = 1 AND create_time < '"+args[0]+"';";

        if(out.isFile()&&out.exists()){
            out.delete();
        }

        BufferedReader reader = new BufferedReader(new FileReader(in));
        FileWriter fw = new FileWriter(out);
        String tempString = null;

        Map<String,KvIn> onlyMap = new HashMap<String,KvIn>();
        List<KvIn> hangList = new ArrayList<KvIn>();

        while ((tempString = reader.readLine()) != null) {
            tempString = new String(tempString.getBytes(),"UTF-8");
            String rsp = null;
            Pattern p = Pattern.compile("(.*)\t(.*)\t(.*)");

            Matcher m = p.matcher(tempString);

            if(m.find()){
                if(m.group(1).replaceAll("\\s","").equals("")){
                    continue;
                }
                KvIn param = new KvIn();
                param.setKey(m.group(1));
                param.setValue(m.group(3));
                if(onlyMap.containsKey(m.group(2))){
                    hangList.add(param);
                }else{
                    onlyMap.put(m.group(2),param);
                }

            }

        }

        int line = 1;
            for (Map.Entry tmp: onlyMap.entrySet()) {
                KvIn kvIn = (KvIn)tmp.getValue();
                System.out.println("第"+line+"条 开始");
                line++;
                String rsp = null;
                    delList.add("DELETE FROM tb_inverst_creditor_relation WHERE id = "+kvIn.getKey()+";");
                    if(!kvIn.getValue().trim().equals("0.00")&&!kvIn.getValue().trim().equals("0")){
                        firstMoneyList.add("update tb_loan_current_receivable set match_method=1,plan_id='-"+dataStr+"',flag=1,match_state=0,no_match_funds="+kvIn.getValue().replaceAll(",","")+",current_receivable_amount="+kvIn.getValue().replaceAll(",","")+" where id="+kvIn.getKey()+";");

                    }else{
                        secondMoneyList.add("update tb_loan_current_receivable r set r.match_state = 2, r.match_method = 0, r.flag = 0, r.remark = '"+dataStr+"_二级资金挂起' where r.id ="+kvIn.getKey()+";");
                    }
            }

        System.out.println("掛起重複======");

        line = 1;
        for (KvIn kvIn:hangList) {
            System.out.println("掛起第"+line+"条");
            line++;
                secondMoneyList.add("update tb_loan_current_receivable r set r.match_state = 2, r.match_method = 0, r.flag = 0, r.remark = '"+dataStr+"_二级资金挂起' where r.id ="+kvIn.getKey()+";");
        }


            fw.write(planSql);
            fw.write("\n");

        for(String tmp:firstMoneyList){

            fw.write(tmp);
            fw.write("\n");
        }

        fw.write(transSql);
        fw.write("\n");

        for(String tmp:secondMoneyList){

            fw.write(tmp);
            fw.write("\n");
        }


        reader.close();
        fw.flush();
        fw.close();
        System.out.println("完成");

    }


}

class KvIn{
    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
