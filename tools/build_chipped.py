"""Generates translation/lang/chipped.json from Chipped's English names.

Names are split into a vanilla material plus describing words, and written as
"Матеріал — опис". The material uses the game's own Ukrainian name; the words come
from translation/chipped_words.json. Names that cannot be built fully stay English
and are listed at the end.

usage: python tools/build_chipped.py <instance dir>
"""
import glob
import io
import json
import os
import re
import sys
import zipfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LANGZIP = os.path.join(ROOT, 'src/main/resources/cte2_uk/install/files/config/openloader/resources/cte2_uk_lang.zip')
WORDS = os.path.join(ROOT, 'translation/chipped_words.json')
OUT = os.path.join(ROOT, 'translation/lang/chipped.json')
LANG = re.compile(r'assets/([^/]+)/lang/(en_us|uk_ua)\.json$')

inst = sys.argv[1]
launcher = os.path.dirname(os.path.dirname(inst.rstrip('/\\')))


def read_lang(z, name):
    return {k: v for k, v in json.loads(z.read(name).decode('utf-8-sig'), strict=False).items() if isinstance(v, str)}


def langs_in(path, namespace):
    out = {'en_us': {}, 'uk_ua': {}}
    with zipfile.ZipFile(path) as z:
        for n in z.namelist():
            m = LANG.match(n)
            if m and m.group(1) == namespace:
                out[m.group(2)].update(read_lang(z, n))
    return out


# vanilla English and Ukrainian (the game's assets, then our pack on top)
client = glob.glob(os.path.join(launcher, 'libraries/net/minecraft/client/1.20.1-*/client-*-extra.jar'))[0]
v_en = langs_in(client, 'minecraft')['en_us']
v_uk = {}
for idx in glob.glob(os.path.join(launcher, 'assets/indexes/*.json')):
    o = json.load(open(idx, encoding='utf-8')).get('objects', {}).get('minecraft/lang/uk_ua.json')
    if o:
        v_uk.update(json.load(open(os.path.join(launcher, 'assets/objects', o['hash'][:2], o['hash']), encoding='utf-8')))
        break
v_uk.update(langs_in(LANGZIP, 'minecraft')['uk_ua'])

chipped_jar = glob.glob(os.path.join(inst, 'mods', 'chipped-*.jar'))[0]
c_en = langs_in(chipped_jar, 'chipped')['en_us']

W = json.load(open(WORDS, encoding='utf-8'))
blacklist = set(W['not_materials'])
materials = {}
for k, v in v_en.items():
    if (k.startswith('block.minecraft.') or k.startswith('item.minecraft.')) and k in v_uk and v not in blacklist:
        materials.setdefault(v, v_uk[k])
for alias, target in W['material_aliases'].items():
    materials[alias] = materials[target] if target in materials else target
NOUNS = W['nouns']
WORDMAP = W['words']
MAXW = max(len(p.split()) for p in WORDMAP)


def gender_of(uk):
    first = uk.split()[0].lower().rstrip(',')
    if first in W['gender_exceptions']:
        return W['gender_exceptions'][first]
    if first.endswith(('ий', 'ій')):
        return 'm'
    if first.endswith(('а', 'я')):
        return 'f'
    if first.endswith(('е', 'є', 'о')):
        return 'n'
    if first.endswith(('і', 'ї', 'и')):
        return 'pl'
    return 'm'


ENDINGS = {'ий': {'m': 'ий', 'f': 'а', 'n': 'е', 'pl': 'і'}, 'ій': {'m': 'ій', 'f': 'я', 'n': 'є', 'pl': 'і'}}


def inflect(phrase, g):
    out = []
    for w in phrase.split():
        for end, forms in ENDINGS.items():
            if w.endswith(end) and len(w) > 3:
                w = w[:-len(end)] + forms[g]
                break
        out.append(w)
    return ' '.join(out)


def words_to_uk(words):
    """Greedy longest match of English words to dictionary entries; None if something is unknown."""
    res, i = [], 0
    while i < len(words):
        for n in range(min(MAXW, len(words) - i), 0, -1):
            p = ' '.join(words[i:i + n])
            if p in WORDMAP:
                res.append(WORDMAP[p])
                i += n
                break
        else:
            return None, words[i]
    return res, None


def material_candidates(words):
    """Known materials inside the name: longest first, then rightmost."""
    for length in range(len(words), 0, -1):
        for start in range(len(words) - length, -1, -1):
            cand = ' '.join(words[start:start + length])
            if cand in materials:
                yield start, length, cand


unknown = {}


def build(words, start, length, mat):
    """Ukrainian name for one way of reading the English name, or (None, problem)."""
    mat_uk = materials[mat]
    pre, post = words[:start], words[start + length:]
    noun = None
    if post:
        p = ' '.join(post)
        if p not in NOUNS:
            return None, '(noun) ' + p
        noun = NOUNS[p]
        if noun[0] == 'цегла' and any(x in mat_uk.lower() for x in W['brick_materials']):
            noun = ['кладка', 'f']
    adjs, bad = words_to_uk(pre)
    if adjs is None:
        return None, bad
    g = noun[1] if noun else gender_of(mat_uk)
    front = [inflect(a, g) for a in adjs if not a.startswith('~')]
    back = [a[1:] for a in adjs if a.startswith('~')]
    parts = front + ([noun[0]] if noun else []) + back
    if not parts:
        return mat_uk, None
    return f'{mat_uk} — {" ".join(parts)}', None


def translate(name):
    if name in W['full']:
        return W['full'][name]
    words = name.split()
    first_problem = '(material) ' + name
    # "Bricks" is a material too, but "Netherrack Bricks" means netherrack made into bricks
    cands = sorted(material_candidates(words), key=lambda c: c[2] in W['weak_materials'])
    for i, (start, length, mat) in enumerate(cands):
        t, problem = build(words, start, length, mat)
        if t:
            return t
        if i == 0:
            first_problem = problem
    unknown[first_problem] = unknown.get(first_problem, 0) + 1
    return None


out = {'_comment': 'Chipped: згенеровано tools/build_chipped.py з translation/chipped_words.json (формат «Матеріал — опис»).'}
skipped = 0
for k, v in sorted(c_en.items()):
    if k in W['keys']:
        out[k] = W['keys'][k]
    elif k.startswith('block.chipped.'):
        t = translate(v)
        if t:
            out[k] = t
        else:
            skipped += 1
    elif k.startswith('tag.item.chipped.'):
        block = 'block.minecraft.' + k[len('tag.item.chipped.'):]
        if block in v_uk:
            out[k] = v_uk[block]
        elif k in W['tags']:
            out[k] = W['tags'][k]
        else:
            skipped += 1
            unknown['(tag) ' + k] = 1
    else:
        skipped += 1
        unknown['(key) ' + k] = 1

json.dump(out, open(OUT, 'w', encoding='utf-8', newline='\n'), ensure_ascii=False, indent=1)
print('translated', len(out) - 1, 'skipped', skipped)
for w, c in sorted(unknown.items(), key=lambda x: -x[1]):
    print(c, w)
