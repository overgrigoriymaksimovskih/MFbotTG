package pro.masterfood.utils;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "yml_catalog")
@XmlAccessorType(XmlAccessType.FIELD)
class YmlCatalog {

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

@XmlAccessorType(XmlAccessType.FIELD)
class Shop {
    @XmlElement(name = "name")
    private String name;
    @XmlElement(name = "company")
    private String company;
    @XmlElement(name = "url")
    private String url;
    @XmlElement(name = "currencies")
    private Currencies currencies;
    @XmlElement(name = "categories")
    private Categories categories;
    @XmlElement(name = "offers")
    private Offers offers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Currencies getCurrencies() {
        return currencies;
    }

    public void setCurrencies(Currencies currencies) {
        this.currencies = currencies;
    }

    public Categories getCategories() {
        return categories;
    }

    public void setCategories(Categories categories) {
        this.categories = categories;
    }

    public Offers getOffers() {
        return offers;
    }

    public void setOffers(Offers offers) {
        this.offers = offers;
    }
}
@XmlAccessorType(XmlAccessType.FIELD)
class Currencies {
    @XmlElement(name = "currency")
    private List<Currency> currency;

    public List<Currency> getCurrency() {
        return currency;
    }

    public void setCurrency(List<Currency> currency) {
        this.currency = currency;
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
class Currency {
    @XmlAttribute(name = "id")
    private String id;
    @XmlAttribute(name = "rate")
    private String rate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
class Categories {
    @XmlElement(name = "category")
    private List<Category> category;

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
class Category {
    @XmlAttribute(name = "id")
    private String id;
    @XmlAttribute(name = "parentId")
    private String parentId;
    @XmlValue
    private String value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
class Offers {
    @XmlElement(name = "offer")
    private List<Offer> offer;

    public List<Offer> getOffer() {
        return offer;
    }

    public void setOffer(List<Offer> offer) {
        this.offer = offer;
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
class Offer {
    @XmlAttribute(name = "id")
    private String id;
    @XmlElement(name = "name")
    private String name;
    @XmlElement(name = "url")
    private String url;
    @XmlElement(name = "price")
    private String price;
    @XmlElement(name = "currencyId")
    private String currencyId;
    @XmlElement(name = "categoryId")
    private String categoryId;
    @XmlElement(name = "picture")
    private String picture;
    @XmlElement(name = "weight")
    private String weight;
    @XmlElement(name = "description")
    private String description;
    @XmlElement(name = "delivery")
    private String delivery;
    @XmlElement(name = "sales_notes")
    private String salesNotes;
    @XmlElement(name = "pickup")
    private String pickup;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getSalesNotes() {
        return salesNotes;
    }

    public void setSalesNotes(String salesNotes) {
        this.salesNotes = salesNotes;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }
}
