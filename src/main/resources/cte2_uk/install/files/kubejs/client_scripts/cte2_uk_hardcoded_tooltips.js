// Клієнтський шар для рядків, які моди виводять Java-літералами без lang-ключів.
// Він не змінює JAR і замінює лише точні англійські рядки після їх появи.
ItemEvents.tooltip(event => {
  const replaceExact = (item, english, ukrainian) => {
    event.addAdvanced(item, (_stack, _advanced, text) => {
      // replaceAll із рядком не сприймає [] як регулярний вираз.
      // Через це форматовані Java-літерали не знаходилися в runtime.
      const index = text.findIndex(line => line.getString().replace(/§[0-9A-FK-OR]/gi, '').trim() === english)
      if (index >= 0) {
        text.set(index, Text.of(ukrainian).gray())
      }
    })
  }

  // KubeJS item selectors are not reliable for several MCreator items in this
  // pack. Run the reviewed exact substitutions over the final tooltip list for
  // every item; unrelated text is untouched.
  const globalExact = {
    'This is an example block from TinyMultiblockLib': 'Це приклад блока з TinyMultiblockLib',
    'This is an example item from TinyMultiblockLib': 'Це приклад предмета з TinyMultiblockLib',
    'Some of its functions may be disabled outside dev env': 'Деякі функції можуть бути вимкнені поза середовищем розробки',
    'Errored Gate Pearl, file a bug report detailing how you obtained this.': 'Пошкоджена перлина брами. Повідомте про помилку й опишіть, як ви її отримали.',
    'This is a testing item, you should not have this in normal gameplay.': 'Тестовий предмет — у звичайній грі його бути не повинно.',
    'Mine and Slash Training Dummy': 'Тренувальний манекен Mine and Slash',
    'When hit :': 'При влучанні:',
    'Clear all effect of ennemy': 'Знімає з противника всі ефекти',
    'Shoot Awful Ghast fire ball': 'Стріляє вогняною кулею моторошного гаста',
    'Brun and Smelt the Block': 'Спалює і переплавляє блок',
    'Imunise you to Burning and Wither Effect': 'Дає несприйнятливість до горіння та висушення',
    'Imunise you to Spore and Wither Effect': 'Дає несприйнятливість до спор та висушення',
    'Imunise you to Levitation Effect': 'Дає несприйнятливість до левітації',
    '+Gain 80% of your Intelligence as Health (Full)': '+80% інтелекту до здоров\'я (повний комплект)',
    '+Gain 1% of your Health as Weapon Damage (Full)': '+1% здоров\'я до шкоди зброї (повний комплект)',
    '+15% Protection from Magic Damage': '+15% захисту від магічної шкоди',
    'Summons Crystal Spikes from the ground when you hit enemies': 'При влучанні у ворога прикликає кристалічні шипи із землі',
    'Summons Crystal Spikes from the ground when thrown': 'При кидку прикликає кристалічні шипи із землі',
    'Summons Crystal Spikes from the ground in the place where it hits': 'У місці влучання прикликає кристалічні шипи із землі',
    'Set Bonus:': 'Бонус комплекту:',
    '+20% Heal Strength (3/4)': '+20% сили зцілення (3/4)',
    'A dagger dripping with poison is probably unsafe for the wielder too...': 'Кинджал стікає отрутою і небезпечний навіть для власника...',
    'A magical staff with blades sharpened by lightning.': 'Магічний посох із клинками, загостреними блискавкою.',
    'A sword forged from blood..': 'Меч, викуваний із крові...',
    'A sword shaped like that of a key.': 'Меч у формі ключа.',
    'Cursed by the wielders before; you shouldn\'t hold this too long.': 'Проклятий колишніми власниками — не тримайте його надто довго.',
    'Harnessing the sun\'s rays into a magnificent magic aura.': 'Перетворює сонячні промені на могутню магічну ауру.',
    'The glow this staff emits is void of all things good...': 'Сяйво цього посоха позбавлене всього доброго...',
    'This dagger looks like it would really hurt.': 'Сам вигляд цього кинджала віщує сильний біль.'
  }
  event.addAdvancedToAll((_stack, _advanced, text) => {
    for (let i = 0; i < text.size(); i++) {
      const raw = text.get(i).getString().replace(/§[0-9A-FK-OR]/gi, '').trim()
      if (globalExact[raw]) text.set(i, Text.of(globalExact[raw]).gray())
    }
  })

  event.addAdvanced(/mimi:.*/, (_stack, _advanced, text) => {
    const index = text.findIndex(line => line.getString().trim() === 'Receive Notes From: None')
    if (index >= 0) {
      text.set(index, Text.of('  Джерело нот: немає').gray())
    }
    const exact = {
      'MIDI Settings:': 'Налаштування MIDI:',
      '  Volume: 5': '  Гучність: 5',
      '  Device Input: Disabled': '  Вхід пристрою: вимкнено'
    }
    for (let i = 0; i < text.size(); i++) {
      let midiText = text.get(i).getString()
      if (exact[midiText]) text.set(i, Text.of(exact[midiText]).gray())
      else if (midiText.startsWith('  Channels:')) text.set(i, Text.of(midiText.replace('Channels:', 'Канали:')).gray())
    }
  })

  replaceExact('mimi:settingssync', 'Crouch + Right-Click Block to Copy Settings', 'Shift + ПКМ по блоку — копіювати налаштування')
  replaceExact('mimi:settingssync', 'Right-Click Block of Same Type to Paste', 'ПКМ по блоку того самого типу — вставити налаштування')
  replaceExact('mimi:sourcelinker', 'Crouch + Right-Click to Link to a Transmitter', 'Shift + ПКМ — прив\'язати до передавача')
  replaceExact('mimi:sourcelinker', 'Right-Click Block to Link it to Saved Transmitter', 'ПКМ по блоку — прив\'язати до збереженого передавача')

  replaceExact('stalwart_dungeons:awful_dagger', 'When hit :', 'При влучанні:')
  replaceExact('stalwart_dungeons:awful_dagger', 'Clear all effect of ennemy', 'Знімає з противника всі ефекти')
  replaceExact('stalwart_dungeons:awful_gun', 'Shoot Awful Ghast fire ball', 'Стріляє вогняною кулею моторошного гаста')
  replaceExact('stalwart_dungeons:nether_hammer', 'Brun and Smelt the Block', 'Спалює і переплавляє блок')
  replaceExact(/stalwart_dungeons:tungsten_.*/, 'Imunise you to Burning and Wither Effect', 'Дає несприйнятливість до горіння та висушення')
  replaceExact(/stalwart_dungeons:warted_tungsten_.*/, 'Imunise you to Spore and Wither Effect', 'Дає несприйнятливість до спор та висушення')
  replaceExact(/stalwart_dungeons:chorundum_armor_.*/, 'Imunise you to Levitation Effect', 'Дає несприйнятливість до левітації')
  replaceExact(/cte2modpackarmors:divine_plate_armor_.*/, '+Gain 80% of your Intelligence as Health (Full)', '+80% інтелекту до здоров\'я (повний комплект)')
  replaceExact(/cte2modpackarmors:endless_blood_armor_.*/, '+Gain 1% of your Health as Weapon Damage (Full)', '+1% здоров\'я до шкоди зброї (повний комплект)')
  replaceExact(/monsterplus:crystal_.*/, '+15% Protection from Magic Damage', '+15% захисту від магічної шкоди')
  replaceExact('monsterplus:crystal_sword', 'Summons Crystal Spikes from the ground when you hit enemies', 'При влучанні у ворога прикликає кристалічні шипи із землі')
  replaceExact('monsterplus:crystal_clump', 'Summons Crystal Spikes from the ground when thrown', 'При кидку прикликає кристалічні шипи із землі')
  replaceExact('monsterplus:crystal_arrow', 'Summons Crystal Spikes from the ground in the place where it hits', 'У місці влучання прикликає кристалічні шипи із землі')

  event.addAdvanced(/cte2modpackarmors:.*/, (_stack, _advanced, text) => {
    const exact = {
      'A dagger dripping with poison is probably unsafe for the wielder too...': 'Кинджал стікає отрутою і небезпечний навіть для власника...',
      'A magical staff with blades sharpened by lightning.': 'Магічний посох із клинками, загостреними блискавкою.',
      'A sword forged from blood..': 'Меч, викуваний із крові...',
      'A sword shaped like that of a key.': 'Меч у формі ключа.',
      'Cursed by the wielders before; you shouldn\'t hold this too long.': 'Проклятий колишніми власниками — не тримайте його надто довго.',
      'Harnessing the sun\'s rays into a magnificent magic aura.': 'Перетворює сонячні промені на могутню магічну ауру.',
      'The glow this staff emits is void of all things good...': 'Сяйво цього посоха позбавлене всього доброго...',
      'This dagger looks like it would really hurt.': 'Сам вигляд цього кинджала віщує сильний біль.'
    }
    for (let i = 0; i < text.size(); i++) {
      const raw = text.get(i).getString().trim()
      if (raw === 'Set Bonus:') text.set(i, Text.of('Бонус комплекту:').gray())
      else if (raw === '+20% Heal Strength (3/4)') text.set(i, Text.of('+20% сили зцілення (3/4)').gray())
      else if (exact[raw]) text.set(i, Text.of(exact[raw]).gray())
    }
  })

  // Mowzie's Mobs 1.20.1 виводить частину підказок літералами, оминаючи lang-файли.
  event.addAdvanced(/mowziesmobs:.*/, (_stack, _advanced, text) => {
    const exact = {
      '+3 attack when punching (including attacks with the Earthrend Gauntlet)': '+3 до шкоди без зброї (включно із Землебуровою рукавицею)',
      '+4 armor and +1 knockback resist while you are inflicted with Slowness': '+4 броні та +1 опору відкиданню під дією сповільнення',
      '1/3 chance to instantly kill animals with less than 30 health': 'Шанс 1/3 миттєво вбити тварину з менш ніж 30 од. здоров\'я',
      'After 2 days, the foliaath will be fully grown': 'Через 2 дні фоліат повністю виросте',
      'Allows you to command the power of the earth.': 'Дає змогу керувати силою землі.',
      'Allows you to command the power of the sun.': 'Дає змогу керувати силою сонця.',
      'A small boost to regeneration and damage resistance.': 'Трохи прискорює регенерацію та підвищує опір шкоді.',
      'Can only be used with blowgun': 'Використовується лише з духовою трубкою',
      'Chance to spawn a geomancy boulder nearby when you deal or take damage': 'При завданні чи отриманні шкоди може створити поруч геомантичний валун',
      'Deals double damage from behind': 'Завдає подвійної шкоди зі спини',
      'Eat to briefly gain night vision': 'З\'їжте, щоб ненадовго отримати нічне бачення',
      'Equip, then right-click while holding another mask to summon minions': 'Надягніть, потім візьміть іншу маску й натисніть ПКМ, щоб прикликати прислужників',
      'Every half-day, the baby foliaath will get hungry': 'Кожні пів дня молодий фоліат голоднітиме',
      'Extends attack range by 1.5 blocks': 'Збільшує дальність атаки на 1,5 блока',
      'Gain increased movement speed for 3 seconds after falling more than 4 blocks': 'Після падіння більш ніж на 4 блоки на 3 секунди підвищує швидкість пересування',
      'Hold right-click to channel an icy whirlwind': 'Утримуйте ПКМ, щоб підтримувати крижаний вихор',
      'Left-click to remove a Mowzie\'s Mobs mob': 'ЛКМ видаляє істоту Mowzie\'s Mobs',
      'Look in the direction you want the rocks to fire, then right-click': 'Спрямуйте погляд у бік пострілу камінням і натисніть ПКМ',
      'Right-click a minion to return it to its mask': 'Натисніть ПКМ по прислужнику, щоб повернути його в маску',
      'Right-click to attack in an large arc': 'ПКМ виконує атаку по широкій дузі',
      'Shift-right-click to slam the ground and create a shockwave': 'Shift + ПКМ: ударити по землі та створити ударну хвилю',
      'Spawns three small rocks that fire as projectiles after a delay': 'Створює три камені, які після затримки летять як снаряди',
      'Toss it any meat to feed it': 'Киньте йому будь-яке м\'ясо, щоб нагодувати',
      'Use to inflict Eclipse on nearby mobs': 'Накладає Затемнення на найближчих істот',
      'Use to inflict Fragility on nearby mobs': 'Накладає Крихкість на найближчих істот',
      'Use to inflict Mining Fatigue on nearby mobs': 'Накладає Втому на найближчих істот',
      'Use to inflict Slowness on nearby mobs': 'Накладає Сповільнення на найближчих істот',
      'Use to inflict Weakness on nearby mobs': 'Накладає Слабкість на найближчих істот',
      'Use to repair Geomancy items, or give to the Sculptor to begin his challenge': 'Лагодить предмети геомантії; віддайте Скульптору, щоб почати випробування',
      'Used to brew Potion of Poison Resistance, craft a Naga Fang Dagger, or craft darts': 'Використовується для зілля захисту від отрути, кинджала з ікла наґи та дротиків'
    }
    for (let i = 0; i < text.size(); i++) {
      const raw = text.get(i).getString().replace(/§[0-9A-FK-OR]/gi, '').trim()
      if (exact[raw]) text.set(i, Text.of(exact[raw]).gray())
    }
  })

  // Mine and Slash складає ці назви з окремих слів в англійському
  // порядку. Lang-файлом узгодження та відмінки виправити неможливо.
  event.addAdvanced(/mmorpg:buff_potion\/.*/, (stack, _advanced, text) => {
    const path = String(stack.id).split('/').pop()
    const split = path.indexOf('_')
    if (split < 0) return
    const rarity = {
      common: 'Звичайний', uncommon: 'Незвичайний', rare: 'Рідкісний',
      epic: 'Епічний', legendary: 'Легендарний', mythic: 'Міфічний'
    }[path.substring(0, split)]
    const kind = {
      arcane: 'магічної шкоди', str: 'сили', crit: 'критичної шкоди',
      int: 'інтелекту', dex: 'спритності', might: 'фізичної шкоди'
    }[path.substring(split + 1)]
    if (rarity && kind && text.size() > 0) {
      text.set(0, Text.of(`${rarity} еліксир ${kind}`))
    }
    for (let i = 0; i < text.size(); i++) {
      if (text.get(i).getString().trim() === '★ Витратний предмет: Еліксир') {
        text.set(i, Text.of('★ Витратний предмет: еліксир').aqua())
      }
    }
  })

  event.addAdvanced(/mmorpg:runes\/.*/, (_stack, _advanced, text) => {
    const exact = {
    }
    for (let i = 0; i < text.size(); i++) {
      const line = text.get(i).getString().replace(/§[0-9A-FK-OR]/gi, '').trim()
      if (exact[line]) text.set(i, Text.of(exact[line]).gray())
    }
  })
})
