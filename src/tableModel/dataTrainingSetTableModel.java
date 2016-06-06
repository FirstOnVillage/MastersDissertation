package tableModel;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;


public class DataTrainingSetTableModel
{
    private final SimpleIntegerProperty dtsIdCol;
    private final SimpleStringProperty dtsBatchCol;
    private final SimpleDoubleProperty dtsFirstDyeConcCol;
    private final SimpleDoubleProperty dtsSecondDyeConcCol;
    private final SimpleDoubleProperty dtsThirdDyeConcCol;
    private final SimpleDoubleProperty dtsLValueCol;
    private final SimpleDoubleProperty dtsAValueCol;
    private final SimpleDoubleProperty dtsBValueCol;

    public DataTrainingSetTableModel(Integer id,
                                     String batch,
                                     Double firstDyeConc,
                                     Double secondDyeConc,
                                     Double thirdDyeConc,
                                     Double lValue,
                                     Double aValue,
                                     Double bValue)
    {
        this.dtsIdCol = new SimpleIntegerProperty(id);
        this.dtsBatchCol = new SimpleStringProperty(batch);
        this.dtsFirstDyeConcCol = new SimpleDoubleProperty(firstDyeConc);
        this.dtsSecondDyeConcCol = new SimpleDoubleProperty(secondDyeConc);
        this.dtsThirdDyeConcCol = new SimpleDoubleProperty(thirdDyeConc);
        this.dtsLValueCol = new SimpleDoubleProperty(lValue);
        this.dtsAValueCol = new SimpleDoubleProperty(aValue);
        this.dtsBValueCol = new SimpleDoubleProperty(bValue);
    }

    public Integer getDtsIdCol()
    {
        return dtsIdCol.get();
    }

    public void setDtsIdCol(Integer id) { dtsIdCol.set(id); }

    public String getDtsBatchCol()
    {
        return dtsBatchCol.get();
    }

    public void setDtsBatchCol(String batch) { dtsBatchCol.set(batch); }

    public Double getDtsFirstDyeConcCol() { return dtsFirstDyeConcCol.get(); }

    public void setDtsFirstDyeConcCol(Double conc) { dtsFirstDyeConcCol.set(conc); }

    public Double getDtsSecondDyeConcCol()
    {
        return dtsSecondDyeConcCol.get();
    }

    public void setDtsSecondDyeConcCol(Double conc) { dtsSecondDyeConcCol.set(conc); }

    public Double getDtsThirdDyeConcCol()
    {
        return dtsThirdDyeConcCol.get();
    }

    public void setDtsThirdDyeConcCol(Double conc) { dtsThirdDyeConcCol.set(conc); }

    public Double getDtsLValueCol() { return dtsLValueCol.get(); }

    public void setDtsLValueCol(Double value) { dtsLValueCol.set(value); }

    public Double getDtsAValueCol() { return dtsAValueCol.get(); }

    public void setDtsAValueCol(Double value) { dtsAValueCol.set(value); }

    public Double getDtsBValueCol() { return dtsBValueCol.get(); }

    public void setDtsBValueCol(Double value) { dtsBValueCol.set(value); }
}
