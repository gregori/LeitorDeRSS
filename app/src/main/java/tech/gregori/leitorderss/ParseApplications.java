 package tech.gregori.leitorderss;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ParseApplications {
    private static final String TAG = "ParseApplications";

    private ArrayList<FeedEntry> applications;

    public ParseApplications() {
        applications = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }

    public boolean parse(String xmlText) {
        boolean status = true; // parse com sucesso?
        FeedEntry entry = null; // entrada do RSS a ser lida
        boolean inEntry = false; // estamos em um <entry>?
        String textValue = ""; // valor texto de cada atributo

        XmlPullParserFactory factory = null;

        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser pullParser = factory.newPullParser(); // objeto que efetivamente faz o parse
            pullParser.setInput(new StringReader(xmlText));

            // começa a fazer o parse
            int eventType = pullParser.getEventType();
            // enquanto não for o fim do documento, tratar tag por tag
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tag = pullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("entry".equalsIgnoreCase(tag)) {
                            inEntry = true;
                            entry = new FeedEntry();
                        }
                        break;
                    case XmlPullParser.TEXT: // conteúdo de uma tag <tag>conteúdo</tag>
                        textValue = pullParser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (inEntry) {
                            if ("entry".equalsIgnoreCase(tag)) { // </entry> ??
                                // terminou a "entry", então armnazenar o FeedEntry no arraylist
                                applications.add(entry);
                                inEntry = false;
                            } else if ("name".equalsIgnoreCase(tag)) { // </name> ??
                                entry.setName(textValue);
                            } else if ("artist".equalsIgnoreCase(tag)) { // </artist> ??
                                entry.setArtist(textValue);
                            } else if ("summary".equalsIgnoreCase(tag)) { // </summary> ??
                                entry.setSummary(textValue);
                            } else if ("image".equalsIgnoreCase(tag)) { // </image> ??
                                entry.setImgURL(textValue);
                            } else if ("releaseDate".equalsIgnoreCase(tag)) { // </releaseDate> ??
                                entry.setReleaseDate(textValue);
                            }
                            break;
                        }
                }
                eventType = pullParser.next();
            }
        } catch (Exception ex) {
            Log.e(TAG, "parse: Erro ao fazer parse: " + ex.getMessage());
            status = false;
        }

        return status;
    }
}
