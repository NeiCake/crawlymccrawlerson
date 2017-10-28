package domain;


import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;

/*

the advertisement’s hyperlink
image collection
type of flat(detached, semi-detached, etc.)
the price (in RON)
advertisement’s publishing date

 */
//@Entity
//@Table
public class Ad {

    private String hyperlink;
    private Collection <Image> images;
    private FlatType flatType;
    private Integer price;

   // @DateTimeFormat(pattern = "yyyy-MM-dd")
    private ZonedDateTime publishedOnDate;

    public String getHyperlink() {
        return hyperlink;
    }

    public void setHyperlink(String hyperlink) {
        this.hyperlink = hyperlink;
    }

    public Collection<Image> getImages() {
        return images;
    }

    public void setImages(Collection<Image> images) {
        this.images = images;
    }

    public FlatType getFlatType() {
        return flatType;
    }

    public void setFlatType(FlatType flatType) {
        this.flatType = flatType;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public ZonedDateTime getPublishedOnDate() {
        return publishedOnDate;
    }

    public void setPublishedOnDate(ZonedDateTime publishedOnDate) {
        this.publishedOnDate = publishedOnDate;
    }


    @Override
    public String toString() {
        return "Ad{" +
                "hyperlink='" + hyperlink + '\'' +
                ", images=" + images +
                ", flatType=" + flatType +
                ", price=" + price +
                ", publishedOnDate=" + publishedOnDate +
                '}';
    }
}
