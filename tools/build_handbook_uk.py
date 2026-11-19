"""Збирає український переклад книги «Довідник шукача пригод» (Patchouli, книга cte2).

Запуск із кореня проєкту:  python tools/build_handbook_uk.py <тека збірки CTE2>

Бере англійські файли книги зі збірки (patchouli_books/cte2/en_us), підставляє рядки з
translation/adventurer_handbook.uk.json і пише результат у
src/main/resources/cte2_uk/install/files/patchouli_books/cte2/uk_ua/. Patchouli бере ці файли для
української мови, для інших лишає англійські. Виправлений нами en_us-файл (зламаний запис) береться з проєкту.

Перевіряє: кожен перекладений рядок існує в оригіналі, жоден рядок не пропущено, а службова розмітка
Patchouli ($(c:...), $(l:...), $(k:...), $(/c), $(/l)) збереглася без змін.
"""
import json
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
TRANSLATION = os.path.join(ROOT, 'translation', 'adventurer_handbook.uk.json')
INSTALL = os.path.join(ROOT, 'src', 'main', 'resources', 'cte2_uk', 'install', 'files', 'patchouli_books', 'cte2')
FIELDS = {'name', 'description', 'text', 'title', 'heading', 'link_text', 'subtitle'}
MARKUP = re.compile(r'\$\((?:c:[^)]*|l:[^)]*|k:[^)]*|/c|/l)\)')
LONE_LF = re.compile(r'(?<!\r)\n')

game = sys.argv[1]
source_root = os.path.join(game, 'patchouli_books', 'cte2', 'en_us')
translation = json.load(open(TRANSLATION, encoding='utf-8'))
problems = []


def get_path(node, parts):
    for p in parts:
        node = node[int(p)] if isinstance(node, list) else node[p]
    return node


def set_path(node, parts, value):
    for p in parts[:-1]:
        node = node[int(p)] if isinstance(node, list) else node[p]
    last = parts[-1]
    if isinstance(node, list):
        node[int(last)] = value
    else:
        node[last] = value


def translatable(node, path, out):
    if isinstance(node, dict):
        for k, v in node.items():
            translatable(v, path + [k], out)
    elif isinstance(node, list):
        for i, v in enumerate(node):
            translatable(v, path + [str(i)], out)
    elif isinstance(node, str) and path and path[-1] in FIELDS and node.strip():
        out.add('/'.join(path))


written = 0
for dirpath, _, files in os.walk(source_root):
    for name in sorted(files):
        if not name.endswith('.json'):
            continue
        rel = os.path.relpath(os.path.join(dirpath, name), source_root).replace(os.sep, '/')
        if rel.startswith('templates/'):
            continue
        ours = os.path.join(INSTALL, 'en_us', rel)
        src_path = ours if os.path.exists(ours) else os.path.join(dirpath, name)
        # Читаємо байти: текстовий режим Python перетворив би CR+LF на LF, а книга має макрос
        # "\r\n" -> $(br); без CR Patchouli малює LF як видимий символ.
        data = json.loads(open(src_path, 'rb').read().decode('utf-8'), strict=False)

        needed = set()
        translatable(data, [], needed)
        strings = translation.get(rel, {})
        for missing in sorted(needed - set(strings)):
            problems.append(f'{rel}: no translation for {missing}')
        for path, uk in strings.items():
            parts = path.split('/')
            try:
                en = get_path(data, parts)
            except (KeyError, IndexError, ValueError):
                problems.append(f'{rel}: {path} does not exist in the original')
                continue
            uk = LONE_LF.sub('\r\n', uk)  # перенос рядка в книзі — лише CR+LF (макрос книги)
            if MARKUP.findall(en) != MARKUP.findall(uk):
                problems.append(f'{rel}: {path} markup differs: {MARKUP.findall(en)} vs {MARKUP.findall(uk)}')
            set_path(data, parts, uk)

        out = os.path.join(INSTALL, 'uk_ua', rel)
        os.makedirs(os.path.dirname(out), exist_ok=True)
        with open(out, 'w', encoding='utf-8', newline='\n') as f:
            json.dump(data, f, ensure_ascii=False, indent=4)
            f.write('\n')
        written += 1

for extra in sorted(set(translation) - {r for r in translation if os.path.exists(os.path.join(source_root, r))}):
    problems.append(f'{extra}: file not found in the game')

print(f'written {written} files to {os.path.relpath(INSTALL, ROOT)}/uk_ua')
for p in problems:
    print('PROBLEM:', p)
sys.exit(1 if problems else 0)
