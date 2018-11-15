package application;
	
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {			
            Parent root = FXMLLoader.load(getClass().getResource("calfx.fxml"));       
            primaryStage.setTitle("¼ÆËãÆ÷");
            primaryStage.setScene(new Scene(root, 508, 604));
            primaryStage.setResizable(false);
            primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);	
	}
}
