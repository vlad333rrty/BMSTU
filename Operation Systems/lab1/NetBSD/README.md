# Установка и сборка ядра<br/>
Пересобрать ядро NetBSD легче "изнутри". Для это необходимо следующее:<br/>
	1) Скачать iso образ с официального сайта и установить его в VirtualBox (не забыть настроить соединение с сетью)<br/>
	2) через ftp скачать исходники (ftp -i ftp://ftp.netbsd.org/pub/NetBSD/NetBSD-8.1 (ваша версия) /source/sets/syssrc.tgz) и разорхивировать в корень системы<br/>
	5) cd /usr/src/sys/kern<br/>
	6) Советую поставить nano (можно с помощью vi) - nano init_main.c и в конце main перед вызовом uvm_scheduler() пишем aprint_verbose("name surname");<br/>
	7) cd ../arch/amd64/conf<br/>
	8) cp GENERIC MYKERN<br/>
	9) config MYKERN<br/>
	10) cd ../compile/MYKERN<br/>
	11) make depend && make<br/>
	12) mv /netbsd /netbsd.old<br/>
	13) mv netbsd /<br/>
	14) reboot now<br/>
	15) grep | "name surname" <br/>
