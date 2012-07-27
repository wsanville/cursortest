package co.touchlab.cursortest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import co.touchlab.cursortest.database.Article;
import co.touchlab.cursortest.database.DatabaseHelper;
import com.commonsware.cwac.loaderex.acl.SQLiteCursorLoader;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>
{
    private ArticleAdapter adapter;
    private SQLiteCursorLoader loader;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getSupportLoaderManager().initLoader(0, null, this);

        adapter = new ArticleAdapter(this, null);
        ListView list = (ListView)findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setEmptyView(findViewById(R.id.empty));
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        DatabaseHelper instance = DatabaseHelper.getInstance(this);
        loader = new SQLiteCursorLoader(this, instance, "select * from articles", new String[0]);
        return loader;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> cursorLoader, Cursor cursor)
    {
        if (cursor.getCount() == 0 && loader != null)
            loader.runInTransaction(new MockDataLoader());

        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> cursorLoader)
    {
        adapter.swapCursor(null);
    }

    static class ArticleAdapter extends SimpleCursorAdapter
    {
        SimpleDateFormat formatter = new SimpleDateFormat("'Created on' M/d/yyyy h:mm a");

        ArticleAdapter(Context context, Cursor c)
        {
            super(context, R.layout.article, c, new String[0], new int[0], 0);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor)
        {
            String title = cursor.getString(2);
            long createdOn = cursor.getLong(5);

            TextView titleView = (TextView)view.findViewById(R.id.title);
            titleView.setText(title);

            TextView valueView = (TextView)view.findViewById(R.id.value);
            valueView.setText(formatDateText(createdOn));
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent)
        {
            return super.newView(context, cursor, parent);
        }

        String formatDateText(long milliseconds)
        {
            return formatter.format(new Date(milliseconds));
        }
    }

    static class MockDataLoader implements SQLiteCursorLoader.TransactionRunnable
    {
        @Override
        public void run(SQLiteDatabase db)
        {
            final String[] bgColors = new String[] { "007ace", "ff3399", "ff9933" };
            final String[] textColors = new String[] { "ffffff", "ffffff", "000000" };
            final String thumbFormat = "http://ipsumimage.appspot.com/110x110?l=hello+world&b=%s&f=%s&t=png&id=%d";
            final String fullFormat = "http://ipsumimage.appspot.com/400x300?l=hello+world&b=%s&f=%s&t=png&id=%d";

            for (int i = 0; i < 10000; i++)
            {
                String title = "Article " + (i + 1);
                String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin nunc nisl, mattis a commodo id, pharetra vitae tortor. Morbi nunc orci, pretium nec venenatis a, posuere sed erat. In hac habitasse platea dictumst. Etiam ut turpis eu ipsum condimentum fermentum et non nulla. Fusce sit amet nisl at metus vehicula sodales commodo sagittis lorem. Fusce vitae dolor dui. Donec fringilla, tellus eget malesuada mollis, sem dui aliquam lectus, hendrerit interdum magna purus vel mi. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam enim mi, lacinia et ornare a, venenatis sit amet nibh. Etiam quam erat. ";
                long createdOn = new Date().getTime();
                String thumb = String.format(thumbFormat, bgColors[i % bgColors.length], textColors[i % textColors.length], i); //add the ID as a parameter, to make all the URLs unique
                String full = String.format(fullFormat, bgColors[i % bgColors.length], textColors[i % textColors.length], i);

                ContentValues values = new ContentValues();
                values.put(Article.TITLE_COLUMN, title);
                values.put(Article.TEXT_COLUMN, text);
                values.put(Article.CREATED_ON_COLUMN, createdOn);
                values.put(Article.THUMBNAIL_COLUMN, thumb);
                values.put(Article.FULL_IMAGE_COLUMN, full);

                db.insert(Article.TABLE_NAME, null, values);
            }
        }
    }
}
