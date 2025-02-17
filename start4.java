import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class VideoPlayer extends Application {
    @Override
    public void start(Stage primaryStage) {
        // 构建视频文件路径
        String videoPath = "system/mp/start.mp4";
        Media media = new Media(new java.io.File(videoPath).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);

        Scene scene = new Scene(mediaView, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("视频播放器");
        primaryStage.show();

        mediaPlayer.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
