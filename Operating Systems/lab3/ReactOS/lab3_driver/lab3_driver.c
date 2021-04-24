#include <ntddk.h>
#include <ntddbeep.h>
#ifndef NDEBUG
#define NDEBUG
#endif
#include <debug.h>
#include <ntifs.h>
#include <mmtypes.h>
#include <exfuncs.h>
#include <ndk/exfuncs.h>

#include <wdm.h>

NTSTATUS NTAPI DriverEntry(IN PDRIVER_OBJECT DriverObject, IN PUNICODE_STRING RegistryPath);
VOID NTAPI BeepUnload(IN PDRIVER_OBJECT DriverObject);


__kernel_entry NTSYSCALLAPI NTSTATUS NTAPI
NtQuerySystemInformation(
    _In_ SYSTEM_INFORMATION_CLASS SystemInformationClass,
    _Out_ PVOID SystemInformation,
    _In_ ULONG InformationLength,
    _Out_opt_ PULONG ResultLength);


typedef struct _SYSTEM_THREADS{
    LARGE_INTEGER KernelTime;
    LARGE_INTEGER UserTime;
    LARGE_INTEGER CreateTime;
    ULONG WaitTime;
    PVOID StartAddress;
    CLIENT_ID ClientId;
    KPRIORITY Priority;
    KPRIORITY BasePriority;
    ULONG ContextSwitchCount;
    LONG State;
    LONG WaitReason;
} SYSTEM_THREADS, *PSYSTEM_THREADS;

typedef struct _SYSTEM_PROCESSES{
    ULONG NextEntryDelta;
    ULONG ThreadCount;
    ULONG Reserved1[6];
    LARGE_INTEGER CreateTime;
    LARGE_INTEGER UserTime;
    LARGE_INTEGER KernelTime;
    UNICODE_STRING ProcessName;
    KPRIORITY BasePriority;
    ULONG ProcessId;
    ULONG InheritedFromProcessId;
    ULONG HandleCount;
    ULONG Reserved2[2];
    VM_COUNTERS VmCounters;
#if _WIN32_WINNT >= 0x500
    IO_COUNTERS IoCounters;
#endif
    SYSTEM_THREADS Threads[1];
} SYSTEM_PROCESSES, *PSYSTEM_PROCESSES;

NTSTATUS NTAPI DriverEntry(IN PDRIVER_OBJECT DriverObject, IN PUNICODE_STRING RegistryPath){
    NTSTATUS status = STATUS_INFO_LENGTH_MISMATCH;
    ULONG size = 16 * 1024;
    ULONG cnt;
    PVOID info;
    PSYSTEM_PROCESSES processes;

    DriverObject->DriverUnload = (PDRIVER_UNLOAD)BeepUnload;
    for (;;){
        info = ExAllocatePool(NonPagedPool, size);
        status = NtQuerySystemInformation(5, info, size, &cnt);
        if (status == STATUS_INFO_LENGTH_MISMATCH)
        {
            ExFreePool(info);
            size *= 2;
        }
        else
            break;
    }

    if (!NT_SUCCESS(status)){
        DPRINT1("QuerySystemInformation error");
        return status;
    }

    processes = (PSYSTEM_PROCESSES)info;
    DPRINT1("lab3 Name Surname");
    for (;;){
        PCWSTR name = processes->ProcessName.Buffer;
        if (name == NULL)
            name = L"Idle";
        DPRINT1("%S\n", name);

        if (!processes->NextEntryDelta)
            break;

        processes = (PSYSTEM_PROCESSES)(((char *)processes) + processes->NextEntryDelta);
    }
    ExFreePool(info);
    return STATUS_SUCCESS;
}

VOID NTAPI BeepUnload(IN PDRIVER_OBJECT DriverObject){
    PDEVICE_OBJECT DeviceObject;
    DPRINT1("STOP DRIVER");
    DeviceObject = DriverObject->DeviceObject;
    IoDeleteDevice(DeviceObject);
}

/* EOF */
