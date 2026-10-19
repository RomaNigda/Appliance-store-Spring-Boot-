
PROJECT_DIR="."
LOG_DIR="./logs"
BACKUP_DIR="./backups"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
LOG_FILE="$LOG_DIR/maven_build_$TIMESTAMP.log"

mkdir -pv "$LOG_DIR"
mkdir -pv "$BACKUP_DIR"

echo "Starting Maven build process..." | tee -a "$LOG_FILE"

cd "$PROJECT_DIR" || { echo "Failed to change directory to $PROJECT_DIR" | tee -a "$LOG_FILE"; exit 1; }

mvn clean package -Dspring.profiles.active=prod 2>&1 | tee -a "$LOG_FILE"

EXIT_CODE=${PIPESTATUS[0]}

if [ "$EXIT_CODE" -eq 0 ]; then
    echo "Maven build completed successfully." | tee -a "$LOG_FILE"

    JAR_FILE=$(find target -name "*.jar" ! -name "*sources.jar" ! -name "*javadoc.jar" | head -n 1)

    if [ -n "$JAR_FILE" ]; then
        BACKUP_FILE="$BACKUP_DIR/$(basename "$JAR_FILE" .jar)_$TIMESTAMP.jar"
        cp "$JAR_FILE" "$BACKUP_FILE"
        echo "Backup of the JAR file created at: $BACKUP_FILE" | tee -a "$LOG_FILE"
    else
        echo "No JAR file found to backup." | tee -a "$LOG_FILE"
    fi
else
    echo "Maven build failed with exit code $EXIT_CODE." | tee -a "$LOG_FILE"
    exit "$EXIT_CODE"
fi
