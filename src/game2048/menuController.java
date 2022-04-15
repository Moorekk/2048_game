package game2048;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

public class menuController{
	@FXML
	AnchorPane background;
	@FXML
	public void onstartPressed() throws IOException{
		Parent plate = FXMLLoader.load(getClass().getResource("plate.fxml"));
		Scene plateScene = new Scene(plate);
		plateScene.getRoot().requestFocus();
		main.currentStage.setScene(plateScene);
	}
	@FXML
	public void onexitPressed() {
		main.currentStage.close();
	}
	
}
