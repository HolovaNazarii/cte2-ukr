"""Застосовує translation/mine_and_slash_names.tsv до мовного пакета й оновлює список англійських оригіналів.

Запуск із кореня проєкту:  python tools/apply_mns_names.py

* У cte2_uk_lang.zip значення ключів mmorpg.spell/effect/talent/asc_class/mercenary, що дорівнюють англійській
  назві з таблиці, замінюються українськими — в усіх трьох копіях файлу Mine and Slash
  (mmorpg, cte2_ua_fix, mmorpg_uk_fix: рідний uk_ua Mine and Slash ламає простір mmorpg, тож пакет дублює його).
* src/main/resources/assets/cte2_uk/original_names.json — ключ -> англійська назва; з нього мод показує
  оригінал у підказці, коли затиснуто Shift. Скрипт можна запускати повторно.
"""
import json
import os
import re
import shutil
import zipfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
TSV = os.path.join(ROOT, 'translation', 'mine_and_slash_names.tsv')
LANGZIP = os.path.join(ROOT, 'src/main/resources/cte2_uk/install/files/config/openloader/resources/cte2_uk_lang.zip')
ORIGINALS = os.path.join(ROOT, 'src/main/resources/assets/cte2_uk/original_names.json')
ENTRIES = ['assets/mmorpg/lang/uk_ua.json', 'assets/cte2_ua_fix/lang/uk_ua.json', 'assets/mmorpg_uk_fix/lang/uk_ua.json']
KEY = re.compile(r'^mmorpg\.(spell|effect|talent|asc_class|mercenary)\.[^.]+$')

names = {}
for line in open(TSV, encoding='utf-8'):
    line = line.rstrip('\n')
    if not line or line.startswith('#'):
        continue
    en, _, uk = line.partition('\t')
    names[en.strip()] = uk.strip()

originals = json.load(open(ORIGINALS, encoding='utf-8')) if os.path.exists(ORIGINALS) else {}


def translate(lang):
    changed = 0
    for key, value in lang.items():
        if not KEY.match(key):
            continue
        en = originals.get(key, value)
        uk = names.get(en)
        if not uk:
            continue
        originals[key] = en
        if value != uk:
            lang[key] = uk
            changed += 1
    return changed


tmp = LANGZIP + '.tmp'
with zipfile.ZipFile(LANGZIP) as src, zipfile.ZipFile(tmp, 'w') as dst:
    for info in src.infolist():
        data = src.read(info.filename)
        if info.filename in ENTRIES:
            lang = json.loads(data.decode('utf-8'))
            print(f'{info.filename}: {translate(lang)} changed')
            data = (json.dumps(lang, ensure_ascii=False, indent=2) + '\n').encode('utf-8')
        dst.writestr(info, data, compress_type=info.compress_type)
shutil.move(tmp, LANGZIP)

os.makedirs(os.path.dirname(ORIGINALS), exist_ok=True)
with open(ORIGINALS, 'w', encoding='utf-8', newline='\n') as f:
    json.dump(dict(sorted(originals.items())), f, ensure_ascii=False, indent=1)
    f.write('\n')
print('originals:', len(originals))
