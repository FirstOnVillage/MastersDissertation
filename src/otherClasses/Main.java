package otherClasses;

import controllers.PasswordFrameController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.encog.Encog;

import javax.swing.*;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage mainStage) throws Exception
    {
        try
        {
            Stage passwordStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("../fxmlFrames/passwordFrame.fxml"));
            passwordStage.setTitle("Аутентификация ");
            passwordStage.getIcons().add(new Image("file:resources/admin.png"));
            passwordStage.setResizable(false);
            Scene scene = new Scene(root);
            passwordStage.setScene(scene);
            passwordStage.initModality(Modality.APPLICATION_MODAL);
            passwordStage.setOnHidden(new EventHandler<WindowEvent>()
            {
                @Override
                public void handle(WindowEvent we)
                {
                    if (PasswordFrameController.pwIsCorrect)
                    {
                        Parent root = null;
                        try {
                            root = FXMLLoader.load(getClass().getResource("../fxmlFrames/mainFrame.fxml"));
                        } catch (IOException ex)
                        {
                            JOptionPane.showMessageDialog(null, ex, "Ошибка", JOptionPane.ERROR_MESSAGE);
                        }
                        mainStage.setTitle("Multilayer perceptron");
                        mainStage.getIcons().add(new Image("file:resources/pc.png"));
                        mainStage.setResizable(false);
                        mainStage.setScene(new Scene(root));
                        mainStage.show();
                    }
                    else
                    {
                        Encog.getInstance().shutdown();
                        System.exit(0);
                    }
                }
            });
            passwordStage.show();
        } catch (IOException ex)
        {
            JOptionPane.showMessageDialog(null, ex, "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
