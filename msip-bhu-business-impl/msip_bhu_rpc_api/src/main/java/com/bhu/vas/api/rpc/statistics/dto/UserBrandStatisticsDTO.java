package com.bhu.vas.api.rpc.statistics.dto;

import com.smartwork.msip.cores.helper.JsonHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bluesand on 5/23/15.
 */
public class UserBrandStatisticsDTO implements Serializable {

    private String brand;

    private int count;

    private String ratio;

    private List<UserBrandDTO> detail;

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

    public List<UserBrandDTO> getDetail() {
        return detail;
    }

    public void setDetail(List<UserBrandDTO> detail) {
        this.detail = detail;
    }


    public static void main(String[] args) {
        List<UserBrandDTO> userBrandDTOList = new ArrayList<UserBrandDTO>();

        UserBrandDTO userBrandDTO = new UserBrandDTO();
        userBrandDTO.setBrand("Sumsang T1");
        userBrandDTO.setCount(10);
        userBrandDTO.setRatio("10%");
        userBrandDTOList.add(userBrandDTO);

        userBrandDTO = new UserBrandDTO();
        userBrandDTO.setBrand("Sumsnag T2");
        userBrandDTO.setCount(12);
        userBrandDTO.setRatio("21%");
        userBrandDTOList.add(userBrandDTO);

        userBrandDTO = new UserBrandDTO();
        userBrandDTO.setBrand("Sumsnag T3");
        userBrandDTO.setCount(23);
        userBrandDTO.setRatio("23%");
        userBrandDTOList.add(userBrandDTO);

        UserBrandStatisticsDTO userBrandStatisticsDTO = new UserBrandStatisticsDTO();

        userBrandStatisticsDTO.setBrand("Sumsang");

        userBrandStatisticsDTO.setCount(45);
        userBrandStatisticsDTO.setRatio("24%");

        userBrandStatisticsDTO.setDetail(userBrandDTOList);

        System.out.println(JsonHelper.getJSONString(userBrandStatisticsDTO));

        List<UserBrandStatisticsDTO> userBrandStatisticsDTOs = new ArrayList<UserBrandStatisticsDTO>();
        userBrandStatisticsDTOs.add(userBrandStatisticsDTO);

        System.out.println(JsonHelper.getJSONString(userBrandStatisticsDTOs));


    }
}
