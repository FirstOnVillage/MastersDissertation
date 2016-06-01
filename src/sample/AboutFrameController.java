package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class AboutFrameController
{
    @FXML
    private Button closeButton;

    @FXML
    private void CloseButtonAction(ActionEvent actionEvent)
    {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
