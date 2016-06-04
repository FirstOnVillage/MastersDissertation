package tableModel;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by jelus on 04.06.2016.
 */
public class dataTrainingSetTableModel
{
    private final SimpleStringProperty dtsBatchCol;
    private final SimpleDoubleProperty dtsFirstDyeConcCol;
    private final SimpleDoubleProperty dtsSecondDyeConcCol;
    private final SimpleDoubleProperty dtsThirdDyeConcCol;
    private final SimpleDoubleProperty dtsLValueCol;
    private final SimpleDoubleProperty dtsAValueCol;
    private final SimpleDoubleProperty dtsBValueCol;

    public dataTrainingSetTableModel(String batch,
                                     Double firstDyeConc,
                                     Double secondDyeConc,
                                     Double thirdDyeConc,
                                     Double lValue,
                                     Double aValue,
                                     Double bValue)
    {
        this.dtsBatchCol = new SimpleStringProperty(batch);
        this.dtsFirstDyeConcCol = new SimpleDoubleProperty(firstDyeConc);
        this.dtsSecondDyeConcCol = new SimpleDoubleProperty(secondDyeConc);
        this.dtsThirdDyeConcCol = new SimpleDoubleProperty(thirdDyeConc);
        this.dtsLValueCol = new SimpleDoubleProperty(lValue);
        this.dtsAValueCol = new SimpleDoubleProperty(aValue);
        this.dtsBValueCol = new SimpleDoubleProperty(bValue);
    }

    public String getDTSBatchCol()
    {
        return dtsBatchCol.get();
    }

    public void setDTSBatchCol(String batch) { dtsBatchCol.set(batch); }

    public Double getDTSFirstDyeConcCol() { return dtsFirstDyeConcCol.get(); }

    public void setDTSFirstDyeConcCol(Double conc) { dtsFirstDyeConcCol.set(conc); }

    public Double getDTSSecondDyeConcCol()
    {
        return dtsSecondDyeConcCol.get();
    }

    public void setDTSSecondDyeConcCol(Double conc) { dtsSecondDyeConcCol.set(conc); }

    public Double getDTSThirdDyeConcCol()
    {
        return dtsThirdDyeConcCol.get();
    }

    public void setDTSThirdDyeConcCol(Double conc) { dtsThirdDyeConcCol.set(conc); }

    public Double getDTSLValueCol() { return dtsLValueCol.get(); }

    public void setDTSLValueCol(Double value) { dtsLValueCol.set(value); }

    public Double getDTSAValueCol() { return dtsAValueCol.get(); }

    public void setDTSAValueCol(Double value) { dtsAValueCol.set(value); }

    public Double getDTSBValueCol() { return dtsBValueCol.get(); }

    public void setDTSBValueCol(Double value) { dtsBValueCol.set(value); }
}
