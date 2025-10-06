import os
from openpyxl import Workbook

# Kreiraj putanju i direktorijum ako ne postoji
os.makedirs("src/main/resources/data", exist_ok=True)

# Kreiraj Excel fajl
wb = Workbook()
ws = wb.active
ws.title = "Recommendation Rules"

# Dodaj zaglavlja
headers = [
    "contentType", "category", "minEngagement", "priorityScore",
    "hashtag", "optimalHour", "targetAudience", "reasoningText"
]
ws.append(headers)

# Dodaj podatke
data = [
    ["video", "fitness", 0.05, 8.5, "#fitness", 18, "influencer", "High-engagement fitness video content"],
    ["video", "tech", 0.04, 8.0, "#technology", 20, "influencer", "Trending tech video for tech audience"],
    ["article", "tech", 0.03, 6.5, "#coding", 14, "creator", "Technical article for developers"],
    ["image", "fashion", 0.06, 7.5, "#style", 19, "influencer", "Viral fashion image content"],
    ["video", "cooking", 0.045, 7.0, "#recipe", 17, "creator", "Popular cooking tutorial"],
    ["article", "fitness", 0.035, 6.0, "#health", 10, "creator", "Health tips"]
]

for row in data:
    ws.append(row)

# Sačuvaj fajl
output_path = "src/main/resources/data/recommendation-rules.xlsx"
wb.save(output_path)

output_path
