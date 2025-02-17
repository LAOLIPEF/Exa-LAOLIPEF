import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class OpenHTMLFile {
    public static void main(String[] args) {
        String filePath = "system/html/c.html";
        File file = new File(filePath);

        if (file.exists() && file.isFile()) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("当前操作系统不支持Desktop类。");
            }
        } else {
            System.out.println("文件不存在或不是一个文件。");
        }
    }
}
