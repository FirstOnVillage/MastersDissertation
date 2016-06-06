package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.CalculateScore;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Greedy;
import org.encog.ml.train.strategy.HybridStrategy;
import org.encog.ml.train.strategy.StopTrainingStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import tableModel.BatchTableModel;
import tableModel.DataTrainingSetTableModel;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class MainFrameController implements Initializable
{
    private BasicNetwork network;
    public static String currentUser;
    private int batchSelRow;
    private int dtsSelRow;

    @FXML
    private TableView<BatchTableModel> batchTable;
    @FXML
    private TableColumn colBatchID = new TableColumn();
    @FXML
    private TableColumn colBatchName = new TableColumn();
    @FXML
    private TableColumn colBatchUser = new TableColumn();
    @FXML
    private TableView<DataTrainingSetTableModel> dtsTable;
    @FXML
    private TableColumn colDtsID = new TableColumn();
    @FXML
    private TableColumn colDtsBatch = new TableColumn();
    @FXML
    private TableColumn colDtsFirstDyeConc = new TableColumn();
    @FXML
    private TableColumn colDtsSecondDyeConc = new TableColumn();
    @FXML
    private TableColumn colDtsThirdDyeConc = new TableColumn();
    @FXML
    private TableColumn colDtsLValue = new TableColumn();
    @FXML
    private TableColumn colDtsAValue = new TableColumn();
    @FXML
    private TableColumn colDtsBValue = new TableColumn();
    @FXML
    private TextArea logTextArea;
    @FXML
    private TextField idealLTextField;
    @FXML
    private TextField idealATextField;
    @FXML
    private TextField idealBTextField;
    @FXML
    private TextField firstHiddenLayerTextField;
    @FXML
    private TextField secondHiddenLayerTextField;
    @FXML
    private TextField batchIdTextField;
    @FXML
    private TextField batchNameTextField;
    @FXML
    private ComboBox batchNameComboBox;
    @FXML
    private TextField firstDyeValueTextField;
    @FXML
    private TextField secondDyeValueTextField;
    @FXML
    private TextField thirdDyeValueTextField;
    @FXML
    private TextField lValueTextField;
    @FXML
    private TextField aValueTextField;
    @FXML
    private TextField bValueTextField;
    @FXML
    private ComboBox dyeNumberComboBox;
    @FXML
    private ComboBox availableBatchNameComboBox;
    @FXML
    private Button testButton;
    @FXML
    private Rectangle showColorRectangle;

    @FXML
    private void exitMenuItemAction() throws SQLException
    {
        Encog.getInstance().shutdown();
        MySQLConnect.CloseConnect();
        System.exit(0);
    }

    @FXML
    public void AboutMenuItemAction()
    {
        try
        {
            Stage aboutStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("../fxmlFrames/aboutFrame.fxml"));
            aboutStage.setTitle("О программе");
            aboutStage.getIcons().add(new Image("file:resources/about.png"));
            aboutStage.setResizable(false);
            Scene scene = new Scene(root);
            aboutStage.setScene(scene);
            aboutStage.initModality(Modality.APPLICATION_MODAL);
            aboutStage.show();
        } catch (IOException ex)
        {
            JOptionPane.showMessageDialog(null, ex, "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    @FXML
    private void testButtonAction(ActionEvent actionEvent)
    {
        // create a neural network, without using a factory
        network = new BasicNetwork();
        network.addLayer(new BasicLayer(null,true,3));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, Integer.valueOf(firstHiddenLayerTextField.getText())));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true, Integer.valueOf(secondHiddenLayerTextField.getText())));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),false,2));
        network.getStructure().finalizeStructure();
        network.reset();

        MLDataSet trainingSet = new BasicMLDataSet(
                Data.getColorCoordValuesFromDB(availableBatchNameComboBox.getValue().toString()),
                Data.getDyeValuesFromDB(availableBatchNameComboBox.getValue().toString()));

        // train the neural network
        /*final ResilientPropagation train = new ResilientPropagation(network, trainingSet);

        int epoch = 1;

        do {
            train.iteration();
            System.out.println("Epoch #" + epoch + " Error:" + train.getError());
            epoch++;
        } while(train.getError() > Double.valueOf(errorTextField.getText().replace(",", ".")));
        train.finishTraining();*/
        trainNetwork("Multilayer perceptron", network, trainingSet);

        logTextArea.setText(logTextArea.getText() + "Результаты обучения ИНС");

        for(MLDataPair pair: trainingSet ) {
            final MLData output = network.compute(pair.getInput());
            logTextArea.setText(logTextArea.getText() + "\n\n" + pair.getInput().getData(0) + ", " + pair.getInput().getData(1) + ", " + pair.getInput().getData(2)
                    + "\nactual=" + output.getData(0) + ", " + output.getData(1)
                    + "\nideal=" + pair.getIdeal().getData(0) + ", " + pair.getIdeal().getData(1));
        }
        testButton.setDisable(true);
//        Data.printArray(Data.getColorCoordValuesFromDB(availableBatchNameComboBox.getValue().toString()));
//        Data.printArray(Data.getDyeValuesFromDB(availableBatchNameComboBox.getValue().toString()));
    }

    @FXML
    private void computeButtonAction(ActionEvent actionEvent)
    {
        double input[] = {Double.valueOf(
                idealLTextField.getText().replace(",", ".")),
                Double.valueOf(idealATextField.getText().replace(",", ".")),
                Double.valueOf(idealBTextField.getText().replace(",", "."))};
        double output[] = {0.0, 0.0};
        network.compute(input, output);
        if (Labrgb.revertLABToRGB(input[0], input[1], input[2]))
        {
            showColorRectangle.setFill(javafx.scene.paint.Color.rgb(Labrgb.colR, Labrgb.colG, Labrgb.colB));
        }
        logTextArea.setText(logTextArea.getText() + "\n\nКонцентрация красителей: ");
        for(double element: output )
            logTextArea.setText(logTextArea.getText() + "\n" + element);
        logTextArea.end();
    }

    @FXML
    private void addBatchTableItemButtonAction(ActionEvent actionEvent)
    {
        Statement st;
        try
        {
            st = MySQLConnect.getConnection().createStatement();
            String recordQuery = "insert into batch (idBatch, name, changedByUser) value (" +
                    batchIdTextField.getText() +
                    ", '" + batchNameTextField.getText() +
                    "', " + currentUserIdSearch() + ")";
            st.executeUpdate(recordQuery);
        } catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(null, ex);
        }
        batchTable.getItems().setAll(getBatchTableInfo());
        batchNameComboBox.getItems().setAll(getBatchNames());
        availableBatchNameComboBox.getItems().setAll(getBatchNames());
    }

    @FXML
    private void updateBatchTableItemButtonAction(ActionEvent actionEvent)
    {
        Statement st;
        try
        {
            st = MySQLConnect.getConnection().createStatement();
            String recordQuery = "update batch set name = '" +
                    batchNameTextField.getText() +
                    "' where idBatch = " + batchTable.getColumns().get(0).getCellData(batchSelRow).toString();
            st.execute(recordQuery);
            recordQuery = "update batch set idBatch = '" +
                    batchIdTextField.getText() + "' where idBatch = " +
                    batchTable.getColumns().get(0).getCellData(batchSelRow).toString();
            st.execute(recordQuery);
        } catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(null, ex);
        }
        batchTable.getItems().setAll(getBatchTableInfo());
        batchNameComboBox.getItems().setAll(getBatchNames());
        availableBatchNameComboBox.getItems().setAll(getBatchNames());
    }

    @FXML
    private void deleteBatchTableItemButtonAction(ActionEvent actionEvent)
    {
        Statement st;
        try
        {
            st = MySQLConnect.getConnection().createStatement();
            String recordQuery = "delete from batch where idBatch = " +
                    batchTable.getColumns().get(0).getCellData(batchSelRow).toString();
            st.execute(recordQuery);
        } catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(null, ex);
        }
        batchTable.getItems().setAll(getBatchTableInfo());
        batchNameComboBox.getItems().setAll(getBatchNames());
        availableBatchNameComboBox.getItems().setAll(getBatchNames());
    }

    @FXML
    private void addDtsTableItemButtonAction(ActionEvent actionEvent)
    {
        Statement st;
        try
        {
            st = MySQLConnect.getConnection().createStatement();
            String recordQuery = "insert into datatrainingset (" +
                    "batch, firstdye, seconddye, thirddye, colorcoordinates) value (" +
                    insertDtsTableBatchDataForDB() +
                    ", " +
                    insertDtsTableDataForDB("firstdye", "concValue", firstDyeValueTextField.getText(), "idFirstDye") +
                    ", " +
                    insertDtsTableDataForDB(
                            "seconddye", "concValue", secondDyeValueTextField.getText(), "idSecondDye") +
                    ", " +
                    insertDtsTableDataForDB("thirddye", "concValue", thirdDyeValueTextField.getText(), "idThirdDye") +
                    ", " +
                    insertDtsTableColorCoordDataForDB() +
                    ")";
            st.executeUpdate(recordQuery);
        } catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(null, ex);
        }
        dtsTable.getItems().setAll(getDtsTableInfo());
        batchNameComboBox.getItems().setAll(getBatchNames());
    }

    @FXML
    private void updateDtsTableItemButtonAction(ActionEvent actionEvent)
    {
        updateDtsTableBatchDataForDB();
        updateDtsTableDataForDB("firstdye","concValue", firstDyeValueTextField.getText(), "idfirstdye", "firstdye");
        updateDtsTableDataForDB("seconddye","concValue", secondDyeValueTextField.getText(), "idseconddye", "seconddye");
        updateDtsTableDataForDB("thirddye","concValue", thirdDyeValueTextField.getText(), "idthirddye", "seconddye");
        updateDtsTableColorCoordDataForDB();

        dtsTable.getItems().setAll(getDtsTableInfo());
        batchNameComboBox.getItems().setAll(getBatchNames());
    }

    @FXML
    private void deleteDtsTableItemButtonAction(ActionEvent actionEvent)
    {
        Statement deleteSt, searchSt;
        ResultSet  searchRs;
        Integer firstDyeId = 0;
        Integer secondDyeId = 0;
        Integer thirdDyeId = 0;
        Integer colCoordId = 0;
        try
        {
            searchSt = MySQLConnect.getConnection().createStatement();
            String recordQuery = "Select * from datatrainingset";
            searchRs = searchSt.executeQuery(recordQuery);
            while (searchRs.next())
            {
                firstDyeId = searchRs.getInt("firstDye");
                secondDyeId = searchRs.getInt("secondDye");
                thirdDyeId = searchRs.getInt("thirdDye");
                colCoordId = searchRs.getInt("colorCoordinates");
            }
            deleteSt = MySQLConnect.getConnection().createStatement();
            recordQuery = "delete from datatrainingset where iddatatrainingset = " +
                    dtsTable.getColumns().get(0).getCellData(dtsSelRow).toString();
            deleteSt.execute(recordQuery);
            deleteDataFromSelectTableDB("firstDye", "idfirstDye", firstDyeId);
            deleteDataFromSelectTableDB("secondDye", "idsecondDye", secondDyeId);
            deleteDataFromSelectTableDB("thirdDye", "idthirdDye", thirdDyeId);
            deleteDataFromSelectTableDB("colorCoordinates", "idcolorCoordinates", colCoordId);
        } catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(null, ex);
        }
        dtsTable.getItems().setAll(getDtsTableInfo());
        batchNameComboBox.getItems().setAll(getBatchNames());
        availableBatchNameComboBox.getItems().setAll(getBatchNames());
    }

    public static double trainNetwork(final String what,
                                      final BasicNetwork network, final MLDataSet trainingSet) {
        // train the neural network
        CalculateScore score = new TrainingSetScore(trainingSet);
        final MLTrain trainAlt = new NeuralSimulatedAnnealing(
                network, score, 10, 2, 100);

        final MLTrain trainMain = new ResilientPropagation(network, trainingSet, 0.000001, 0.0);

        final StopTrainingStrategy stop = new StopTrainingStrategy();
        trainMain.addStrategy(new Greedy());
        trainMain.addStrategy(new HybridStrategy(trainAlt));
        trainMain.addStrategy(stop);

        int epoch = 0;
        while (!stop.shouldStop()) {
            trainMain.iteration();
            System.out.println("Training " + what + ", Epoch #" + epoch
                    + " Error:" + trainMain.getError());
            epoch++;
        }
        return trainMain.getError();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        colBatchID.setCellValueFactory(new PropertyValueFactory<BatchTableModel, Integer>("batchIdCol"));
        colBatchName.setCellValueFactory(new PropertyValueFactory<BatchTableModel, String>("batchNameCol"));
        colBatchUser.setCellValueFactory(new PropertyValueFactory<BatchTableModel, String>("addByUserBatchCol"));

        colDtsID.setCellValueFactory(new PropertyValueFactory<DataTrainingSetTableModel, Integer>("dtsIdCol"));
        colDtsBatch.setCellValueFactory(new PropertyValueFactory<DataTrainingSetTableModel, String>("dtsBatchCol"));
        colDtsFirstDyeConc.setCellValueFactory(new PropertyValueFactory<DataTrainingSetTableModel, Double>("dtsFirstDyeConcCol"));
        colDtsSecondDyeConc.setCellValueFactory(new PropertyValueFactory<DataTrainingSetTableModel, Double>("dtsSecondDyeConcCol"));
        colDtsThirdDyeConc.setCellValueFactory(new PropertyValueFactory<DataTrainingSetTableModel, Double>("dtsThirdDyeConcCol"));
        colDtsLValue.setCellValueFactory(new PropertyValueFactory<DataTrainingSetTableModel, Double>("dtsLValueCol"));
        colDtsAValue.setCellValueFactory(new PropertyValueFactory<DataTrainingSetTableModel, Double>("dtsAValueCol"));
        colDtsBValue.setCellValueFactory(new PropertyValueFactory<DataTrainingSetTableModel, Double>("dtsBValueCol"));

        batchTable.getItems().setAll(getBatchTableInfo());
        dtsTable.getItems().setAll(getDtsTableInfo());

        batchTable.setOnMouseClicked(event -> {
            batchSelRow = batchTable.getSelectionModel().getSelectedCells().get(0).getRow();
            batchIdTextField.setText(batchTable.getColumns().get(0).getCellData(batchSelRow).toString());
            batchNameTextField.setText(batchTable.getColumns().get(1).getCellData(batchSelRow).toString());
        });

        dtsTable.setOnMouseClicked(event -> {
            dtsSelRow = dtsTable.getSelectionModel().getSelectedCells().get(0).getRow();
            for (int i = 0; i < batchNameComboBox.getItems().size(); i++)
            {
                if (dtsTable.getColumns().get(1).getCellData(dtsSelRow).toString().equals(batchNameComboBox.getItems().get(i)))
                {
                    batchNameComboBox.getSelectionModel().select(i);
                }
            }
            firstDyeValueTextField.setText(dtsTable.getColumns().get(2).getCellData(dtsSelRow).toString());
            secondDyeValueTextField.setText(dtsTable.getColumns().get(3).getCellData(dtsSelRow).toString());
            thirdDyeValueTextField.setText(dtsTable.getColumns().get(4).getCellData(dtsSelRow).toString());
            lValueTextField.setText(dtsTable.getColumns().get(5).getCellData(dtsSelRow).toString());
            aValueTextField.setText(dtsTable.getColumns().get(6).getCellData(dtsSelRow).toString());
            bValueTextField.setText(dtsTable.getColumns().get(7).getCellData(dtsSelRow).toString());
        });

        batchNameComboBox.getItems().setAll(getBatchNames());
        dyeNumberComboBox.getItems().setAll(getDyeNumber());
        availableBatchNameComboBox.getItems().setAll(getBatchNames());
        dyeNumberComboBox.getSelectionModel().select(1);
        availableBatchNameComboBox.getSelectionModel().select(0);
    }

    private List<BatchTableModel> getBatchTableInfo()
    {
        List list = new LinkedList();
        Statement batchSt, usersSt;
        ResultSet batchRs, usersRs;
        String changedByUser = "";
        try
        {
            batchSt = MySQLConnect.getConnection().createStatement();
            String batchRecordQuery = "Select * from batch";
            batchRs = batchSt.executeQuery(batchRecordQuery);
            while (batchRs.next())
            {
                Integer id = batchRs.getInt("idBatch");
                String name = batchRs.getString("name");
                String user = batchRs.getString("changedByUser");
                usersSt = MySQLConnect.getConnection().createStatement();
                String usersRecordQuery = "Select * from users where idUsers = " + user;
                usersRs = usersSt.executeQuery(usersRecordQuery);
                while (usersRs.next())
                {
                    changedByUser = usersRs.getString("name");
                }
                list.add(new BatchTableModel(id, name, changedByUser));
            }
        } catch (SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return list;
    }

    private List<DataTrainingSetTableModel> getDtsTableInfo()
    {
        List list = new LinkedList();
        Statement dtsSt, colorCoordSt;
        ResultSet dtsRs, colorCoordRs;
        Double lValue = 0.0;
        Double aValue = 0.0;
        Double bValue = 0.0;
        try
        {
            dtsSt = MySQLConnect.getConnection().createStatement();
            String dtsRecordQuery = "Select * from datatrainingset";
            dtsRs = dtsSt.executeQuery(dtsRecordQuery);
            while (dtsRs.next())
            {
                Integer id = dtsRs.getInt("iddatatrainingset");
                Integer idBatch = dtsRs.getInt("batch");
                Integer firstDyeId = dtsRs.getInt("firstDye");
                Integer secondDyeId = dtsRs.getInt("secondDye");
                Integer thirdDyeId = dtsRs.getInt("thirdDye");
                Integer colCoordId = dtsRs.getInt("colorCoordinates");
                colorCoordSt = MySQLConnect.getConnection().createStatement();
                String colorCoordRecordQuery = "Select * from colorcoordinates where idColorCoordinates = " + colCoordId;
                colorCoordRs = colorCoordSt.executeQuery(colorCoordRecordQuery);
                while (colorCoordRs.next())
                {
                    lValue = colorCoordRs.getDouble("lValue");
                    aValue = colorCoordRs.getDouble("aValue");
                    bValue = colorCoordRs.getDouble("bValue");
                }
                list.add(new DataTrainingSetTableModel(
                        id,
                        getValuePerIdFromDB("batch", "idBatch", idBatch.toString(), "name").toString(),
                        Double.valueOf(getValuePerIdFromDB("firstdye", "idFirstDye", firstDyeId.toString(), "concValue").toString()),
                        Double.valueOf(getValuePerIdFromDB("seconddye", "idSecondDye", secondDyeId.toString(), "concValue").toString()),
                        Double.valueOf(getValuePerIdFromDB("thirddye", "idThirdDye", thirdDyeId.toString(), "concValue").toString()),
                        lValue,
                        aValue,
                        bValue));
            }
        } catch (SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return list;
    }

    private Object getValuePerIdFromDB(String table, String idName, String idValue, String nameField)
    {
        Object result = null;
        try
        {
            Statement st = MySQLConnect.getConnection().createStatement();
            String recordQuery = "Select * from " +  table + " where " + idName + " = " + idValue;
            ResultSet rs = st.executeQuery(recordQuery);
            while (rs.next())
            {
                result = rs.getObject(nameField);
            }
            return result;
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    private Integer insertDtsTableDataForDB(String table, String fieldName, String fieldValue, String idName)
    {
        Integer result = 0;
        try
        {
            Statement insetSt = MySQLConnect.getConnection().createStatement();
            String insercRecordQuery = "insert into " +  table + " ( " + fieldName + " ) value ( " + fieldValue + ")";
            insetSt.executeUpdate(insercRecordQuery);
            Statement getSt = MySQLConnect.getConnection().createStatement();
            String getRecordQuery = "select * from " + table + " where " + fieldName + " = " + fieldValue;
            ResultSet rs = getSt.executeQuery(getRecordQuery);
            while (rs.next())
            {
                result = rs.getInt(idName);
            }
        } catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(null, ex);
        }
        return result;
    }

    private Integer insertDtsTableBatchDataForDB()
    {
        Integer result = 0;
        try
        {
            Statement st = MySQLConnect.getConnection().createStatement();
            String recordQuery = "select * from batch where name = '" + batchNameComboBox.getValue().toString() + "'";
            ResultSet rs = st.executeQuery(recordQuery);
            while (rs.next())
            {
                result = rs.getInt("idBatch");
            }
        } catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(null, ex);
        }
        return result;
    }

    private Integer insertDtsTableColorCoordDataForDB()
    {
        Integer result = 0;
        try
        {
            Statement insetSt = MySQLConnect.getConnection().createStatement();
            String insercRecordQuery = "insert into colorcoordinates (lValue, aValue, bValue) value (" +
                    lValueTextField.getText() +
                    ", " +
                    aValueTextField.getText() +
                    ", " +
                    bValueTextField.getText() +
                    ")";
            insetSt.executeUpdate(insercRecordQuery);
            Statement getSt = MySQLConnect.getConnection().createStatement();
            String getRecordQuery = "select * from colorcoordinates where lValue = " +
                    lValueTextField.getText() +
                    " and aValue = " +
                    aValueTextField.getText() +
                    " and bValue = " +
                    bValueTextField.getText();
            ResultSet rs = getSt.executeQuery(getRecordQuery);
            while (rs.next())
            {
                result = rs.getInt("idColorCoordinates");
            }
        } catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(null, ex);
        }
        return result;
    }

    private void updateDtsTableDataForDB(
            String table, String fieldName, String fieldValue, String idName, String fieldDtsTableName)
    {
        Statement st;
        try
        {
            st = MySQLConnect.getConnection().createStatement();
            String recordQuery = "update " + table + " set " + fieldName + " = " + fieldValue +
                    " where " + idName + " = " +
                    getDataValueById(fieldDtsTableName);
            st.execute(recordQuery);
        } catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    private void updateDtsTableBatchDataForDB()
    {
        Statement st;
        try
        {
            st = MySQLConnect.getConnection().createStatement();
            String recordQuery = "update datatrainingset set batch = " +
                    insertDtsTableBatchDataForDB() +
                    " where iddatatrainingset = " + dtsTable.getColumns().get(0).getCellData(dtsSelRow).toString();
            st.execute(recordQuery);
        } catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    private void updateDtsTableColorCoordDataForDB()
    {
        Statement st;
        try
        {
            st = MySQLConnect.getConnection().createStatement();
            String recordQuery = "update colorcoordinates set lValue = " + lValueTextField.getText() +
                    " where idcolorcoordinates = " +
                    getDataValueById("colorcoordinates");
            st.execute(recordQuery);
            recordQuery = "update colorcoordinates set aValue = " + aValueTextField.getText() +
                    " where idcolorcoordinates = " +
                    getDataValueById("colorcoordinates");
            st.execute(recordQuery);
            recordQuery = "update colorcoordinates set bValue = " + bValueTextField.getText() +
                    " where idcolorcoordinates = " +
                    getDataValueById("colorcoordinates");
            st.execute(recordQuery);
        } catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    private Integer getDataValueById(String fieldName)
    {
        Integer result = 0;
        try
        {
            Statement st = MySQLConnect.getConnection().createStatement();
            String recordQuery = "select * from datatrainingset where idDataTrainingSet = " +
                    dtsTable.getColumns().get(0).getCellData(dtsSelRow).toString();
            ResultSet rs = st.executeQuery(recordQuery);
            while (rs.next())
            {
                result = rs.getInt(fieldName);
            }
        } catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(null, ex);
        }
        return result;
    }

    private void deleteDataFromSelectTableDB(String table, String idName, Integer idValue)
    {
        Statement st;
        try
        {
            st = MySQLConnect.getConnection().createStatement();
            String recordQuery = "delete from " + table + " where " + idName + " = " + idValue;
            st.execute(recordQuery);
        } catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    public List<String> getBatchNames()
    {
        Statement st;
        ResultSet rs;
        List list = new LinkedList();
        try
        {
            st = MySQLConnect.getConnection().createStatement();
            String recordQuery = ("Select * from batch");
            rs = st.executeQuery(recordQuery);
            while (rs.next())
            {
                list.add(rs.getString("name"));
            }
        } catch (SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return list;
    }

    public List<String> getDyeNumber()
    {
        List list = new LinkedList();
        list.add(1);
        list.add(2);
        list.add(3);
        return list;
    }

    private int currentUserIdSearch()
    {
        Statement st;
        ResultSet rs;
        try
        {
            st = MySQLConnect.getConnection().createStatement();
            String recordQuery = "Select * from users where name = '" + currentUser + "'";
            rs = st.executeQuery(recordQuery);
            while (rs.next())
            {
                return rs.getInt("idUsers");
            }
        } catch (SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return 1;
    }
}
