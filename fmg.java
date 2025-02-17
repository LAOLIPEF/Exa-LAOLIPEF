import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileManager extends Application {

    private Label statusLabel;
    private TextArea textArea;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private ImageView imageView;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("文件管理器");

        statusLabel = new Label();
        textArea = new TextArea();
        mediaView = new MediaView();
        imageView = new ImageView();

        MenuBar menuBar = createMenuBar();
        ListView<String> fileListView = createFileListView();

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(menuBar);
        borderPane.setLeft(fileListView);

        VBox contentBox = new VBox();
        contentBox.getChildren().addAll(textArea, mediaView, imageView);
        borderPane.setCenter(contentBox);

        borderPane.setBottom(statusLabel);
        BorderPane.setMargin(statusLabel, new Insets(10));

        Scene scene = new Scene(borderPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar createMenuBar() {
        Menu fileMenu = new Menu("文件");

        MenuItem openMenuItem = new MenuItem("打开");
        openMenuItem.setOnAction(event -> openFile());

        MenuItem saveMenuItem = new MenuItem("保存");
        saveMenuItem.setOnAction(event -> saveFile());

        fileMenu.getItems().addAll(openMenuItem, saveMenuItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(fileMenu);
        return menuBar;
    }

    private ListView<String> createFileListView() {
        ObservableList<String> fileList = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>(fileList);
        listView.setPrefWidth(200);
        return listView;
    }

    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("打开文件");
        File file = fileChooser.showOpenDialog(null);

        if (file!= null) {
            String filePath = file.getAbsolutePath();
            if (filePath.endsWith(".png") || filePath.endsWith(".bmp") || filePath.endsWith(".jpg") || filePath.endsWith(".gif")) {
                openImage(file);
            } else if (filePath.endsWith(".txt") || filePath.endsWith(".ini") || filePath.endsWith(".md") || filePath.endsWith(".log")) {
                openTextFile(file);
            } else if (filePath.endsWith(".mp3") || filePath.endsWith(".wav")) {
                openAudioFile(file);
            } else if (filePath.endsWith(".mp4")) {
                openVideoFile(file);
            } else {
                statusLabel.setText("不支持的文件类型");
            }
        }
    }

    private void openImage(File file) {
        Image image = new Image(file.toURI().toString());
        imageView.setImage(image);
        imageView.setFitWidth(400);
        imageView.setFitHeight(300);
        imageView.setPreserveRatio(true);

        statusLabel.setText("图片尺寸: " + image.getWidth() + "x" + image.getHeight() + ", 大小: " + file.length() + " bytes");
        textArea.setVisible(false);
        mediaView.setVisible(false);
    }

    private void openTextFile(File file) {
        try (Scanner scanner = new Scanner(file)) {
            StringBuilder content = new StringBuilder();
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
            textArea.setText(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("无法打开文本文件");
        }

        textArea.setVisible(true);
        imageView.setVisible(false);
        mediaView.setVisible(false);
    }

    private void openAudioFile(File file) {
        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                updateAudioStatus();
            }
        });

        mediaPlayer.setOnReady(() -> {
            statusLabel.setText("音频长度: " + mediaPlayer.getTotalDuration().toSeconds() + " 秒, 已播放: 0 秒");
        });

        mediaPlayer.play();
        textArea.setVisible(false);
        imageView.setVisible(false);
        mediaView.setVisible(true);
    }

    private void openVideoFile(File file) {
        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                updateVideoStatus();
            }
        });

        mediaPlayer.setOnReady(() -> {
            statusLabel.setText("视频长度: " + mediaPlayer.getTotalDuration().toSeconds() + " 秒, 大小: " + file.length() + " bytes, 尺寸: " + mediaView.getMediaPlayer().getMedia().getWidth() + "x" + mediaView.getMediaPlayer().getMedia().getHeight() + ", 已播放: 0 秒");
        });

        mediaPlayer.play();
        textArea.setVisible(false);
        imageView.setVisible(false);
        mediaView.setVisible(true);
    }

    private void saveFile() {
        if (textArea.isVisible()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("保存文件");
            File file = fileChooser.showSaveDialog(null);

            if (file!= null) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(textArea.getText());
                    statusLabel.setText("文件已保存");
                } catch (IOException e) {
                    e.printStackTrace();
                    statusLabel.setText("无法保存文件");
                }
            }
        }
    }

    private void updateAudioStatus() {
        if (mediaPlayer!= null) {
            statusLabel.setText("音频长度: " + mediaPlayer.getTotalDuration().toSeconds() + " 秒, 已播放: " + mediaPlayer.getCurrentTime().toSeconds() + " 秒");
        }
    }

    private void updateVideoStatus() {
        if (mediaPlayer!= null) {
            statusLabel.setText("视频长度: " + mediaPlayer.getTotalDuration().toSeconds() + " 秒, 大小: " + new File(mediaPlayer.getMedia().getSource()).length() + " bytes, 尺寸: " + mediaView.getMediaPlayer().getMedia().getWidth() + "x" + mediaView.getMediaPlayer().getMedia().getHeight() + ", 已播放: " + mediaPlayer.getCurrentTime().toSeconds() + " 秒");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
