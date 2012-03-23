#include <stdio.h>

#include <vector>
#include <map>

#include <pdh.h>
#include <pdhmsg.h>

#include "org_karatachi_jni_win32_PerformanceCounter.h"

typedef std::vector<PDH_HQUERY> QueryList;
typedef std::vector<PDH_HCOUNTER> CounterList;
typedef std::vector<CounterList> CounterLists;
typedef std::map<int, CounterLists> CounterListsMap;

static int g_counter_id;
static QueryList g_queries;
static CounterListsMap g_counters;

BOOL WINAPI DllMain(HINSTANCE hinstDLL,DWORD fdwReason,LPVOID lpvReserved)
{
    switch(fdwReason) {
    case DLL_PROCESS_ATTACH:
        break;
    case DLL_PROCESS_DETACH:
        for (size_t i = 0; i < g_queries.size(); ++i) {
            ::PdhCollectQueryDataEx(g_queries[i], 0, NULL);
            ::PdhCloseQuery(g_queries[i]);
        }
        g_queries.resize(0);
        break;
    case DLL_THREAD_ATTACH:
        break;
    case DLL_THREAD_DETACH:
        break;
    }
    return  TRUE;
}

JNIEXPORT jboolean JNICALL Java_org_karatachi_jni_win32_PerformanceCounter_initialize(
    JNIEnv* env, jclass clazz, jintArray intervals)
{
    if (g_queries.size() != 0)
        return JNI_FALSE;

    jint* const intervals_buf = env->GetIntArrayElements(intervals, NULL);

    g_queries.resize(env->GetArrayLength(intervals));
    for (size_t i = 0; i < g_queries.size(); ++i) {
        ::PdhOpenQuery(NULL, 0, &(g_queries[i]));
        ::PdhCollectQueryDataEx(g_queries[i], intervals_buf[i], NULL);
    }

    env->ReleaseIntArrayElements(intervals, intervals_buf, 0);

    return g_queries.size() != 0 ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jint JNICALL Java_org_karatachi_jni_win32_PerformanceCounter_addPerformanceCounter(
    JNIEnv* env, jclass clazz, jstring path)
{
    if (g_queries.size() == 0)
        return -1;

    const char* const szWildCardPath = env->GetStringUTFChars(path, NULL);

    DWORD dwCtrPathSize = 0;
    if (::PdhExpandCounterPath(szWildCardPath, NULL, &dwCtrPathSize) != PDH_MORE_DATA) {
        env->ReleaseStringUTFChars(path, szWildCardPath);
        return -1;
    }

    char* const szCtrPath = new char[dwCtrPathSize];
    if (::PdhExpandCounterPath(szWildCardPath, szCtrPath, &dwCtrPathSize) != PDH_CSTATUS_VALID_DATA) {
        env->ReleaseStringUTFChars(path, szWildCardPath);
        return -1;
    }

    env->ReleaseStringUTFChars(path, szWildCardPath);

    int id = ++g_counter_id;
    CounterLists& lists = g_counters[id];

    char* ptr;
    ptr = szCtrPath;
    while (*ptr) {
        CounterList counter_list(g_queries.size());
        for (size_t i = 0; i < g_queries.size(); ++i) {
            ::PdhAddCounter(g_queries[i], ptr, 0, &(counter_list[i]));
        }
        lists.push_back(counter_list);
        ptr += strlen(ptr);
        ++ptr;
    }
    delete[] szCtrPath;

    if (lists.size() == 0) {
        g_counters.erase(id);
        return -1;
    }

    return id;
}

JNIEXPORT jdoubleArray JNICALL JNICALL Java_org_karatachi_jni_win32_PerformanceCounter_getPerformanceCounterValue(
    JNIEnv* env, jclass clazz, jint id)
{
    if (g_queries.size() == 0)
        return NULL;

    if (g_counters.find(id) == g_counters.end())
        return NULL;

    jdoubleArray ret = env->NewDoubleArray((jsize) g_queries.size());

    jdouble* const ret_buf = env->GetDoubleArrayElements(ret, NULL);

    CounterLists& lists = g_counters[id];
    for (CounterLists::iterator itr = lists.begin(); itr != lists.end(); ++itr) {
        for (size_t i = 0; i < g_queries.size(); ++i) {
            PDH_FMT_COUNTERVALUE fmtVal;
            if (PdhGetFormattedCounterValue((*itr)[i], PDH_FMT_DOUBLE, NULL, &fmtVal) == ERROR_SUCCESS) {
                ret_buf[i] += fmtVal.doubleValue;
            }
        }
    }

    env->ReleaseDoubleArrayElements(ret, ret_buf, 0);

    return ret;
}
