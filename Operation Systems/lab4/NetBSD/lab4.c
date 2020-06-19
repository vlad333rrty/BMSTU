#include <sys/cdefs.h>
#include <sys/module.h>
#include <sys/param.h>
#include <sys/sysctl.h>
#include <uvm/uvm.h>
MODULE(MODULE_CLASS_MISC,lab4,NULL);
#define PAGESIZE 0x1000;
extern paddr_t avail_end;
vaddr_t va;
struct pglist plist;

static int lab4_modcmd(modcmd_t cmd,void *arg){
	va=uvm_km_alloc(kernel_map,10*PAGESIZE,0,UVM_KMF_VAONLY);
	if (va==0){
		printf("Error occurred allocating va");
		return 0;
	}
	int error=uvm_pglistalloc(5*PAGESIZE,0,avail_end,0,0,&plist,5,0);
	if (error){ 
		printf("Error %d\n",error);
		return 0;
	}
	printf("Success");
	struct vm_page *page=TAILQ_FIRST(&plist);
	for (int i=0;page;i++){
		pd_entry_t *ppte;
		ppte=L2_BASE+pl2_i(va+PAGESIZE*i);
		paddr_t pa=VM_PAGE_TO_PHYS(page);
		printf("page %d\n",i+1);
		printf("phys_addr=0x%lx\n",pa);
		printf("Valid: %d\n",((*ppte & PG_V) ? 1 : 0));
		page=TAILQ_NEXT(page,pageq.queue);
	}
	uvm_pglistfree(&plist);
	uvm_km_free(kernel_map,va,10*PAGESIZE,UVM_KMF_VAONLY);
	return 0;
}
