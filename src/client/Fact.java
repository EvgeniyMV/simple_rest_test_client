package client;
public class Fact {
    private String content;
    private String author;
    private int id;

    public Fact(String content, String author, int id) {
        this.content = content;
        this.author = author;
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public int getId() {
        return id;
    }


    @Override
    public String toString() {
        return author + ": " + content + " id:" + id;
    }
}
