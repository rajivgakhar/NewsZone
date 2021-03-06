package com.rajiv.a300269668.newsapp;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.MyAdapter;
import Adapter.SavedNewsAdapter;
import Model.ListItem;
import Model.Region;
import Model.SavedNews;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragment extends Fragment {
    public static String category = "";
    private Activity context;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ListItem> listItems;
    ProgressDialog progressDialog;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    List<ListItem> savedItemKeys = new ArrayList<>();
    int position;
    String searchText;
    private TextView txtMessage;
    Toolbar mToolbar;
    LinearLayoutManager mLayoutManager;
    SharedPreferences pref;

    public TabFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public TabFragment(int pos) {
        // Required empty public constructor
        this.position = pos;
        this.searchText = "";
    }

    @SuppressLint("ValidFragment")
    public TabFragment(String searchText, int pos) {
        // Required empty public constructor
        this.searchText = searchText;
        this.position = pos;
    }

    public static TabFragment newInstance() {
        TabFragment fragment = new TabFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab, container, false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        txtMessage = (TextView) view.findViewById(R.id.txtMessage);
        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mToolbar.getMenu().findItem(R.id.search).setVisible(true);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView1);
        recyclerView.setHasFixedSize(true);
        //every item has fixed size
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        mLayoutManager = new LinearLayoutManager(getContext());

        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("savedNews");
        pref = getActivity().getSharedPreferences("_androidId", Context.MODE_PRIVATE);
        String android_id = pref.getString("android_id", "0");
        mFirebaseDatabase = mFirebaseDatabase.child(android_id);
        this.context = getActivity();
        switch (position) {
            case 0:
                category = "business";
                getNewsByCategory(category);
                mToolbar.setTitle("Business");
                break;
            case 1:
                category = "entertainment";
                getNewsByCategory(category);
                mToolbar.setTitle("Entertainment");
                break;
            case 2:
                category = "general";
                getNewsByCategory(category);
                mToolbar.setTitle("General");
                break;
            case 3:
                category = "Health";
                getNewsByCategory(category);
                mToolbar.setTitle("Health");
                break;
            case 4:
                category = "Science";
                getNewsByCategory(category);
                mToolbar.setTitle("Science");
                break;
            case 5:
                category = "Sports";
                getNewsByCategory(category);
                mToolbar.setTitle("Sports");
                break;
            case 6:
                category = "Technology";
                getNewsByCategory(category);
                mToolbar.setTitle("Technology");
                break;
            case 10:
                if (mToolbar.getTitle().toString().equals("Top Stories"))
                    getNewsBySearch("");
                else
                    getNewsBySearch(mToolbar.getTitle().toString());
                break;
            case 11:
                getSavedNews(view);
                mToolbar.getMenu().findItem(R.id.search).setVisible(false);
                mToolbar.setTitle("Saved News");
                break;
            default:
                category = "";
                getNewsByCategory(category);
                break;
        }
        return view;
    }

    private void getSavedNews(final View view) {
        progressDialog.show();
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    SavedNews sn = new SavedNews();
                    sn.setSavedItem(postSnapshot.getValue(ListItem.class));

                    ListItem listItem = new ListItem(sn.getSavedItem().getTitle()
                            , sn.getSavedItem().getTitle()
                            , sn.getSavedItem().getImage()
                            , sn.getSavedItem().getUrl()
                            , sn.getSavedItem().getPublishedAt()
                    );
                    savedItemKeys.add(listItem);
                    // TODO: handle the post
                }


                adapter = new SavedNewsAdapter(context, savedItemKeys);
                mLayoutManager.setReverseLayout(true);
                mLayoutManager.setStackFromEnd(true);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(adapter);
                if (savedItemKeys.size() == 0) {
                    txtMessage.setText("You haven't saved any articles.");
                } else {
                    txtMessage.setText("");
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (savedItemKeys.size() == 0) {
                    txtMessage.setText("You haven't saved any articles.");
                } else {
                    txtMessage.setText("");
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        };
        mFirebaseDatabase.addChildEventListener(childEventListener);
        progressDialog.dismiss();
        if (savedItemKeys.size() == 0) {
            txtMessage.setText("You haven't saved any articles.");
        } else {
            txtMessage.setText("");
        }
    }

    public void getNewsByCategory(String category) {
        String selectedR = pref.getString("selectedRegion", "ca");
        Region region = new Region();
        String shortlbl = "ca";
        if (!selectedR.equals("ca"))
            shortlbl = region.getShortLabel(selectedR);
        String url = "https://newsapi.org/v2/top-headlines?country=" + shortlbl +
                "&apiKey=a119b4537d944624af08fb67a63e46ca" +
                "&category=" + category;
        listItems = new ArrayList<>();
        progressDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", "abx");
        CustomRequest user_request = new CustomRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    String status = response.getString("status");
                    if (status.equals("ok")) {
                        JSONArray ja = response.getJSONArray("articles");
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject jobj = ja.getJSONObject(i);
                            ListItem listItem = new ListItem(jobj.getString("title")
                                    , jobj.getString("description")
                                    , jobj.getString("urlToImage")
                                    , jobj.getString("url")
                                    , jobj.getString("publishedAt")
                            );
                            listItems.add(listItem);
                        }
                        if (listItems.size() == 0) {
                            txtMessage.setText("Sorry, No news available!");
                        } else {
                            txtMessage.setText("");
                        }
                        adapter = new MyAdapter(getContext(), listItems);
                        mLayoutManager.setReverseLayout(false);
                        mLayoutManager.setStackFromEnd(false);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setAdapter(adapter);

                        progressDialog.dismiss();

                    }

                } catch (Exception e) {

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError response) {
                progressDialog.dismiss();
            }
        });

        progressDialog.dismiss();
        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(user_request);
    }

    public void getNewsBySearch(String category) {
        String selectedR = pref.getString("selectedRegion", "ca");
        Region region = new Region();
        String shortlbl = "ca";
        if (!selectedR.equals("ca"))
            shortlbl = region.getShortLabel(selectedR);
        String url = "https://newsapi.org/v2/top-headlines?q=" + searchText +
                "&apiKey=a119b4537d944624af08fb67a63e46ca&country=" + shortlbl + "&category=" + category;
        listItems = new ArrayList<>();
        progressDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", "abx");
        CustomRequest user_request = new CustomRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    String status = response.getString("status");
                    if (status.equals("ok")) {
                        JSONArray ja = response.getJSONArray("articles");
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject jobj = ja.getJSONObject(i);
                            ListItem listItem = new ListItem(jobj.getString("title")
                                    , jobj.getString("description")
                                    , jobj.getString("urlToImage")
                                    , jobj.getString("url")
                                    , jobj.getString("publishedAt")
                            );
                            listItems.add(listItem);
                        }
                        if (listItems.size() == 0) {
                            txtMessage.setText("Sorry, No news available, Search something else!");
                        } else {
                            txtMessage.setText("");
                        }
                        adapter = new MyAdapter(getContext(), listItems);
                        mLayoutManager.setReverseLayout(false);
                        mLayoutManager.setStackFromEnd(false);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setAdapter(adapter);

                        progressDialog.dismiss();

                    }


                } catch (Exception e) {
                    Log.e("dateaaaaaaaaaaaaaa", "FAILEDDDDDD");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError response) {
                progressDialog.dismiss();
            }
        });

        progressDialog.dismiss();
        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(user_request);
    }
}
