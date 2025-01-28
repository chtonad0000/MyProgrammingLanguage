#pragma once

#include <vector>
#include "Value.h"

struct GCObject {
    bool marked = false;
    std::vector<Value> arrayData;

    GCObject();

    ~GCObject();
    void resizeArray(size_t newSize);
};

extern size_t g_TotalElements;
extern std::vector<GCObject*> g_Heap;
