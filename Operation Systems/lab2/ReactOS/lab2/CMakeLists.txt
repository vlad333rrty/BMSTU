
add_library(lab2 MODULE lab2.c lab2.rc)
set_module_type(lab2 kernelmodedriver)
add_importlibs(lab2 ntoskrnl hal)
add_cd_file(TARGET lab2 DESTINATION reactos/system32/drivers FOR all)
add_registry_inf(lab2_reg.inf)
