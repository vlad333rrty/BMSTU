#include <sys/module.h>
MODULE(MODULE_CLASS_MISC,lab2,NULL);
static int lab2_modcmd(modcmd_t cmd,void *arg){
	printf("Lab2 Name Surname");
	return 0;
}
