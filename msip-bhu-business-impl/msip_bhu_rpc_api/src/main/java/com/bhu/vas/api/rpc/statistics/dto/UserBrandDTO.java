package com.bhu.vas.api.rpc.statistics.dto;

import com.smartwork.msip.cores.helper.JsonHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bluesand on 5/23/15.
 */
public class UserBrandDTO implements Serializable {

    private String brand;

    private int count;

    private String ratio;

    private List<UserBrandSubDTO> detail;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public List<UserBrandSubDTO> getDetail() {
        return detail;
    }

    public void setDetail(List<UserBrandSubDTO> detail) {
        this.detail = detail;
    }


    public static void main(String[] args) {
        List<UserBrandSubDTO> userBrandSubDTOList = new ArrayList<UserBrandSubDTO>();

        UserBrandSubDTO userBrandSubDTO = new UserBrandSubDTO();
        userBrandSubDTO.setBrand("Sumsang T1");
        userBrandSubDTO.setCount(10);
        userBrandSubDTO.setRatio("10%");
        userBrandSubDTOList.add(userBrandSubDTO);

        userBrandSubDTO = new UserBrandSubDTO();
        userBrandSubDTO.setBrand("Sumsnag T2");
        userBrandSubDTO.setCount(12);
        userBrandSubDTO.setRatio("21%");
        userBrandSubDTOList.add(userBrandSubDTO);

        userBrandSubDTO = new UserBrandSubDTO();
        userBrandSubDTO.setBrand("Sumsnag T3");
        userBrandSubDTO.setCount(23);
        userBrandSubDTO.setRatio("23%");
        userBrandSubDTOList.add(userBrandSubDTO);

        UserBrandDTO userBrandDTO = new UserBrandDTO();

        userBrandDTO.setBrand("Sumsang");

        userBrandDTO.setCount(45);
        userBrandDTO.setRatio("24%");

        userBrandDTO.setDetail(userBrandSubDTOList);

        System.out.println(JsonHelper.getJSONString(userBrandDTO));

        List<UserBrandDTO> userBrandDTOs = new ArrayList<UserBrandDTO>();
        userBrandDTOs.add(userBrandDTO);

        System.out.println(JsonHelper.getJSONString(userBrandDTOs));


    }
}
