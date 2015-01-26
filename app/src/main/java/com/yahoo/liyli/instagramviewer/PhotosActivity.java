package com.yahoo.liyli.instagramviewer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.http.Header;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;


public class PhotosActivity extends Activity {
    public static final String CLIENT_ID="68ecacb5efa94844a70e7a6e55e44d2d";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotoAdapter aPhotos;

    private PullToRefreshLayout mPullToRefreshLayout;

    private boolean ptr = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        fetchPopularPhotos();

        // Now find the PullToRefreshLayout to setup
        mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);

        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(this)
                // Mark All Children as pullable
                .allChildrenArePullable()
                // Set a OnRefreshListener
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(android.view.View view) {
                        // Your code to refresh the list here.
                        // Make sure you call listView.onRefreshComplete() when
                        // once the network request has completed successfully.
                        Log.i("INFO", "refresh...");
                        ptr = true;
                        fetchPopularPhotos();

                    }
                })
                // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);
    }

    private void fetchPopularPhotos() {
        photos = new ArrayList<InstagramPhoto>();

        aPhotos = new InstagramPhotoAdapter(this, photos);
        final ListView lvPhotos = (ListView)findViewById(R.id.lvPhotos);
        lvPhotos.setAdapter(aPhotos);

        // https://api.instagram.com/v1/media/popular?client_id=
        // {data => [x] ==> "images"}
        // setup popular url endpoints
        String popularUrl = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;

        // Create the network client
        AsyncHttpClient client = new AsyncHttpClient();

        // Trigger the network request
        client.get(popularUrl, new JsonHttpResponseHandler(){
            // define the success and failure callbacks
            // Handle the successful response
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                // url, height, username, caption
                JSONArray photosJSON = null;
                try {
                    photos.clear();
                    photosJSON = response.getJSONArray("data");
                    for (int i = 0; i < photosJSON.length(); i++) {
                        JSONObject photoJSON = photosJSON.getJSONObject(i);
                        InstagramPhoto photo = new InstagramPhoto();
                        photo.username = photoJSON.getJSONObject("user").getString("username");
                        if (photoJSON.isNull("caption") == false) {
                            photo.caption = photoJSON.getJSONObject("caption").getString("text");
                        }
                        photo.imageUrl = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                        photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                        photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");
                        photo.userPhotoUrl = photoJSON.getJSONObject("user").getString("profile_picture");

                        photos.add(photo);


                    }
                    aPhotos.notifyDataSetChanged();
                    if (ptr) {
                        ptr = false;
                        mPullToRefreshLayout.setRefreshComplete();
                    }

                 } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

        // Handle the successful response
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
