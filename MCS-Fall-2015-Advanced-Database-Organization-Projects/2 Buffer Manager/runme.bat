#! /bin/bash

rm testbuffer.txt
gcc -o test_assign2_1 test_assign2_1.c buffer_mgr.c storage_mgr.c buffer_mgr_stat.c dberror.c
./test_assign2_1
