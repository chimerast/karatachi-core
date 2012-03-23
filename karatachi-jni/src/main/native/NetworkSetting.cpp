#include <stdio.h>

#include <winsock2.h>
#include <iphlpapi.h>

#include "org_karatachi_jni_win32_NetworkSetting.h"

DWORD GetInterfaceIndexByAddress(DWORD dwAddr)
{
    PMIB_IPADDRTABLE pIPAddrTable;
    DWORD dwSize = 0;

    pIPAddrTable = (MIB_IPADDRTABLE*) malloc(sizeof(MIB_IPADDRTABLE));

    if (GetIpAddrTable(pIPAddrTable, &dwSize, 0) == ERROR_INSUFFICIENT_BUFFER) {
        free(pIPAddrTable);
        pIPAddrTable = (MIB_IPADDRTABLE *) malloc(dwSize);
    }

    if (GetIpAddrTable(pIPAddrTable, &dwSize, 0) != NO_ERROR) {
        free(pIPAddrTable);
        return -1;
    }

    for (DWORD i = 0; i < pIPAddrTable->dwNumEntries; i++) {
        if (pIPAddrTable->table[i].dwAddr == dwAddr) {
            DWORD dwIndex = pIPAddrTable->table[i].dwIndex;
            free(pIPAddrTable);
            return dwIndex;
        }
    }

    free(pIPAddrTable);
    return -1;
}

JNIEXPORT jboolean JNICALL Java_org_karatachi_jni_win32_NetworkSetting_addIpAddress(
    JNIEnv* env, jclass clazz, jint org, jint append, jint mask)
{
    DWORD IfIndex = GetInterfaceIndexByAddress(org);

    if (IfIndex < 0) {
        return JNI_FALSE;
    }

    DWORD dwRetVal = 0;

    ULONG NTEContext = 0;
    ULONG NTEInstance = 0;

    dwRetVal = AddIPAddress(append, mask, IfIndex, &NTEContext, &NTEInstance);
    if (dwRetVal == NO_ERROR) {
        return JNI_TRUE;
    } else if (dwRetVal == ERROR_DUP_DOMAINNAME) {
        return JNI_TRUE;
    } else {
        return JNI_FALSE;
    }
}
