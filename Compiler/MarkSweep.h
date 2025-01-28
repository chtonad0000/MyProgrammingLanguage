#pragma once
#include "Value.h"

struct State;

void markObject(GCObject *obj);

void markValue(const Value &val);

void markAllThreaded(State &st);

void sweep();

void collectGarbage(State &st);
