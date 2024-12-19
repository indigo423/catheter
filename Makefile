.DEFAULT_GOAL := build

SHELL               := /bin/bash -o nounset -o pipefail -o errexit
MAVEN_SETTINGS_XML  ?= ./.cicd-assets/settings.xml
PACKAGE_VERSION     ?= 0
VERSION             ?= $(shell git describe --tags --abbrev=0 | grep -oE '([0-9]+\.[0-9]+.[0-9]+)' | head -n 1)
VERSION             := $(if $(VERSION),$(VERSION),2.0.5-SNAPSHOT)

.PHONY help:
help:
	@echo ""
	@echo "Build testing tool Catheter"
	@echo "Goals:"
	@echo "  mvn-download: Resolve and download Maven dependencies"
	@echo "  help:         Show this help with explaining the build goals"
	@echo "  build:        Compile and build the package for the Cetheter tool"
	@echo "  clean:        Clean the build artifacts"
	@echo ""

.PHONY deps-build:
deps-build:
	command -v java
	command -v javac
	command -v mvn

.PHONY build:
build: deps-build
	mvn --settings=$(MAVEN_SETTINGS_XML) install

.PHONY clean:
clean: deps-build
	mvn --settings=$(MAVEN_SETTINGS_XML) clean
