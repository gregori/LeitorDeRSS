package tech.gregori.leitorderss;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class FeedDetails extends AppCompatActivity {
    private static final String TAG = "FeedDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_details);

        TextView tvName = findViewById(R.id.tvName);
        TextView tvSummary = findViewById(R.id.tvSummary);
        TextView tvArtist = findViewById(R.id.tvArtist);

        Intent intent = getIntent();

        FeedEntry feedEntry = (FeedEntry) intent.getSerializableExtra("feedEntry");
//        Log.d(TAG, "onCreate: Do FeedDetails - recebi: " + feedEntry);
        tvName.setText(feedEntry.getName());
        tvSummary.setText(feedEntry.getSummary());
        tvArtist.setText(feedEntry.getArtist());
    }
}