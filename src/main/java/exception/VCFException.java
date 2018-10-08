package exception;

import lombok.Getter;

/**
 * @Author ：yaxuSong
 * @Description:
 * @Date: 19:29 2018/10/8
 * @Modified by:
 */
@Getter
public class VCFException extends RuntimeException{

    private int               code;
    private String            message;

    public VCFException(String message){
        this.code = -1;
        this.message = message;
    }

    public VCFException(String message, Throwable e) {
        super(message,e);
        this.code = -1;
        this.message = message;
    }


    public VCFException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
