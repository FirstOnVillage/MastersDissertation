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
import tableModel.batchTableModel;
import tableModel.dataTrainingSetTableModel;

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

    @FXML
    TableView<batchTableModel> batchTable;
    @FXML
    TableColumn colBatchID = new TableColumn();
    @FXML
    TableColumn colBatchName = new TableColumn();
    @FXML
    TableColumn colBatchUser = new TableColumn();
    @FXML
    TableView<dataTrainingSetTableModel> dtsTable;
    @FXML
    TableColumn colDtsBatch = new TableColumn();
    @FXML
    TableColumn colDtsFirstDyeConc = new TableColumn();
    @FXML
    TableColumn colDtsSecondDyeConc = new TableColumn();
    @FXML
    TableColumn colDtsThirdDyeConc = new TableColumn();
    @FXML
    TableColumn colDtsLValue = new TableColumn();
    @FXML
    TableColumn colDtsAValue = new TableColumn();
    @FXML
    TableColumn colDtsBValue = new TableColumn();
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

        MLDataSet trainingSet = new BasicMLDataSet(Data.INPUT, Data.IDEAL);

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
        //logTextArea.selectEnd();
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
        colBatchID.setCellValueFactory(new PropertyValueFactory<batchTableModel, Integer>("batchIdCol"));
        colBatchName.setCellValueFactory(new PropertyValueFactory<batchTableModel, String>("batchNameCol"));
        colBatchUser.setCellValueFactory(new PropertyValueFactory<batchTableModel, String>("addByUserBatchCol"));

        colDtsBatch.setCellValueFactory(new PropertyValueFactory<dataTrainingSetTableModel, String>("dtsBatchCol"));
        colDtsFirstDyeConc.setCellValueFactory(new PropertyValueFactory<dataTrainingSetTableModel, Double>("dtsFirstDyeConcCol"));
        colDtsSecondDyeConc.setCellValueFactory(new PropertyValueFactory<dataTrainingSetTableModel, Double>("dtsSecondDyeConcCol"));
        colDtsThirdDyeConc.setCellValueFactory(new PropertyValueFactory<dataTrainingSetTableModel, Double>("dtsThirdDyeConcCol"));
        colDtsLValue.setCellValueFactory(new PropertyValueFactory<dataTrainingSetTableModel, Double>("dtsLValueCol"));
        colDtsAValue.setCellValueFactory(new PropertyValueFactory<dataTrainingSetTableModel, Double>("dtsAValueCol"));
        colDtsBValue.setCellValueFactory(new PropertyValueFactory<dataTrainingSetTableModel, Double>("dtsBValueCol"));


        batchTable.getItems().setAll(getBatchTableInfo());
        dtsTable.getItems().setAll(getDtsTableInfo());

        batchTable.setOnMouseClicked(event -> {
            batchSelRow = batchTable.getSelectionModel().getSelectedCells().get(0).getRow();
            batchIdTextField.setText(batchTable.getColumns().get(0).getCellData(batchSelRow).toString());
            batchNameTextField.setText(batchTable.getColumns().get(1).getCellData(batchSelRow).toString());
        });
    }

    private List<batchTableModel> getBatchTableInfo()
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
                list.add(new batchTableModel(id, name, changedByUser));
            }
        } catch (SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return list;
    }

    private List<dataTrainingSetTableModel> getDtsTableInfo()
    {
        List list = new LinkedList();
        Statement dtsSt, batchSt, firstDyeSt, SecondDyeSt, ThirdDyeSt, colCoordSt;
        ResultSet dtsRs, batchRs, firstDyeRt, SecondDyeRt, ThirdDyeRt, colCoordRt;
        String batch = "";
        try
        {
            dtsSt = MySQLConnect.getConnection().createStatement();
            String dtsRecordQuery = "Select * from datatrainingset";
            dtsRs = dtsSt.executeQuery(dtsRecordQuery);
            while (dtsRs.next())
            {
                //Integer id = dtsRs.getInt("idDataTrainingSet");
                Integer idBatch = dtsRs.getInt("batch");
                Integer firstDyeId = dtsRs.getInt("firstDye");
                Integer secondDyeId = dtsRs.getInt("secondDye");
                Integer thirdDyeId = dtsRs.getInt("thirdDye");
                Integer colCoordId = dtsRs.getInt("colorCoordinates");

                batchSt = MySQLConnect.getConnection().createStatement();
                String batchRecordQuery = "Select * from batch where idBatch = " + idBatch;
                batchRs = batchSt.executeQuery(batchRecordQuery);
                while (batchRs.next())
                {
                    batch = batchRs.getString("name");
                }
                list.add(new dataTrainingSetTableModel("batch", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
            }
        } catch (SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
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
