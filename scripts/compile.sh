#!/usr/bin/env bash
# ====================================================================
# Career Recommendation System - Compilation Script (Linux / macOS)
# ====================================================================

set -e

# Change to project root directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/.."

echo "===================================================================="
echo " Career Recommendation System - Compilation Script (Linux / macOS)"
echo "===================================================================="
echo ""

# Verify javac
if ! command -v javac &> /dev/null; then
    echo "[ERROR] 'javac' compiler was not found in PATH."
    echo "Please ensure JDK is installed and PATH is configured."
    exit 1
fi

mkdir -p src/main/webapp/WEB-INF/classes

echo "[INFO] Compiling all Java source files..."
find src/main/java -name "*.java" > /tmp/career_sources.txt
javac -cp "lib/*" -d src/main/webapp/WEB-INF/classes @/tmp/career_sources.txt
rm -f /tmp/career_sources.txt

if [ -f "config/db.properties" ]; then
    cp config/db.properties src/main/webapp/WEB-INF/classes/db.properties
fi

echo ""
echo "[SUCCESS] Compilation completed successfully into src/main/webapp/WEB-INF/classes!"
