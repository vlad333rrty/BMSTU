# Установка и сборка ReactOS
	1) Устанавить RosBE и исходники из SVN-репозитория
	2) В папке и исходниками:
		а) Заходим в ntoskrnl
		б) находим kdio.c
		в) В KdpPrintBanner пишем DPRINT1(...);
	3) Открываем RosBE и заходим в папку и исходниками
	4) ./configure.sh
	5) cd output-MinGW-i386
	6) ninja && ninja bootcd
	7) Устанавливаем полученный iso в VirtualBox	
