package com.example.expensemanager2;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ArticleActivity extends AppCompatActivity {

    private RecyclerView articleRecyclerView;
    private ArticleAdapter articleAdapter;
    private ArrayList<ArticleModel> articleList;

    // RapidAPI details
    private static final String API_URL = "https://seeking-alpha.p.rapidapi.com/analysis/v2/list?id=aapl&size=20&number=1";
    private static final String API_KEY = "bb3ebe7f9fmsh12d45eb0701c154p1987c1jsn31de3749df7f";
    private static final String API_HOST = "seeking-alpha.p.rapidapi.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        // Initialize RecyclerView and Adapter
        articleRecyclerView = findViewById(R.id.articleRecyclerView);
        articleList = new ArrayList<>();
        articleAdapter = new ArticleAdapter(this, articleList);

        articleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        articleRecyclerView.setAdapter(articleAdapter);

        // Fetch articles from RapidAPI
        fetchArticles();
    }

    private void fetchArticles() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_URL)
                .get()
                .addHeader("x-rapidapi-key", API_KEY)
                .addHeader("x-rapidapi-host", API_HOST)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ArticleActivity", "API Request Failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonData = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONArray dataArray = jsonObject.getJSONArray("data");

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject article = dataArray.getJSONObject(i);
                            JSONObject attributes = article.getJSONObject("attributes");
                            JSONObject links = article.getJSONObject("links");

                            // Parse insights as plain text
                            String rawInsights = attributes.optString("structuredInsights", "");
                            String formattedInsights = parseStructuredInsights(rawInsights);

                            // Extract other fields
                            String id = article.getString("id");
                            String title = attributes.optString("title", "No Title");
                            String publishOn = attributes.optString("publishOn", "No Date");
                            String imageUrl = attributes.optString("gettyImageUrl", "");
                            String articleLink = links.optString("self", "");

                            // Add the parsed data to the list
                            articleList.add(new ArticleModel(id, title, publishOn, imageUrl, formattedInsights, articleLink));
                        }

                        // Update RecyclerView on UI thread
                        runOnUiThread(() -> articleAdapter.notifyDataSetChanged());

                    } catch (Exception e) {
                        Log.e("ArticleActivity", "Error parsing JSON: " + e.getMessage());
                    }
                } else {
                    Log.e("ArticleActivity", "API call unsuccessful");
                }
            }
        });
    }

    // Helper method to parse the structuredInsights JSON string
    private String parseStructuredInsights(String rawInsights) {
        StringBuilder insightsBuilder = new StringBuilder();
        try {
            JSONObject insightsJson = new JSONObject(rawInsights);

            // Extract each section and append to the builder
            appendSection(insightsBuilder, "Key Takeaways", insightsJson.optJSONArray("Key Takeaways"));
            appendSection(insightsBuilder, "Positives", insightsJson.optJSONArray("Positives"));
            appendSection(insightsBuilder, "Concerns", insightsJson.optJSONArray("Concerns"));
            appendSection(insightsBuilder, "Catalysts", insightsJson.optJSONArray("Catalysts"));

        } catch (Exception e) {
            Log.e("ArticleActivity", "Error parsing structuredInsights: " + e.getMessage());
            return "No Insights Available";
        }
        return insightsBuilder.toString();
    }

    // Helper method to format a section
    private void appendSection(StringBuilder builder, String sectionTitle, JSONArray sectionData) {
        if (sectionData != null && sectionData.length() > 0) {
            builder.append(sectionTitle).append(":\n");
            for (int i = 0; i < sectionData.length(); i++) {
                builder.append("- ").append(sectionData.optString(i)).append("\n");
            }
            builder.append("\n");
        }
    }

}
