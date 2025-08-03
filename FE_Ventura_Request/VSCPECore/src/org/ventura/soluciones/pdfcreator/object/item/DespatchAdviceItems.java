/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.soluciones.pdfcreator.object.item;

import java.math.BigDecimal;

/**
 * @author VS-LT-06
 */
public class DespatchAdviceItems {

    private BigDecimal quantityItem;

    private String unitMeasureItem;

    private String descriptionItem;

    public BigDecimal getQuantityItem() {
        return quantityItem;
    }

    public void setQuantityItem(BigDecimal quantityItem) {
        this.quantityItem = quantityItem;
    }

    public String getUnitMeasureItem() {
        return unitMeasureItem;
    }

    public void setUnitMeasureItem(String unitMeasureItem) {
        this.unitMeasureItem = unitMeasureItem;
    }

    public String getDescriptionItem() {
        return descriptionItem;
    }

    public void setDescriptionItem(String descriptionItem) {
        this.descriptionItem = descriptionItem;
    }

}
