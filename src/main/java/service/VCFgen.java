package service;

import bean.PhoneResult;
import client.VCFGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

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

    private static final String TEST = "测试";

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

    private void genVCFFile(String outputFilePath,String inputFilePath){
        try {
            File file = new File(outputFilePath);
            FileOutputStream outputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            VCFGenerator client = new VCFGenerator();
            List<PhoneResult> resultList = getPhoneResultList(inputFilePath);
            for (int i = 0;i< resultList.size();i++){
                String name = TEST+(i+1);
                String fName = TEST+(i+1);
                PhoneResult phoneResult = resultList.get(i);
                String writeStr = client.genVCFString(name,fName,phoneResult.getTelString(),TEST);
                bufferedOutputStream.write(writeStr.getBytes());
            }
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (IOException e){
            System.out.println("写入文件IO异常");
        } catch (Exception e){
            System.out.println("执行异常");
        }
    }

    public static void main(String[] args) {
        VCFgen vcFgen = new VCFgen();
        vcFgen.genVCFFile("C:\\Users\\yaxuSong\\Desktop\\test.vcf","C:\\Users\\yaxuSong\\Desktop\\templatevcf.json");
    }
}
