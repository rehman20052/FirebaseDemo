package aydin.firebasedemo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class WelcomeController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @FXML
    void onRegisterClicked(ActionEvent event) {
        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(emailField.getText())
                    .setPassword(passwordField.getText())
                    .setEmailVerified(false)
                    .setDisabled(false);

            UserRecord userRecord = auth.createUser(request);
            showAlert("Success", "User registered: " + userRecord.getEmail());
        } catch (FirebaseAuthException e) {
            showAlert("Error", "Registration failed: " + e.getMessage());
        }
    }

    @FXML
    void onSignInClicked(ActionEvent event) throws IOException {
        try {

            UserRecord user = auth.getUserByEmail(emailField.getText());

            if (user != null) {

                if (!passwordField.getText().isEmpty()) {
                    DemoApp.setRoot("primary"); // switch to data access screen
                    showAlert("Success", "Welcome back, " + user.getEmail());
                } else {
                    showAlert("Error", "Password cannot be empty.");
                }
            }
        } catch (FirebaseAuthException e) {
            showAlert("Error", "Sign-in failed: user not found.");
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
