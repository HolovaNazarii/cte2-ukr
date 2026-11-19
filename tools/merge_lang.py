"""Вливає переклади з translation/lang/<простір>.json у мовний пакет і оновлює manifest.tsv.

Запуск із кореня проєкту:  python tools/merge_lang.py

* translation/lang/<простір>.json — {ключ: український текст}; ключі на «_» (коментарі) пропускаються.
  Рядки додаються/замінюються в assets/<простір>/lang/uk_ua.json усередині cte2_uk_lang.zip
  (файл створюється, якщо його ще немає). Решта архіву копіюється без змін.
* У manifest.tsv оновлюється хеш архіву, а попередній хеш додається як відомий оригінал —
  щоб у гравців попередня версія пакета теж замінилася.
"""
import glob
import hashlib
import json
import os
import shutil
import zipfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
INSTALL = os.path.join(ROOT, 'src', 'main', 'resources', 'cte2_uk', 'install')
REL = 'config/openloader/resources/cte2_uk_lang.zip'
LANGZIP = os.path.join(INSTALL, 'files', REL)
MANIFEST = os.path.join(INSTALL, 'manifest.tsv')


def sha(path):
    return hashlib.sha256(open(path, 'rb').read()).hexdigest()


updates = {}
for path in sorted(glob.glob(os.path.join(ROOT, 'translation', 'lang', '*.json'))):
    ns = os.path.splitext(os.path.basename(path))[0]
    data = json.load(open(path, encoding='utf-8'))
    updates[f'assets/{ns}/lang/uk_ua.json'] = {k: v for k, v in data.items() if not k.startswith('_')}

before = sha(LANGZIP)
tmp = LANGZIP + '.tmp'
with zipfile.ZipFile(LANGZIP) as src, zipfile.ZipFile(tmp, 'w') as dst:
    seen = set()
    for info in src.infolist():
        data = src.read(info.filename)
        if info.filename in updates:
            lang = json.loads(data.decode('utf-8'))
            changed = sum(1 for k, v in updates[info.filename].items() if lang.get(k) != v)
            lang.update(updates[info.filename])
            data = (json.dumps(lang, ensure_ascii=False, indent=2) + '\n').encode('utf-8')
            print(f'{info.filename}: {changed} changed')
            seen.add(info.filename)
        dst.writestr(info, data, compress_type=info.compress_type)
    for name, lang in updates.items():
        if name not in seen:
            info = zipfile.ZipInfo(name, date_time=(2026, 11, 15, 0, 0, 0))
            dst.writestr(info, (json.dumps(lang, ensure_ascii=False, indent=2) + '\n').encode('utf-8'),
                         compress_type=zipfile.ZIP_DEFLATED)
            print(f'{name}: created with {len(lang)} keys')
shutil.move(tmp, LANGZIP)

after = sha(LANGZIP)
if after == before:
    print('language pack unchanged')
else:
    rows = open(MANIFEST, encoding='utf-8').read().rstrip('\n').split('\n')
    out = []
    for row in rows:
        if row.startswith(REL + '\t'):
            path, original, _ = row.split('\t')
            row = f'{path}\t{original}\t{after}'
        out.append(row)
    last = max(i for i, r in enumerate(out) if r.startswith(REL + '\t'))
    out.insert(last + 1, f'{REL}\t{before}\t{after}')
    open(MANIFEST, 'w', encoding='utf-8', newline='\n').write('\n'.join(out) + '\n')
    print('manifest updated:', after)
