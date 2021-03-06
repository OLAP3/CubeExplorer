package com.olap3.cubeexplorer.model.legacy;


import com.google.gson.annotations.JsonAdapter;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonAdapter(QueryPartSerializer.class)
public class QueryPart implements Comparable<QueryPart> {

    private static TreeMap<Integer, List<QueryPart>> dimension_qps = new TreeMap<>();
    private static TreeMap<Integer, List<QueryPart>> measure_qps = new TreeMap<>();
    private static TreeMap<Integer, List<QueryPart>> filter_qps = new TreeMap<>();

    public enum Type {
        DIMENSION(0), FILTER(1), MEASURE(2);
        private int value;
        private static Map map = new HashMap<>();

        private Type(int value) {
            this.value = value;
        }

        static {
            for (Type pageType : Type.values()) {
                map.put(pageType.value, pageType);
            }
        }

        public static Type valueOf(int pageType) {
            return (Type) map.get(pageType);
        }

        public int getValue() {
            return value;
        }

        public byte[] getBytes(){
            ByteBuffer b = ByteBuffer.allocate(4);
            b.putInt(value);
            return b.array();
        }
    }

    private static final HashMap<Type, String> display = new HashMap<>();

    static {
        display.put(Type.DIMENSION, "Dimension");
        display.put(Type.FILTER, "Filter");
        display.put(Type.MEASURE, "Measure");
    }


    /* Instance variables */
    Type t;
    String value;
    // Only for filters allows for better compatibility
    String level;

    /**
     * Query parts are cached for efficiency and to better avoid duplicates, they should never be build outside of this class
     * Do not attempt to modify this it will come back to bite your a** -Alex
     */
    private QueryPart(){
    }

    private static final Pattern legacyP = Pattern.compile("\\[[^\\[\\]]*\\].\\[[^\\[\\]]*\\]\\.\\[[^\\[\\]]*\\]");
    private static final boolean enforce = false;
    public static QueryPart newDimension(String value) {
        if (enforce){
            Matcher m = legacyP.matcher(value);
            if (m.matches()){
                System.out.println("Detected legacy dimension format !");
            }
        }
        return getQueryPart(value, Type.DIMENSION, dimension_qps);
    }


    public static QueryPart newMeasure(String value) {
        return getQueryPart(value, Type.MEASURE, measure_qps);
    }

    public static QueryPart newFilter(String value) {
        return getQueryPart(value, Type.FILTER, filter_qps);
    }

    public static QueryPart newFilter(String value, String level) {
        QueryPart part = getQueryPart(value, Type.FILTER, filter_qps);
        part.level = level;
        return part;
    }


    private static QueryPart build(Type t, String value){
        QueryPart qp = new QueryPart();
        qp.t = t;
        qp.value = value;
        return qp;
    }

    private static QueryPart getQueryPart(String value, Type t, TreeMap<Integer, List<QueryPart>> dimension_qps) {
        List<QueryPart> queryParts = dimension_qps.computeIfAbsent(Objects.hash(t, value), x -> {
            List<QueryPart> l = new ArrayList<>();
            l.add(build(t, value));
            return l;
        });

        for (QueryPart queryPart : queryParts) {
            if (queryPart.value.equals(value)) {
                return queryPart;
            }
        }

        QueryPart queryPart = build(t, value);

        queryParts.add(queryPart);

        return queryPart;
    }

    public void debugDumpMaps(){
        System.out.println("--- Dimensions ---");
        System.out.println(dimension_qps.values());
        System.out.println("--- Filters ---");
        System.out.println(filter_qps.values());
        System.out.println("--- Measures ---");
        System.out.println(measure_qps.values());
    }

    public boolean isFilter(){ return t == Type.FILTER;}

    public boolean isMeasure() { return t == Type.MEASURE;}

    public boolean isDimension() {return t == Type.DIMENSION;}

    public Type getType() {
        return t;
    }

    public String getValue() {
        return value;
    }

    public String getLevel() {
        if (!this.isFilter())
            throw new UnsupportedOperationException("Level only exists for filters");
        return level;
    }

    @Override
    public String toString() {
        return "QueryPart{" +
                "" + display.get(t) +
                ", '" + value + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass())
            return false;

        QueryPart other = (QueryPart) obj;
        if (this.t == other.t){
            return other.value.equals(this.value);
        } else
            return false;

    }

    @Override
    public int compareTo(QueryPart o) {
        if (this.equals(o))
            return 0;
        else
            return this.value.compareTo(o.value);
    }


    @Override
    public int hashCode() {
        return Objects.hash(this.t, this.value);
    }
}


