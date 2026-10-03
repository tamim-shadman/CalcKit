package com.tamim.calculator.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.tamim.calculator.model.HistoryItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository to persist calculation history using SharedPreferences.
 */
public class HistoryRepository {

    private static final String PREF_NAME = "calckit_history_prefs";
    private static final String KEY_HISTORY = "history_entries";
    private static final int MAX_HISTORY_ITEMS = 100;

    private final SharedPreferences prefs;

    public HistoryRepository(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public synchronized List<HistoryItem> loadHistory() {
        List<HistoryItem> list = new ArrayList<>();
        String json = prefs.getString(KEY_HISTORY, null);
        if (json == null || json.isEmpty()) {
            return list;
        }

        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                long id = obj.optLong("id", System.currentTimeMillis());
                String expr = obj.getString("expression");
                String res = obj.getString("result");
                long timestamp = obj.optLong("timestamp", id);
                list.add(new HistoryItem(id, expr, res, timestamp));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public synchronized void addHistory(HistoryItem item) {
        if (item == null) return;
        List<HistoryItem> list = loadHistory();
        // Insert newest at the beginning
        list.add(0, item);

        if (list.size() > MAX_HISTORY_ITEMS) {
            list = list.subList(0, MAX_HISTORY_ITEMS);
        }

        saveHistory(list);
    }

    public synchronized void clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply();
    }

    private void saveHistory(List<HistoryItem> list) {
        try {
            JSONArray array = new JSONArray();
            for (HistoryItem item : list) {
                JSONObject obj = new JSONObject();
                obj.put("id", item.getId());
                obj.put("expression", item.getExpression());
                obj.put("result", item.getResult());
                obj.put("timestamp", item.getTimestamp());
                array.put(obj);
            }
            prefs.edit().putString(KEY_HISTORY, array.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
