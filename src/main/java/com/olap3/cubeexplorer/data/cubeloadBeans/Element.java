
package com.olap3.cubeexplorer.data.cubeloadBeans;

import javax.annotation.processing.Generated;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}Hierarchy" minOccurs="0"/>
 *         &lt;element ref="{}Level" minOccurs="0"/>
 *         &lt;element ref="{}Predicate" minOccurs="0"/>
 *         &lt;element ref="{}YearPrompt" minOccurs="0"/>
 *         &lt;element ref="{}SegregationPredicate" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
@XmlRootElement(name = "Element")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-05-23T01:58:24+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class Element {

    @XmlElementRefs({
        @XmlElementRef(name = "SegregationPredicate", type = SegregationPredicate.class, required = false),
        @XmlElementRef(name = "Level", type = Level.class, required = false),
        @XmlElementRef(name = "Hierarchy", type = Hierarchy.class, required = false),
        @XmlElementRef(name = "Predicate", type = Predicate.class, required = false),
        @XmlElementRef(name = "YearPrompt", type = YearPrompt.class, required = false)
    })
    @XmlMixed
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-05-23T01:58:24+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected List<Object> content;
    @XmlAttribute(name = "value")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-05-23T01:58:24+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String value;

    /**
     * Gets the value of the content property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SegregationPredicate }
     * {@link Level }
     * {@link Hierarchy }
     * {@link Predicate }
     * {@link String }
     * {@link YearPrompt }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-05-23T01:58:24+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<Object>();
        }
        return this.content;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-05-23T01:58:24+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-05-23T01:58:24+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setValue(String value) {
        this.value = value;
    }

}
