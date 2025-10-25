package aydin.firebasedemo;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class PrimaryController {

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField ageTextField;

    @FXML
    private TextField phoneTextField;

    @FXML
    private TextArea outputTextArea;

    @FXML
    private Button readButton;

    @FXML
    private Button writeButton;

    @FXML
    private Button registerButton;

    @FXML
    private Button switchSecondaryViewButton;

    private final ObservableList<Person> listOfUsers = FXCollections.observableArrayList();
    private boolean key;

    public ObservableList<Person> getListOfUsers() {
        return listOfUsers;
    }


    @FXML
    void readButtonClicked(ActionEvent event) {
        readFirebase();
    }

    @FXML
    void writeButtonClicked(ActionEvent event) {
        addData();
    }

    @FXML
    void registerButtonClicked(ActionEvent event) {
        registerUser();
    }

    @FXML
    private void switchToSecondary() throws IOException {
        DemoApp.setRoot("secondary");
    }


    public boolean readFirebase() {
        key = false;

        ApiFuture<QuerySnapshot> future = DemoApp.fstore.collection("Persons").get();
        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            outputTextArea.clear();

            if (documents.isEmpty()) {
                outputTextArea.setText("No data found in Firebase.");
                System.out.println("No data found in Firebase.");
            } else {
                System.out.println("Reading data from Firebase...");
                listOfUsers.clear();

                for (QueryDocumentSnapshot document : documents) {
                    String name = String.valueOf(document.get("Name"));
                    int age = Integer.parseInt(document.get("Age").toString());
                    String phone = document.contains("Phone") ? String.valueOf(document.get("Phone")) : "N/A";

                    outputTextArea.appendText("Name: " + name + " | Age: " + age + " | Phone: " + phone + "\n");
                    System.out.println(document.getId() + " => " + name);

                    Person person = new Person(name, age, phone);
                    listOfUsers.add(person);
                }
            }
            key = true;

        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
        return key;
    }

    public boolean registerUser() {

        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail("user222@example.com")
                .setEmailVerified(false)
                .setPassword("secretPassword")
                .setPhoneNumber("+11234567890")
                .setDisplayName("John Doe")
                .setDisabled(false);

        try {
            UserRecord userRecord = DemoApp.fauth.createUser(request);
            System.out.println("✅ Successfully created new user with Firebase UID: " + userRecord.getUid());
            System.out.println("Check Firebase Console > Authentication > Users tab");
            return true;

        } catch (FirebaseAuthException ex) {
            System.out.println("❌ Error creating new user in Firebase: " + ex.getMessage());
            return false;
        }
    }

    public void addData() {
        String name = nameTextField.getText().trim();
        String ageText = ageTextField.getText().trim();
        String phone = phoneTextField.getText().trim();

        if (name.isEmpty() || ageText.isEmpty() || phone.isEmpty()) {
            outputTextArea.setText("Please fill all fields (Name, Age, Phone).");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            outputTextArea.setText("Age must be a valid number.");
            return;
        }

        DocumentReference docRef = DemoApp.fstore.collection("Persons")
                .document(UUID.randomUUID().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("Name", name);
        data.put("Age", age);
        data.put("Phone", phone);

        ApiFuture<WriteResult> result = docRef.set(data);
        System.out.println("✅ Added new person: " + name + " | Age: " + age + " | Phone: " + phone);
        outputTextArea.setText("Person added successfully!");
    }
}
