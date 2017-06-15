package main.java;

/**
 * Created by mmatak on 6/15/17.
 */
public class Tool {
    public int id;
    public int size;
    public int count;
    public int cost;

    public Tool(int id, int size, int count, int cost) {
        this.id = id;
        this.size = size;
        this.count = count;
        this.cost = cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tool tool = (Tool) o;

        if (id != tool.id) return false;
        if (size != tool.size) return false;
        if (count != tool.count) return false;
        return cost == tool.cost;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + size;
        result = 31 * result + count;
        result = 31 * result + cost;
        return result;
    }
}
