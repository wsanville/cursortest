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

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import com.commonsware.cwac.loaderex.SharedPreferencesLoader;

public class SharedPreferencesDemo extends Activity implements
    LoaderManager.LoaderCallbacks<SharedPreferences> {
  private static final String KEY="sample";
  private TextView tv;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.prefs);

    tv=(TextView)findViewById(R.id.pref);
    getLoaderManager().initLoader(0, null, this);
  }

  @Override
  public Loader<SharedPreferences> onCreateLoader(int id, Bundle args) {
    return(new SharedPreferencesLoader(this));
  }

  @Override
  public void onLoadFinished(Loader<SharedPreferences> loader,
                             SharedPreferences prefs) {
    int value=prefs.getInt(KEY, 0);

    value+=1;
    tv.setText(String.valueOf(value));

    SharedPreferences.Editor editor=prefs.edit();

    editor.putInt(KEY, value);

    SharedPreferencesLoader.persist(editor);
  }

  @Override
  public void onLoaderReset(Loader<SharedPreferences> arg0) {
    // unused
  }
}
