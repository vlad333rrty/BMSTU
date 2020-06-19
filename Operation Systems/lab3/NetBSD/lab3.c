#include <sys/module.h>
#include <sys/proc.h>
MODULE(MODULE_CLASS_MISC,lab3,NULL);
static int lab3_modcmd(modcmd_t cmd,void *arg){
	printf("Lab3 Name Surname");
	struct proc *it;
	PROCLIST_FOREACH(it,&allproc)
		printf("%d %s\n",it->p_pid,it->p_comm);
	return 0;
}
