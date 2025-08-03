package org.ventura.soluciones.pdfcreator.object.item;

import java.math.BigDecimal;

/**
 * Esta clase contiene la informacion de ITEM de una NOTA DE CREDITO.
 *
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
public class CreditNoteItemObject {

    private BigDecimal quantityItem;

    private String unitMeasureItem;

    private String descriptionItem;

    private BigDecimal unitValueItem;

    private BigDecimal unitPriceItem;

    private String amountItem;


    /**
     * Constructor basico de la clase CreditNoteItemObject.
     */
    public CreditNoteItemObject() {
    }


    public BigDecimal getQuantityItem() {
        return quantityItem;
    } //getQuantityItem

    public void setQuantityItem(BigDecimal quantityItem) {
        this.quantityItem = quantityItem;
    } //setQuantityItem

    public String getUnitMeasureItem() {
        return unitMeasureItem;
    } //getUnitMeasureItem

    public void setUnitMeasureItem(String unitMeasureItem) {
        this.unitMeasureItem = unitMeasureItem;
    } //setUnitMeasureItem

    public String getDescriptionItem() {
        return descriptionItem;
    } //getDescriptionItem

    public void setDescriptionItem(String descriptionItem) {
        this.descriptionItem = descriptionItem;
    } //setDescriptionItem

    public BigDecimal getUnitValueItem() {
        return unitValueItem;
    } //getUnitValueItem

    public void setUnitValueItem(BigDecimal unitValueItem) {
        this.unitValueItem = unitValueItem;
    } //setUnitValueItem

    public BigDecimal getUnitPriceItem() {
        return unitPriceItem;
    } //getUnitPriceItem

    public void setUnitPriceItem(BigDecimal unitPriceItem) {
        this.unitPriceItem = unitPriceItem;
    } //setUnitPriceItem

    public String getAmountItem() {
        return amountItem;
    } //getAmountItem

    public void setAmountItem(String amountItem) {
        this.amountItem = amountItem;
    } //setAmountItem

} //CreditNoteItemObject
