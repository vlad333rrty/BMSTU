# Установка и сборка ядра
Пересобрать ядро NetBSD легче "изнутри". Для это необходимо следующее:
	1) Скачать iso образ с официального сайта и установить его в VirtualBox (не забыть настроить соединение с сетью)
	2) через ftp скачать исходники (ftp -i ftp://ftp.netbsd.org/pub/NetBSD/NetBSD-8.1 (ваша версия) /source/sets/syssrc.tgz) и разорхивировать в корень системы
	5) cd /usr/src/sys/kern
	6) Советую поставить nano (можно с помощью vi) - nano init_main.c и в конце main перед вызовом uvm_scheduler() пишем aprint_verbose("name surname");
	7) cd ../arch/amd64/conf
	8) cp GENERIC MYKERN
	9) config MYKERN
	10) cd ../compile/MYKERN
	11) make depend && make
	12) mv /netbsd /netbsd.old
	13) mv netbsd /
	14) reboot now
	15) grep | "name surname" 
