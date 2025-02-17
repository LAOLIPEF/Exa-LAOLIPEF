#include <windows.h>
#include <stdio.h>
#include <stdlib.h>

// 播放音频文件
void PlaySoundFile(const char* filePath) {
    PlaySound(filePath, NULL, SND_FILENAME | SND_ASYNC);
}

// 打开文件
void OpenFile(const char* filePath) {
    ShellExecute(NULL, "open", filePath, NULL, NULL, SW_SHOWNORMAL);
}

int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance,
                    PSTR szCmdLine, int iCmdShow) {
    char input[100];
    BOOL isClosing = FALSE;

    // 播放start.wav文件
    PlaySoundFile("sounds\\start.wav");

    while (1) {
        if (isClosing) {
            // 显示关闭提示
            if (MessageBox(NULL, "输入\"ec\"两个按键后", "提示", MB_OKCANCEL) == IDOK) {
                // 获取用户输入
                GetDlgItemText(NULL, 0, input, sizeof(input));
                if (strcmp(input, "ec") == 0) {
                    break;
                }
            }
            isClosing = FALSE;
        } else {
            // 获取用户输入
            if (GetInputText(NULL, "请输入", "输入", input, sizeof(input)) == IDOK) {
                if (strcmp(input, "fmg") == 0) {
                    char filePath[MAX_PATH];
                    snprintf(filePath, sizeof(filePath), "system\\fmg.c");
                    if (GetFileAttributes(filePath)!= INVALID_FILE_ATTRIBUTES) {
                        OpenFile(filePath);
                    } else {
                        // 播放Errors.wav文件
                        PlaySoundFile("sounds\\Errors.wav");
                        MessageBox(NULL, "系统文件不全，系统建议您重装系统", "提示", MB_OK);
                    }
                }
            }
        }

        // 处理窗口消息
        MSG msg;
        while (PeekMessage(&msg, NULL, 0, 0, PM_REMOVE)) {
            TranslateMessage(&msg);
            DispatchMessage(&msg);
            if (msg.message == WM_CLOSE) {
                isClosing = TRUE;
            }
        }
    }

    return 0;
}

// 自定义获取输入文本的函数
int GetInputText(HWND hwnd, LPCTSTR lpPrompt, LPCTSTR lpTitle, LPTSTR lpText, int cchTextMax) {
    INPUT_RECORD irInBuf[128];
    DWORD cNumRead, i;
    int nLength = 0;
    HANDLE hStdin = GetStdHandle(STD_INPUT_HANDLE);

    // 显示提示信息
    printf("%s: ", lpPrompt);

    // 读取输入
    while (1) {
        ReadConsoleInput(hStdin, irInBuf, 128, &cNumRead);
        for (i = 0; i < cNumRead; i++) {
            if (irInBuf[i].EventType == KEY_EVENT) {
                if (irInBuf[i].Event.KeyEvent.bKeyDown) {
                    if (irInBuf[i].Event.KeyEvent.wVirtualKeyCode == VK_RETURN) {
                        lpText[nLength] = '\0';
                        return IDOK;
                    } else if (irInBuf[i].Event.KeyEvent.wVirtualKeyCode == VK_ESCAPE) {
                        return IDCANCEL;
                    } else if (irInBuf[i].Event.KeyEvent.uChar.AsciiChar >= 32 && irInBuf[i].Event.KeyEvent.uChar.AsciiChar <= 126) {
                        if (nLength < cchTextMax - 1) {
                            lpText[nLength++] = irInBuf[i].Event.KeyEvent.uChar.AsciiChar;
                            putchar(irInBuf[i].Event.KeyEvent.uChar.AsciiChar);
                        }
                    }
                }
            }
        }
    }
}
