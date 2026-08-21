#!/usr/bin/env bash

# =====================================================
# سكربت إنشاء أيقونات التطبيق (ic_launcher.png)
# يدعم: ImageMagick أو Python/Pillow
# =====================================================

set -e

BASE_DIR="app/src/main/res"
BG_COLOR="#2c3e50"      # لون الخلفية
TEXT_COLOR="#ffffff"     # لون النص
TEXT="RR"                # النص الذي سيظهر

# الأبعاد لكل كثافة
declare -A SIZES=(
    ["mipmap-mdpi"]=48
    ["mipmap-hdpi"]=72
    ["mipmap-xhdpi"]=96
    ["mipmap-xxhdpi"]=144
    ["mipmap-xxxhdpi"]=192
)

# اختيار أداة الرسم
if command -v magick &> /dev/null; then
    DRAW_CMD="magick"
elif command -v convert &> /dev/null; then
    DRAW_CMD="convert"
else
    DRAW_CMD=""
fi

# دالة إنشاء أيقونة باستخدام Python
create_with_python() {
    local size="$1"
    local filepath="$2"
    python3 - "$size" "$filepath" <<'PYEOF'
import sys
from PIL import Image, ImageDraw, ImageFont

size = int(sys.argv[1])
filepath = sys.argv[2]

bg = (44, 62, 80)        # لون الخلفية
fg = (255, 255, 255)     # لون النص

img = Image.new("RGB", (size, size), bg)
draw = ImageDraw.Draw(img)

try:
    font = ImageFont.truetype("DejaVuSans-Bold.ttf", size // 3)
except IOError:
    font = ImageFont.load_default()

text = "RR"
bbox = draw.textbbox((0, 0), text, font=font)
w, h = bbox[2] - bbox[0], bbox[3] - bbox[1]
pos = ((size - w) // 2, (size - h) // 2)
draw.text(pos, text, fill=fg, font=font)
img.save(filepath)
PYEOF
}

# إنشاء المجلدات والصور
mkdir -p "$BASE_DIR"

for folder in "${!SIZES[@]}"; do
    size="${SIZES[$folder]}"
    dir_path="$BASE_DIR/$folder"
    mkdir -p "$dir_path"
    file_path="$dir_path/ic_launcher.png"

    echo "▶ إنشاء $folder/ic_launcher.png (${size}x${size})"

    if [[ -n "$DRAW_CMD" ]]; then
        # استخدام ImageMagick
        if ! $DRAW_CMD -size "${size}x${size}" \
            -background "$BG_COLOR" \
            -fill "$TEXT_COLOR" \
            -gravity center \
            -pointsize $((size / 2)) \
            "label:$TEXT" "$file_path" >/dev/null 2>&1; then
            echo "   ⚠️ فشل ImageMagick، جارٍ استخدام Python..."
            create_with_python "$size" "$file_path"
        fi
    else
        # استخدام Python/Pillow إذا كان متاحًا
        if command -v python3 &> /dev/null && python3 -c "import PIL" &> /dev/null; then
            create_with_python "$size" "$file_path"
        else
            echo "   ❌ لا توجد أداة متاحة (ImageMagick أو Python/Pillow)"
            echo "   يرجى تثبيت ImageMagick أو Pillow"
            exit 1
        fi
    fi

    echo "   ✅ تم الحفظ: $file_path"
done

echo ""
echo "🎉 تم إنشاء جميع الأيقونات بنجاح!"
