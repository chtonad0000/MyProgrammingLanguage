#include "GCObject.h"

#include <iostream>

std::vector<GCObject*> g_Heap;
size_t g_TotalElements = 0;

GCObject::GCObject()
{
    g_Heap.push_back(this);
}

GCObject::~GCObject() {
    g_TotalElements -= arrayData.size();
}

void GCObject::resizeArray(size_t newSize) {
    size_t oldSize = arrayData.size();
    arrayData.resize(newSize);
    g_TotalElements += (newSize - oldSize);
    //std::cout << g_TotalElements << std::endl;
}