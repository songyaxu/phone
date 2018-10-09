package service;

import bean.PhoneResult;
import client.VCFGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author ：yaxuSong
 * @Description:
 * @Date: 19:16 2018/10/8
 * @Modified by:
 */
public class VCFgen {

    private String test = "PhoneResult(mts=1810046, province=内蒙古, catName=中国电信, telString=18100461272, areaVid=30495, ispVid=138238560, carrier=内蒙古电信)";

    private List<PhoneResult> getPhoneResultList(String fileLocation){
        List<PhoneResult> resultList= new ArrayList<PhoneResult>();
        try {
            FileInputStream fileInputStream = new FileInputStream(fileLocation);
            JsonReader jsonReader = new JsonReader(new InputStreamReader(fileInputStream,"UTF-8"));
            Gson gson = new GsonBuilder().create();
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                PhoneResult result = gson.fromJson(jsonReader, PhoneResult.class);
                resultList.add(result);
                System.out.println("读取成功 ：" + result);
            }
            jsonReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("找不到指定文件：" + fileLocation);
        } catch (UnsupportedEncodingException e) {
            System.out.println("不支持的编码格式");
        } catch (IOException e){
            System.out.println("输入流异常");
        }
        return resultList;
    }

    public static void main(String[] args) {
        VCFgen vcFgen = new VCFgen();
        List<PhoneResult> resultList = vcFgen.getPhoneResultList("C:\\Users\\yaxuSong\\Desktop\\vcf.json");
        System.out.println(resultList.size());
    }
}
