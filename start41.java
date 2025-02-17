import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class ExternalVideoPlayer {
    public static void main(String[] args) {
        String videoPath = "system/mp/start.mp4";
        File videoFile = new File(videoPath);
        if (Desktop.isDesktopSupported() && videoFile.exists()) {
            try {
                Desktop.getDesktop().open(videoFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("无法打开视频文件，可能是系统不支持或文件不存在。");
        }
    }
}
