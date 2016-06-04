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
    BasicNetwork network;

    @FXML
    TableView<batchTableModel> batchTable;
    @FXML
    TableColumn colBatchID = new TableColumn();
    @FXML
    TableColumn colBatchName = new TableColumn();
    @FXML
    TableColumn colBatchUser = new TableColumn();
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

        batchTable.getItems().setAll(getBatchTableInfo());
    }

    public List<batchTableModel> getBatchTableInfo()
    {
        List list = new LinkedList();
        Statement st;
        ResultSet rs;
        try
        {
            st = MySQLConnect.getConnection().createStatement();
            String recordQuery = ("Select * from batch");
            rs = st.executeQuery(recordQuery);
            while (rs.next())
            {
                Integer id = rs.getInt("idBatch");
                String name = rs.getString("name");
                String user = rs.getString("changedByUser");
                list.add(new batchTableModel(id, name, user));
            }
        } catch (SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return list;
    }
}
