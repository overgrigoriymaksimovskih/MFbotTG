package pro.masterfood.utils.catalogelements;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "yml_catalog")
@XmlAccessorType(XmlAccessType.FIELD)
public class YmlCatalog {

    @XmlAttribute(name = "date")
    private String date;
    @XmlElement(name = "shop")
    private Shop shop;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}