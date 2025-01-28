#include "MarkSweep.h"
#include <algorithm>
#include <iostream>
#include "GCObject.h"
#include "Interpreter.h"

extern std::vector<GCObject*> g_Heap;

void markObject(GCObject *obj) {
    if (!obj || obj->marked) return;

    obj->marked = true;
    for (auto &elem : obj->arrayData) {
        markValue(elem);
    }
}

void markValue(const Value &val) {
    if (std::holds_alternative<GCObject*>(val)) {
        GCObject *ptr = std::get<GCObject*>(val);
        if (ptr) markObject(ptr);
    }
}

void markAllThreaded(State &st) {
    for (auto &val : st.stack) markValue(val);
    for (auto &kv : st.variables) markValue(kv.second);
}

void sweep() {
    auto newEnd = std::remove_if(g_Heap.begin(), g_Heap.end(), [](GCObject *o) {
        if (!o->marked) {
            delete o;
            return true;
        }
        return false;
    });

    g_Heap.erase(newEnd, g_Heap.end());

    for (auto *o : g_Heap) o->marked = false;
}

void collectGarbage(State &st) {
    markAllThreaded(st);
    sweep();
}
