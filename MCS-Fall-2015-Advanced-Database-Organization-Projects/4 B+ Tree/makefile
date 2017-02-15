#compiler to be used is gcc
CC=gcc
#define the target file
all:test_assign4_1
test_assign4_1: test_assign4_1.o dberror.o storage_mgr.o buffer_mgr.o buffer_mgr_stat.o expr.o record_mgr.o btree_mgr.o rm_serializer.o
	$(CC) test_assign4_1.o dberror.o storage_mgr.o buffer_mgr.o buffer_mgr_stat.o expr.o record_mgr.o btree_mgr.o rm_serializer.o -o test_assign4_1
dberror.o: dberror.c dberror.h
	$(CC) -c dberror.c
storage_mgr.o: storage_mgr.c storage_mgr.h
	$(CC) -c storage_mgr.c
buffer_mgr.o: buffer_mgr.c buffer_mgr.h dberror.h dt.h storage_mgr.h test_helper.h
	$(CC) -c buffer_mgr.c
buffer_mgr_stat.o: buffer_mgr_stat.c buffer_mgr.h dberror.h dt.h storage_mgr.h buffer_mgr_stat.h
	$(CC) -c buffer_mgr_stat.c
expr.o: expr.c record_mgr.h dberror.h expr.h tables.h
	$(CC) -c expr.c
record_mgr.o: record_mgr.c record_mgr.h buffer_mgr.h
	$(CC) -c record_mgr.c
rm_serializer.o: rm_serializer.c record_mgr.h dberror.h tables.h
	$(CC) -c rm_serializer.c
btree_mgr.o: btree_mgr.c btree_mgr.h record_mgr.h
	$(CC) -c btree_mgr.c
test_expr.o: test_expr.c dberror.h expr.h record_mgr.h btree_mgr.c tables.h test_helper.h
	$(CC) -c test_expr.c
clean :
	rm test_assign4_1.o dberror.o storage_mgr.o buffer_mgr.o buffer_mgr_stat.o expr.o record_mgr.o rm_serializer.o btree_mgr.o test_expr.o
