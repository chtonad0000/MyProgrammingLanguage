#pragma once

#include <string>
#include <variant>

struct GCObject;

using Value = std::variant<long long, bool, GCObject*>;
