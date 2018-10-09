cmake_minimum_required(VERSION 3.1)
project(A2)

set(CMAKE_CXX_FLAGS "-std=c++17 -Wall -pedantic")



add_executable(TTT   TTT/main.cpp TTT/player.cpp TTT/gamestate.cpp)
include_directories(TTT PRIVATE TTT)

add_executable(TTT3D TTT3D/main.cpp TTT3D/player.cpp TTT3D/gamestate.cpp)
include_directories(TTT3D PRIVATE TTT3D)

add_executable(A2 main.cpp)