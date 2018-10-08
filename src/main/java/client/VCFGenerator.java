package client;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;
import exception.VCFException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * @Author ：yaxuSong
 * @Description:
 * @Date: 19:07 2018/10/8
 * @Modified by:
 */
@Slf4j
public class VCFGenerator {

    private static final String VCARD_BEGIN = "BEGIN:VCARD\r\n";

    private static final String VCARD_VERSION = "VERSION:2.1\r\n";

    private static final String VCARD_END = "END:VCARD\r\n";

    private static final String VCARD_GROUP_REPLACE = "X-GROUP-MEMBERSHIP:#\r\n";

    private static final String VCARD_NAME_REPLACE = "N;CHARSET=UTF-8;ENCODING=QUOTED-PRINTABLE:;#;;;\r\n";

    private static final String VCARD_FNAME_REPLACE = "FN;CHARSET=UTF-8;ENCODING=QUOTED-PRINTABLE:#\r\n";

    private static final String VCARD_TEL = "TEL;";

    private static final String VCARD_CELL_REPLACE = "CELL:#\r\n";

    private static final String REPLACE_WORD = "#";

    private String getQuotedPrintableString(String original) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStream encodedOut = MimeUtility.encode(baos, "quoted-printable");
            encodedOut.write(original.getBytes());
            String encoded = baos.toString();
            return encoded;
        }catch (Exception e){
            log.error("转换字符编码失败");
            throw new VCFException("转换字符编码失败");
        }
    }

    public String genVCFString(String name,String fName,String phone,String group){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(VCARD_BEGIN);
        stringBuilder.append(VCARD_VERSION);
        stringBuilder.append(VCARD_NAME_REPLACE.replace(REPLACE_WORD,getQuotedPrintableString(name)));
        stringBuilder.append(VCARD_FNAME_REPLACE.replace(REPLACE_WORD,getQuotedPrintableString(fName)));
        stringBuilder.append(VCARD_TEL).append(VCARD_CELL_REPLACE.replace(REPLACE_WORD,phone));
        stringBuilder.append(VCARD_GROUP_REPLACE.replace(REPLACE_WORD,group));
        stringBuilder.append(VCARD_END);
        return stringBuilder.toString();
    }
}
