package com.olap3.cubeexplorer.model.columnStore;

import com.alexscode.utilities.Future;
import com.alexscode.utilities.collection.Pair;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A 2D (CSV like) representation of a result that can be processed by a MLModel
 */
public class DataSet {

    @Getter
    final List<Pair<String, Datatype>> descriptors;
    List<String> lineOrder;
    int[] typeMap;
    int width, //Number of columns
            depth;

    boolean allocated = false;
    int insert_index = 0;

    HashMap<String, Integer> posReal;
    HashMap<String, Integer> posInt;
    HashMap<String, Integer> posString;

    double[][] dataReal;
    int[][] dataInt;
    String[][] dataStr;


    public DataSet(List<Pair<String, Datatype>> columnsDef, int size) {
        depth = size;
        this.descriptors = columnsDef;

        //Save column order and type
        this.lineOrder = columnsDef.stream().map(p -> p.left).collect(Collectors.toList());
        width = columnsDef.size();
        typeMap = new int[width];
        for (int i = 0; i < columnsDef.size(); i++) {
            switch (columnsDef.get(i).right){
                case REAL -> typeMap[i] = 0;
                case INTEGER -> typeMap[i] = 1;
                case STRING -> typeMap[i] = 2;
            }
        }

        /*
            Build the real store
         */
        // Get relevant columns
        List<String> cols = columnsDef.stream().filter(p -> p.right.isReal()).map(p -> p.left).collect(Collectors.toList());
        // Build the index map and malloc arrays
        posReal = new HashMap<>(cols.size());
        dataReal = new double[cols.size()][];
        for (int i = 0; i < cols.size(); i++) {
            posReal.put(cols.get(i), i);
        }

        /*
            Build the int store
         */
        // Get relevant columns
        cols = columnsDef.stream().filter(p -> p.right.isInt()).map(p -> p.left).collect(Collectors.toList());
        // Build the index map and malloc arrays
        posInt = new HashMap<>(cols.size());
        dataInt = new int[cols.size()][];
        for (int i = 0; i < cols.size(); i++) {
            posInt.put(cols.get(i), i);
        }

        /*
            Build the String store
         */
        // Get relevant columns
        cols = columnsDef.stream().filter(p -> p.right.isString()).map(p -> p.left).collect(Collectors.toList());
        // Build the index map and malloc arrays
        posString = new HashMap<>(cols.size());
        dataStr = new String[cols.size()][];
        for (int i = 0; i < cols.size(); i++) {
            posString.put(cols.get(i), i);
        }

    }

    /**
     * don't replace this with Arrays.fill, especially if you have no idea why you wouldn't do that
     */
    public void allocate(){
        for (int i = 0; i < dataReal.length; i++) {
            dataReal[i] = new double[depth];
        }
        for (int i = 0; i < dataInt.length; i++) {
            dataInt[i] = new int[depth];
        }
        for (int i = 0; i < dataStr.length; i++) {
            dataStr[i] = new String[depth];
        }

        allocated = true;
    }

    public Object[] getLine(int line){
        Object[] res = new Object[width];
        for (int j = 0; j < width; j++) {
            switch (typeMap[j]){
                case 0 -> res[j] = dataReal[posReal.get(lineOrder.get(j))][line];
                case 1 -> res[j] = dataInt[posInt.get(lineOrder.get(j))][line];
                case 2 -> res[j] = dataStr[posString.get(lineOrder.get(j))][line];
            }
        }
        return res;
    }

    public void setLine(Object[] input, int row_index){
        //System.out.println(Arrays.toString(input));
        if (input.length != width)
            throw new IllegalArgumentException("Input size doesn't match number of columns");
        for (int i = 0; i < width; i++) {
            //System.out.println(typeMap[i]);
            switch (typeMap[i]){
                case 0 -> {
                    try {
                        dataReal[posReal.get(lineOrder.get(i))][row_index] = (double) input[i];
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
                case 1 -> dataInt[posInt.get(lineOrder.get(i))][row_index] = (int) input[i];
                case 2 -> dataStr[posString.get(lineOrder.get(i))][row_index] = (String) input[i];
            }
        }
    }

    public void pushLine(Object[] input){
        if (!allocated) {
            allocated = true;
            allocate();
        }
        setLine(input, insert_index);
        insert_index++;
    }

    public List<Object[]> selectConjunctive(List<Predicate> predicates){
        boolean[] sel = new boolean[depth];
        Arrays.fill(sel, true);

        for (int i = 0; i < predicates.size(); i++) {
            Predicate p = predicates.get(i);
            boolean[] vec = new boolean[sel.length];
            switch (typeMap[lineOrder.indexOf(p.getCol())]) {
                case 0 -> vec = p.getBinaryVector(getDoubleColumn(p.getCol()));
                case 1 -> vec = p.getBinaryVector(getIntColumn(p.getCol()));
                case 2 -> vec = p.getBinaryVector(getStringColumn(p.getCol()));
            }
            for (int j = 0; j < vec.length; j++) {
                sel[j] = sel[j] && vec[j];
            }
        }

        List<Object[]> lines = new ArrayList<>();
        for (int i = 0; i < sel.length; i++) {
            if (sel[i])
                lines.add(getLine(i));
        }

        return lines;
    }

    public void setColumn(String name, double[] array){
        dataReal[posReal.get(name)] = array;
    }
    public void setColumn(String name, int[] array){
        dataInt[posInt.get(name)] = array;
    }
    public void setColumn(String name, String[] array){
        dataStr[posString.get(name)] = array;
    }

    public int getNumberOfColumns(){
        return width;
    }
    public int getNumberOfRows(){
        return depth;
    }

    public int[] getIntColumn(int column_index) {
        return dataInt[column_index];
    }
    public int[] getIntColumn(String column_name) {
        return dataInt[posInt.get(column_name)];
    }
    public double[] getDoubleColumn(int column_index) {
        return dataReal[column_index];
    }
    public double[] getDoubleColumn(String column_name) {
        Integer index = posReal.get(column_name);
        if (index == null)
            return null;
        return dataReal[index];
    }
    public String[] getStringColumn(int column_index) {
        return dataStr[column_index];
    }
    public String[] getStringColumn(String column_name) {
        return dataStr[posString.get(column_name)];
    }

    public String getColName(int column_index) {
        return  lineOrder.get(column_index);
    }
    public int getColIndex(String colName){
        return lineOrder.indexOf(colName);
    }

    public Datatype getColDatatype(int column_index) {
        switch (typeMap[column_index]) {
            case 0 : return Datatype.REAL;
            case 1 : return Datatype.INTEGER;
            case 2 : return Datatype.STRING;
        }
        throw new IllegalStateException("Illegal datatype for column " + column_index + " : " + typeMap[column_index]);
    }

    public Datatype getColDatatype(String column_name) {
       return getColDatatype(lineOrder.indexOf(column_name));
    }

    public String[] getHeader(){
        return lineOrder.toArray(new String[0]);
    }


    @Override
    public String toString() {
        String str =  "--- DataSet ---\n";
        str += Future.join(lineOrder, ",") + "\n";
        for (int i = 0; i < depth; i++) {
            str += Arrays.toString(getLine(i)) + "\n";
        }
        return str;
    }

}


