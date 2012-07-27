package co.touchlab.cursortest.database;

/**
 * User: William Sanville
 * Date: 7/27/12
 * Time: 9:48 AM
 * A simple structure used to store junk data in the database.
 */
public class Article
{
    public final static String
        TABLE_NAME = "articles",
        TEXT_COLUMN = "text",
        TITLE_COLUMN = "title",
        THUMBNAIL_COLUMN = "thumbUrl",
        FULL_IMAGE_COLUMN = "fullUrl",
        CREATED_ON_COLUMN = "createdOn",
        ID_COLUMN = "_id";

    private String text, title, thumbUrl, fullUrl;
    private long createdOn;
    private int id;

    public long getCreatedOn()
    {
        return createdOn;
    }

    public void setCreatedOn(long createdOn)
    {
        this.createdOn = createdOn;
    }

    public String getFullUrl()
    {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl)
    {
        this.fullUrl = fullUrl;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getThumbUrl()
    {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl)
    {
        this.thumbUrl = thumbUrl;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
}
