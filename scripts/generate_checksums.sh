#!/bin/bash
APK_DIR="app/build/outputs/apk"
OUTPUT_FILE="CHECKSUMS.txt"

echo "Gu3ssWeak Artifact Checksums" > "$OUTPUT_FILE"
echo "Generated: $(date -u '+%Y-%m-%d %H:%M:%S UTC')" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

find "$APK_DIR" -name "*.apk" | while read -r apk; do
    name=$(basename "$apk")
    hash=$(shasum -a 256 "$apk" | awk '{print $1}')
    size=$(du -h "$apk" | awk '{print $1}')
    echo "File:   $name" >> "$OUTPUT_FILE"
    echo "Path:   $apk" >> "$OUTPUT_FILE"
    echo "Size:   $size" >> "$OUTPUT_FILE"
    echo "SHA256: $hash" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
done

echo "Checksums written to $OUTPUT_FILE"
cat "$OUTPUT_FILE"
