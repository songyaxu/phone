package service;

import bean.PhoneResult;
import com.alibaba.fastjson.JSONObject;
import utils.HttpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author ：yaxuSong
 * @Description:
 * @Date: 11:27 2018/10/8
 * @Modified by:
 */
public class PhoneSearch {

    private static final String URL = "http://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=PHONE";

    private static final String PHONE = "PHONE";

    private static final String DEFAULT_PREFIX = "00000";

    private static final String DEFAULT_PHONE_PREFIX = "150";

    private static final String DEFAULT_PHONE_POSTFIX = "222";

    private static final String REPLACE_STR = "__GetZoneResult_ = ";


    public static String getPhoneNumber(String phoneMidNumber) {
        phoneMidNumber = DEFAULT_PREFIX + phoneMidNumber;
        phoneMidNumber = phoneMidNumber.substring(phoneMidNumber.length()-5,phoneMidNumber.length());
        //System.out.println(phoneMidNumber);
        String phoneNumber = DEFAULT_PHONE_PREFIX + phoneMidNumber + DEFAULT_PHONE_POSTFIX;
        return phoneNumber;
    }

    public static void main(String[] args) {
        List<PhoneResult> phoneInnerMongoliaResults = new ArrayList<PhoneResult>();
        List<String> failureLists = new ArrayList<String>();
        int success = 0;
        int failure = 0;
        for (int i= 0 ;i<= 99999; i++){
            String phoneMidNumber = String.valueOf(i);
            String phone = getPhoneNumber(phoneMidNumber);
            String url = URL.replace(PHONE, phone);
            try {
                System.out.println("查询 ：" + i);
                String result = HttpUtil.post(url, null);
                PhoneResult phoneResult = JSONObject.parseObject(result.replace(REPLACE_STR,""),PhoneResult.class);
                if (phoneResult.getProvince().contains("内蒙")) {
                    phoneInnerMongoliaResults.add(phoneResult);
                }
                success += 1;
            }catch (Exception e){
                failureLists.add(phone);
                System.out.println("查询失败");
                failure += 1;
            }
        }
        for (int i = 0; i<phoneInnerMongoliaResults.size();i++){
            System.out.println(phoneInnerMongoliaResults.get(i));
        }
        System.out.println("成功数："+success+",失败数："+failure);
        for (String s : failureLists) {
            System.out.println("失败的手机号码: "+ s);
        }
    }
}
