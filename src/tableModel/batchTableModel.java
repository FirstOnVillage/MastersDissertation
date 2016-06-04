package tableModel;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class batchTableModel
{
    private final SimpleIntegerProperty batchIdCol;
    private final SimpleStringProperty batchNameCol;
    private final SimpleStringProperty addByUserBatchCol;

    public batchTableModel(Integer id, String name, String addByUser)
    {
        this.batchIdCol = new SimpleIntegerProperty(id);
        this.batchNameCol = new SimpleStringProperty(name);
        this.addByUserBatchCol = new SimpleStringProperty(addByUser);
    }

    public Integer getBatchIdCol()
    {
        return batchIdCol.get();
    }

    public void setBatchIdCol(Integer id)
    {
        batchIdCol.set(id);
    }

    public String getBatchNameCol()
    {
        return batchNameCol.get();
    }

    public void setBatchNameCol(String name) { batchNameCol.set(name); }

    public String getAddByUserBatchCol()
    {
        return addByUserBatchCol.get();
    }

    public void setAddByUserBatchCol(String user) { addByUserBatchCol.set(user); }

}
