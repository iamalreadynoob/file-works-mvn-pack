package fileWorks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CSV
{

    private final String path;
    private String equivalent, separator, nullSign;
    private ArrayList<String> headers;
    private ArrayList<ArrayList<String>> columns;

    public CSV(String path)
    {
        this.path = path;
        headers = new ArrayList<>();
        columns = new ArrayList<>();
        setDefault();
    }

    public void setEquivalent(String equivalent) {this.equivalent = equivalent;}
    public String getEquivalent() {return equivalent;}
    public void setSeparator(String separator) {this.separator = separator;}
    public String getSeparator() {return separator;}
    public void setNull(String nullSign) {this.nullSign = nullSign;}
    public String getNull() {return nullSign;}

    public void setDefault()
    {
        equivalent = "%comma%";
        separator = ",";
        nullSign = "NULL";
    }

    public void scan()
    {
        ArrayList<String> rawLines = TextCommunication.read(path);
        ArrayList<String> lines = new ArrayList<>();
        for (String ln: rawLines) if (!ln.isBlank() && !ln.isEmpty()) lines.add(ln);

        boolean flag = isDB(lines);

        if (flag)
        {
            String[] hList = lines.get(0).split(separator);
            for (String h: hList) headers.add(h.replaceAll(equivalent, separator));

            for (int i = 0; i < headers.size(); i++) columns.add(new ArrayList<>());

            for (int i = 1; i < lines.size(); i++)
            {
                String[] cells = lines.get(i).split(separator);
                for (int j = 0; j < headers.size(); j++) columns.get(j).add(cells[j].replaceAll(equivalent, separator));
            }
        }
        else
        {
            System.err.println("The database is broken, contains much or less cells at least in one row");
            System.exit(1);
        }
    }

    public ArrayList<String> getHeaders() {return headers;}

    public ArrayList<ArrayList<String>> getColumns() {return columns;}

    public ArrayList<String> getColumn(String header)
    {
        if (headers.contains(header))
        {
            int index = headers.indexOf(header);
            return columns.get(index);
        }

        return new ArrayList<>();
    }

    public void addRow(ArrayList<String> rows)
    {
        boolean flag = true;

        for (String r: rows)
            if (r.split(separator).length != headers.size())
            {
                flag = false;
                break;
            }

        if (flag)
        {
            for (String r: rows)
            {
                String[] cells = r.split(separator);
                for (int j = 0; j < headers.size(); j++) columns.get(j).add(cells[j].replaceAll(equivalent, separator));
            }
        }
        else System.err.println("Rows weren't added because they don't have exact cell amount");
    }

    public void addRow(String[] rows)
    {
        ArrayList<String> temp = new ArrayList<>();
        for (String r: rows) temp.add(r);
        addRow(temp);
    }

    public void addRow(String row)
    {
        ArrayList<String> temp = new ArrayList<>();
        temp.add(row);
        addRow(temp);
    }

    public String getCell(String header, int row)
    {
        String cell = null;

        if (headers.contains(header) && row >= 0 && row < columns.get(0).size())
        {
            cell = columns.get(headers.indexOf(header)).get(row);
            if (cell.isEmpty() || cell.isBlank()) cell = nullSign;
        }


        return cell;
    }

    public void setCell(String header, int row, String newVal)
    {
        if (newVal == null) newVal = nullSign;

        if (headers.contains(header) && row >= 0 && row < columns.get(0).size())
            columns.get(headers.indexOf(header)).set(row, newVal);
    }

    public void deleteCell(String header, int row)
    {
        setCell(header, row, nullSign);
    }

    public boolean isNull(String header, int row)
    {
        boolean flag = false;

        String cell = getCell(header, row);
        if (cell.equals(nullSign)) flag = true;

        return flag;
    }

    public void deleteRow(int index)
    {
        if (index < columns.get(0).size() && index >= 0)
            for (int i = 0; i < headers.size(); i++)
                columns.get(i).remove(index);
    }

    public Map<String, String> getMap(String keyHeader, String valueHeader)
    {
        Map<String, String> map = new HashMap<>();

        if (headers.contains(keyHeader) && headers.contains(valueHeader))
        {
            ArrayList<String> keyColumn = getColumn(keyHeader);
            ArrayList<String> valColumn = getColumn(valueHeader);

            //filter them to unique
        }

        return map;
    }

    public void save()
    {

    }

    private boolean isDB(ArrayList<String> lines)
    {
        boolean flag = true;

        int headerAmount = lines.get(0).split(separator).length;

        for (int i = 1; i < lines.size(); i++)
        {
            int cells = lines.get(i).split(separator).length;

            if (headerAmount != cells)
            {
                flag = false;
                break;
            }
        }

        return flag;
    }
}
