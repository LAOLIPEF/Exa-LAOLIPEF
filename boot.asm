[org 0x7C00]
[bits 16]

start:
    ; ...（原有代码：设置栈、加载内核）...

    ; 初始化鼠标
    call init_mouse

    ; 跳转到内核
    jmp 0x1000:0x0

init_mouse:
    ; 启用 PS/2 鼠标
    mov ax, 0xC205  ; 启用鼠标
    int 0x15
    ret

times 510 - ($-$$) db 0
dw 0xAA55