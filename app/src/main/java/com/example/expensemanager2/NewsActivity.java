package com.example.expensemanager2;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager2.NewsAdapter;
import com.example.expensemanager2.NewsModel;
import com.example.expensemanager2.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsActivity extends AppCompatActivity {

    private RecyclerView newsRecyclerView;
    private NewsAdapter newsAdapter;
    private ArrayList<NewsModel> newsList;

    private final String RAPIDAPI_URL = "https://seeking-alpha.p.rapidapi.com/news/v2/list?size=20&category=market-news%3A%3Aall&number=1";
    private final String RAPIDAPI_KEY = "bb3ebe7f9fmsh12d45eb0701c154p1987c1jsn31de3749df7f";
    private final String RAPIDAPI_HOST = "seeking-alpha.p.rapidapi.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        newsRecyclerView = findViewById(R.id.newsRecyclerView);
        newsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, newsList);

        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsRecyclerView.setAdapter(newsAdapter);

        fetchFinanceNews();
    }

    private void fetchFinanceNews() {
        OkHttpClient client = new OkHttpClient();

        // Create the API request with headers
        Request request = new Request.Builder()
                .url(RAPIDAPI_URL)
                .get()
                .addHeader("x-rapidapi-key", RAPIDAPI_KEY)
                .addHeader("x-rapidapi-host", RAPIDAPI_HOST)
                .build();

        // Make the asynchronous API call
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("NewsActivity", "API call failed: " + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    final String jsonResponse = response.body().string();
                    try {
                        // Parse JSON
                        JSONObject mainData = new JSONObject(jsonResponse);
                        JSONArray dataArray = mainData.getJSONArray("data"); // Adjust according to the API structure

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject newsObject = dataArray.getJSONObject(i);
                            JSONObject attributes = newsObject.getJSONObject("attributes");
                            JSONObject links = newsObject.getJSONObject("links");

                            String title = attributes.optString("title", "No Title");
                            String publishOn = attributes.optString("publishOn", "No Date");
                            String imageUrl = attributes.optString("gettyImageUrl", null);
                            String content = attributes.optString("content", "No Content");
                            String canonicalLink = links.optString("canonical", "No Link");

                            // Add the parsed data to the news list
                            newsList.add(new NewsModel(title, publishOn, content, imageUrl, canonicalLink));
                        }

                        // Update the RecyclerView on the main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                newsAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (Exception e) {
                        Log.e("NewsActivity", "Error parsing JSON: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    Log.e("NewsActivity", "API call unsuccessful");
                }
            }
        });
    }
}
