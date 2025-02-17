global init_mouse
extern handle_mouse

init_mouse:
    ; 启用鼠标中断
    mov ax, 0xC207
    int 0x15
    ret

; 鼠标中断处理
mouse_isr:
    pusha
    call handle_mouse
    popa
    iret