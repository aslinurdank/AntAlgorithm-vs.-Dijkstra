package antalgortihmasli;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.util.*;
import java.net.URL;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class MainFXMLController implements Initializable  {

    @FXML
    private BorderPane borderpane;
    @FXML
    private ChoiceBox algorithmChoice;
    @FXML
    private Slider slideAlpha;
    @FXML
    private Label lbAlpha;
    @FXML
    private Slider slideBeta;
    @FXML
    private Label lbBeta;
    @FXML
    private Slider slideFeromonUcuculuk;
    @FXML
    private Label lbFeromonUcuculuk;
    @FXML
    private Slider slideKarinca;
    @FXML
    private Label lbKarinca;
    @FXML
    private Pane simulasyonpane;
    @FXML
    private VBox vboxKarinca;
     @FXML
    private Button btnIstatistik;
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    
   
        algorithmChoice.getItems().addAll("Dijkstra Algoritması","Karınca Algoritması");
        algorithmChoice.setValue("Dijkstra Algoritması");
        
        slideAlpha.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldVale, Number newValue) -> {
            lbAlpha.setText(String.valueOf(newValue.intValue()));
        });
        
        slideBeta.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldVale, Number newValue) -> {
            lbBeta.setText(String.valueOf(newValue.intValue()));
        });
        
        slideFeromonUcuculuk.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldVale, Number newValue) -> {
            lbFeromonUcuculuk.setText(String.valueOf(newValue.intValue()));
        });
        
        slideKarinca.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldVale, Number newValue) -> {
            lbKarinca.setText(String.valueOf(newValue.intValue()));
        });
        
    }
   
}