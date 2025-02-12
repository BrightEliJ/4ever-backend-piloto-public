package tim.field.application.TarefasWFM.dto;

import java.util.List;

public class DataTablesResponse<T> {
    private int draw;
    private long recordsTotal;
    private long recordsFiltered;
    private List<T> data;

    // Construtor
    public DataTablesResponse(int draw, long recordsTotal, long recordsFiltered, List<T> data) {
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.data = data;
    }

    // Getters e Setters
    public int getDraw() { return draw; }
    public void setDraw(int draw) { this.draw = draw; }

    public long getRecordsTotal() { return recordsTotal; }
    public void setRecordsTotal(long recordsTotal) { this.recordsTotal = recordsTotal; }

    public long getRecordsFiltered() { return recordsFiltered; }
    public void setRecordsFiltered(long recordsFiltered) { this.recordsFiltered = recordsFiltered; }

    public List<T> getData() { return data; }
    public void setData(List<T> data) { this.data = data; }
}