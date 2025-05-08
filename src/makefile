# =====================
# SPROLA Interpreter Makefile
# =====================

SRC_DIR = src
BIN_DIR = bin
TEST_DIR = test
MAIN_CLASS = Main
ENTRY_CLASS = $(BIN_DIR)/$(MAIN_CLASS).class

JAVA_FILES := $(shell find $(SRC_DIR) -name "*.java")
TEST_FILES := $(shell find $(TEST_DIR) -name "*.java")

.PHONY: all run test clean

all: $(ENTRY_CLASS)

$(ENTRY_CLASS): $(JAVA_FILES)
	@mkdir -p $(BIN_DIR)
	javac -d $(BIN_DIR) $(JAVA_FILES)

run: all
	java -cp $(BIN_DIR) $(MAIN_CLASS) input.sprola

test:
	@echo "Running tests with JUnit 5..."
	@mkdir -p $(BIN_DIR)
	javac -d $(BIN_DIR) -cp .:lib/junit-platform-console-standalone-1.10.0.jar $(JAVA_FILES) $(TEST_FILES)
	java -jar lib/junit-platform-console-standalone-1.10.0.jar --class-path $(BIN_DIR) --scan-class-path

clean:
	rm -rf $(BIN_DIR)
