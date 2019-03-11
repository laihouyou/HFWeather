package com.gddst.lhy.weather.fragment.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample name for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private static final int COUNT = 25;

    public static void addItem(long id, String name, int code,String dataType,long cId) {
        ITEMS.add(new DummyItem(id,name,code,dataType,cId));
    }


    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of name.
     */
    public static class DummyItem {
        public final long id;
        public final String name;
        public final int code;
        public final String dataType;
        public final long fId;

        public DummyItem(long id, String name, int code,String dataType,long cId) {
            this.id = id;
            this.name = name;
            this.code = code;
            this.dataType=dataType;
            this.fId =cId;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
