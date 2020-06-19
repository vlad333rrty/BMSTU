# Установка и сборка ReactOS <br/>
	1) Устанавить RosBE и исходники из SVN-репозитория<br/>
	2) В папке и исходниками:<br/>
		а) Заходим в ntoskrnl<br/>
		б) находим kdio.c<br/>
		в) В KdpPrintBanner пишем DPRINT1(...);<br/>
	3) Открываем RosBE и заходим в папку и исходниками<br/>
	4) ./configure.sh<br/>
	5) cd output-MinGW-i386<br/>
	6) ninja && ninja bootcd<br/>
	7) Устанавливаем полученный iso в VirtualBox<br/>	
