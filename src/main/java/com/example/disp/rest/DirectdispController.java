package com.example.disp.rest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.servlet.http.HttpServletRequest;
import java.lang.Exception;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.Data;
import net.minidev.json.*;


@RestController
@Data
public class DirectdispController{

   final static Logger logger = LoggerFactory.getLogger(DirectdispController.class);
   Object ob =new Object();
   

@PostMapping(value = "/getns")
public JSONObject getns(@RequestBody String d, HttpServletRequest request) throws SQLException, IOException, NullPointerException  {
    
            ObjectMapper mapper = new  ObjectMapper();

            JsonNode node = mapper.readValue(d, JsonNode.class);
            String fields = node.get("fields").toString();
            Map<String, Object> map = mapper.readValue(fields, Map.class);
            Set<String> strmap = map.keySet();
            String strKey = strmap.toString().substring(1, strmap.toString().length()-1).replaceAll(", ", ", ");

            String cmd = node.get("cmd").asText();
            String table_name = node.get("table_name").asText();  
            String kpl = " ";
            String name = " ";
            String adr = " ";
            String kmc = " ";
            String kpr = " ";
            String code = " ";

            // Запрос данных по Where    
           try {
            if(node.get("fields").path("KPL").path("value").textValue() == null){
                kpl = ""; 
            } else{kpl = " kpl='" + node.get("fields").path("KPL").path("value").asText() + "'" + " and ";}

            if(node.get("fields").path("ADR").path("value").textValue() == null){
                adr = "";
            } else{adr = " adr like '%"+ node.get("fields").path("ADR").path("value").asText() +"%'" + " and ";}
            
            if (node.get("fields").path("NAME").path("value").textValue() == null) {
                name = "";           
            } else {name = node.get("fields").get("NAME").get("value").asText();}
             code = "200";
           } catch (Exception e) {
             code = "500";
           }
            String row_count = node.get("row_count").asText() + " ";
            System.out.println("SELECT top " + row_count + strKey + " FROM " + table_name + " where" + kpl + kmc + kpr + adr + " name like '%"+ name +"%'");
            String connectionUrl =
            "jdbc:sqlserver://10.35.0.1;"
                    + "databaseName=DISP;"
                    + "user=javadev1;"
                    + "password=Dev1298$;"
                    + "encrypt=true;"
                    + "trustServerCertificate=true;"
                    + "loginTimeout=30;";

            Connection connection = DriverManager.getConnection(connectionUrl);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT top " + row_count + strKey + " FROM " + table_name + " where" + kpl + kmc + kpr + adr + " name like '%"+ name +"%'");           
            ResultSetMetaData md = resultSet.getMetaData();

            int numCols = md.getColumnCount();
            List<String> colNames = IntStream.range(0, numCols).mapToObj(i -> {
                try {
                    return md.getColumnName(i + 1);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return "?";
                }
            })
            .collect(Collectors.toList());

            JSONArray result = new JSONArray();
        
        while (resultSet.next()) {   
            JSONObject row = new JSONObject();
            
            colNames.forEach(cn -> {
                try {
                    row.put(cn, resultSet.getObject(cn));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            result.add(row);
        } 
        JSONObject jsonObject = new JSONObject();
        jsonObject
        .appendField("cmd", cmd)
        .appendField("table_name", table_name)
        .appendField("status", code)
        .appendField("error", "")
        .appendField("rows", result);
        return jsonObject;            
    }



    @PostMapping(value = "/regtprp")
public JSONObject regtprp(@RequestBody String p) throws SQLException, IOException, NullPointerException  {
    
            ObjectMapper mapper = new  ObjectMapper();

            JsonNode node = mapper.readTree(p);
            String arrayTPRP = node.get("BD_TPRP").toString();
            JSONArray map = mapper.readValue(arrayTPRP, JSONArray.class);
            String strmap = map.toString().substring(1, map.toString().length()-1).replaceAll(", ", ", ");
            JsonNode node2 = mapper.readValue(strmap, JsonNode.class);
            
            //
            String cmd = node.path("cmd").asText();
            String kpl = node.path("KPL").asText().toString();
            String kpp = node.path("KPP").asText().toString();
            String stp = node.path("STP").asText().toString();
            System.out.println(kpp);
            //
            String kmc = node2.path("KMC").asText();
            String dtv = node2.path("DTV").asText();
            String dtg = node2.path("DTG").asText();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");                
            String date1 = LocalDate.parse(dtv, formatter).toString();
            String date2 = LocalDate.parse(dtg, formatter).toString();
            System.out.println(date1);
            System.out.println(date2);
            String kpr = node2.path("KPR").asText();
            Integer krn = node2.path("KRN").asInt();
            Integer kole = node2.path("KOLE").asInt();
            String kmg = node2.path("KMG").asText();          
            
            
            String connectionUrl =
            "jdbc:sqlserver://10.35.0.1;"
                    + "databaseName=DISP;"
                    + "user=javadev1;"
                    + "password=Dev1298$;"
                    + "encrypt=true;"
                    + "trustServerCertificate=true;"
                    + "loginTimeout=30;";

            Connection connection = DriverManager.getConnection(connectionUrl);
            Statement statement = connection.createStatement();
            
            String status = "";
            String error = "";
           try {
            String dateTime = DateTimeFormatter.ofPattern("yyyyMMdd hh:mm:ss a")
                    .format(LocalDateTime.now());
            
            statement.execute("SET NOCOUNT ON; INSERT INTO BD_ZPRP (SYSN, [KPL], [KPP], DT, STP) VALUES (1, '" + kpl +"', '" + kpp +"', '" + dateTime +"', '" + stp +"' );");
            
            ResultSet rs = statement.executeQuery("SELECT @@IDENTITY AS F_ID");
            int F_ID = 0;
            while (rs.next()) {
                 F_ID = rs.getInt(1);
            }
            System.out.println("F_ID ___________________ " + F_ID);
            System.out.println("INSERT INTO BD_TPRP (SYSN, KMC, DTV, DTG, KPRP1, KRN, KOLE, KMG) VALUES (" + F_ID +", " + kmc +", '" + date1 +"', '" + date2 +"', " + kpr +", " + krn +", " + kole +", " + kmg +")");          
            statement.execute("UPDATE BD_ZPRP SET SYSN='" + F_ID + "' WHERE F_ID='" + F_ID + "'");
            statement.execute("INSERT INTO BD_TPRP (SYSN, KMC, DTV, DTG, KPRP1, KRN, KOLE, KMG) VALUES (" + F_ID +", " + kmc +", '" + dtv +"', '" + dtg +"', " + kpr +", " + krn +", " + kole +", " + kmg +")");
             status = "200";
             //connection.commit();
        } catch (SQLException e) {
            System.out.println(e);
            error = e.toString();
            status = "500";
           }
           JSONObject jsonObject = new JSONObject();
           jsonObject.appendField("cmd", cmd)
           .appendField("status", status)
           .appendField("error", error);
            
            return jsonObject;
           
    }


}


