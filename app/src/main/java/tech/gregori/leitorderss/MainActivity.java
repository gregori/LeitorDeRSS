package tech.gregori.leitorderss;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ListView rssListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rssListView = findViewById(R.id.rssListView);

        downloadUrl("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");
    }

    // Chamado quando é hora de "inflar" o menu
    // Vincular o arquivo de leiaute do menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        String feedUrl;

        switch (id) {
            case R.id.mnuFree:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml";
                break;
            case R.id.mnuPaid:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=10/xml";
                break;
            case R.id.mnuSongs:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=10/xml";
                break;
            default:
                return super.onOptionsItemSelected(item); // caso haja um submenu esta opção é chamada
        }

        downloadUrl(feedUrl);
        return true;
    }

    private void downloadUrl(String feedUrl) {
        Log.d(TAG, "downloadUrl: iniciando a AsyncTask");
        DownloadData downloadData = new DownloadData();
        downloadData.execute(feedUrl);
        Log.d(TAG, "downloadUrl: terminou.");
    }

    // 1º parametro - dado passado para a classe - uma URL - String
    // 2º parâmetro - dados referente a progresso - ignoramos aqui - Void
    // 3º parâmetro - tipo de resultado - no nosso caso uma String
    private class DownloadData extends AsyncTask<String, Void, String> {
        private static final String TAG = "DownloadData";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Log.d(TAG, "onPostExecute: o parâmetro recebido é " + s);
            ParseApplications parser = new ParseApplications();
            parser.parse(s);

            FeedAdapter feedAdapter = new FeedAdapter(
                    MainActivity.this, R.layout.list_records, parser.getApplications()
            );
//            FeedImageAdapter feedAdapter = new FeedImageAdapter(
//                    MainActivity.this, R.layout.list_record_with_image, parser.getApplications()
//            );

            rssListView.setAdapter(feedAdapter);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: recebeu " + strings[0]);
            String contents = downloadContents(strings[0]);
            if (contents == null) {
                Log.e(TAG, "doInBackground: Erro baixando dados.");
            }
            return contents;
        }

        private String downloadContents(String urlPath) {
            // StringBuilder é mais eficiente para concatenar strings
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "downloadContents: O código de resposta foi: " + responseCode);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                int charsRead; // quantidade de caracteres lida
                char[] inputBuffer = new char[500];

                while (true) {
                    charsRead = reader.read(inputBuffer);
                    if (charsRead < 0) {
                        break;
                    }
                    if (charsRead > 0) {
                        // copie o conteúdo do inputBuffer para dentro de result.
                        result.append(String.copyValueOf(inputBuffer, 0, charsRead));
                    }
                }
                reader.close();

                return result.toString();
            } catch (MalformedURLException ex) {
                Log.e(TAG, "downloadContents: URL inválida: " + ex.getMessage());
            } catch (IOException ex) {
                Log.e(TAG, "downloadContents: IOException ao ler os dados: " + ex.getMessage());
            } catch (SecurityException ex) {
                Log.e(TAG, "downloadContents: Exceção de segurança. Falta permissão? " + ex.getMessage());
            }

            return null;
        }
    }
}