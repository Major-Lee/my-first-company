import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingVapDTO;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * Created by bluesand on 6/4/15.
 */
public class CmdValidate {
    public static void main(String[] args) throws Exception{
        String opt = "151";
        String subopt = "03";
        String  extparams = " {\"auth_key\":\"zj1234567890\",\"ssid\":\"urouter_test_曾娟\",\"auth\":\"WPA\\/WPA2-PSK\"}";
        if (OperationCMD.ModifyDeviceSetting.getNo().equals(opt)) {
            if (OperationDS.DS_VapPassword.getNo().equals(subopt)) {
                WifiDeviceSettingVapDTO wifiDeviceSettingVapDTO =
                        JsonHelper.getDTO(extparams, WifiDeviceSettingVapDTO.class);

                if (wifiDeviceSettingVapDTO == null) {
                    //非法格式
                    //SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
                    return;
                } else {
                    String ssid = wifiDeviceSettingVapDTO.getSsid();
                    if (ssid == null) {
                        //非法格式
                        //SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
                        return;
                    }
                    String auth = wifiDeviceSettingVapDTO.getAuth();
                    //TODO(bluesand):此处auth：  WPA/WPA2-PSK / open

                    if (!"WPA/WPA2-PSK".equals(auth) && !"open".equals(auth)) {
                        //SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
                        return;
                    } else {
                        if ("WPA/WPA2-PSK".equals(auth)) {
                            String auth_key = wifiDeviceSettingVapDTO.getAuth_key();
                            if (auth_key == null) {
                                //SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
                                return;
                            }
                            if (auth_key.getBytes("utf-8").length < 8 || auth_key.getBytes("utf-8").length > 32) {
                                //非法长度
                                //SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR));
                                return;
                            }
                        }
                        if ("open".equals(auth)) {
                            //nothing
                        }
                    }
                }

            }
        }
    }
}
