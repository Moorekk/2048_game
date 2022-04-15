package game2048;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.PathTransition.OrientationType;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class plateController implements EventHandler<KeyEvent>,Initializable{
	@Override
	public void initialize(java.net.URL arg0, ResourceBundle arg1) {
		background.requestFocus();
		Random ran = new Random();
		int time = ran.nextInt(4)+1;
		for(int i=0;i<4;i++)for(int j=0;j<4;j++) table[i][j]=0;
		for(int i =0;i<time;i++) generateOneRandomNum();
//		table[0][1]=1024;
//		table[0][0]=1024;
		toPlate();
		plate.setAlignment(Pos.CENTER);
		endGamePane.setAlignment(endGameText, Pos.CENTER);
		//set vieworder
		background.setViewOrder(5);
		plateB.setViewOrder(4);
		plate.setViewOrder(3);
		endGamePane.setViewOrder(0);
		roll= new ParallelTransition();
	}
	@FXML
	AnchorPane background;
	@FXML
	Text heighestNum;
	@FXML
	Text endGameText;
	@FXML
	Button continueButton;
	@FXML
	Button backButton;
	@FXML
	StackPane endGamePane;
	@FXML
	GridPane plate;
	@FXML
	GridPane plateB;
	private int [][] table = new int[4][4];
	int[][][] toPlaceIndex;
	boolean flag = true;ParallelTransition roll;
	@FXML
	public void onRestartPress() {
		background.requestFocus();
		Random ran = new Random();
		int time = ran.nextInt(4)+1;
		for(int i=0;i<4;i++)for(int j=0;j<4;j++) table[i][j]=0;
		for(int i =0;i<time;i++) generateOneRandomNum();
		toPlate();
		plate.setAlignment(Pos.CENTER);
		heighestNum.setText("0");
		endGamePane.setVisible(false);
		flag=true;
	}
	@FXML
	public void onBackPress() throws IOException {
		Parent menu = FXMLLoader.load(getClass().getResource("menu.fxml"));
		Scene menuScene = new Scene(menu);
		menuScene.getRoot().requestFocus();
		main.currentStage.setScene(menuScene);
	}
	@FXML
	public void onContinuePress() {
		endGamePane.setVisible(false);
		continueButton.setVisible(false);
		background.requestFocus();
	}
	@Override
	public void handle(KeyEvent e) {
		
		int[][] originTable = new int[4][4];
		for(int i=0;i<4;i++)for(int j=0;j<4;j++)originTable[i][j]=table[i][j];
//		for(int i=0;i<4;i++) {
//			for(int j=0;j<4;j++) {
//				System.out.print(table[i][j]);
//			}
//			System.out.println();
//		}
		System.out.println();
		if(e.getCode()==KeyCode.UP) {
			toPlaceIndex=moveBlocksUp();
		}else if(e.getCode()==KeyCode.DOWN) {
			toPlaceIndex=moveBlocksDown();
		}else if(e.getCode()==KeyCode.LEFT) {
			toPlaceIndex=moveBlocksLeft();
		}else if(e.getCode()==KeyCode.RIGHT) {
			toPlaceIndex=moveBlocksRight();
		}else return;
		background.setDisable(true);
		animate(toPlaceIndex,originTable);
//		
//		for(int i=0;i<4;i++) {
//			for(int j=0;j<4;j++) {
//				System.out.print("("+toPlaceIndex[i][j][0]+","+toPlaceIndex[i][j][1]+") ");
//			}
//			System.out.println();
//		}
		if(!Arrays.deepEquals(originTable, table))generateOneRandomNum();
		roll.play();
		roll.setOnFinished(b->{
			
			toPlate();
			setHeighestNum();
			background.setDisable(false);
			background.requestFocus();
			if(isWin()&& flag) {
				flag=false;
				endGameText.setText("You Win!!");
				endGamePane.setVisible(true);
				continueButton.setVisible(true);
				continueButton.requestFocus();
				
			}
			if(isGameOver()) {
				backButton.requestFocus();
				endGamePane.setVisible(true);
			}
		});
	} 
	private StackPane getNodeByIndex (int row, int column) {
		StackPane result =null;
	    for (Node node : plate.getChildren()) {
	        if(this.plate.getRowIndex(node) == row && this.plate.getColumnIndex(node) == column) {
	            result=(StackPane) node;
	        }
	    }
		return result;
	}
	private void animate(int[][][] toPlaceIndex,int[][] originTable) {
		for(int i=0;i<4;i++) {
			for(int j=0;j<4;j++) {
				final int toi = toPlaceIndex[i][j][0];
				final int toj = toPlaceIndex[i][j][1];
				if(toi!=i||toj!=j) {
//					animation					
					StackPane movingUnit = new unit(originTable[i][j],new Text(""+originTable[i][j]),new StackPane(),new Rectangle(70,70)).getStackPane();
					StackPane toPlaceCoveringUnit = new unit(originTable[toi][toj],new Text(""+originTable[toi][toj]),new StackPane(),new Rectangle(70,70)).getStackPane();
					StackPane originCoveringUnit = new unit(0,new Text(""),new StackPane(),new Rectangle(70,70)).getStackPane();
					background.getChildren().addAll(toPlaceCoveringUnit,originCoveringUnit,movingUnit);
					movingUnit.setViewOrder(1);
					toPlaceCoveringUnit.setViewOrder(2);
					originCoveringUnit.setViewOrder(2);
					Double originX = plate.getLayoutX()+74*(j+1)-70;
					Double originY = plate.getLayoutY()+74*(i+1)-70;
					Double toX = plate.getLayoutX()+74*(toj+1)-70;
					Double toY = plate.getLayoutY()+74*(toi+1)-70;
					//create origin and toPlace pane till translate end
					originCoveringUnit.setLayoutX(originX);
					originCoveringUnit.setLayoutY(originY);
					toPlaceCoveringUnit.setLayoutX(toX);
					toPlaceCoveringUnit.setLayoutY(toY);
//					System.out.println("originX,Y"+originX+"\t,"+originY+"\ttoX,Y"+toX+"\t,"+toY);
					TranslateTransition translatetransition = new TranslateTransition(Duration.seconds(0.25),movingUnit);
					translatetransition.setFromX(originX);
					translatetransition.setFromY(originY);
					translatetransition.setToX(toX);
					translatetransition.setToY(toY);
					translatetransition.setCycleCount(1);
					roll.getChildren().add(translatetransition);
					translatetransition.setOnFinished(e->{
						background.getChildren().removeAll(movingUnit,originCoveringUnit,toPlaceCoveringUnit);
						});
				}
			}
		}
	}
	private void setHeighestNum() {
		int heighestNum=0;
		for(int i=0;i<4;i++) {
			for(int j=0;j<4;j++) {
				if(heighestNum<table[i][j]) heighestNum=table[i][j];
		}}
		this.heighestNum.setText(""+heighestNum);
	}
	private void generateOneRandomNum() {
		Random ran = new Random();
		while(true) {
			int ranColumn = ran.nextInt(4);
			int ranRow = ran.nextInt(4);
			int poss = ran.nextInt(41);
			int ranNum ;
			if(poss==0) ranNum=4;
			else ranNum=2;
			if(isFull())break;
			if(table[ranColumn][ranRow]!=0) continue;
			table[ranColumn][ranRow]=ranNum;
			break;
		}
	}
	private void toPlate() {
		plate.getChildren().clear();
		for(int i=0;i<4;i++) {
			for(int j=0;j<4;j++) {
				unit a = new unit(table[i][j],new Text(""+table[i][j]),new StackPane(),new Rectangle(70,70));
				plate.add(a.getStackPane(),j,i);
			}
		}
	}
	private boolean isFull() {
		boolean isFull=true;
		for(int i=0;i<4;i++)for(int j=0;j<4;j++)if(table[i][j]==0) isFull=false;
		return isFull;
	}
	private boolean isWin() {
		for(int i=0;i<4;i++) {
			for(int j=0;j<4;j++) {
				if(table[i][j]==2048) return true;
			}
		}
		return false;
	}
	private boolean isGameOver() {
		boolean isEnd;
		boolean neighborIsSame=false;
		for(int i=0;i<3;i++)for(int j=0;j<3;j++) {
			if(table[i][j]==table[i+1][j]) neighborIsSame=true;
			if(table[i][j]==table[i][j+1]) neighborIsSame=true;
		}
		for(int i=0;i<3;i++) {
			if(table[3][i]==table[3][i+1])neighborIsSame=true;
			if(table[i][3]==table[i+1][3])neighborIsSame=true;
		}
		
		isEnd=isFull() && !neighborIsSame;
		return isEnd;
	}
	//moveBlocks
	private void move(int indexi,int indexj,int toi,int toj) {
		if(indexi==toi&&indexj==toj)return;
		table[toi][toj] = table[indexi][indexj];
		table[indexi][indexj] = 0;
	}
	private int[][][] moveBlocksUp() {
		int[][][] toPlaceIndex = new int[4][4][2];
		for(int i=0;i<4;i++) {
			for(int j=0;j<4;j++) {
				toPlaceIndex[i][j][0]=i;
				toPlaceIndex[i][j][1]=j;
			}
		}
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (table[i][j] != 0) {// find a block not 0
					// collapse same blocks
					int k = i + 1;
					while (k != 4) {
						if (table[k][j] == table[i][j]) {
							table[i][j] *= 2;
							table[k][j] = 0;
							toPlaceIndex[k][j]=toPlaceIndex[i][j];
							break;
						}
						if(table[k][j]!=0)break;
						k++;
					}
					// move blocks
					int l = i - 1;
					while (l != -1) {
						if (l == 0&&table[l][j] == 0) {
							move(i,j,l,j);
							toPlaceIndex[i][j][0]=l;
							toPlaceIndex[i][j][1]=j;
							break;
						}
						if (table[l][j] != 0) {
							move(i,j,l+1,j);
							toPlaceIndex[i][j][0]=l+1;
							toPlaceIndex[i][j][1]=j;
							break;
						}
						l--;
					}
				}
			}
		}
		return toPlaceIndex;
	}
	private int[][][] moveBlocksDown() {
		int[][][] toPlaceIndex = new int[4][4][2];
		for(int i=0;i<4;i++) {
			for(int j=0;j<4;j++) {
				toPlaceIndex[i][j][0]=i;
				toPlaceIndex[i][j][1]=j;
			}
		}
		for (int i = 3; i >=0; i--) {
			for (int j = 3; j >=0; j--) {
				if (table[i][j] != 0) {// find a block not 0
					// collapse same blocks
					int k = i -1;
					while (k != -1) {
						if (table[k][j] == table[i][j]) {
							table[i][j] *= 2;
							table[k][j] = 0;
							toPlaceIndex[k][j]=toPlaceIndex[i][j];
							break;
						}
						if(table[k][j]!=0)break;
						k--;
					}
					// move blocks
					int l = i + 1;
					while (l != 4) {
						if (l == 3&&table[l][j] == 0) {
							move(i,j,l,j);
							toPlaceIndex[i][j][0]=l;
							toPlaceIndex[i][j][1]=j;
							break;
						}
						if (table[l][j] != 0) {
							move(i,j,l-1,j);
							toPlaceIndex[i][j][0]=l-1;
							toPlaceIndex[i][j][1]=j;
							break;
						}
						l++;
					}
				}
			}
		}
		return toPlaceIndex;
	}
	private int[][][] moveBlocksLeft() {
		int[][][] toPlaceIndex = new int[4][4][2];
		for(int i=0;i<4;i++) {
			for(int j=0;j<4;j++) {
				toPlaceIndex[i][j][0]=i;
				toPlaceIndex[i][j][1]=j;
			}
		}
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (table[i][j] != 0) {// find a block not 0
					// collapse same blocks
					int k = j + 1;
					while (k != 4) {
						if (table[i][k] == table[i][j]) {
							table[i][j] *= 2;
							table[i][k] = 0;
							toPlaceIndex[i][k]=toPlaceIndex[i][j];
							break;
						}
						if(table[i][k]!=0)break;
						k++;
					}
					// move blocks
					int l = j - 1;
					while (l != -1) {
						if (l == 0&&table[i][l] == 0) {
							move(i,j,i,l);
							toPlaceIndex[i][j][0]=i;
							toPlaceIndex[i][j][1]=l;
							break;
						}
						if (table[i][l] != 0) {
							move(i,j,i,l+1);
							toPlaceIndex[i][j][0]=i;
							toPlaceIndex[i][j][1]=l+1;
							break;
						}
						l--;
					}
				}
			}
		}
		return toPlaceIndex;
	}
	private int[][][] moveBlocksRight() {
		int[][][] toPlaceIndex = new int[4][4][2];
		for(int i=0;i<4;i++) {
			for(int j=0;j<4;j++) {
				toPlaceIndex[i][j][0]=i;
				toPlaceIndex[i][j][1]=j;
			}
		}
		for (int i = 3; i >=0; i--) {
			for (int j = 3; j >=0; j--) {
				if (table[i][j] != 0) {// find a block not 0
					// collapse same blocks
					int k = j -1;
					while (k != -1) {
						if (table[i][k] == table[i][j]) {
							table[i][j] *= 2;
							table[i][k] = 0;
							toPlaceIndex[i][k]=toPlaceIndex[i][j];
							break;
						}
						if(table[i][k]!=0)break;
						k--;
					}				
					// move blocks
					int l = j + 1;
					while (l != 4) {
						if (l == 3&&table[i][l] == 0) {
							move(i,j,i,l);
							toPlaceIndex[i][j][0]=i;
							toPlaceIndex[i][j][1]=l;
							break;
						}
						if (table[i][l] != 0) {
							move(i,j,i,l-1);
							toPlaceIndex[i][j][0]=i;
							toPlaceIndex[i][j][1]=l-1;
							break;
						}
						l++;
					}
				}
			}
		}
		return toPlaceIndex;
	}
}
class unit {
	private StackPane pane;
	private Text numInA;
	private Rectangle rectangle;
	public unit(int unitNum,Text numInA,StackPane pane,Rectangle rectangle) {
		this.rectangle=rectangle;
		this.pane=pane;
		this.numInA=numInA;
		this.rectangle.setVisible(false);
		pane.setAlignment(Pos.CENTER);
		pane.setStyle("-fx-background-color:"+color(unitNum)+";-fx-background-radius:5");
		if(unitNum==0) numInA.setVisible(false);
		numInA.setFont(new Font(28));
		numInA.setTextAlignment(TextAlignment.CENTER);
		pane.getChildren().addAll(numInA,this.rectangle);
	}
	public Double getWidth() {
		return pane.getWidth();
	}
	public Double getHeight() {
		return pane.getHeight();
	}
	private String color(int num) {
		switch(num) {
			case 0:return "#cccccc";
			case 2:return "#eed9c4";
			case 4:return "#e8ccb0";
			case 8:return "#e2bf9c";
			case 16:return "#ddb388";
			case 32:return "#d7a675";
			case 64:return "#d19961";
			case 128:return "#cb8c4d";
			case 256:return "#c68039";
			case 512:return "#b27334";
			case 1024:return "#e8ccb0";
			case 2048:return "#9e662e";
			case 4096:return "#8a5928";
			case 8192:return "#774d22";
			case 16384:return "#63401d";
			default:
				return "#cccccc";
		}
	}
	public Text getText() {
		return this.numInA;
	}
	public StackPane getStackPane() {
		return this.pane;
	}
}
