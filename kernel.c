#include <stdint.h>
#include <stddef.h>
#include <string.h>

#define VGA_WIDTH 320
#define VGA_HEIGHT 200
#define VGA_BASE 0xA0000

// 鼠标状态
int mouse_x = 160, mouse_y = 100;  // 初始位置
uint8_t mouse_buttons = 0;         // 按钮状态

// 窗口结构
typedef struct {
    int x, y;          // 窗口位置
    int width, height; // 窗口大小
    uint8_t color;     // 窗口颜色
    int dragging;      // 是否正在拖动
    const char *title; // 窗口标题
} Window;

// 任务栏结构
typedef struct {
    int height;        // 任务栏高度
    uint8_t color;     // 任务栏颜色
} Taskbar;

// 软件包结构
typedef struct {
    const char *name;  // 软件包名称
    const char *path;  // 软件包路径
} SoftwarePackage;

// 窗口和任务栏实例
Window windows[10];    // 最多支持10个窗口
int window_count = 0;  // 当前窗口数量
Taskbar taskbar = {20, 8}; // 灰色任务栏
SoftwarePackage software_packages[10]; // 软件包列表
int package_count = 0; // 软件包数量

// 绘制矩形
void draw_rect(int x, int y, int width, int height, uint8_t color) {
    for (int i = y; i < y + height; i++) {
        for (int j = x; j < x + width; j++) {
            *(volatile uint8_t*)(VGA_BASE + i * VGA_WIDTH + j) = color;
        }
    }
}

// 绘制文本
void draw_text(int x, int y, const char *text, uint8_t color) {
    for (int i = 0; text[i] != '\0'; i++) {
        *(volatile uint8_t*)(VGA_BASE + y * VGA_WIDTH + x + i) = text[i];
    }
}

// 绘制窗口
void draw_window(Window *window) {
    draw_rect(window->x, window->y, window->width, window->height, window->color);
    // 绘制标题栏
    draw_rect(window->x, window->y, window->width, 20, window->color + 1);
    draw_text(window->x + 5, window->y + 5, window->title, 15);
}

// 绘制任务栏
void draw_taskbar() {
    draw_rect(0, VGA_HEIGHT - taskbar.height, VGA_WIDTH, taskbar.height, taskbar.color);
    // 绘制开始按钮
    draw_rect(5, VGA_HEIGHT - taskbar.height + 5, 50, taskbar.height - 10, 7);
    draw_text(10, VGA_HEIGHT - taskbar.height + 10, "Start", 15);
}

// 绘制安装程序界面
void draw_installer(Window *window) {
    draw_text(window->x + 10, window->y + 30, "Install Software", 15);
    for (int i = 0; i < package_count; i++) {
        draw_text(window->x + 10, window->y + 50 + i * 20, software_packages[i].name, 15);
    }
}

// 处理鼠标事件
void handle_mouse() {
    __asm__ volatile (
        "mov $0xC201, %%ax\n"  // 读取鼠标数据
        "int $0x15\n"
        "mov %%al, %0\n"       // 按钮状态
        "mov %%bx, %1\n"       // X 位移
        "mov %%cx, %2\n"       // Y 位移
        : "=m"(mouse_buttons), "=m"(mouse_x), "=m"(mouse_y)
        :
        : "eax", "ebx", "ecx"
    );

    // 检测窗口拖动
    for (int i = 0; i < window_count; i++) {
        if (mouse_buttons & 0x01) {  // 左键按下
            if (mouse_x >= windows[i].x && mouse_x < windows[i].x + windows[i].width &&
                mouse_y >= windows[i].y && mouse_y < windows[i].y + 20) {  // 标题栏区域
                windows[i].dragging = 1;
            }
        } else {
            windows[i].dragging = 0;
        }

        // 拖动窗口
        if (windows[i].dragging) {
            windows[i].x = mouse_x - windows[i].width / 2;
            windows[i].y = mouse_y - 10;
        }
    }

    // 检测开始按钮点击
    if (mouse_buttons & 0x01) {  // 左键按下
        if (mouse_x >= 5 && mouse_x < 55 &&
            mouse_y >= VGA_HEIGHT - taskbar.height + 5 && mouse_y < VGA_HEIGHT - 5) {
            // 打开安装程序窗口
            create_window(100, 50, 150, 150, 7, "Installer");
        }
    }
}

// 创建新窗口
void create_window(int x, int y, int width, int height, uint8_t color, const char *title) {
    if (window_count < 10) {
        windows[window_count].x = x;
        windows[window_count].y = y;
        windows[window_count].width = width;
        windows[window_count].height = height;
        windows[window_count].color = color;
        windows[window_count].title = title;
        window_count++;
    }
}

// 安装软件包
void install_package(const char *name, const char *path) {
    if (package_count < 10) {
        software_packages[package_count].name = name;
        software_packages[package_count].path = path;
        package_count++;
    }
}

// 主函数
void main() {
    draw_rect(0, 0, VGA_WIDTH, VGA_HEIGHT, 1);  // 蓝色桌面
    draw_taskbar();  // 绘制任务栏

    // 安装示例软件包
    install_package("Calculator", "/apps/calculator.bin");
    install_package("Text Editor", "/apps/editor.bin");

    while (1) {
        for (int i = 0; i < window_count; i++) {
            draw_window(&windows[i]);  // 绘制所有窗口
            if (strcmp(windows[i].title, "Installer") == 0) {
                draw_installer(&windows[i]);  // 绘制安装程序界面
            }
        }
        handle_mouse();  // 处理鼠标事件
    }
}