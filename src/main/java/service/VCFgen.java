package service;

import bean.PhoneResult;
import client.VCFGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author ：yaxuSong
 * @Description:
 * @Date: 19:16 2018/10/8
 * @Modified by:
 */
public class VCFgen {


    public List<PhoneResult> getPhoneResultList(String fileLocation){
        List<PhoneResult> resultList= new ArrayList<PhoneResult>();
        File file = new File(fileLocation);
        while ()
    }

    public static void main(String[] args) {
        VCFGenerator client = new VCFGenerator();
        System.out.println(client.genVCFString("白","白","15326085176","测试"));
    }
}
