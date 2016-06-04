package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class PasswordFrameController implements Initializable
{
    @FXML
    private TextField nameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button checkButton;
    public static boolean pwIsCorrect = false;

    @FXML
    private void CheckButtonAction(ActionEvent actionEvent)
    {
        Statement st;
        ResultSet rs;
        String password = null;
        try
        {
            st = MySQLConnect.getConnection().createStatement();
            String RecordQuery = ("Select * from users where name = '" + nameTextField.getText() + "'");
            rs = st.executeQuery(RecordQuery);
            while (rs.next())
            {
                password = rs.getString("pw");
            }
            if (passwordField.getText().equals(password))
            {
                if (nameTextField.getText().equals("Admin"))
                    pwIsCorrect = true;
                Stage stage = (Stage) checkButton.getScene().getWindow();
                stage.close();
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Неверное имя пользователя или пароль!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        MySQLConnect.ConnectDB();
    }
}
