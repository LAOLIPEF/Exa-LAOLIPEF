import cv2


def play_video():
    video_path = "system/mp/start.mp4"
    cap = cv2.VideoCapture(video_path)

    if not cap.isOpened():
        print(f"无法打开视频文件: {video_path}")
        return

    while True:
        ret, frame = cap.read()
        if not ret:
            break
        cv2.imshow('Video Player', frame)

        if cv2.waitKey(int(1000 / cap.get(cv2.CAP_PROP_FPS))) & 0xFF == ord('q'):
            break

    cap.release()
    cv2.destroyAllWindows()


if __name__ == "__main__":
    play_video()

