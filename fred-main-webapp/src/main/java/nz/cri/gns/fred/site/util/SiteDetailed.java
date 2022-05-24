/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.fred.site.util;

/**
 *
 * @author sitikond
 */
public class SiteDetailed {
        
    private SiteModel model;
    
    private Island island;
    
    public SiteDetailed(SiteModel sm)   {
        this.model = sm;
    }

    public SiteModel getModel() {
        return model;
    }

    public void setModel(SiteModel model) {
        this.model = model;
    }

    public Island getIsland() {
        return island;
    }

    public void setIsland(Island island) {
        this.island = island;
    }
    
    
    
}
