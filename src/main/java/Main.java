import domain.Ad;
import domain.FlatType;
import domain.Image;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Main {


    private static RequestBO requestBO;
    public static final List<String> baseLinks = new ArrayList<String>(Arrays.asList(

            //ommited some links cause they were full of crap -_-
  /*          "http://anuntul.ro/anunturi-imobiliare-vanzari/cumparari-schimburi",
            "http://anuntul.ro/anunturi-imobiliare-vanzari/spatii-comerciale-industriale",
            "http://anuntul.ro/anunturi-imobiliare-vanzari/terenuri",
            "http://anuntul.ro/anunturi-imobiliare-vanzari/case-vile",*/
            "http://anuntul.ro/anunturi-imobiliare-vanzari/apartamente-4-camere",
            "http://anuntul.ro/anunturi-imobiliare-vanzari/apartamente-3-camere",
            "http://anuntul.ro/anunturi-imobiliare-vanzari/apartamente-2-camere",
            "http://anuntul.ro/anunturi-imobiliare-vanzari/garsoniere"
    ));

    // static final String LINK_VANZARI = "http://anuntul.ro/anunturi-imobiliare-vanzari/";

    static final String QUESTION_MARK = "?";
    private HashSet<String> links;

    public Main() {
        links = new HashSet<String>();
    }

    public void getPageLinks(String URL) throws IOException {

        if (!links.contains(URL) && validateRootURL(URL)) {
            //4. (i) If not add it to the index
            if (links.add(URL)) {
           //     System.out.println(URL);
            }

            //2. Fetch the HTML code
            Document document = Jsoup.connect(URL).get();

            //check if url is an ad url
            if (validateURL(URL)) {
                //from here on need to parse the page
                Ad ad = parseAdFromURL(URL);
                if(ad.getPrice()!=null){
                    if((ad.getPrice()<requestBO.getMaxPrice())&&ad.getPrice()>requestBO.getMinPrice()){
                        long i = ChronoUnit.DAYS.between(ZonedDateTime.now(),ad.getPublishedOnDate());
                        if(i<requestBO.getDaysOld()){
                            System.out.println(ad);
                        }
                    }
                }



            }


            //3. Parse the HTML to extract links to other URLs
            Elements linksOnPage = document.select("a[href]");

            //5. For each extracted URL... go back to Step 4.
            for (Element page : linksOnPage) {

                getPageLinks(page.attr("abs:href"));


            }

        }
    }


    static final Map<String, Month> monthEquivalenceMap = new HashMap<>();

    static {
        monthEquivalenceMap.put("ian", Month.JANUARY);
        monthEquivalenceMap.put("feb", Month.FEBRUARY);
        monthEquivalenceMap.put("mar", Month.MARCH);
        monthEquivalenceMap.put("apr", Month.APRIL);
        monthEquivalenceMap.put("mai", Month.MAY);
        monthEquivalenceMap.put("iun", Month.JUNE);
        monthEquivalenceMap.put("iul", Month.JULY);
        monthEquivalenceMap.put("aug", Month.AUGUST);
        monthEquivalenceMap.put("sep", Month.SEPTEMBER);
        monthEquivalenceMap.put("oct", Month.OCTOBER);
        monthEquivalenceMap.put("noi", Month.NOVEMBER);
        monthEquivalenceMap.put("dec", Month.DECEMBER);
    }

    //time thingy written by Demetrescu Cristian
    private static ZonedDateTime getTimeFromDocument(Document document) {

        Element date = document.select("div.loc-data").first();
        String[] dateString = date.text().split(",");

        DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_TIME;
        LocalTime localTime = LocalTime.parse(dateString[2].trim(), timeFormatter);

        LocalDate localDate = null;

        if ("azi".equals(dateString[1].trim())) {
            localDate = LocalDate.now();
        } else if ("ieri".equals(dateString[1].trim())) {
            localDate = LocalDate.now().minusDays(1);
        } else {
            String[] tempArray = dateString[1].trim().split(" ");
            Integer day = null;
            try {
                day = Integer.parseInt(tempArray[0]);
            } catch (Exception e) {
            }
            String month = tempArray[1];

            int year = LocalDate.now().getYear();

            localDate = LocalDate.of(year, monthEquivalenceMap.get(month), day);
        }

        ZonedDateTime result = ZonedDateTime.of(localDate, localTime, ZoneId.systemDefault());

        return result;
    }

    private boolean validateRootURL(String url) {
        for (String link : baseLinks) {
            if (url.toLowerCase().contains(link.toLowerCase()))
                return true;

        }
        return false;
    }

    private Ad parseAdFromURL(String URL) throws IOException {
        Document document = Jsoup.connect(URL).get();
        Ad ad = new Ad();
        ad.setHyperlink(URL);
        Element price = document.select("div.price").first();
        if (price != null) {
            String priceToParse = price.text();
            ad.setPrice(parsePrice(priceToParse));
        }

        ad.setFlatType(findTypeDocument(document));

        ad.setImages(collectAdImages(document));

        ad.setPublishedOnDate(getTimeFromDocument(document));


        return ad;
    }


    private Collection<Image> collectAdImages(Document document) {
        HashSet<Image> set = new HashSet<Image>();

        //find and add first image
        Element img = document.select("img#imgFull").first();
        if (img != null) {
            //continue to look for thumbnails since there was a first image
            Element gallery = document.select("ul#image-gallery").first();
            Elements imgs = gallery.select("li>img");
            for (Element e : imgs) {
                Image image = new Image();
                String pictureThumbnailLink = e.absUrl("src");
                image.setLink(turnThumbnailLinkIntoPictureLink(pictureThumbnailLink));
                set.add(image);
            }

        }


        return set;
    }

    private FlatType findTypeDocument(Document document) {

        Elements ul = document.select("div.label-list > ul");
        Elements li = ul.select("li");
        for (Element e : li) {
            if (e.text().equalsIgnoreCase(FlatType.DECOMANDAT.toString())) return FlatType.DECOMANDAT;
            if (e.text().equalsIgnoreCase(FlatType.SEMIDECOMANDAT.toString())) return FlatType.SEMIDECOMANDAT;
            if (e.text().equalsIgnoreCase(FlatType.CIRCULAR.toString())) return FlatType.CIRCULAR;
        }


        return FlatType.NON_APPLICABLE;
    }


    public String turnThumbnailLinkIntoPictureLink(String string) {
        String[] parts = string.split("thumb2");
        if (parts.length == 2) {
            return parts[0] + "imgs" + parts[1];
        }
        return "";
    }


    private Integer parsePrice(String priceToParse) {
        priceToParse = priceToParse.replace(" €", "");
        priceToParse = priceToParse.replace(".", "");
        priceToParse = priceToParse.replace(",", "");
        if (priceToParse.contains(" Lei")) {
            priceToParse = priceToParse.replace(" Lei", "");
            return Integer.valueOf(priceToParse);
        }

        return Integer.valueOf(priceToParse) * 4;
    }


    private boolean validateURL(String URL) {
        String contained = null;

        for (String x : baseLinks) {
            if (URL.contains(x + "/")) {
                contained = x + "/";
            }
        }
        if (contained != null) {
            URL = URL.replace(contained, "");
            if (!URL.startsWith("?") && !URL.startsWith("#") && !URL.isEmpty()) {
                return true;
            }
        }

        return false;
    }


    public static void main(String[] args) throws IOException {


        System.out.println("ENTER MIN PRICE (INTEGER)");
        Scanner s = new Scanner(System.in);
        int min = s.nextInt();

        System.out.println("ENTER MAX PRICE (INTEGER)");
        int max = s.nextInt();
        //1. Pick a URL from the frontier

        System.out.println("Number of days to look back on to");
        int days = s.nextInt();

        requestBO = new RequestBO(min, max, days);
        for (String link : baseLinks) {
            new Main().getPageLinks(link);
        }

    }
}
