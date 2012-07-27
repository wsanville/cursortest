/* Copyright (c) 2011-2012 -- CommonsWare, LLC

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.commonsware.cwac.loaderex.demo;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.commonsware.cwac.loaderex.acl.SQLiteCursorLoader;

public class ConstantsBrowserACL extends FragmentActivity implements
    LoaderManager.LoaderCallbacks<Cursor> {
  private static final int ADD_ID=Menu.FIRST + 1;
  private static final int DELETE_ID=Menu.FIRST + 3;
  private DatabaseHelper db=null;
  private SimpleCursorAdapter adapter=null;
  private SQLiteCursorLoader loader=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll()
                                                                    .penaltyLog()
                                                                    .build());
    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
                                                            .detectLeakedClosableObjects()
                                                            .penaltyLog()
                                                            .penaltyDeath()
                                                            .build());

    setContentView(R.layout.main);

    db=new DatabaseHelper(this);

    adapter=
        new SimpleCursorAdapter(this, R.layout.row, null, new String[] {
            DatabaseHelper.TITLE, DatabaseHelper.VALUE }, new int[] {
            R.id.title, R.id.value });

    ListView lv=(ListView)findViewById(R.id.constants);

    lv.setAdapter(adapter);
    registerForContextMenu(lv);
    getSupportLoaderManager().initLoader(0, null, this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    db.close();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(Menu.NONE, ADD_ID, Menu.NONE, "Add")
        .setIcon(R.drawable.add).setAlphabeticShortcut('a');

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case ADD_ID:
        add();
        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
                                  ContextMenu.ContextMenuInfo menuInfo) {
    menu.add(Menu.NONE, DELETE_ID, Menu.NONE, "Delete")
        .setAlphabeticShortcut('d');
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case DELETE_ID:
        AdapterView.AdapterContextMenuInfo info=
            (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        delete(info.id);
        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
    loader=
        new SQLiteCursorLoader(this, db, "SELECT _ID, title, value "
            + "FROM constants ORDER BY title", null);

    return(loader);
  }

  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    adapter.changeCursor(cursor);
  }

  public void onLoaderReset(Loader<Cursor> loader) {
    adapter.changeCursor(null);
  }

  private void add() {
    LayoutInflater inflater=LayoutInflater.from(this);
    View addView=inflater.inflate(R.layout.add_edit, null);
    final DialogWrapper wrapper=new DialogWrapper(addView);

    new AlertDialog.Builder(this).setTitle(R.string.add_title)
                                 .setView(addView)
                                 .setPositiveButton(R.string.ok,
                                                    new DialogInterface.OnClickListener() {
                                                      public void onClick(DialogInterface dialog,
                                                                          int whichButton) {
                                                        processAdd(wrapper);
                                                      }
                                                    })
                                 .setNegativeButton(R.string.cancel,
                                                    new DialogInterface.OnClickListener() {
                                                      public void onClick(DialogInterface dialog,
                                                                          int whichButton) {
                                                        // ignore,
                                                        // just
                                                        // dismiss
                                                      }
                                                    }).show();
  }

  private void delete(final long rowId) {
    if (rowId > 0) {
      new AlertDialog.Builder(this).setTitle(R.string.delete_title)
                                   .setPositiveButton(R.string.ok,
                                                      new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog,
                                                                            int whichButton) {
                                                          processDelete(rowId);
                                                        }
                                                      })
                                   .setNegativeButton(R.string.cancel,
                                                      new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog,
                                                                            int whichButton) {
                                                          // ignore,
                                                          // just
                                                          // dismiss
                                                        }
                                                      }).show();
    }
  }

  private void processAdd(DialogWrapper wrapper) {
    ContentValues values=new ContentValues(2);

    values.put(DatabaseHelper.TITLE, wrapper.getTitle());
    values.put(DatabaseHelper.VALUE, wrapper.getValue());

    loader.insert("constants", DatabaseHelper.TITLE, values);
  }

  private void processDelete(long rowId) {
    String[] args= { String.valueOf(rowId) };

    loader.delete("constants", "_ID=?", args);
  }

  class DialogWrapper {
    EditText titleField=null;
    EditText valueField=null;
    View base=null;

    DialogWrapper(View base) {
      this.base=base;
      valueField=(EditText)base.findViewById(R.id.value);
    }

    String getTitle() {
      return(getTitleField().getText().toString());
    }

    float getValue() {
      return(new Float(getValueField().getText().toString()).floatValue());
    }

    private EditText getTitleField() {
      if (titleField == null) {
        titleField=(EditText)base.findViewById(R.id.title);
      }

      return(titleField);
    }

    private EditText getValueField() {
      if (valueField == null) {
        valueField=(EditText)base.findViewById(R.id.value);
      }

      return(valueField);
    }
  }
}