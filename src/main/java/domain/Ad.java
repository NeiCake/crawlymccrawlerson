package domain;


import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/*

the advertisement’s hyperlink
image collection
type of flat(detached, semi-detached, etc.)
the price (in RON)
advertisement’s publishing date

 */
@Entity
@Table
public class Ad {

    private String hyperlink;
    private Collection <Image> images;
    private FlatType flatType;
    private BigDecimal price;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date publishedOnDate;

}
