package cbp.marketlist.tableview;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cbp.marketlist.key.Field;

/**
 * 列集合
 *
 * @author cbp
 */
public class ColumnSet {
    private List<Column> mColumns = new ArrayList<>();
    private List<Field<?, ?>> mFields = new ArrayList<>();

    //
    public static ColumnSet newInstance() {
        return new ColumnSet();
    }

    private ColumnSet() {
    }

    private static class Column {
        String name;
        Field<?, ?>[] fields;

        private Column(String name, Field<?, ?>... fields) {
            this.name = name;
            this.fields = fields;
        }
    }

    public ColumnSet defineColumn(String name, Field<?, ?>... fields) {
        Collections.addAll(mFields, fields);
        mColumns.add(new Column(name, fields));
        return this;
    }

    public String[] getColumnNames() {
        int size = mColumns.size();
        String[] columnNames = new String[size];
        for (int i = 0; i < size; i++) {
            columnNames[i] = mColumns.get(i).name;
        }
        return columnNames;
    }

    public Field<?, ?>[] getColumnFields() {
        int size = mFields.size();
        Field<?, ?>[] fields = new Field<?, ?>[size];
        for (int i = 0; i < size; i++) {
            fields[i] = mFields.get(i);
        }
        return fields;
    }

    public String getColumnNameByIndex(int index) {
        return mColumns.get(index).name;
    }

    public Field<?, ?>[] getColumnFieldsByIndex(int index) {
        return mColumns.get(index).fields;
    }

    public int getColumnIndexByField(Field<?, ?> field) {
        for (int i = 0; i < mColumns.size(); i++) {
            for (int j = 0; j < mColumns.get(i).fields.length; j++) {
                if (field == mColumns.get(i).fields[j]) {
                    return i;
                }
            }
        }
        return 0;
    }
}
