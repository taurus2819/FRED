/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.fred.site.util;

import java.io.Serializable;

/**
 *
 * @author sitikond
 */
public class Island implements Serializable {    
    
    private Integer islandId;
    private String name;
    private String countryCode;
    private double bboxTop;
    private double bboxBottom;
    private double bboxLeft;
    private double bboxRight;

    public Integer getIslandId() {
        return islandId;
    }

    public void setIslandId(Integer islandId) {
        this.islandId = islandId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public double getBboxTop() {
        return bboxTop;
    }

    public void setBboxTop(double bboxTop) {
        this.bboxTop = bboxTop;
    }

    public double getBboxBottom() {
        return bboxBottom;
    }

    public void setBboxBottom(double bboxBottom) {
        this.bboxBottom = bboxBottom;
    }

    public double getBboxLeft() {
        return bboxLeft;
    }

    public void setBboxLeft(double bboxLeft) {
        this.bboxLeft = bboxLeft;
    }

    public double getBboxRight() {
        return bboxRight;
    }

    public void setBboxRight(double bboxRight) {
        this.bboxRight = bboxRight;
    }
    
    

}
