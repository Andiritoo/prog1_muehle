default: build

MVN_WRAPPER?=$(shell pwd)/mvnw

build:
	${MVN_WRAPPER} -B --update-snapshots verify  

.PHONY: build