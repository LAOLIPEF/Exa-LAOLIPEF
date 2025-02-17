# 编译内核
gcc -ffreestanding -m32 -c kernel.c -o kernel.o
nasm -f elf32 mouse.asm -o mouse.o
ld -m elf_i386 -Ttext 0x1000 kernel.o mouse.o -o kernel.bin

# 其他步骤与之前相同（写入磁盘镜像）