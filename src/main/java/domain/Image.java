package domain;

public class Image {
    String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "Image{" +
                "link='" + link + '\'' +
                '}';
    }
}
